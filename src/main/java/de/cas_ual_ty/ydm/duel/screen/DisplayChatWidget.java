package de.cas_ual_ty.ydm.duel.screen;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

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
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int color = this.getFGColor();
        DisplayChatWidget.drawLines(ms, fontrenderer, this.textSupplier.get(), this.x, this.y, this.width, this.height, color);
    }
    
    public DisplayChatWidget setTextSupplier(Supplier<List<ITextComponent>> textSupplier)
    {
        this.textSupplier = textSupplier;
        return this;
    }
    
    public static void drawLines(MatrixStack ms, FontRenderer fontRenderer, List<ITextComponent> list, int x, int y, int maxWidth, int maxHeight, int color)
    {
        ITextComponent t;
        List<IReorderingProcessor> ps;
        IReorderingProcessor p;
        int i, j;
        
        int minY = y;
        int maxY = y + maxHeight;
        
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
    }
}
