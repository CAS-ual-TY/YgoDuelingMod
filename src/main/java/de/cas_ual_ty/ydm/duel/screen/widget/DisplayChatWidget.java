package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

public class DisplayChatWidget extends Widget
{
    public Supplier<List<ITextComponent>> textSupplier;
    
    public DisplayChatWidget(int x, int y, int width, int height, ITextComponent title)
    {
        super(x, y, width, height, title);
        this.textSupplier = null;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if(this.textSupplier != null)
        {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        ScreenUtil.white();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1F, 1F, 1F, this.alpha);
        int color = this.getFGColor();
        DisplayChatWidget.drawLines(ms, fontrenderer, this.textSupplier.get(), this.x, this.y, this.width, this.height, color, (float)ClientProxy.duelChatSize);
    }
    
    public DisplayChatWidget setTextSupplier(Supplier<List<ITextComponent>> textSupplier)
    {
        this.textSupplier = textSupplier;
        return this;
    }
    
    public static void drawLines(MatrixStack ms, FontRenderer fontRenderer, List<ITextComponent> list, float x, float y, int maxWidth, float maxHeight, int color, final float downScale)
    {
        final float upScale = 1F / downScale;
        
        ms.push();
        
        ms.scale(downScale, downScale, 1F);
        
        x *= upScale;
        y *= upScale;
        maxWidth = Math.round(maxWidth * upScale);
        maxHeight *= upScale;
        
        ITextComponent t;
        List<IReorderingProcessor> ps;
        IReorderingProcessor p;
        int i, j;
        
        float minY = y;
        float maxY = y + maxHeight;
        
        y = maxY - fontRenderer.FONT_HEIGHT; // were in position of the last line
        
        for(i = list.size() - 1; y >= minY && i >= 0; --i)
        {
            t = list.get(i);
            
            if(t.getString().isEmpty() && t.getSiblings().isEmpty())
            {
                y -= fontRenderer.FONT_HEIGHT;
            }
            else
            {
                ps = fontRenderer.trimStringToWidth(t, maxWidth);
                
                for(j = ps.size() - 1; y >= minY && j >= 0; --j)
                {
                    p = ps.get(j);
                    fontRenderer.func_238407_a_(ms, p, x, y, color);
                    y -= fontRenderer.FONT_HEIGHT;
                }
            }
        }
        
        ms.pop();
    }
}
