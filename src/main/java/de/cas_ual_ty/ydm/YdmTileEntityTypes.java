package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.playmat.PlaymatTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmTileEntityTypes
{
    public static final TileEntityType<?> PLAYMAT = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<TileEntityType<?>> event)
    {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.create(() -> new PlaymatTileEntity(YdmTileEntityTypes.PLAYMAT), YdmBlocks.PLAYMAT).build(null).setRegistryName(YDM.MOD_ID, "playmat"));
    }
}