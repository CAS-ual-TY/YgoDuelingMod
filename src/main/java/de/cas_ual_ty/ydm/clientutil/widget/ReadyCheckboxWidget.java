package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Supplier;

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
        active = isActive.get();
        super.render(ms, mouseX, mouseY, partial);
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float p_renderButton_3_)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(Widget.WIDGETS_LOCATION);
        ScreenUtil.white();
        int i = getYImage(isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(ms, x, y, 0, 46 + i * 20, width / 2, height);
        blit(ms, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);
        if(isChecked.get())
        {
            int j = getFGColor();
            AbstractGui.drawCenteredString(ms, minecraft.font, new StringTextComponent("✔"), x + width / 2, y + (height - 8) / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
        }
    }
}