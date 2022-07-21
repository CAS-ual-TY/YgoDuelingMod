package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.io.FileFilter;

public interface FileFilterSuffix extends FileFilter
{
    String getRequiredSuffix();
    
    @Override
    default boolean accept(File f)
    {
        return f.isFile() && f.getName().toLowerCase().endsWith(getRequiredSuffix());
    }
}
