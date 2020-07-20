package de.cas_ual_ty.ydm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
                j = Database.parseJsonFile(cardFile);
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
            Database.CARDS_LIST.add(new Card(properties));
        }
        Database.CARDS_LIST.sort();
    }
    
    public static JsonObject parseJsonFile(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException
    {
        return Database.JSON_PARSER.parse(new FileReader(file)).getAsJsonObject();
    }
}