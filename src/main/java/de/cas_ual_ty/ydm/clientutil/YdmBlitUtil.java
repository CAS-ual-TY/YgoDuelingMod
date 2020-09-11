package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class YdmBlitUtil
{
    // custom class making blit easier
    
    public static void fullBlit(int renderX, int renderY, int renderWidth, int renderHeight)
    {
        YdmBlitUtil.customInnerBlit(renderX, renderX + renderWidth, renderY, renderY + renderHeight, 0, 0F, 1F, 0F, 1F);
    }
    
    /**
     * Param 1-4: Where to and how big to draw on the screen
     * Param 5-8: What part of the texture file to cut out and draw
     * Param 9-10: How big the entire texture file is in general (pow2 only)
     * 
     * @param renderX Where to draw on the screen
     * @param renderY Where to draw on the screen
     * @param renderWidth How big to draw on the screen
     * @param renderHeight How big to draw on the screen
     * @param textureX
     * @param textureY
     * @param textureWidth
     * @param textureHeight
     * @param totalTextureFileWidth The total texture file size
     * @param totalTextureFileHeight The total texture file size
     */
    public static void blit(int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        YdmBlitUtil.customInnerBlit(renderX, renderX + renderWidth, renderY, renderY + renderHeight, 0, textureX / (float)totalTextureFileWidth, (textureX + textureWidth) / (float)totalTextureFileWidth, textureY / (float)totalTextureFileHeight, (textureY + textureHeight) / (float)totalTextureFileHeight);
    }
    
    public static void blit90Degree(int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / (float)totalTextureFileWidth;
        float y1 = textureY / (float)totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / (float)totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / (float)totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(renderX, renderX + renderWidth, renderY, renderY + renderHeight, 0, x2, y1, x2, y2, x1, y2, x1, y1);
    }
    
    public static void blit180Degree(int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / (float)totalTextureFileWidth;
        float y1 = textureY / (float)totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / (float)totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / (float)totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(renderX, renderX + renderWidth, renderY, renderY + renderHeight, 0, x2, y2, x1, y2, x1, y1, x2, y1);
    }
    
    public static void blit270Degree(int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / (float)totalTextureFileWidth;
        float y1 = textureY / (float)totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / (float)totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / (float)totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(renderX, renderX + renderWidth, renderY, renderY + renderHeight, 0, x1, y2, x1, y1, x2, y1, x2, y2);
    }
    
    protected static void customInnerBlit(int posX1, int posX2, int posY1, int posY2, int posZ, float texX1, float texX2, float texY1, float texY2)
    {
        YdmBlitUtil.customInnerBlit(posX1, posX2, posY1, posY2, posZ, texX1, texY1, texX2, texY1, texX2, texY2, texX1, texY2);
    }
    
    protected static void customInnerBlit(int posX1, int posX2, int posY1, int posY2, int posZ, float topLeftX, float topLeftY, float topRightX, float topRightY, float botRightX, float botRightY, float botLeftX, float botLeftY)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)posX1, (double)posY1, (double)posZ).tex(topLeftX, topLeftY).endVertex();
        bufferbuilder.pos((double)posX2, (double)posY1, (double)posZ).tex(topRightX, topRightY).endVertex();
        bufferbuilder.pos((double)posX2, (double)posY2, (double)posZ).tex(botRightX, botRightY).endVertex();
        bufferbuilder.pos((double)posX1, (double)posY2, (double)posZ).tex(botLeftX, botLeftY).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }
}
