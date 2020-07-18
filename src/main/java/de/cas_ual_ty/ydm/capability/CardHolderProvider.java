package de.cas_ual_ty.ydm.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CardHolderProvider implements ICapabilitySerializable<INBT>
{
    @CapabilityInject(value = ICardHolder.class)
    public static final Capability<ICardHolder> CAPABILITY_PRINT_HOLDER = null;
    
    private final LazyOptional<ICardHolder> instance;
    
    public CardHolderProvider()
    {
        this.instance = LazyOptional.of(CardHolderProvider.CAPABILITY_PRINT_HOLDER::getDefaultInstance);
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        return cap == CardHolderProvider.CAPABILITY_PRINT_HOLDER ? this.instance.cast() : LazyOptional.empty();
    }
    
    @Override
    public INBT serializeNBT()
    {
        return CardHolderProvider.CAPABILITY_PRINT_HOLDER.getStorage().writeNBT(CardHolderProvider.CAPABILITY_PRINT_HOLDER, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }
    
    @Override
    public void deserializeNBT(INBT nbt)
    {
        CardHolderProvider.CAPABILITY_PRINT_HOLDER.getStorage().readNBT(CardHolderProvider.CAPABILITY_PRINT_HOLDER, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
    
    public Runnable getListener()
    {
        return this.instance::invalidate;
    }
}
