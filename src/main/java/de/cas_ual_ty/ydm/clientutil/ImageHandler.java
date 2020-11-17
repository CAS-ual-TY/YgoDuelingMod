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

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.task.Task;
import de.cas_ual_ty.ydm.task.TaskPriority;
import de.cas_ual_ty.ydm.task.TaskQueue;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;

public class ImageHandler
{
    private static final String IN_PROGRESS_IMAGE = "card_back";
    private static final String FAILED_IMAGE = "blanc_card";
    
    public static ImageList RAW_IMAGE_LIST = new ImageList();
    public static ImageList ADJUSTED_IMAGE_LIST = new ImageList();
    
    // only for dev workspace! requires raw image to be manually put in the raw images folder
    @Deprecated // so I get a warning
    public static void createCustomCardImages(Card card) throws IOException
    {
        YDM.log("creating custom card images!");
        
        File parent = new File(ClientProxy.cardImagesFolder, "custom");
        YdmIOUtil.createDirIfNonExistant(parent);
        
        // size 16 to 1024
        int size;
        for(int i = 4; i <= 10; ++i)
        {
            size = YdmUtil.getPow2(i);
            YdmIOUtil.createDirIfNonExistant(new File(parent, "" + size));
            ImageHandler.adjustRawImage(
                ImageHandler.getAdjustedImageFile(card.getImageName(), size),
                ImageHandler.getRawImageFile(card.getImageName()),
                size);
        }
    }
    
    public static String getReplacementImage(Properties properties, byte imageIndex, int imageSize)
    {
        String imageName = ImageHandler.tagImage(properties.getImageName(imageIndex), imageSize);
        
        // if its hardcoded, image should already exist
        if(properties.getIsHardcoded())
        {
            return imageName;
        }
        
        if(!ImageHandler.ADJUSTED_IMAGE_LIST.isFinished(imageName))
        {
            if(!ImageHandler.ADJUSTED_IMAGE_LIST.isInProgress(imageName))
            {
                // not finished, not in progress
                
                if(ImageHandler.getImageFile(imageName).exists())
                {
                    // image exists, so set ready and return
                    ImageHandler.ADJUSTED_IMAGE_LIST.setImmediateFinished(imageName);
                    return imageName;
                }
                else if(ImageHandler.ADJUSTED_IMAGE_LIST.isFailed(imageName))
                {
                    // image does not exist, check if failed already and return replacement
                    return ImageHandler.tagImage(ImageHandler.FAILED_IMAGE, imageSize);
                }
                else
                {
                    // image does not exist and has not been tried, so make it ready and return replacement
                    ImageHandler.makeImageReady(properties, imageIndex, imageSize);
                    return ImageHandler.tagImage(ImageHandler.IN_PROGRESS_IMAGE, imageSize);
                }
            }
            else
            {
                // in progress
                return ImageHandler.tagImage(ImageHandler.IN_PROGRESS_IMAGE, imageSize);
            }
        }
        else
        {
            // finished
            return imageName;
        }
    }
    
    public static String getInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getReplacementImage(properties, imageIndex, ClientProxy.activeInfoImageSize);
    }
    
    public static String getMainReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getReplacementImage(properties, imageIndex, ClientProxy.activeMainImageSize);
    }
    
    @Nullable
    public static Task makeMissingRawTask(Properties p, byte imageIndex)
    {
        final String imageName = p.getImageName(imageIndex);
        File raw = ImageHandler.getRawImageFile(imageName);
        
        if(ImageHandler.RAW_IMAGE_LIST.isFinished(imageName) ||
            ImageHandler.RAW_IMAGE_LIST.isInProgress(imageName) ||
            ImageHandler.RAW_IMAGE_LIST.isFailed(imageName))
        {
            return null;
        }
        
        boolean exists = raw.exists();
        
        if(exists)
        {
            ImageHandler.RAW_IMAGE_LIST.setImmediateFinished(imageName);
            return null;
        }
        
        ImageHandler.RAW_IMAGE_LIST.setInProgress(imageName);
        
        Task task = new Task(TaskPriority.IMG_DOWNLOAD, () ->
        {
            try
            {
                long start = System.currentTimeMillis();
                ImageHandler.downloadRawImage(p.getImageURL(imageIndex), raw);
                
                ImageHandler.RAW_IMAGE_LIST.setFinished(imageName);
                
                long end = System.currentTimeMillis();
                
                long duration = end - start;
                
                // only 20 pictures per 1 second allowed.
                // so each picture dl must take atleast 1000/20 = 50 millis
                if(duration < 50)
                {
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(50 - duration + 1);
                    }
                    catch (InterruptedException e)
                    {
                        //                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                ImageHandler.RAW_IMAGE_LIST.setFailed(imageName);
            }
        })
            .setCancelable(() -> !ImageHandler.RAW_IMAGE_LIST.isInProgress(imageName))
            .setOnCancel(() -> ImageHandler.RAW_IMAGE_LIST.unInProgress(imageName));
        
        return task;
    }
    
    public static Task makeMissingAdjustedTask(Properties p, byte imageIndex, int imageSize)
    {
        final String imageName = p.getImageName(imageIndex);
        final String adjustedImageName = ImageHandler.tagImage(imageName, imageSize);
        File raw = ImageHandler.getRawImageFile(imageName);
        File adjusted = ImageHandler.getImageFile(adjustedImageName);
        
        if(ImageHandler.ADJUSTED_IMAGE_LIST.isFinished(adjustedImageName) ||
            ImageHandler.ADJUSTED_IMAGE_LIST.isInProgress(adjustedImageName) ||
            ImageHandler.ADJUSTED_IMAGE_LIST.isFailed(adjustedImageName))
        {
            return null;
        }
        
        boolean exists = adjusted.exists();
        
        if(exists)
        {
            ImageHandler.ADJUSTED_IMAGE_LIST.setImmediateFinished(adjustedImageName);
            return null;
        }
        
        ImageHandler.ADJUSTED_IMAGE_LIST.setInProgress(adjustedImageName);
        
        Task task = new Task(TaskPriority.IMG_ADJUSTMENT, () ->
        {
            try
            {
                ImageHandler.adjustRawImage(adjusted, raw, imageSize);
                ImageHandler.ADJUSTED_IMAGE_LIST.setFinished(adjustedImageName);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                ImageHandler.ADJUSTED_IMAGE_LIST.setFailed(adjustedImageName);
            }
        })
            .setDependency(() -> ImageHandler.RAW_IMAGE_LIST.isFinished(imageName))
            .setCancelable(() -> ImageHandler.RAW_IMAGE_LIST.isFailed(imageName) ||
                (!ImageHandler.RAW_IMAGE_LIST.isFinished(imageName) && !ImageHandler.RAW_IMAGE_LIST.isInProgress(imageName)))
            .setOnCancel(() -> ImageHandler.ADJUSTED_IMAGE_LIST.unInProgress(adjustedImageName));
        
        return task;
    }
    
    public static void makeImageReady(Properties p, byte imageIndex, int imageSize)
    {
        Task t = ImageHandler.makeMissingRawTask(p, imageIndex);
        if(t != null)
        {
            TaskQueue.addTask(t);
        }
        
        t = ImageHandler.makeMissingAdjustedTask(p, imageIndex, imageSize);
        if(t != null)
        {
            TaskQueue.addTask(t);
        }
    }
    
    public static void downloadRawImage(String imageUrl, File rawImageFile) throws MalformedURLException, IOException
    {
        YdmIOUtil.downloadFile(new URL(imageUrl), rawImageFile);
        
        if(!ClientProxy.keepCachedImages)
        {
            rawImageFile.deleteOnExit();
        }
    }
    
    public static void adjustRawImage(File adjusted, File raw, int size) throws IOException
    {
        // size: target size, maybe make different versions for card info and card item
        
        try(InputStream in = new FileInputStream(raw))
        {
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
            
            ImageIO.write(newImg, "PNG", adjusted);
        }
    }
    
    public static File getRawImageFile(String imageName)
    {
        File f = new File(ClientProxy.rawCardImagesFolder, imageName + ".png");
        
        // prefer png over jpg
        if(f.exists())
        {
            return f;
        }
        else
        {
            return new File(ClientProxy.rawCardImagesFolder, imageName + ".jpg");
        }
    }
    
    public static File getAdjustedImageFile(String imageName, int size)
    {
        return ImageHandler.getImageFile(ImageHandler.tagImage(imageName, size));
    }
    
    public static String tagImage(String imageName, int size)
    {
        return size + "/" + imageName;
    }
    
    public static File getImageFile(String imagePathName)
    {
        return ImageHandler.getFile(imagePathName + ".png");
    }
    
    public static File getFile(String imagePathName)
    {
        return new File(ClientProxy.cardImagesFolder, imagePathName);
    }
    
    public static List<Card> getMissingItemImages()
    {
        List<Card> list = new LinkedList<>();
        
        for(Card card : YdmDatabase.CARDS_LIST)
        {
            if(!card.isHardcoded() && !ImageHandler.getImageFile(card.getItemImageName()).exists())
            {
                list.add(card);
            }
        }
        
        return list;
    }
    
    public static void downloadCardImages(List<Card> missingList)
    {
        for(Card card : missingList)
        {
            ImageHandler.makeImageReady(card.getProperties(), card.getImageIndex(), ClientProxy.activeItemImageSize);
        }
    }
    
    public static class ImageList
    {
        private final DNCList<String, String> finishedList;
        private final DNCList<String, String> inProgressList;
        private final DNCList<String, String> failedList;
        
        public ImageList()
        {
            this.finishedList = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
            this.inProgressList = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
            this.failedList = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
        }
        
        public void setInProgress(String imageName)
        {
            synchronized(this.inProgressList)
            {
                this.inProgressList.addKeepSorted(imageName);
            }
        }
        
        public void unInProgress(String imageName)
        {
            if(this.inProgressList.contains(imageName))
            {
                synchronized(this.inProgressList)
                {
                    this.inProgressList.remove(imageName);
                }
            }
        }
        
        public void setImmediateFinished(String imageName)
        {
            synchronized(this.finishedList)
            {
                this.finishedList.addKeepSorted(imageName);
            }
        }
        
        public void setFinished(String imageName)
        {
            this.unInProgress(imageName);
            this.setImmediateFinished(imageName);
        }
        
        public void setFailed(String imageName)
        {
            this.unInProgress(imageName);
            synchronized(this.failedList)
            {
                this.failedList.addKeepSorted(imageName);
            }
        }
        
        public boolean isInProgress(String imageName)
        {
            return this.inProgressList.contains(imageName);
        }
        
        public boolean isFinished(String imageName)
        {
            return this.finishedList.contains(imageName);
        }
        
        public boolean isFailed(String imageName)
        {
            return this.failedList.contains(imageName);
        }
    }
}
