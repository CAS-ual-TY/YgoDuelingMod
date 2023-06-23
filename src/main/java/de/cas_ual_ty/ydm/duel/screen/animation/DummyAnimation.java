package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.vertex.PoseStack;

public class DummyAnimation extends Animation
{
    public DummyAnimation()
    {
        super(1);
    }
    
    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
    }
}
