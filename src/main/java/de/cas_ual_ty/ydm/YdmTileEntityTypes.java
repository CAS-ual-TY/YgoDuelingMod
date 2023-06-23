package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duel.block.DuelTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class YdmTileEntityTypes
{
    private static final DeferredRegister<BlockEntityType<?>> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, YDM.MOD_ID);
    public static final RegistryObject<BlockEntityType<DuelTileEntity>> DUEL = DEFERRED_REGISTER.register("duel", () -> BlockEntityType.Builder.of((pos, state) -> new DuelTileEntity(YdmTileEntityTypes.DUEL.get(), pos, state), YdmBlocks.DUEL_PLAYMAT.get(), YdmBlocks.DUEL_TABLE.get()).build(null));
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
}