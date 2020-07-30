package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.cas_ual_ty.ydm.Database;

public class YdmIOUtil
{
    public static final FileFilterSuffix JSON_FILTER = YdmIOUtil.createFileFilter(".json");
    public static final FileFilterSuffix PNG_FILTER = YdmIOUtil.createFileFilter(".png");
    
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static FileFilterSuffix createFileFilter(String requiredSuffix)
    {
        return () -> requiredSuffix;
    }
    
    public static void downloadFile(URL url, File target) throws IOException
    {
        InputStream in = url.openStream();
        Files.copy(in, Paths.get(target.toURI()));
    }
    
    public static void setAgent()
    {
        System.setProperty("http.agent", "Netscape 1.0");
    }
    
    public static void createDirIfNonExistant(File file)
    {
        if(!file.exists())
        {
            file.mkdir();
        }
    }
    
    public static void writeJson(File target, JsonElement json) throws IOException
    {
        if(target.exists())
        {
            target.delete();
        }
        
        target.createNewFile();
        FileWriter fw = new FileWriter(target);
        YdmIOUtil.GSON.toJson(json, fw);
        fw.flush();
        fw.close();
    }
    
    public static boolean doForDeepSearched(File parent, Predicate<File> predicate, Consumer<File> consumer)
    {
        for(File file : parent.listFiles())
        {
            if(predicate.test(file))
            {
                consumer.accept(file);
                return true;
            }
            else if(file.isDirectory() && YdmIOUtil.doForDeepSearched(file, predicate, consumer))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public static void deleteRecursively(File parent)
    {
        if(parent.isDirectory())
        {
            for(File file : parent.listFiles())
            {
                YdmIOUtil.deleteRecursively(file);
            }
        }
        
        parent.delete();
    }
    
    public static JsonElement parseJsonFile(File file) throws JsonIOException, JsonSyntaxException, IOException
    {
        FileReader fr = new FileReader(file);
        JsonElement e = Database.JSON_PARSER.parse(fr);
        fr.close();
        return e;
    }
}
