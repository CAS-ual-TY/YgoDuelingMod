package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ScreenUtil
{
    public static void drawLineRect(PoseStack ms, float x, float y, float w, float h, float lineWidth, float r, float g, float b, float a)
    {
        ScreenUtil.drawRect(ms, x, y, w, lineWidth, r, g, b, a); //top
        ScreenUtil.drawRect(ms, x, y + h - lineWidth, w, lineWidth, r, g, b, a); //bot
        ScreenUtil.drawRect(ms, x, y, lineWidth, h, r, g, b, a); //left
        ScreenUtil.drawRect(ms, x + w - lineWidth, y, lineWidth, h, r, g, b, a); //right
    }
    
    public static void drawRect(PoseStack ms, float x, float y, float w, float h, float r, float g, float b, float a)
    {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
    
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        
        // Use src_color * src_alpha
        // and dest_color * (1 - src_alpha) for colors
        RenderSystem.defaultBlendFunc();
    
        //RenderSystem.setShaderColor(r, g, b, a);
        
        Matrix4f m = ms.last().pose();
    
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(m, x, y + h, 0F).color(r, g, b, a).endVertex(); // BL
        bufferbuilder.vertex(m, x + w, y + h, 0F).color(r, g, b, a).endVertex(); // BR
        bufferbuilder.vertex(m, x + w, y, 0F).color(r, g, b, a).endVertex(); // TR
        bufferbuilder.vertex(m, x, y, 0F).color(r, g, b, a).endVertex(); // TL
        //tessellator.end();
        BufferUploader.drawWithShader(bufferbuilder.end());
    
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        
        //ScreenUtil.white();
    }
    
    public static void drawSplitString(PoseStack ms, Font fontRenderer, List<Component> list, float x, float y, int maxWidth, int color)
    {
        for(Component t : list)
        {
            if(t.getString().isEmpty() && t.getSiblings().isEmpty())
            {
                y += fontRenderer.lineHeight;
            }
            else
            {
                for(FormattedCharSequence p : fontRenderer.split(t, maxWidth))
                {
                    fontRenderer.drawShadow(ms, p, x, y, color);
                    y += fontRenderer.lineHeight;
                }
            }
        }
    }
    
    public static void renderHoverRect(PoseStack ms, float x, float y, float w, float h)
    {
        // from ContainerScreen#render
        
        RenderSystem.colorMask(true, true, true, false);
        ScreenUtil.drawRect(ms, x, y, w, h, 1F, 1F, 1F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void renderDisabledRect(PoseStack ms, float x, float y, float w, float h)
    {
        RenderSystem.colorMask(true, true, true, false);
        ScreenUtil.drawRect(ms, x, y, w, h, 0F, 0F, 0F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void white()
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
