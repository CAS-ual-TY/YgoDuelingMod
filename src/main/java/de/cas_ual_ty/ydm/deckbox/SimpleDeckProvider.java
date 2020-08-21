package de.cas_ual_ty.ydm.deckbox;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SimpleDeckProvider extends DeckProvider
{
    public Supplier<DeckHolder> supplier;
    public ItemStack item;
    
    public SimpleDeckProvider(Supplier<DeckHolder> supplier, ItemStack item)
    {
        this.supplier = supplier;
        this.item = item;
    }
    
    public SimpleDeckProvider(Supplier<DeckHolder> supplier)
    {
        this(supplier, ItemStack.EMPTY);
    }
    
    @Override
    public DeckHolder provideDeck(PlayerEntity player)
    {
        return this.supplier.get();
    }
    
    @Override
    public ItemStack getShownItem(PlayerEntity player)
    {
        return this.item;
    }
}
