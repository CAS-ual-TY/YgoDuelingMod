package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Supplier;

public class TextWidget extends Widget
{
    public Supplier<ITextComponent> msgGetter;
    public ITooltip tooltip;
    
    public TextWidget(int xIn, int yIn, int widthIn, int heightIn, Supplier<ITextComponent> msgGetter, ITooltip tooltip)
    {
        super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY);
        this.msgGetter = msgGetter;
        active = false;
        this.tooltip = tooltip;
    }
    
    public TextWidget(int xIn, int yIn, int widthIn, int heightIn, Supplier<ITextComponent> msgGetter)
    {
        this(xIn, yIn, widthIn, heightIn, msgGetter, null);
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(Widget.WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(ms, x, y, 0, 46 + i * 20, width / 2, height / 2);
        blit(ms, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height / 2);
        blit(ms, x, y + height / 2, 0, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
        blit(ms, x + width / 2, y + height / 2, 200 - width / 2, 46 + (i + 1) * 20 - height / 2, width / 2, height / 2);
        renderBg(ms, minecraft, mouseX, mouseY);
        int j = getFGColor();
        AbstractGui.drawCenteredString(ms, fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
        
        if(isHovered() && tooltip != null)
        {
            tooltip.onTooltip(this, ms, mouseX, mouseY);
        }
    }
    
    @Override
    public int getFGColor()
    {
        return 16777215; //From super
    }
    
    @Override
    public ITextComponent getMessage()
    {
        return msgGetter.get();
    }
}