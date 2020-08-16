package de.cas_ual_ty.ydm.deckbox;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;

public class SimpleDeckProvider extends DeckProvider
{
    public Supplier<IDeckHolder> supplier;
    
    public SimpleDeckProvider(Supplier<IDeckHolder> supplier)
    {
        this.supplier = supplier;
    }
    
    @Override
    public IDeckHolder provideDeck(PlayerEntity player)
    {
        return this.supplier.get();
    }
}
