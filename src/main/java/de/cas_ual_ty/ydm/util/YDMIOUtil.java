package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class YdmIOUtil
{
    public static final FileFilterSuffix JSON_FILTER = YdmIOUtil.createFileFilter(".json");
    public static final FileFilterSuffix PNG_FILTER = YdmIOUtil.createFileFilter(".png");
    
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
}
