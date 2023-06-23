package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;

public abstract class Animation implements Widget
{
    public Runnable onStart;
    public Runnable onEnd;
    
    public int tickTime;
    public int maxTickTime;
    
    public Animation(int maxTickTime)
    {
        tickTime = 0;
        this.maxTickTime = maxTickTime;
    }
    
    public Animation setOnStart(Runnable onStart)
    {
        this.onStart = onStart;
        return this;
    }
    
    public Animation setOnEnd(Runnable onEnd)
    {
        this.onEnd = onEnd;
        return this;
    }
    
    public void tick()
    {
        if(ended())
        {
            return;
        }
        
        if(tickTime == 0 && onStart != null)
        {
            onStart.run();
        }
        
        ++tickTime;
        
        if(tickTime == maxTickTime && onEnd != null)
        {
            onEnd.run();
        }
    }
    
    public boolean ended()
    {
        return tickTime >= maxTickTime;
    }
    
    @Override
    public abstract void render(PoseStack ms, int mouseX, int mouseY, float partialTicks);
    
    /**
     * @return true if this animation works in parallel to other animations
     * @see ParallelListAnimation
     */
    public boolean worksInParallel()
    {
        return true;
    }
}