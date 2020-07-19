package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.util.Map;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;

public class YdmResourcePackFinder implements IPackFinder
{
    private final File folder;
    
    public YdmResourcePackFinder(File folderIn)
    {
        this.folder = folderIn;
    }
    
    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory)
    {
        String s = YDM.MOD_ID;
        T t = ResourcePackInfo.createResourcePack(s, true, this.makePackSupplier(), packInfoFactory, ResourcePackInfo.Priority.TOP);
        nameToPackMap.put(s, t);
    }
    
    private Supplier<IResourcePack> makePackSupplier()
    {
        return () -> new YdmResourcePack(this.folder);
    }
}