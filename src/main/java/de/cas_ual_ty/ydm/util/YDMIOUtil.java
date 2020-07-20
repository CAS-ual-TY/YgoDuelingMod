package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
    
    public static void writeJson(File target, JsonObject json) throws IOException
    {
        if(target.exists())
        {
            target.delete();
        }
        
        target.createNewFile();
        FileWriter fw = new FileWriter(target);
        YdmIOUtil.GSON.toJson(json, fw);
        fw.flush();
    }
}
