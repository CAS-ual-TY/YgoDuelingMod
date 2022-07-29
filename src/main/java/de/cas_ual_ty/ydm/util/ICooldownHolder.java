package de.cas_ual_ty.ydm.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICooldownHolder extends INBTSerializable<CompoundNBT>
{
    void tick();
    
    boolean isOffCooldown();
    
    void setCooldown(int cooldown);
}
