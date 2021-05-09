package de.cas_ual_ty.ydm.clientutil;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;

public class ScreenUtil
{
    public static void drawLineRect(MatrixStack ms, float x, float y, float w, float h, float lineWidth, float r, float g, float b, float a)
    {
        ScreenUtil.drawRect(ms, x, y, w, lineWidth, r, g, b, a); //top
        ScreenUtil.drawRect(ms, x, y + h - lineWidth, w, lineWidth, r, g, b, a); //bot
        ScreenUtil.drawRect(ms, x, y, lineWidth, h, r, g, b, a); //left
        ScreenUtil.drawRect(ms, x + w - lineWidth, y, lineWidth, h, r, g, b, a); //right
    }
    
    public static void drawRect(MatrixStack ms, float x, float y, float w, float h, float r, float g, float b, float a)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        
        // Use src_color * src_alpha
        // and dest_color * (1 - src_alpha) for colors
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
        RenderSystem.color4f(r, g, b, a);
        
        Matrix4f m = ms.getLast().getMatrix();
        
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(m, x, y + h, 0F).endVertex(); // BL
        bufferbuilder.pos(m, x + w, y + h, 0F).endVertex(); // BR
        bufferbuilder.pos(m, x + w, y, 0F).endVertex(); // TR
        bufferbuilder.pos(m, x, y, 0F).endVertex(); // TL
        tessellator.draw();
        
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        ScreenUtil.white();
    }
    
    public static void drawSplitString(MatrixStack ms, FontRenderer fontRenderer, List<ITextComponent> list, float x, float y, int maxWidth, int color)
    {
        for(ITextComponent t : list)
        {
            if(t.getString().isEmpty() && t.getSiblings().isEmpty())
            {
                y += fontRenderer.FONT_HEIGHT;
            }
            else
            {
                for(IReorderingProcessor p : fontRenderer.trimStringToWidth(t, maxWidth))
                {
                    fontRenderer.drawTextWithShadow(ms, p, x, y, color);
                    y += fontRenderer.FONT_HEIGHT;
                }
            }
        }
    }
    
    public static void renderHoverRect(MatrixStack ms, float x, float y, float w, float h)
    {
        // from ContainerScreen#render
        
        RenderSystem.colorMask(true, true, true, false);
        ScreenUtil.drawRect(ms, x, y, w, h, 1F, 1F, 1F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void renderDisabledRect(MatrixStack ms, float x, float y, float w, float h)
    {
        RenderSystem.colorMask(true, true, true, false);
        ScreenUtil.drawRect(ms, x, y, w, h, 0F, 0F, 0F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void white()
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
