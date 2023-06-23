package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Queue;

public class QueueAnimation extends Animation
{
    public final Queue<Animation> animations;
    
    private Runnable replacementOnStart;
    private Runnable replacementOnEnd;
    
    public QueueAnimation(Queue<Animation> animations)
    {
        super(1);
        
        this.animations = animations;
        
        maxTickTime = 0;
        
        for(Animation a : this.animations)
        {
            maxTickTime += a.maxTickTime;
        }
        
        onStart = () ->
        {
            throw new RuntimeException();
        };
        onEnd = () ->
        {
            throw new RuntimeException();
        };
    }
    
    public QueueAnimation setOnStartAlt(Runnable onStart)
    {
        replacementOnStart = onStart;
        return this;
    }
    
    public QueueAnimation setOnEndAlt(Runnable onEnd)
    {
        replacementOnEnd = onEnd;
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
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        if(!animations.isEmpty())
        {
            Animation a = animations.peek();
            a.render(ms, mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public void tick()
    {
        if(ended())
        {
            return;
        }
        
        if(tickTime == 0 && replacementOnStart != null)
        {
            replacementOnStart.run();
        }
        
        ++tickTime;
        
        if(!animations.isEmpty())
        {
            Animation a = animations.peek();
            a.tick();
            
            if(a.ended())
            {
                animations.poll();
            }
        }
        
        if(tickTime == maxTickTime && replacementOnEnd != null)
        {
            replacementOnEnd.run();
        }
    }
    
    @Override
    public boolean worksInParallel()
    {
        return false;
    }
}
