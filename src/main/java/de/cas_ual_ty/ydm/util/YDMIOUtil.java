package de.cas_ual_ty.ydm.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class YDMIOUtil
{
    public static final FileFilterSuffix JSON_FILTER = YDMIOUtil.createFileFilter(".json");
    public static final FileFilterSuffix PNG_FILTER = YDMIOUtil.createFileFilter(".png");

    public static FileFilterSuffix createFileFilter(String requiredSuffix)
    {
        return () -> requiredSuffix;
    }

    public static void downloadFile(URL url, String fileName) throws IOException
    {
        InputStream in = url.openStream();
        Files.copy(in, Paths.get(fileName));
    }

    public static void setAgent()
    {
        System.setProperty("http.agent", "Netscape 1.0");
    }
    
}
