package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.IRenderable;

public abstract class Animation implements IRenderable
{
    public Runnable onStart;
    public Runnable onEnd;
    
    public int tickTime;
    public int maxTickTime;
    
    public Animation()
    {
        this.tickTime = 0;
        this.maxTickTime = 16;
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
        if(this.ended())
        {
            return;
        }
        
        if(this.tickTime == 0 && this.onStart != null)
        {
            this.onStart.run();
        }
        
        ++this.tickTime;
        
        if(this.tickTime == this.maxTickTime && this.onEnd != null)
        {
            this.onEnd.run();
        }
    }
    
    public boolean ended()
    {
        return this.tickTime >= this.maxTickTime;
    }
    
    @Override
    public abstract void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks);
    
    /**
     * @return true if this animation works in parallel to other animations
     * @see ParallelListAnimation
     */
    public boolean worksInParallel()
    {
        return true;
    }
}