package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.Supplier;


public class DisplayChatWidget extends AbstractWidget
{
    public Supplier<List<Component>> textSupplier;
    
    public DisplayChatWidget(int x, int y, int width, int height, Component title)
    {
        super(x, y, width, height, title);
        textSupplier = null;
    }
    
    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks)
    {
        if(textSupplier != null)
        {
            super.render(PoseStack, mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        ScreenUtil.white();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        int color = getFGColor();
        DisplayChatWidget.drawLines(ms, fontrenderer, textSupplier.get(), x, y, width, height, color, (float) ClientProxy.duelChatSize);
    }
    
    public DisplayChatWidget setTextSupplier(Supplier<List<Component>> textSupplier)
    {
        this.textSupplier = textSupplier;
        return this;
    }
    
    public static void drawLines(PoseStack ms, Font fontRenderer, List<Component> list, float x, float y, int maxWidth, float maxHeight, int color, final float downScale)
    {
        final float upScale = 1F / downScale;
        
        ms.pushPose();
        
        ms.scale(downScale, downScale, 1F);
        
        x *= upScale;
        y *= upScale;
        maxWidth = Math.round(maxWidth * upScale);
        maxHeight *= upScale;
        
        Component t;
        List<FormattedCharSequence> ps;
        FormattedCharSequence p;
        int i, j;
        
        float minY = y;
        float maxY = y + maxHeight;
        
        y = maxY - fontRenderer.lineHeight; // were in position of the last line
        
        for(i = list.size() - 1; y >= minY && i >= 0; --i)
        {
            t = list.get(i);
            
            if(t.getString().isEmpty() && t.getSiblings().isEmpty())
            {
                y -= fontRenderer.lineHeight;
            }
            else
            {
                ps = fontRenderer.split(t, maxWidth);
                
                for(j = ps.size() - 1; y >= minY && j >= 0; --j)
                {
                    p = ps.get(j);
                    fontRenderer.drawShadow(ms, p, x, y, color);
                    y -= fontRenderer.lineHeight;
                }
            }
        }
        
        ms.popPose();
    }
    
    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
    
    }
}
