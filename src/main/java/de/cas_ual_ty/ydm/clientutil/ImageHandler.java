package de.cas_ual_ty.ydm.clientutil;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;

public class ImageHandler
{
    private static final String IN_PROGRESS_IMAGE = "card_back";
    private static final String FAILED_IMAGE = "blanc_card";
    
    private static final String INFO_SUFFIX = "_info";
    private static final String ITEM_SUFFIX = "_item";
    private static final String MAIN_SUFFIX = "_main";
    
    private static final ImageHandler INFO_IMAGE_HANDLER = new ImageHandler(YDM.activeInfoImageSize, YDM.cardInfoImagesFolder, (p, i) -> p.getInfoImageName(i));
    private static final ImageHandler MAIN_IMAGE_HANDLER = new ImageHandler(YDM.activeMainImageSize, YDM.cardMainImagesFolder, (p, i) -> p.getMainImageName(i));
    
    private DNCList<String, String> FINAL_IMAGE_READY_LIST = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
    private List<String> IN_PROGRESS = new LinkedList<>();
    private List<String> FAILED = new LinkedList<>();
    
    private int imageSize;
    private File parentFolder;
    private BiFunction<Properties, Byte, String> nameGetter;
    
    private ImageHandler(int imageSize, File parentFolder, BiFunction<Properties, Byte, String> nameGetter)
    {
        this.imageSize = imageSize;
        this.parentFolder = parentFolder;
        this.nameGetter = nameGetter;
    }
    
    private String getReplacementImage(Properties properties, byte imageIndex)
    {
        //        String imageName = properties.getImageName(imageIndex);
        String imageName = this.nameGetter.apply(properties, imageIndex);
        
        int index = this.FINAL_IMAGE_READY_LIST.getIndex(imageName);
        
        if(index == -1)
        {
            if(!this.isImageInProgress(imageName))
            {
                // not finished, not in progress
                
                if(this.getFile(imageName).exists())
                {
                    // image exists, so set ready and return
                    this.setImageFinished(imageName, false);
                    return imageName;
                }
                else if(this.isImageFailed(imageName))
                {
                    // image does not exist, check if failed already and return replacement
                    return ImageHandler.FAILED_IMAGE + "_" + this.imageSize;
                }
                else
                {
                    // image does not exist and has not been tried, so make it ready and return replacement
                    this.makeImageReady(imageName, properties, imageIndex);
                    return ImageHandler.IN_PROGRESS_IMAGE + "_" + this.imageSize;
                }
            }
            else
            {
                // in progress
                return ImageHandler.IN_PROGRESS_IMAGE + "_" + this.imageSize;
            }
        }
        else
        {
            // finished
            return imageName;
        }
    }
    
    private File getFile(String imageName)
    {
        return new File(this.parentFolder, ImageHandler.cutSuffix(imageName) + ".png");
    }
    
    private boolean isImageInProgress(String imageName)
    {
        return this.IN_PROGRESS.contains(imageName);
    }
    
    private boolean isImageFailed(String imageName)
    {
        return this.FAILED.contains(imageName);
    }
    
    private void setImageInProgress(String imageName)
    {
        synchronized(this.IN_PROGRESS)
        {
            this.IN_PROGRESS.add(imageName);
        }
    }
    
    private void setImageFinished(String imageName, boolean failed)
    {
        synchronized(this.IN_PROGRESS)
        {
            this.IN_PROGRESS.remove(imageName);
        }
        
        if(!failed)
        {
            synchronized(this.FINAL_IMAGE_READY_LIST)
            {
                this.FINAL_IMAGE_READY_LIST.addKeepSorted(imageName);
            }
        }
        else
        {
            synchronized(this.FAILED)
            {
                this.FAILED.add(imageName);
            }
        }
    }
    
    // true = info; false = main
    private void makeImageReady(String imageName, Properties properties, byte imageIndex)
    {
        this.setImageInProgress(imageName);
        Thread t = new Thread(new GuiImageWizard(properties, imageIndex, this.imageSize, this.getFile(imageName), (failed) -> this.setImageFinished(imageName, failed)), "YDM Image Downloader");
        t.start();
    }
    
    public static String cutSuffix(String imageName)
    {
        if(ImageHandler.hasInfoSuffix(imageName) || ImageHandler.hasItemSuffix(imageName) || ImageHandler.hasMainSuffix(imageName))
        {
            imageName = imageName.substring(0, imageName.length() - 5);
        }
        
        return imageName;
    }
    
    public static String addInfoSuffix(String imageName)
    {
        return imageName + ImageHandler.INFO_SUFFIX;
    }
    
    public static String addItemSuffix(String imageName)
    {
        return imageName + ImageHandler.ITEM_SUFFIX;
    }
    
    public static String addMainSuffix(String imageName)
    {
        return imageName + ImageHandler.MAIN_SUFFIX;
    }
    
    public static boolean hasInfoSuffix(String imageName)
    {
        return imageName.endsWith(ImageHandler.INFO_SUFFIX);
    }
    
    public static boolean hasItemSuffix(String imageName)
    {
        return imageName.endsWith(ImageHandler.ITEM_SUFFIX);
    }
    
    public static boolean hasMainSuffix(String imageName)
    {
        return imageName.endsWith(ImageHandler.MAIN_SUFFIX);
    }
    
    public static String getInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.INFO_IMAGE_HANDLER.getReplacementImage(properties, imageIndex);
    }
    
    public static String getMainReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.MAIN_IMAGE_HANDLER.getReplacementImage(properties, imageIndex);
    }
    
    private static void downloadRawImage(String imageUrl, File rawImageFile) throws MalformedURLException, IOException
    {
        YdmIOUtil.downloadFile(new URL(imageUrl), rawImageFile);
    }
    
    private static void convertImage(File converted, File raw, int size) throws IOException
    {
        // size: target size, maybe make different versions for card info and card item
        
        InputStream in = new FileInputStream(raw);
        
        BufferedImage img = ImageIO.read(in);
        
        int margin = size / 8;
        
        int sizeX = img.getWidth();
        int sizeY = img.getHeight();
        
        double factor = (double)sizeY / sizeX;
        
        // (sizeX / sizeY =) factor = newSizeX / newSizeY
        // <=> newSizeY = newSizeX / factor
        
        int newSizeY = size - margin;
        int newSizeX = (int)Math.round(newSizeY / factor);
        
        double scaleFactorX = (double)newSizeX / sizeX;
        double scaleFactorY = (double)newSizeY / sizeY;
        
        // Resize card image to size that fits the next image
        BufferedImage after = new BufferedImage(newSizeX, newSizeY, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleFactorX, scaleFactorY);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(img, after);
        img = after;
        
        // Create new image with pow2 resolution, stick previous image in the middle
        BufferedImage newImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImg.getGraphics();
        g.drawImage(img, (size - img.getWidth()) / 2, (size - img.getHeight()) / 2, null);
        g.dispose();
        
        ImageIO.write(newImg, "PNG", converted);
        
        in.close();
    }
    
    public static File getRawFile(String imageName)
    {
        return new File(YDM.rawImagesFolder, imageName + ".jpg");
    }
    
    public static File getFileBySuffix(String imageName)
    {
        if(ImageHandler.hasItemSuffix(imageName))
        {
            return ImageHandler.getItemFile(ImageHandler.cutSuffix(imageName));
        }
        else if(ImageHandler.hasInfoSuffix(imageName))
        {
            return ImageHandler.getInfoFile(ImageHandler.cutSuffix(imageName));
        }
        else
        {
            // need to return something, so by default we pick the main images
            return ImageHandler.getMainFile(ImageHandler.cutSuffix(imageName));
        }
    }
    
    public static File getInfoFile(String imageName)
    {
        return new File(YDM.cardInfoImagesFolder, imageName + ".png");
    }
    
    public static File getItemFile(String imageName)
    {
        return new File(YDM.cardItemImagesFolder, imageName + ".png");
    }
    
    public static File getMainFile(String imageName)
    {
        return new File(YDM.cardMainImagesFolder, imageName + ".png");
    }
    
    public static boolean areAllItemImagesReady()
    {
        return ImageHandler.getMissingItemImages().isEmpty();
    }
    
    public static List<Card> getMissingItemImages()
    {
        List<Card> list = new LinkedList<>();
        for(Card card : YdmDatabase.CARDS_LIST)
        {
            if(!ImageHandler.getItemFile(card.getImageName()).exists())
            {
                list.add(card);
            }
        }
        return list;
    }
    
    public static void downloadAllCardImages()
    {
        Thread t = new Thread(new ItemImagesWizard(YdmDatabase.CARDS_LIST, YdmDatabase.CARDS_LIST.size()));
        t.start();
    }
    
    public static void downloadCardImages(List<Card> list)
    {
        Thread t = new Thread(new ItemImagesWizard(list, list.size()));
        t.start();
    }
    
    private static int imagePipeline(String imageName, String imageUrl, File convertedTarget, int size, Consumer<Boolean> onFinish)
    {
        // onFinish params: (failed) -> ???
        
        // ret codes:
        
        // -2: raw already existed + conversion fails
        // -1: raw download fail
        //  0: raw already existed + converted already existed
        //  1: downloaded raw + converted already existed
        //  2: raw already existed + done conversion
        //  3: downloaded raw + done conversion
        
        int ret = 0;
        
        File raw = ImageHandler.getRawFile(imageName);
        
        if(!raw.exists())
        {
            try
            {
                ret = 1;
                ImageHandler.downloadRawImage(imageUrl, raw);
                ret += 1;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                
                onFinish.accept(true);
                
                // Without the raw image we cant do anything anyways
                return -1;
            }
        }
        
        boolean failed = false;
        
        if(!convertedTarget.exists())
        {
            try
            {
                ImageHandler.convertImage(convertedTarget, raw, size);
                ret += 2;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                failed = true;
                ret = -2;
            }
        }
        
        // Delete cache if requested
        if(!YDM.keepCachedImages)
        {
            raw.delete();
        }
        
        onFinish.accept(failed);
        
        return ret;
    }
    
    private static class GuiImageWizard implements Runnable
    {
        private final Properties properties;
        private final byte imageIndex;
        private final int size;
        private final File file;
        private final Consumer<Boolean> callback;
        
        private GuiImageWizard(Properties properties, byte imageIndex, int size, File file, Consumer<Boolean> callback)
        {
            this.properties = properties;
            this.imageIndex = imageIndex;
            this.size = size;
            this.file = file;
            this.callback = callback;
        }
        
        @Override
        public void run()
        {
            ImageHandler.imagePipeline(
                this.properties.getImageName(this.imageIndex),
                this.properties.getImageURL(this.imageIndex),
                this.file,
                this.size,
                this.callback);
        }
    }
    
    private static class ItemImagesWizard implements Runnable
    {
        private Iterable<Card> list;
        private int size;
        
        private ItemImagesWizard(Iterable<Card> list, int size)
        {
            this.list = list;
            this.size = size;
        }
        
        @Override
        public void run()
        {
            int i = 0;
            int j = 0;
            long millies = System.currentTimeMillis();
            int status;
            
            for(Card card : this.list)
            {
                YDM.log("Fetching image of: " + ++j + "/" + this.size + ": " + card.getProperties().getName() + " (Variant " + card.getImageIndex() + ")");
                
                status = ImageHandler.imagePipeline(card.getImageName(), card.getItemImageURL(), ImageHandler.getItemFile(card.getImageName()), YDM.activeItemImageSize, (failed) ->
                {});
                
                if(status < 0)
                {
                    if(status == -1)
                    {
                        YDM.log("Failed downloading raw image!");
                    }
                    else if(status == -2)
                    {
                        YDM.log("Failed converting image to square format!");
                    }
                }
                else if(status % 2 == 1)
                {
                    ++i;
                }
                
                if(i >= 20)
                {
                    i = 0;
                    millies = System.currentTimeMillis() - millies;
                    
                    if(millies <= 1100)
                    {
                        // In case we pulled 20 images in less than 1 second, we need to slow down a bit
                        // otherwise IP gets blacklisted.
                        // 1100 instead of 1000 just to make sure any inaccuracy doesnt get us blacklisted.
                        
                        try
                        {
                            TimeUnit.MILLISECONDS.sleep(1100 - millies);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    
                    millies = System.currentTimeMillis();
                    
                    break;
                }
            }
            
            YDM.log("Done fething images! Rechecking images to make sure...");
            
            for(Card card : ImageHandler.getMissingItemImages())
            {
                YDM.log("Missing image of: " + card.getProperties().getName() + " (Variant " + card.getImageIndex() + ")");
            }
            
            YDM.log("Done checking images!");
        }
    }
}
