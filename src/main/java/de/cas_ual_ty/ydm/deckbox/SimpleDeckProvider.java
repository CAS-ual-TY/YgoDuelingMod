package de.cas_ual_ty.ydm.deckbox;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class SimpleDeckProvider extends DeckProvider
{
    public Supplier<DeckHolder> supplier;
    public ResourceLocation icon;
    
    public SimpleDeckProvider(Supplier<DeckHolder> supplier, ResourceLocation icon)
    {
        this.supplier = supplier;
        this.icon = icon;
    }
    
    public SimpleDeckProvider(Supplier<DeckHolder> supplier)
    {
        this(supplier, null);
    }
    
    @Override
    public DeckHolder provideDeck(PlayerEntity player)
    {
        return this.supplier.get();
    }
    
    @Override
    public ResourceLocation getShownIcon(PlayerEntity player)
    {
        return this.icon;
    }
}
