package de.cas_ual_ty.ydm.clientutil;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;

public class ScreenUtil
{
    public static LimitedTextureBinder infoTextureBinder;
    public static LimitedTextureBinder mainTextureBinder;
    
    public static void renderCardInfo(MatrixStack ms, CardHolder card, ContainerScreen<?> screen)
    {
        ScreenUtil.renderCardInfo(ms, card, screen.getGuiLeft());
    }
    
    public static void renderCardInfo(MatrixStack ms, CardHolder card)
    {
        ScreenUtil.renderCardInfo(ms, card, 100);
    }
    
    public static void renderCardInfo(MatrixStack ms, CardHolder card, int width)
    {
        if(card == null || card.getCard() == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.push();
        ScreenUtil.white();
        
        {
            int x = margin;
            
            if(maxWidth < imageSize)
            {
                // draw it centered if the space we got is limited
                // to make sure the image is NOT rendered more to the right of the center
                x = (maxWidth - imageSize) / 2 + margin;
            }
            
            // card texture
            
            ScreenUtil.bindInfoResourceLocation(card);
            YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        }
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        ms.scale(f, f, f);
        
        {
            // card description text
            
            @SuppressWarnings("resource")
            FontRenderer fontRenderer = ClientProxy.getMinecraft().fontRenderer;
            
            List<ITextComponent> list = new LinkedList<>();
            card.getProperties().addInformation(list);
            
            ScreenUtil.drawSplitString(ms, fontRenderer, list, margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        }
        
        ms.pop();
    }
    
    public static void bindInfoResourceLocation(CardHolder c)
    {
        ScreenUtil.infoTextureBinder.bind(c.getInfoImageResourceLocation());
    }
    
    public static void bindMainResourceLocation(CardHolder c)
    {
        ScreenUtil.mainTextureBinder.bind(c.getMainImageResourceLocation());
    }
    
    public static void bindInfoResourceLocation(Properties p, byte imageIndex)
    {
        ScreenUtil.infoTextureBinder.bind(p.getInfoImageResourceLocation(imageIndex));
    }
    
    public static void bindMainResourceLocation(Properties p, byte imageIndex)
    {
        ScreenUtil.mainTextureBinder.bind(p.getMainImageResourceLocation(imageIndex));
    }
    
    public static void bindInfoResourceLocation(ResourceLocation r)
    {
        ScreenUtil.infoTextureBinder.bind(r);
    }
    
    public static void bindMainResourceLocation(ResourceLocation r)
    {
        ScreenUtil.mainTextureBinder.bind(r);
    }
    
    public static ResourceLocation getInfoCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeInfoImageSize + "/" + YdmItems.CARD_BACK.getRegistryName().getPath() + ".png");
    }
    
    public static ResourceLocation getMainCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeMainImageSize + "/" + YdmItems.CARD_BACK.getRegistryName().getPath() + ".png");
    }
    
    public static ResourceLocation getInfoTokenOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeInfoImageSize + "/" + "token_overlay" + ".png");
    }
    
    public static ResourceLocation getMainMainOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeMainImageSize + "/" + "token_overlay" + ".png");
    }
    
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
            if(t.getUnformattedComponentText().isEmpty() && t.getSiblings().isEmpty())
            {
                y += fontRenderer.FONT_HEIGHT;
            }
            else
            {
                for(IReorderingProcessor p : fontRenderer.trimStringToWidth(t, maxWidth))
                {
                    fontRenderer.func_238407_a_(ms, p, x, y, color);
                    y += fontRenderer.FONT_HEIGHT;
                }
            }
        }
    }
    
    public static void white()
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
