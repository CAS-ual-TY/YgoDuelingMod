package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;


public class TextAnimation extends Animation
{
    public Component message;
    public float centerPosX;
    public float centerPosY;
    
    public TextAnimation(Component message, float centerPosX, float centerPosY)
    {
        super(ClientProxy.announcementAnimationLength);
        
        this.message = message;
        this.centerPosX = centerPosX;
        this.centerPosY = centerPosY;
    }
    
    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Font f = ClientProxy.getMinecraft().font;
        
        double relativeTickTime = (tickTime + partialTicks) / maxTickTime;
        
        // [0, 1/2pi]
        double cosTime1 = 0.5D * Math.PI * relativeTickTime;
        // [0, 1]
        float alpha = (float) (Math.cos(cosTime1));
        
        ms.pushPose();
        
        ms.translate(centerPosX, centerPosY - f.lineHeight / 2, 0);
        
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        int j = 16777215; //See TextWidget
        Screen.drawCenteredString(ms, f, message, 0, 0, j | Mth.ceil(alpha * 255.0F) << 24);
        
        RenderSystem.disableBlend();
        ScreenUtil.white();
        
        ms.popPose();
    }
}
