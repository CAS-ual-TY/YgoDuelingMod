package de.cas_ual_ty.ydm.clientutil;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class YdmResourcePackFinder implements IPackFinder
{
    public YdmResourcePackFinder()
    {
    }
    
    @Override
    public void loadPacks(Consumer<ResourcePackInfo> infoConsumer, IFactory infoFactory)
    {
        infoConsumer.accept(ResourcePackInfo.create(YDM.MOD_ID, true, makePackSupplier(), infoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.DEFAULT));
    }
    
    private Supplier<IResourcePack> makePackSupplier()
    {
        return () -> new YdmCardResourcePack();
    }
}