package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.cardsupply.CardSupplyBlock;
import de.cas_ual_ty.ydm.duel.block.DuelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.shapes.VoxelShapes;
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
    public static final CardSupplyBlock CARD_SUPPLY = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new DuelBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL), Block.makeCuboidShape(2D, 0, 2D, 14D, 1D, 14D)).setRegistryName(YDM.MOD_ID, "duel_playmat"));
        registry.register(new DuelBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL), VoxelShapes.or(
            Block.makeCuboidShape(4, 3, 4, 12, 12.5, 12),
            Block.makeCuboidShape(1, 0, 1, 15, 3, 15),
            Block.makeCuboidShape(0, 13, 0, 16, 15, 16),
            Block.makeCuboidShape(1, 12.5, 1, 15, 15.5, 15))).setRegistryName(YDM.MOD_ID, "duel_table"));
        registry.register(new CardSupplyBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON)).setRegistryName(YDM.MOD_ID, "card_supply"));
    }
}