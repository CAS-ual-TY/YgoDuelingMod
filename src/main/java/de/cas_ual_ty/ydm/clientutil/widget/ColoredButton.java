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
        offset = 0;
    }
    
    public ColoredButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ITooltip onTooltip)
    {
        super(x, y, width, height, title, pressedAction, onTooltip);
        offset = 0;
    }
    
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(ColoredButton.RESOURCE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(matrixStack, x, y, 0, offset + i * 20, width / 2, height / 2);
        blit(matrixStack, x + width / 2, y, 200 - width / 2, offset + i * 20, width / 2, height / 2);
        blit(matrixStack, x, y + height / 2, 0, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
        blit(matrixStack, x + width / 2, y + height / 2, 200 - width / 2, offset + (i + 1) * 20 - height / 2, width / 2, height / 2);
        renderBg(matrixStack, minecraft, mouseX, mouseY);
        
        int j = getFGColor();
        AbstractGui.drawCenteredString(matrixStack, fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
        
        if(isHovered())
        {
            renderToolTip(matrixStack, mouseX, mouseY);
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
