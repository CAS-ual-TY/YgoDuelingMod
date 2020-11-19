package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class TextAnimation extends Animation
{
    public ITextComponent message;
    public float centerPosX;
    public float centerPosY;
    
    public TextAnimation(ITextComponent message, float centerPosX, float centerPosY)
    {
        super(ClientProxy.announcementAnimationLength);
        
        this.message = message;
        this.centerPosX = centerPosX;
        this.centerPosY = centerPosY;
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        FontRenderer f = ClientProxy.getMinecraft().fontRenderer;
        
        double relativeTickTime = (float)(this.tickTime + partialTicks) / this.maxTickTime;
        
        // [0, 1/2pi]
        double cosTime1 = 0.5D * Math.PI * relativeTickTime;
        // [0, 1]
        float alpha = (float)(Math.cos(cosTime1));
        
        ms.push();
        
        ms.translate(this.centerPosX, this.centerPosY - f.FONT_HEIGHT / 2, 0);
        
        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        int j = 16777215; //See TextWidget
        AbstractGui.drawCenteredString(ms, f, this.message, 0, 0, j | MathHelper.ceil(alpha * 255.0F) << 24);
        
        RenderSystem.disableBlend();
        ScreenUtil.white();
        
        ms.pop();
    }
}
