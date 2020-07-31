package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duel.PlaymatBlock;
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
    public static final PlaymatBlock PLAYMAT = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new PlaymatBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)).setRegistryName(YDM.MOD_ID, "playmat"));
    }
}