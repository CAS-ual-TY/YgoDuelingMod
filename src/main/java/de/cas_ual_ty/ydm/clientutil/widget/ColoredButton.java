package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class ColoredButton extends Button
{
    public static final ResourceLocation RESOURCE = new ResourceLocation(YDM.MOD_ID, "textures/gui/colored_button.png");
    
    public int offset;
    
    public ColoredButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction)
    {
        super(x, y, width, height, title, pressedAction);
        this.offset = 0;
    }
    
    public ColoredButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ITooltip onTooltip)
    {
        super(x, y, width, height, title, pressedAction, onTooltip);
    }
    
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(ColoredButton.RESOURCE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(matrixStack, this.x, this.y, 0, this.offset + i * 20, this.width / 2, this.height / 2);
        this.blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, this.offset + i * 20, this.width / 2, this.height / 2);
        this.blit(matrixStack, this.x, this.y + this.height / 2, 0, this.offset + (i + 1) * 20 - this.height / 2, this.width / 2, this.height / 2);
        this.blit(matrixStack, this.x + this.width / 2, this.y + this.height / 2, 200 - this.width / 2, this.offset + (i + 1) * 20 - this.height / 2, this.width / 2, this.height / 2);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        
        int j = this.getFGColor();
        AbstractGui.drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        
        if(this.isHovered())
        {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }
    
    public ColoredButton setBlue()
    {
        this.offset = 0;
        return this;
    }
    
    public ColoredButton setRed()
    {
        this.offset = 60;
        return this;
    }
}
