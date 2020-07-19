package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.util.Map;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;

public class YDMResourcePackFinder implements IPackFinder
{
    private final File folder;
    private FileFilterSuffix filter;
    
    public YDMResourcePackFinder(File folderIn, FileFilterSuffix filter)
    {
        this.folder = folderIn;
        this.filter = filter;
    }
    
    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory)
    {
        YDM.debug("addPackInfosToMap: " + nameToPackMap);
        //        String s = "file/" + file1.getName();
        String s = YDM.MOD_ID;
        T t = ResourcePackInfo.createResourcePack(s, true, this.makePackSupplier(), packInfoFactory, ResourcePackInfo.Priority.TOP);
        nameToPackMap.put(s, t);
    }
    
    private Supplier<IResourcePack> makePackSupplier()
    {
        return () -> new YDMResourcePack(this.folder, this.filter);
    }
}