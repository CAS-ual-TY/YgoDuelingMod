package de.cas_ual_ty.ydm.deckbox;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class DeckProvider extends ForgeRegistryEntry<DeckProvider>
{
    @Nullable
    public abstract IDeckHolder provideDeck(PlayerEntity player);
}
