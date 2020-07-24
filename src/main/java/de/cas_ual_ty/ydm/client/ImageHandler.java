package de.cas_ual_ty.ydm.client;

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
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;

public class ImageHandler
{
    public static final String IN_PROGRESS_IMAGE = "card_back";
    public static final String FAILED_IMAGE = "blanc_card";
    
    public static final String INFO_SUFFIX = "_info";
    public static final String ITEM_SUFFIX = "_item";
    
    private static DNCList<String, String> FINAL_IMAGE_READY_LIST = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
    private static List<String> IN_PROGRESS = new LinkedList<>();
    private static List<String> FAILED = new LinkedList<>();
    
    public static String cutSuffix(String imageName)
    {
        if(ImageHandler.hasInfoSuffix(imageName) || ImageHandler.hasItemSuffix(imageName))
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
    
    public static boolean hasInfoSuffix(String imageName)
    {
        return imageName.endsWith(ImageHandler.INFO_SUFFIX);
    }
    
    public static boolean hasItemSuffix(String imageName)
    {
        return imageName.endsWith(ImageHandler.ITEM_SUFFIX);
    }
    
    public static String getInfoReplacementImage(Card card)
    {
        String imageName = card.getInfoImageName();
        
        int index = ImageHandler.FINAL_IMAGE_READY_LIST.getIndex(imageName);
        
        if(index == -1)
        {
            if(!ImageHandler.isInfoImageInProgress(imageName))
            {
                // not finished, not in progress
                
                if(ImageHandler.getInfoFile(imageName).exists())
                {
                    // image exists, so set ready and return
                    ImageHandler.setFinished(imageName, false);
                    return imageName;
                }
                else if(ImageHandler.isInfoImageFailed(imageName))
                {
                    // image does not exist, check if failed already and return replacement
                    return ImageHandler.FAILED_IMAGE + "_" + YDM.activeInfoImageSize;
                }
                else
                {
                    // image does not exist and has not been tried, so make it ready and return replacement
                    ImageHandler.makeInfoImageReady(card);
                    return ImageHandler.IN_PROGRESS_IMAGE + "_" + YDM.activeInfoImageSize;
                }
            }
            else
            {
                // in progress
                return ImageHandler.IN_PROGRESS_IMAGE + "_" + YDM.activeInfoImageSize;
            }
        }
        else
        {
            // finished
            return imageName;
        }
    }
    
    public static boolean isInfoImageReady(String imageName)
    {
        return ImageHandler.FINAL_IMAGE_READY_LIST.contains(imageName);
    }
    
    private static boolean isInfoImageInProgress(String imageName)
    {
        return ImageHandler.IN_PROGRESS.contains(imageName);
    }
    
    private static boolean isInfoImageFailed(String imageName)
    {
        return ImageHandler.FAILED.contains(imageName);
    }
    
    private static void setInfoImageInProgress(String imageName)
    {
        synchronized(ImageHandler.IN_PROGRESS)
        {
            ImageHandler.IN_PROGRESS.add(imageName);
        }
    }
    
    private static void setFinished(String imageName, boolean failed)
    {
        synchronized(ImageHandler.IN_PROGRESS)
        {
            ImageHandler.IN_PROGRESS.remove(imageName);
        }
        
        if(!failed)
        {
            synchronized(ImageHandler.FINAL_IMAGE_READY_LIST)
            {
                ImageHandler.FINAL_IMAGE_READY_LIST.addKeepSorted(imageName);
            }
        }
        else
        {
            synchronized(ImageHandler.FAILED)
            {
                ImageHandler.FAILED.add(imageName);
            }
        }
    }
    
    private static void makeInfoImageReady(Card card)
    {
        ImageHandler.setInfoImageInProgress(card.getInfoImageName());
        Thread t = new Thread(new InfoImageWizard(card), "YDM Image Downloader");
        t.start();
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
    
    private static File getRawFile(String imageName)
    {
        return new File(YDM.rawImagesFolder, imageName + ".jpg");
    }
    
    public static File getFileBySuffix(String imageName)
    {
        if(ImageHandler.hasItemSuffix(imageName))
        {
            return new File(YDM.cardItemImagesFolder, ImageHandler.cutSuffix(imageName) + ".png");
        }
        else
        {
            // need to return something, so by default we pick the info images
            return new File(YDM.cardInfoImagesFolder, ImageHandler.cutSuffix(imageName) + ".png");
        }
    }
    
    public static File getInfoFile(String imageName)
    {
        return new File(YDM.cardInfoImagesFolder, ImageHandler.cutSuffix(imageName) + ".png");
    }
    
    public static File getItemFile(String imageName)
    {
        return new File(YDM.cardItemImagesFolder, ImageHandler.cutSuffix(imageName) + ".png");
    }
    
    public static boolean areAllItemImagesReady()
    {
        return ImageHandler.getMissingItemImages().isEmpty();
    }
    
    public static List<Card> getMissingItemImages()
    {
        List<Card> list = new LinkedList<>();
        for(Card card : Database.CARDS_LIST)
        {
            if(!ImageHandler.getItemFile(card.getDirectImageName()).exists())
            {
                list.add(card);
            }
        }
        return list;
    }
    
    public static void downloadAllCardImages()
    {
        Thread t = new Thread(new ItemImagesWizard(Database.CARDS_LIST, Database.CARDS_LIST.size()));
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
    
    private static class InfoImageWizard implements Runnable
    {
        private final Card card;
        
        public InfoImageWizard(Card card)
        {
            this.card = card;
        }
        
        @Override
        public void run()
        {
            ImageHandler.imagePipeline(this.card.getDirectImageName(), this.card.getImageURL(), ImageHandler.getInfoFile(this.card.getDirectImageName()), YDM.activeInfoImageSize, (failed) -> ImageHandler.setFinished(InfoImageWizard.this.card.getInfoImageName(), failed));
        }
    }
    
    private static class ItemImagesWizard implements Runnable
    {
        private Iterable<Card> list;
        private int size;
        
        public ItemImagesWizard(Iterable<Card> list, int size)
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
                
                status = ImageHandler.imagePipeline(card.getDirectImageName(), card.getImageURL(), ImageHandler.getItemFile(card.getDirectImageName()), YDM.activeItemImageSize, (failed) ->
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
