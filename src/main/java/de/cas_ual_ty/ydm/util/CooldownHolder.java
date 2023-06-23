package de.cas_ual_ty.ydm.util;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.nbt.CompoundTag;

public class CooldownHolder implements ICooldownHolder
{
    private int cooldown;
    
    public CooldownHolder()
    {
        cooldown = 0;
    }
    
    @Override
    public void tick()
    {
        if(cooldown > 0)
        {
            cooldown--;
        }
    }
    
    @Override
    public boolean isOffCooldown()
    {
        return cooldown <= 0;
    }
    
    @Override
    public void setCooldown(int cooldown)
    {
        this.cooldown = cooldown;
    }
    
    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("cooldown", cooldown);
        nbt.putLong("time", System.currentTimeMillis());
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        cooldown = nbt.getInt("cooldown");
        
        long lastTime = nbt.getLong("time");
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastTime;
        
        if(!YDM.commonConfig.cooldownOnlyWhileOnServer.get())
        {
            cooldown = Math.max(0, cooldown - (int) (deltaTime / 1000L));
        }
    }
}
