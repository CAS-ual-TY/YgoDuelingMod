package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class ReadyCheckboxWidget extends Button
{
    public Supplier<Boolean> isChecked;
    public Supplier<Boolean> isActive;
    
    public ReadyCheckboxWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, IPressable onPress, Supplier<Boolean> isChecked, Supplier<Boolean> isActive)
    {
        super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY, onPress);
        this.isChecked = isChecked;
        this.isActive = isActive;
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
    {
        this.active = this.isActive.get();
        super.render(ms, mouseX, mouseY, partial);
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float p_renderButton_3_)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(Widget.WIDGETS_LOCATION);
        ScreenUtil.white();
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(ms, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(ms, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        if(this.isChecked.get())
        {
            int j = this.getFGColor();
            AbstractGui.drawCenteredString(ms, minecraft.fontRenderer, new StringTextComponent("✔"), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}