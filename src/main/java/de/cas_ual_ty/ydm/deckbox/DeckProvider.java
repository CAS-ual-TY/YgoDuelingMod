package de.cas_ual_ty.ydm.deckbox;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class DeckProvider extends ForgeRegistryEntry<DeckProvider>
{
    @Nullable
    public abstract DeckHolder provideDeck(PlayerEntity player);
    
    @Nullable
    public ResourceLocation getShownIcon(PlayerEntity player)
    {
        return null;
    }
}
