package de.cas_ual_ty.ydm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.Files;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;

public class Database
{
    public static final DNCList<Long, Properties> PROPERTIES_LIST = new DNCList<>((p) -> p.getId(), Long::compare);
    public static final DNCList<String, Card> CARDS_LIST = new DNCList<>((c) -> c.getSetId(), (s1, s2) -> s1.compareTo(s2));
    
    public static final JsonParser JSON_PARSER = new JsonParser();
    
    public static void readFiles()
    {
        File mainFolder = YDM.mainFolder;
        
        if(!mainFolder.exists())
        {
            return;
        }
        
        File cardsFolder = YDM.cardsFolder;
        
        if(!cardsFolder.exists())
        {
            return;
        }
        
        File setsFolder = YDM.setsFolder;
        
        if(!setsFolder.exists())
        {
            return;
        }
        
        Database.readCards(cardsFolder);
        Database.readSets(setsFolder);
    }
    
    public static void downloadDatabase() throws IOException
    {
        YDM.log("Downloading cards database...");
        
        URL url = new URL(YDM.dbSourceUrl);
        
        // archive containing the files
        File zip = new File("ydm_db_temp.zip");
        if(zip.exists())
        {
            zip.delete();
        }
        
        // archive to inpack to
        File temp = new File("ydm_db_temp");
        if(temp.exists())
        {
            temp.delete();
        }
        temp.mkdir();
        
        // download the zipped db
        YdmIOUtil.downloadFile(url, zip);
        
        // --- zip unpack ---
        
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zip));
        ZipEntry entry = zipIn.getNextEntry();
        
        byte[] buffer = new byte[1024];
        
        File currentFile;
        FileOutputStream zipOut;
        int length;
        
        while(entry != null)
        {
            currentFile = new File(temp, entry.getName());
            
            if(entry.isDirectory())
            {
                currentFile.mkdir();
            }
            else
            {
                zipOut = new FileOutputStream(currentFile);
                
                while((length = zipIn.read(buffer)) > 0)
                {
                    zipOut.write(buffer, 0, length);
                }
                
                zipOut.close();
            }
            
            entry = zipIn.getNextEntry();
        }
        
        zipIn.closeEntry();
        zipIn.close();
        
        zip.delete();
        
        // --- zip unpack end ---
        
        // now move the file out
        YdmIOUtil.doForDeepSearched(temp, (file) -> file.getName().equals(YDM.mainFolder.getName()), (file) ->
        {
            try
            {
                Files.move(file, YDM.mainFolder);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        
        // now delete temp folder
        YdmIOUtil.deleteRecursively(temp);
        
        YDM.log("Finished downloading cards database!");
    }
    
    private static void readCards(File cardsFolder)
    {
        YDM.log("Reading card files from: " + cardsFolder.getAbsolutePath());
        
        File[] cardsFiles = cardsFolder.listFiles(YdmIOUtil.JSON_FILTER);
        Database.PROPERTIES_LIST.ensureExtraCapacity(cardsFiles.length);
        
        JsonObject j;
        Properties p;
        
        for(File cardFile : cardsFiles)
        {
            try
            {
                j = YdmIOUtil.parseJsonFile(cardFile);
                p = YdmUtil.buildProperties(j);
                Database.PROPERTIES_LIST.add(p);
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
            }
            catch (JsonIOException | FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        
        Database.PROPERTIES_LIST.sort();
    }
    
    private static void readSets(File setsFolder)
    {
        YDM.log("Reading set files from: " + setsFolder.getAbsolutePath());
        
        //TODO
        
        Database.CARDS_LIST.ensureExtraCapacity(Database.PROPERTIES_LIST.size());
        for(Properties properties : Database.PROPERTIES_LIST)
        {
            for(byte imageIndex = 0; imageIndex < properties.images.length; ++imageIndex)
            {
                Database.CARDS_LIST.add(new Card(properties, imageIndex));
            }
        }
        Database.CARDS_LIST.sort();
    }
}
