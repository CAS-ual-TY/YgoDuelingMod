package de.cas_ual_ty.ydm.util;

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

import javax.imageio.ImageIO;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.Card;

public class ImageHandler
{
    public static final String IN_PROGRESS_IMAGE = "blanc_card";
    public static final String FAILED_IMAGE = "failed_card";
    
    private static DNCList<String, String> FINAL_IMAGE_READY_LIST = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
    private static List<String> IN_PROGRESS = new LinkedList<>();
    private static List<String> FAILED = new LinkedList<>();
    
    public static String getInfoReplacementImage(Card card)
    {
        String imageName = card.getImageName();
        
        int index = ImageHandler.FINAL_IMAGE_READY_LIST.getIndex(imageName);
        
        if(index == -1)
        {
            if(!ImageHandler.isInProgress(imageName))
            {
                if(ImageHandler.getInfoFile(imageName).exists())
                {
                    ImageHandler.setFinished(imageName, false);
                    return imageName;
                }
                else if(ImageHandler.isFailed(imageName))
                {
                    return ImageHandler.FAILED_IMAGE;
                }
                else
                {
                    ImageHandler.makeImageReady(card, YDM.activeInfoImageSize);
                    return ImageHandler.IN_PROGRESS_IMAGE;
                }
            }
            else
            {
                return ImageHandler.IN_PROGRESS_IMAGE;
            }
        }
        else
        {
            return imageName;
        }
    }
    
    public static boolean isImageReady(String imageName)
    {
        return ImageHandler.FINAL_IMAGE_READY_LIST.contains(imageName);
    }
    
    private static boolean isInProgress(String imageName)
    {
        return ImageHandler.IN_PROGRESS.contains(imageName);
    }
    
    private static boolean isFailed(String imageName)
    {
        return ImageHandler.FAILED.contains(imageName);
    }
    
    private static void setInProgress(String imageName)
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
    
    private static void makeImageReady(Card card, int size)
    {
        String imageName = card.getImageName();
        String imageUrl = card.getImageURL();
        
        ImageHandler.setInProgress(imageName);
        
        Thread t = new Thread(new ImageWizard(imageName, imageUrl, size), "YDM Image Downloader");
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
        g.drawImage(img, 1 + (size - img.getWidth()) / 2, 1 + (size - img.getHeight()) / 2, null);
        g.dispose();
        
        ImageIO.write(newImg, "PNG", converted);
        
        in.close();
    }
    
    private static File getRawFile(String imageName)
    {
        return new File(YDM.rawImagesFolder, imageName + ".jpg");
    }
    
    public static File getInfoFile(String imageName)
    {
        return new File(YDM.cardInfoImagesFolder, imageName + ".png");
    }
    
    public static File getItemFileNoSuffix(String imageName)
    {
        return new File(YDM.cardItemImagesFolder, imageName);
    }
    
    private static class ImageWizard implements Runnable
    {
        private final String imageName;
        private final String imageUrl;
        private final int size;
        
        public ImageWizard(String imageName, String imageUrl, int size)
        {
            this.imageName = imageName;
            this.imageUrl = imageUrl;
            this.size = size;
        }
        
        @Override
        public void run()
        {
            File raw = ImageHandler.getRawFile(this.imageName);
            
            if(!raw.exists())
            {
                try
                {
                    ImageHandler.downloadRawImage(this.imageUrl, raw);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    
                    ImageHandler.setFinished(this.imageName, true);
                    
                    // Without the raw image we cant do anything anyways
                    return;
                }
            }
            
            File converted = ImageHandler.getInfoFile(this.imageName);
            boolean failed = false;
            
            if(!converted.exists())
            {
                try
                {
                    ImageHandler.convertImage(converted, raw, this.size);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    failed = true;
                }
            }
            
            // Delete cache if requested
            if(!YDM.keepCachedImages)
            {
                raw.delete();
            }
            
            ImageHandler.setFinished(this.imageName, failed);
        }
    }
}
