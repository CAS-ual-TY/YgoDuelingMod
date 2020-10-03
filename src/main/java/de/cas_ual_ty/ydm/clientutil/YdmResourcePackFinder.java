package de.cas_ual_ty.ydm.clientutil;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;

public class YdmResourcePackFinder implements IPackFinder
{
    public YdmResourcePackFinder()
    {
    }
    
    @Override
    public void findPacks(Consumer<ResourcePackInfo> infoConsumer, IFactory infoFactory)
    {
        infoConsumer.accept(ResourcePackInfo.createResourcePack(YDM.MOD_ID, true, this.makePackSupplier(), infoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.PLAIN));
    }
    
    private Supplier<IResourcePack> makePackSupplier()
    {
        return () -> new YdmResourcePack();
    }
}