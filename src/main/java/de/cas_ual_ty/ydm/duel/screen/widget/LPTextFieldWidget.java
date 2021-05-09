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
        this.setMaxStringLength(6);
        
        this.setValidator((text) ->
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
    public void renderWidget(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        this.x *= 2;
        this.y *= 2;
        this.width *= 2;
        this.height *= 2;
        
        this.x += 1;
        this.y += 1;
        this.width -= 2;
        this.height -= 2;
        
        ms.push();
        ms.scale(0.5F, 0.5F, 1);
        
        super.renderWidget(ms, mouseX * 2, mouseY * 2, partialTicks);
        
        ms.pop();
        
        this.x -= 1;
        this.y -= 1;
        this.width += 2;
        this.height += 2;
        
        this.x /= 2;
        this.y /= 2;
        this.width /= 2;
        this.height /= 2;
        
        if(this.isMouseOver(mouseX, mouseY))
        {
            this.tooltip.onTooltip(this, ms, mouseX, mouseY);
        }
    }
    
    @Override
    public int getAdjustedWidth()
    {
        return 2 * (this.enableBackgroundDrawing ? this.width - 8 : this.width);
    }
    
    // the getter is private,
    // so we gotta catch the value here
    @Override
    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn)
    {
        this.enableBackgroundDrawing = enableBackgroundDrawingIn;
        super.setEnableBackgroundDrawing(enableBackgroundDrawingIn);
    }
}
