package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.util.ResourceLocation;

public class RemoveTokenAnimation extends Animation
{
    public float centerPosX;
    public float centerPosY;
    public int size;
    public int endSize;
    
    public RemoveTokenAnimation(float centerPosX, float centerPosY, int size, int endSize)
    {
        super(ClientProxy.specialAnimationLength);
        
        this.centerPosX = centerPosX;
        this.centerPosY = centerPosY;
        this.size = size;
        this.endSize = endSize;
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        double relativeTickTime = (float)(this.tickTime + partialTicks) / this.maxTickTime;
        
        // [0, 1/2pi]
        double cosTime1 = 0.5D * Math.PI * relativeTickTime;
        // [0, 1]
        float alpha = (float)(Math.cos(cosTime1));
        
        float size = (float)relativeTickTime * (this.endSize - this.size) + this.size;
        float halfSize = 0.5F * size;
        
        ms.push();
        
        ms.translate(this.centerPosX, this.centerPosY, 0);
        
        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        
        ClientProxy.getMinecraft().textureManager.bindTexture(this.getTexture());
        YdmBlitUtil.fullBlit(ms, -halfSize, -halfSize, size, size);
        
        RenderSystem.disableBlend();
        
        ms.pop();
    }
    
    public ResourceLocation getTexture()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/gui/action_animations/remove_token.png");
    }
}
