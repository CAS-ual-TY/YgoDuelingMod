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
    
    private static ImageHandler INFO_IMAGE_HANDLER = null;
    private static ImageHandler MAIN_IMAGE_HANDLER = null;
    
    private DNCList<String, String> FINAL_IMAGE_READY_LIST = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
    private List<String> IN_PROGRESS = new LinkedList<>();
    private List<String> FAILED = new LinkedList<>();
    
    private int imageSize;
    private BiFunction<Properties, Byte, String> nameGetter;
    
    public static void init()
    {
        ImageHandler.INFO_IMAGE_HANDLER = new ImageHandler(ClientProxy.activeInfoImageSize, (p, i) -> p.getInfoImageName(i));
        ImageHandler.MAIN_IMAGE_HANDLER = new ImageHandler(ClientProxy.activeMainImageSize, (p, i) -> p.getMainImageName(i));
    }
    
    private ImageHandler(int imageSize, BiFunction<Properties, Byte, String> nameGetter)
    {
        this.imageSize = imageSize;
        this.nameGetter = nameGetter;
    }
    
    private String getReplacementImage(Properties properties, byte imageIndex)
    {
        String imageName = this.nameGetter.apply(properties, imageIndex);
        
        // if its hardcoded, image should already exist
        if(properties.getIsHardcoded())
        {
            return imageName;
        }
        
        int index = this.FINAL_IMAGE_READY_LIST.getIndex(imageName);
        
        if(index == -1)
        {
            if(!this.isImageInProgress(imageName))
            {
                // not finished, not in progress
                
                if(ImageHandler.getTaggedFile(imageName).exists())
                {
                    // image exists, so set ready and return
                    this.setImageFinished(imageName, false);
                    return imageName;
                }
                else if(this.isImageFailed(imageName))
                {
                    // image does not exist, check if failed already and return replacement
                    return ImageHandler.tagWithSize(ImageHandler.FAILED_IMAGE, this.imageSize);
                }
                else
                {
                    // image does not exist and has not been tried, so make it ready and return replacement
                    this.makeImageReady(imageName, properties, imageIndex);
                    return ImageHandler.tagWithSize(ImageHandler.IN_PROGRESS_IMAGE, this.imageSize);
                }
            }
            else
            {
                // in progress
                return ImageHandler.tagWithSize(ImageHandler.IN_PROGRESS_IMAGE, this.imageSize);
            }
        }
        else
        {
            // finished
            return imageName;
        }
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
    
    private void makeImageReady(String imageName, Properties properties, byte imageIndex)
    {
        this.setImageInProgress(imageName);
        Thread t = new Thread(new GuiImageWizard(properties, imageIndex, this.imageSize, ImageHandler.getTaggedFile(imageName), (failed) -> this.setImageFinished(imageName, failed)), "YDM Image Downloader");
        t.setDaemon(false);
        t.start();
    }
    
    private static String tagWithSize(String imageName, int size)
    {
        return size + "/" + imageName;
    }
    
    public static String addInfoTag(String imageName)
    {
        return ImageHandler.tagWithSize(imageName, ClientProxy.activeInfoImageSize);
    }
    
    public static String addItemTag(String imageName)
    {
        return ImageHandler.tagWithSize(imageName, ClientProxy.activeItemImageSize);
    }
    
    public static String addMainTag(String imageName)
    {
        return ImageHandler.tagWithSize(imageName, ClientProxy.activeMainImageSize);
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
        return new File(ClientProxy.rawCardImagesFolder, imageName + ".jpg");
    }
    
    public static File getTaggedFile(String taggedImageName)
    {
        return ImageHandler.getFile(taggedImageName + ".png");
    }
    
    public static File getFile(String imageName)
    {
        return new File(ClientProxy.cardImagesFolder, imageName);
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
            if(!card.isHardcoded() && !ImageHandler.getTaggedFile(card.getItemImageName()).exists())
            {
                list.add(card);
            }
        }
        return list;
    }
    
    public static void downloadCardImages(List<Card> list)
    {
        Thread t = new Thread(new ItemImagesWizard(list, list.size()), "YDM Item Image Downloader");
        t.setDaemon(false);
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
        if(!ClientProxy.keepCachedImages)
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
                // should never be true, but lets make sure
                if(card.isHardcoded())
                {
                    continue;
                }
                
                if(!ClientProxy.getMinecraft().isRunning())
                {
                    return;
                }
                
                YDM.log("Fetching image of: " + ++j + "/" + this.size + ": " + card.getProperties().getName() + " (Variant " + card.getImageIndex() + ")");
                
                status = ImageHandler.imagePipeline(card.getImageName(), card.getItemImageURL(), ImageHandler.getTaggedFile(card.getItemImageName()), ClientProxy.activeItemImageSize, (failed) ->
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
                else if(status % 2 == 1) // this means that the image needed to be downloaded
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
