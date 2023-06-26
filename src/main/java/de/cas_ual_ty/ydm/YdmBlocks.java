package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.cardsupply.CardSupplyBlock;
import de.cas_ual_ty.ydm.duel.block.DuelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class YdmBlocks
{
    private static final DeferredRegister<Block> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, YDM.MOD_ID);
    public static final RegistryObject<DuelBlock> DUEL_PLAYMAT = DEFERRED_REGISTER.register("duel_playmat", () -> new DuelBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL), Block.box(2D, 0, 2D, 14D, 1D, 14D)));
    public static final RegistryObject<DuelBlock> DUEL_TABLE = DEFERRED_REGISTER.register("duel_table", () -> new DuelBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL), Shapes.or(
            Block.box(4, 3, 4, 12, 12.5, 12),
            Block.box(1, 0, 1, 15, 3, 15),
            Block.box(0, 13, 0, 16, 15, 16),
            Block.box(1, 12.5, 1, 15, 15.5, 15))));
    public static final RegistryObject<CardSupplyBlock> CARD_SUPPLY = DEFERRED_REGISTER.register("card_supply", () -> new CardSupplyBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL)));
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
}