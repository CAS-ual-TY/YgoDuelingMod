package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.LinkedList;
import java.util.Queue;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duel.screen.animation.Animation;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class AnimationsWidget extends Widget
{
    public Queue<Animation> animations;
    
    public AnimationsWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.animations = new LinkedList<>();
    }
    
    public void addAnimation(Animation animation)
    {
        this.animations.add(animation);
    }
    
    public void forceFinish()
    {
        Animation a;
        while(!this.animations.isEmpty())
        {
            a = this.animations.poll();
            
            while(!a.ended())
            {
                a.tick();
            }
        }
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        if(this.visible)
        {
            for(Animation a : this.animations)
            {
                a.render(ms, mouseX, mouseY, partialTicks);
            }
        }
    }
    
    public void tick()
    {
        if(!this.animations.isEmpty())
        {
            Animation a = this.animations.element();
            
            a.tick();
            
            if(a.ended())
            {
                this.animations.poll();
            }
        }
    }
    
    public void onInit()
    {
        Animation a;
        
        while(this.animations.size() > 0)
        {
            a = this.animations.poll();
            
            while(!a.ended())
            {
                a.tick();
            }
        }
    }
}
