package de.cas_ual_ty.ydm.clientutil.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ColoredTextWidget extends Widget
{
    public static final ResourceLocation RESOURCE = new ResourceLocation(YDM.MOD_ID, "textures/gui/colored_button.png");
    
    public Supplier<ITextComponent> msgGetter;
    public ITooltip tooltip;
    public int offset;
    
    public ColoredTextWidget(int xIn, int yIn, int widthIn, int heightIn, Supplier<ITextComponent> msgGetter, ITooltip tooltip)
    {
        super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY);
        this.msgGetter = msgGetter;
        this.active = false;
        this.tooltip = tooltip;
        this.offset = 0;
    }
    
    public ColoredTextWidget(int xIn, int yIn, int widthIn, int heightIn, Supplier<ITextComponent> msgGetter)
    {
        this(xIn, yIn, widthIn, heightIn, msgGetter, null);
    }
    
    @Override
    public void renderWidget(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(ColoredTextWidget.RESOURCE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(ms, this.x, this.y, 0, this.offset + i * 20, this.width / 2, this.height / 2);
        this.blit(ms, this.x + this.width / 2, this.y, 200 - this.width / 2, this.offset + i * 20, this.width / 2, this.height / 2);
        this.blit(ms, this.x, this.y + this.height / 2, 0, this.offset + (i + 1) * 20 - this.height / 2, this.width / 2, this.height / 2);
        this.blit(ms, this.x + this.width / 2, this.y + this.height / 2, 200 - this.width / 2, this.offset + (i + 1) * 20 - this.height / 2, this.width / 2, this.height / 2);
        this.renderBg(ms, minecraft, mouseX, mouseY);
        int j = this.getFGColor();
        AbstractGui.drawCenteredString(ms, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        
        if(this.isHovered() && this.tooltip != null)
        {
            this.tooltip.onTooltip(this, ms, mouseX, mouseY);
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
        return this.msgGetter.get();
    }
    
    public ColoredTextWidget setBlue()
    {
        this.offset = 0;
        return this;
    }
    
    public ColoredTextWidget setRed()
    {
        this.offset = 60;
        return this;
    }
}