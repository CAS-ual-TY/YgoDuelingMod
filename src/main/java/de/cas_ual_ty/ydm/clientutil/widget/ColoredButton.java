package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;


public class ColoredButton extends Button
{
    public static final ResourceLocation RESOURCE = new ResourceLocation(YDM.MOD_ID, "textures/gui/colored_button.png");
    
    public int offset;
    
    public ColoredButton(int x, int y, int width, int height, Component title, OnPress pressedAction)
    {
        super(x, y, width, height, title, pressedAction);
        offset = 0;
    }
    
    public ColoredButton(int x, int y, int width, int height, Component title, OnPress pressedAction, OnTooltip onTooltip)
    {
        super(x, y, width, height, title, pressedAction, onTooltip);
        offset = 0;
    }
    
    @Override
    public void renderButton(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShaderTexture(0, ColoredButton.RESOURCE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(PoseStack, x, y, 0, offset + i * 20, width / 2, height / 2);
        blit(PoseStack, x + width / 2, y, 200 - width / 2, offset + i * 20, width / 2, height / 2);
        blit(PoseStack, x, y + height / 2, 0, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
        blit(PoseStack, x + width / 2, y + height / 2, 200 - width / 2, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
        renderBg(PoseStack, minecraft, mouseX, mouseY);
        
        int j = getFGColor();
        Screen.drawCenteredString(PoseStack, fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | Mth.ceil(alpha * 255.0F) << 24);
        
        if(isHoveredOrFocused())
        {
            renderToolTip(PoseStack, mouseX, mouseY);
        }
    }
    
    public ColoredButton setBlue()
    {
        offset = 0;
        return this;
    }
    
    public ColoredButton setRed()
    {
        offset = 60;
        return this;
    }
}
