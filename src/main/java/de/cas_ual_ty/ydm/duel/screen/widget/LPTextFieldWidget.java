package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

public class LPTextFieldWidget extends TextFieldWidget
{
    // doing it exactly in the super class
    private boolean enableBackgroundDrawing = true;
    public ITooltip tooltip;
    
    public LPTextFieldWidget(FontRenderer fontrenderer, int x, int y, int width, int height, ITooltip tooltip)
    {
        super(fontrenderer, x, y, width, height, StringTextComponent.EMPTY);
        this.tooltip = tooltip;
        setMaxLength(6);
        
        setFilter((text) ->
        {
            if(text.isEmpty())
            {
                return true;
            }
            else
            {
                String pre = text.substring(0, 1);
                
                if(!pre.equals("+") && !pre.equals("-"))
                {
                    return false;
                }
                
                if(text.length() == 1)
                {
                    return true;
                }
                else
                {
                    return text.substring(1).matches("\\d+");
                }
            }
        });
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        x *= 2;
        y *= 2;
        width *= 2;
        height *= 2;
        
        x += 1;
        y += 1;
        width -= 2;
        height -= 2;
        
        ms.pushPose();
        ms.scale(0.5F, 0.5F, 1);
        
        super.renderButton(ms, mouseX * 2, mouseY * 2, partialTicks);
        
        ms.popPose();
        
        x -= 1;
        y -= 1;
        width += 2;
        height += 2;
        
        x /= 2;
        y /= 2;
        width /= 2;
        height /= 2;
        
        if(isMouseOver(mouseX, mouseY))
        {
            tooltip.onTooltip(this, ms, mouseX, mouseY);
        }
    }
    
    @Override
    public int getInnerWidth()
    {
        return 2 * (enableBackgroundDrawing ? width - 8 : width);
    }
    
    // the getter is private,
    // so we gotta catch the value here
    @Override
    public void setBordered(boolean enableBackgroundDrawingIn)
    {
        enableBackgroundDrawing = enableBackgroundDrawingIn;
        super.setBordered(enableBackgroundDrawingIn);
    }
}
