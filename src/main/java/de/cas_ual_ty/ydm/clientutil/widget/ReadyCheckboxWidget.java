package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;


import java.util.function.Supplier;

public class ReadyCheckboxWidget extends Button
{
    public Supplier<Boolean> isChecked;
    public Supplier<Boolean> isActive;
    
    public ReadyCheckboxWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, OnPress onPress, Supplier<Boolean> isChecked, Supplier<Boolean> isActive)
    {
        super(xIn, yIn, widthIn, heightIn, Component.empty(), onPress);
        this.isChecked = isChecked;
        this.isActive = isActive;
    }
    
    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partial)
    {
        active = isActive.get();
        super.render(ms, mouseX, mouseY, partial);
    }
    
    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float p_renderButton_3_)
    {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
        ScreenUtil.white();
        int i = getYImage(isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(ms, x, y, 0, 46 + i * 20, width / 2, height);
        blit(ms, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);
        if(isChecked.get())
        {
            int j = getFGColor();
            Screen.drawCenteredString(ms, minecraft.font, Component.literal("✔"), x + width / 2, y + (height - 8) / 2, j | Mth.ceil(alpha * 255.0F) << 24);
        }
    }
}