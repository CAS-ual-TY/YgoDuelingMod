package de.cas_ual_ty.ydm.clientutil;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class YdmResourcePackFinder implements RepositorySource
{
    public YdmResourcePackFinder()
    {
    }
    
    @Override
    public void loadPacks(Consumer<Pack> infoConsumer, Pack.PackConstructor infoFactory)
    {
        infoConsumer.accept(Pack.create(YDM.MOD_ID, true, makePackSupplier(), infoFactory, Pack.Position.TOP, PackSource.DEFAULT));
    }
    
    private Supplier<PackResources> makePackSupplier()
    {
        return () -> new YdmCardResourcePack();
    }
}