package de.cas_ual_ty.ydm.duel.screen.animation;

import java.util.Queue;

import com.mojang.blaze3d.matrix.MatrixStack;

public class QueueAnimation extends Animation
{
    public final Queue<Animation> animations;
    
    private Runnable replacementOnStart;
    private Runnable replacementOnEnd;
    
    public QueueAnimation(Queue<Animation> animations)
    {
        this.animations = animations;
        
        this.maxTickTime = 0;
        
        for(Animation a : this.animations)
        {
            this.maxTickTime += a.maxTickTime;
        }
        
        this.onStart = () ->
        {
            throw new RuntimeException();
        };
        this.onEnd = () ->
        {
            throw new RuntimeException();
        };
    }
    
    public QueueAnimation setOnStartAlt(Runnable onStart)
    {
        this.replacementOnStart = onStart;
        return this;
    }
    
    public QueueAnimation setOnEndAlt(Runnable onEnd)
    {
        this.replacementOnEnd = onEnd;
        return this;
    }
    
    @Override
    public Animation setOnStart(Runnable onStart)
    {
        throw new RuntimeException();
    }
    
    @Override
    public Animation setOnEnd(Runnable onEnd)
    {
        throw new RuntimeException();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        if(!this.animations.isEmpty())
        {
            Animation a = this.animations.peek();
            a.render(ms, mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public void tick()
    {
        if(this.ended())
        {
            return;
        }
        
        if(this.tickTime == 0 && this.replacementOnStart != null)
        {
            this.replacementOnStart.run();
        }
        
        ++this.tickTime;
        
        if(!this.animations.isEmpty())
        {
            Animation a = this.animations.peek();
            a.tick();
            
            if(a.ended())
            {
                this.animations.poll();
            }
        }
        
        if(this.tickTime == this.maxTickTime && this.replacementOnEnd != null)
        {
            this.replacementOnEnd.run();
        }
    }
    
    @Override
    public boolean worksInParallel()
    {
        return false;
    }
}
