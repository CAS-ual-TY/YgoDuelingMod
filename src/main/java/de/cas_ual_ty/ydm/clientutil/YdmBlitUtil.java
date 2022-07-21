package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

public class YdmBlitUtil
{
    // custom class making blit easier
    
    static float blitOffset = 0;
    
    public static void fullBlit(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight)
    {
        YdmBlitUtil.blit(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    public static void fullBlit90Degree(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight)
    {
        YdmBlitUtil.blit90Degree(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    public static void fullBlit180Degree(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight)
    {
        YdmBlitUtil.blit180Degree(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    public static void fullBlit270Degree(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight)
    {
        YdmBlitUtil.blit270Degree(ms, renderX, renderY, renderWidth, renderHeight, 0, 0, 1, 1, 1, 1);
    }
    
    /**
     * Param 1-4: Where to and how big to draw on the screen
     * Param 5-8: What part of the texture file to cut out and draw
     * Param 9-10: How big the entire texture file is in general (pow2 only)
     *
     * @param renderX                Where to draw on the screen
     * @param renderY                Where to draw on the screen
     * @param renderWidth            How big to draw on the screen
     * @param renderHeight           How big to draw on the screen
     * @param textureX
     * @param textureY
     * @param textureWidth
     * @param textureHeight
     * @param totalTextureFileWidth  The total texture file size
     * @param totalTextureFileHeight The total texture file size
     */
    public static void blit(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight, float textureX, float textureY, float textureWidth, float textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        YdmBlitUtil.customInnerBlit(ms.last().pose(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, textureX / totalTextureFileWidth, (textureX + textureWidth) / totalTextureFileWidth, textureY / totalTextureFileHeight, (textureY + textureHeight) / totalTextureFileHeight);
    }
    
    public static void blit90Degree(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight, float textureX, float textureY, float textureWidth, float textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / totalTextureFileWidth;
        float y1 = textureY / totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(ms.last().pose(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, x2, y1, x2, y2, x1, y2, x1, y1);
    }
    
    public static void blit180Degree(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight, float textureX, float textureY, float textureWidth, float textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / totalTextureFileWidth;
        float y1 = textureY / totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(ms.last().pose(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, x2, y2, x1, y2, x1, y1, x2, y1);
    }
    
    public static void blit270Degree(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight, float textureX, float textureY, float textureWidth, float textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        float x1 = textureX / totalTextureFileWidth;
        float y1 = textureY / totalTextureFileHeight;
        float x2 = (textureX + textureWidth) / totalTextureFileWidth;
        float y2 = (textureY + textureHeight) / totalTextureFileHeight;
        YdmBlitUtil.customInnerBlit(ms.last().pose(), renderX, renderX + renderWidth, renderY, renderY + renderHeight, YdmBlitUtil.blitOffset, x1, y2, x1, y1, x2, y1, x2, y2);
    }
    
    // see https://github.com/CAS-ual-TY/UsefulCodeBitsForTheBlocksGame/blob/main/src/main/java/com/example/examplemod/client/screen/BlitUtil.java
    // use full mask (64x64) for 16x16 texture
    public static void advancedMaskedBlit(MatrixStack ms, float renderX, float renderY, float renderWidth, float renderHeight, Runnable maskBinderAndDrawer, Runnable textureBinderAndDrawer)
    {
        //        RenderSystem.pushMatrix();
        ScreenUtil.white();
        RenderSystem.enableBlend();
        
        // We want a blendfunc that doesn't change the color of any pixels,
        // but rather replaces the framebuffer alpha values with values based
        // on the whiteness of the mask. In other words, if a pixel is white in the mask,
        // then the corresponding framebuffer pixel's alpha will be set to 1.
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ZERO);
        
        // Addendum to previous comment: Making sure that we write ALL pixels with ANY alpha.
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL11.GL_ALWAYS, 0);
        
        // Now "draw" the mask (again, this doesn't produce a visible result, it just
        // changes the alpha values in the framebuffer)
        maskBinderAndDrawer.run();
        
        // Finally, we want a blendfunc that makes the foreground visible only in
        // areas with high alpha.
        RenderSystem.blendFunc(SourceFactor.DST_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA);
        textureBinderAndDrawer.run();
        
        RenderSystem.disableBlend();
        //        RenderSystem.popMatrix();
    }
    
    protected static void customInnerBlit(Matrix4f matrix, float posX1, float posX2, float posY1, float posY2, float posZ, float texX1, float texX2, float texY1, float texY2)
    {
        YdmBlitUtil.customInnerBlit(matrix, posX1, posX2, posY1, posY2, posZ, texX1, texY1, texX2, texY1, texX2, texY2, texX1, texY2);
    }
    
    protected static void customInnerBlit(Matrix4f matrix, float posX1, float posX2, float posY1, float posY2, float posZ, float topLeftX, float topLeftY, float topRightX, float topRightY, float botRightX, float botRightY, float botLeftX, float botLeftY)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(matrix, posX1, posY2, posZ).uv(botLeftX, botLeftY).endVertex();
        bufferbuilder.vertex(matrix, posX2, posY2, posZ).uv(botRightX, botRightY).endVertex();
        bufferbuilder.vertex(matrix, posX2, posY1, posZ).uv(topRightX, topRightY).endVertex();
        bufferbuilder.vertex(matrix, posX1, posY1, posZ).uv(topLeftX, topLeftY).endVertex();
        bufferbuilder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(bufferbuilder);
    }
    
    public interface FullBlitMethod
    {
        void fullBlit(MatrixStack ms, float x, float y, float width, float height);
    }
}
