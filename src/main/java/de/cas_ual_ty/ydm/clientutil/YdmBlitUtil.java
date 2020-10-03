package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class YdmBlitUtil
{
    // custom class making blit easier
    
    static int blitOffset = 0;
    
    public static void fullBlit(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight)
    {
        YdmBlitUtil.blit(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    public static void fullBlit90Degree(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight)
    {
        YdmBlitUtil.blit90Degree(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    public static void fullBlit180Degree(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight)
    {
        YdmBlitUtil.blit180Degree(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    public static void fullBlit270Degree(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight)
    {
        YdmBlitUtil.blit270Degree(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
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
    public static void blit(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        YdmBlitUtil.customInnerBlit(ms.getLast().getMatrix(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, textureX / (float)totalTextureFileWidth, (textureX + textureWidth) / (float)totalTextureFileWidth, textureY / (float)totalTextureFileHeight, (textureY + textureHeight) / (float)totalTextureFileHeight);
    }
    
    public static void blit90Degree(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / (float)totalTextureFileWidth;
        float y1 = textureY / (float)totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / (float)totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / (float)totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(ms.getLast().getMatrix(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, x2, y1, x2, y2, x1, y2, x1, y1);
    }
    
    public static void blit180Degree(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / (float)totalTextureFileWidth;
        float y1 = textureY / (float)totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / (float)totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / (float)totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(ms.getLast().getMatrix(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, x2, y2, x1, y2, x1, y1, x2, y1);
    }
    
    public static void blit270Degree(MatrixStack ms, int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / (float)totalTextureFileWidth;
        float y1 = textureY / (float)totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / (float)totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / (float)totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(ms.getLast().getMatrix(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, x1, y2, x1, y1, x2, y1, x2, y2);
    }
    
    protected static void customInnerBlit(Matrix4f matrix, int posX1, int posX2, int posY1, int posY2, int posZ, float texX1, float texX2, float texY1, float texY2)
    {
        YdmBlitUtil.customInnerBlit(matrix, posX1, posX2, posY1, posY2, posZ, texX1, texY1, texX2, texY1, texX2, texY2, texX1, texY2);
    }
    
    protected static void customInnerBlit(Matrix4f matrix, int posX1, int posX2, int posY1, int posY2, int posZ, float topLeftX, float topLeftY, float topRightX, float topRightY, float botRightX, float botRightY, float botLeftX, float botLeftY)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float)posX1, (float)posY2, (float)posZ).tex(botLeftX, botLeftY).endVertex();
        bufferbuilder.pos(matrix, (float)posX2, (float)posY2, (float)posZ).tex(botRightX, botRightY).endVertex();
        bufferbuilder.pos(matrix, (float)posX2, (float)posY1, (float)posZ).tex(topRightX, topRightY).endVertex();
        bufferbuilder.pos(matrix, (float)posX1, (float)posY1, (float)posZ).tex(topLeftX, topLeftY).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }
    
    public static interface FullBlitMethod
    {
        void fullBlit(MatrixStack ms, int x, int y, int width, int height);
    }
}
