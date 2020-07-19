package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.properties.Properties;

public class DatabaseReader
{
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
        
        DatabaseReader.readCards(cardsFolder);
        DatabaseReader.readSets(setsFolder);
    }
    
    private static void readCards(File cardsFolder)
    {
        YDM.log("Reading card files from: " + cardsFolder.getAbsolutePath());
        
        File[] cardsFiles = cardsFolder.listFiles();
        Database.initPropertiesList(cardsFiles.length);
        
        JsonObject j;
        Properties p;
        
        for(File cardFile : cardsFiles)
        {
            try
            {
                j = DatabaseReader.parseJsonFile(cardFile);
                p = YDMUtil.buildProperties(j);
                Database.registerProperties(p);
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
        
        Database.sortPropertiesList();
    }
    
    private static void readSets(File setsFolder)
    {
        YDM.log("Reading set files from: " + setsFolder.getAbsolutePath());
        
        //TODO
        
        Database.initCardsList(Database.PROPERTIES_LIST.size());
        for(Properties properties : Database.getPropertiesIterable())
        {
            Database.registerCard(new Card(properties));
        }
        Database.sortCardsList();
    }
    
    public static JsonObject parseJsonFile(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException
    {
        return DatabaseReader.JSON_PARSER.parse(new FileReader(file)).getAsJsonObject();
    }
}
