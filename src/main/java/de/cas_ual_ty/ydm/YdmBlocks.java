package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duel.DuelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmBlocks
{
    public static final DuelBlock DUEL_PLAYMAT = null;
    public static final DuelBlock DUEL_TABLE = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new DuelBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL), Block.makeCuboidShape(2D, 0, 2D, 14D, 1D, 14D)).setRegistryName(YDM.MOD_ID, "duel_playmat"));
        registry.register(new DuelBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL), Block.makeCuboidShape(1D, 0, 1D, 15D, 15.5D, 15D)).setRegistryName(YDM.MOD_ID, "duel_table"));
    }
}