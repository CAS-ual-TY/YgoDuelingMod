package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class SmallTextButton extends Button
{
    public SmallTextButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ITooltip onTooltip)
    {
        super(x, y, width, height, title, pressedAction, onTooltip);
    }
    
    public SmallTextButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction)
    {
        super(x, y, width, height, title, pressedAction);
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
        
        ms.pushPose();
        ms.scale(0.5F, 0.5F, 1F);
        int j = getFGColor();
        AbstractGui.drawCenteredString(ms, fontrenderer, getMessage(), (x + width / 2) * 2, (y + height / 2) * 2 - fontrenderer.lineHeight / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
        ms.popPose();
        
        if(isHovered())
        {
            renderToolTip(ms, mouseX, mouseY);
        }
    }
}
