package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duel.dueldisk.DuelEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmEntityTypes
{
    public static final EntityType<?> DUEL = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<EntityType<?>> event)
    {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        registry.register(EntityType.Builder.of(DuelEntity::new, EntityClassification.MISC).noSave().setShouldReceiveVelocityUpdates(false).sized(0, 0).fireImmune().build(null).setRegistryName(YDM.MOD_ID, "duel"));
    }
}