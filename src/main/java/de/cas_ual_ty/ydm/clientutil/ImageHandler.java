package de.cas_ual_ty.ydm.clientutil;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.rarity.RarityEntry;
import de.cas_ual_ty.ydm.rarity.RarityLayer;
import de.cas_ual_ty.ydm.set.CardSet;
import de.cas_ual_ty.ydm.task.Task;
import de.cas_ual_ty.ydm.task.TaskPriority;
import de.cas_ual_ty.ydm.task.TaskQueue;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
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

public class ImageHandler
{
    private static final String CARD_IN_PROGRESS = "card_loading";
    private static final String CARD_FAILED = "card_failed";
    private static final String SET_IN_PROGRESS = "set_loading";
    private static final String SET_FAILED = "set_failed";
    //    private static final String FAILED_IMAGE = "blanc_card";
    
    public static ImageList RAW_IMAGE_LIST = new ImageList();
    public static ImageList ADJUSTED_IMAGE_LIST = new ImageList();
    public static ImageList RARITY_IMAGE_LIST = new ImageList();
    
    // only for dev workspace!
    // put raw image in the raw images folder
    // make sure all size folders (16, 32, 64... exist)
    @Deprecated // so I get a warning
    public static void createCustomCardImages(Properties card) throws IOException
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
                    new File(parent, ImageHandler.getAdjustedCardImageFile(card.getImageName((byte) 0), size).getName()),
                    new File(parent, ImageHandler.getRawCardImageFile(card.getImageName((byte) 0)).getName()),
                    size);
        }
    }
    
    // only for dev workspace!
    // put raw image in the raw images folder
    // make sure all size folders (16, 32, 64... exist)
    @Deprecated // so I get a warning
    public static void createCustomSleevesImages(CardSleevesType sleeve, String rawType) throws IOException
    {
        YDM.log("creating sleeves card images!");
        
        File parent = new File(ClientProxy.cardImagesFolder, "custom");
        YdmIOUtil.createDirIfNonExistant(parent);
        
        // size 16 to 1024
        int size;
        for(int i = 4; i <= 10; ++i)
        {
            size = YdmUtil.getPow2(i);
            YdmIOUtil.createDirIfNonExistant(new File(parent, "" + size));
            ImageHandler.adjustRawImage(
                    new File(parent, size + "/" + sleeve.getResourceName() + ".png"),
                    new File(parent, "raw/" + sleeve.getResourceName() + "." + rawType),
                    size);
        }
    }
    
    public static void prepareRarityImages(int imageSize)
    {
        for(RarityEntry entry : YdmDatabase.RARITIES_LIST.getList())
        {
            for(RarityLayer l : entry.layers)
            {
                File finished = getRarityFile(imageSize + "/" + l.texture + ".png");
                File raw = getRawRarityImageFile(l.texture);
                
                if(raw.exists() && !finished.exists())
                {
                    try
                    {
                        ImageHandler.adjustRawImage(finished, raw, imageSize);
                    }
                    catch(IOException e)
                    {
                        YDM.log("Error adjusting image of rarity \"" + entry.rarity + "\" and layer image \"" + l.texture + "\"");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static String getReplacementImage(Properties p, byte imageIndex, int imageSize)
    {
        String imageName = p.getImageName(imageIndex);
        String imagePathName = ImageHandler.tagImage(imageName, imageSize);
        
        if(p.getIsHardcoded())
        {
            return imagePathName;
        }
        
        return ImageHandler.getReplacementImage(ADJUSTED_IMAGE_LIST, imageName, imagePathName, p.getImageURL(imageIndex), ImageHandler.CARD_IN_PROGRESS, ImageHandler.CARD_FAILED, imageSize, ImageHandler.getCardImageFile(imagePathName), ImageHandler.getRawCardImageFile(imageName));
    }
    
    public static String getReplacementImage(CardSet s, int imageSize)
    {
        String imageName = s.getImageName();
        String imagePathName = ImageHandler.tagImage(imageName, imageSize);
        
        if(s.getIsHardcoded())
        {
            return imagePathName;
        }
        
        return ImageHandler.getReplacementImage(ADJUSTED_IMAGE_LIST, imageName, imagePathName, s.getImageURL(), ImageHandler.SET_IN_PROGRESS, ImageHandler.SET_FAILED, imageSize, ImageHandler.getSetImageFile(imagePathName), ImageHandler.getRawSetImageFile(imageName));
    }
    
    public static String getReplacementImage(ImageList list, String imageName, String imagePathName, String imageURL, String inProgress, String failed, int imageSize, File adjusted, File raw)
    {
        if(!list.isFinished(imagePathName))
        {
            if(!list.isInProgress(imagePathName))
            {
                // not finished, not in progress
                
                if(adjusted.exists())
                {
                    // image exists, so set ready and return
                    list.setImmediateFinished(imagePathName);
                    return imagePathName;
                }
                else if(list.isFailed(imagePathName))
                {
                    // image does not exist, check if failed already and return replacement
                    return ImageHandler.tagImage(failed, imageSize);
                }
                else
                {
                    // image does not exist and has not been tried, so make it ready and return replacement
                    ImageHandler.makeImageReady(imageName, imageURL, imageSize, adjusted, raw);
                    return ImageHandler.tagImage(inProgress, imageSize);
                }
            }
            else
            {
                // in progress
                return ImageHandler.tagImage(inProgress, imageSize);
            }
        }
        else
        {
            // finished
            return imagePathName;
        }
    }
    
    public static String getInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getReplacementImage(properties, imageIndex, ClientProxy.activeCardInfoImageSize);
    }
    
    public static String getMainReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getReplacementImage(properties, imageIndex, ClientProxy.activeCardMainImageSize);
    }
    
    public static String getInfoReplacementImage(CardSet set)
    {
        return ImageHandler.getReplacementImage(set, ClientProxy.activeSetInfoImageSize);
    }
    
    public static String getRarityMainImage(RarityLayer layer)
    {
        return ImageHandler.tagImage(layer.texture, ClientProxy.activeCardMainImageSize);
    }
    
    public static String getRarityInfoImage(RarityLayer layer)
    {
        return ImageHandler.tagImage(layer.texture, ClientProxy.activeCardInfoImageSize);
    }
    
    @Nullable
    public static Task makeMissingRawTask(String imageName, String imageURL, File raw)
    {
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
        
        Task task = new ClientTask(TaskPriority.IMG_DOWNLOAD, () ->
        {
            try
            {
                long start = System.currentTimeMillis();
                ImageHandler.downloadRawImage(imageURL, raw);
                
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
                    catch(InterruptedException e)
                    {
                        //                        e.printStackTrace();
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                ImageHandler.RAW_IMAGE_LIST.setFailed(imageName);
            }
        })
                .setCancelable(() -> !ImageHandler.RAW_IMAGE_LIST.isInProgress(imageName))
                .setOnCancel(() -> ImageHandler.RAW_IMAGE_LIST.unInProgress(imageName));
        
        return task;
    }
    
    public static Task makeMissingAdjustedTask(String imageName, int imageSize, File raw, File adjusted)
    {
        final String adjustedImageName = ImageHandler.tagImage(imageName, imageSize);
        
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
        
        Task task = new ClientTask(TaskPriority.IMG_ADJUSTMENT, () ->
        {
            try
            {
                ImageHandler.adjustRawImage(adjusted, raw, imageSize);
                ImageHandler.ADJUSTED_IMAGE_LIST.setFinished(adjustedImageName);
            }
            catch(IOException e)
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
    
    public static void makeImageReady(String imageName, String imageURL, int imageSize, File adjusted, File raw)
    {
        Task t = ImageHandler.makeMissingRawTask(imageName, imageURL, raw);
        if(t != null)
        {
            TaskQueue.addTask(t);
        }
        
        t = ImageHandler.makeMissingAdjustedTask(imageName, imageSize, raw, adjusted);
        if(t != null)
        {
            TaskQueue.addTask(t);
        }
    }
    
    public static void downloadRawImage(String imageUrl, File rawImageFile) throws IOException
    {
        URL url;
        try
        {
            url = new URL(imageUrl);
        }
        catch(MalformedURLException e)
        {
            YDM.log("Malformed url: \"" + imageUrl + "\" for raw image file target: " + rawImageFile.getAbsolutePath());
            throw e;
        }
        
        YdmIOUtil.downloadFile(url, rawImageFile);
        
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
            BufferedImage rawImg = ImageIO.read(in);
            
            if(rawImg == null)
            {
                YDM.log("Can not read image: " + raw.getAbsolutePath());
                throw new NullPointerException();
            }
            
            BufferedImage img = new BufferedImage(rawImg.getWidth(), rawImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            img.getGraphics().drawImage(rawImg, 0, 0, null);
            
            int margin = size / 8;
            
            int sizeX = img.getWidth();
            int sizeY = img.getHeight();
            
            double factor = (double) sizeY / sizeX;
            
            // (sizeX / sizeY =) factor = newSizeX / newSizeY
            // <=> newSizeY = newSizeX / factor
            
            int newSizeY = size - margin;
            int newSizeX = (int) Math.round(newSizeY / factor);
            
            double scaleFactorX = (double) newSizeX / sizeX;
            double scaleFactorY = (double) newSizeY / sizeY;
            
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
            
            int x, y, color;
            for(x = 0; x < newImg.getWidth(); ++x)
            {
                for(y = 0; y < newImg.getHeight(); ++y)
                {
                    color = newImg.getRGB(x, y);
                    
                    if((color >> 24) == 0x00)
                    {
                        newImg.setRGB(x, y, 0x00000000);
                    }
                    else
                    {
                        newImg.setRGB(x, y, color | 0xFF000000);
                    }
                    
                }
            }
            
            newImg.flush();
            ImageIO.write(newImg, "PNG", adjusted);
        }
    }
    
    public static File getRawCardImageFile(String imageName)
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
    
    public static File getRawSetImageFile(String imageName)
    {
        File f = new File(ClientProxy.rawSetImagesFolder, imageName + ".png");
        
        // prefer png over jpg
        if(f.exists())
        {
            return f;
        }
        else
        {
            return new File(ClientProxy.rawSetImagesFolder, imageName + ".jpg");
        }
    }
    
    public static File getRawRarityImageFile(String imageName)
    {
        File f = new File(ClientProxy.rawRarityImagesFolder, imageName + ".png");
        
        // prefer png over jpg
        if(f.exists())
        {
            return f;
        }
        else
        {
            return new File(ClientProxy.rawRarityImagesFolder, imageName + ".jpg");
        }
    }
    
    public static File getAdjustedCardImageFile(String imageName, int size)
    {
        return ImageHandler.getCardImageFile(ImageHandler.tagImage(imageName, size));
    }
    
    public static File getAdjustedSetImageFile(String imageName, int size)
    {
        return ImageHandler.getSetImageFile(ImageHandler.tagImage(imageName, size));
    }
    
    public static String tagImage(String imageName, int size)
    {
        return size + "/" + imageName;
    }
    
    public static File getCardImageFile(String imagePathName)
    {
        return ImageHandler.getCardFile(imagePathName + ".png");
    }
    
    public static File getSetImageFile(String imagePathName)
    {
        return ImageHandler.getSetFile(imagePathName + ".png");
    }
    
    public static File getCardFile(String imagePathName)
    {
        return new File(ClientProxy.cardImagesFolder, imagePathName);
    }
    
    public static File getSetFile(String imagePathName)
    {
        return new File(ClientProxy.setImagesFolder, imagePathName);
    }
    
    public static File getRarityFile(String imagePathName)
    {
        return new File(ClientProxy.rarityImagesFolder, imagePathName);
    }
    
    public static List<CardHolder> getMissingItemImages()
    {
        List<CardHolder> list = new LinkedList<>();
        
        YdmDatabase.forAllCardVariants((card, imageIndex) ->
        {
            if(!card.getIsHardcoded() && !ImageHandler.getCardImageFile(card.getItemImageName(imageIndex)).exists())
            {
                list.add(new CardHolder(card, imageIndex, null, null));
            }
        });
        
        return list;
    }
    
    public static List<CardSet> getMissingSetImages()
    {
        List<CardSet> list = new LinkedList<>();
        
        for(CardSet set : YdmDatabase.SETS_LIST)
        {
            if(set.isIndependentAndItem() && !set.getIsHardcoded() && !ImageHandler.getSetImageFile(set.getItemImageName()).exists())
            {
                list.add(set);
            }
        }
        
        return list;
    }
    
    public static void downloadCardImages(List<CardHolder> missingList)
    {
        for(CardHolder card : missingList)
        {
            ImageHandler.makeImageReady(card.getImageName(), card.getImageURL(), ClientProxy.activeCardItemImageSize, ImageHandler.getCardImageFile(card.getItemImageName()), ImageHandler.getRawCardImageFile(card.getImageName()));
        }
    }
    
    public static void downloadSetImages(List<CardSet> missingList)
    {
        for(CardSet set : missingList)
        {
            ImageHandler.makeImageReady(set.getImageName(), set.getImageURL(), ClientProxy.activeSetItemImageSize, ImageHandler.getSetImageFile(set.getItemImageName()), ImageHandler.getRawSetImageFile(set.getImageName()));
        }
    }
    
    public static class ImageList
    {
        private final DNCList<String, String> finishedList;
        private final DNCList<String, String> inProgressList;
        private final DNCList<String, String> failedList;
        
        public ImageList()
        {
            finishedList = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
            inProgressList = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
            failedList = new DNCList<>((s) -> s, (s1, s2) -> s1.compareTo(s2));
        }
        
        public void setInProgress(String imagePathName)
        {
            synchronized(inProgressList)
            {
                inProgressList.addKeepSorted(imagePathName);
            }
        }
        
        public void unInProgress(String imagePathName)
        {
            if(inProgressList.contains(imagePathName))
            {
                synchronized(inProgressList)
                {
                    inProgressList.remove(imagePathName);
                }
            }
        }
        
        public void setImmediateFinished(String imagePathName)
        {
            synchronized(finishedList)
            {
                finishedList.addKeepSorted(imagePathName);
            }
        }
        
        public void setFinished(String imagePathName)
        {
            unInProgress(imagePathName);
            setImmediateFinished(imagePathName);
        }
        
        public void setFailed(String imagePathName)
        {
            unInProgress(imagePathName);
            synchronized(failedList)
            {
                failedList.addKeepSorted(imagePathName);
            }
        }
        
        public boolean isInProgress(String imagePathName)
        {
            return inProgressList.contains(imagePathName);
        }
        
        public boolean isFinished(String imagePathName)
        {
            return finishedList.contains(imagePathName);
        }
        
        public boolean isFailed(String imagePathName)
        {
            return failedList.contains(imagePathName);
        }
    }
}
