package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TextureButton extends Button
{
    public ResourceLocation textureLocation;
    
    public int texX;
    public int texY;
    public int texW;
    public int texH;
    
    public TextureButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ITooltip onTooltip)
    {
        super(x, y, width, height, title, pressedAction, onTooltip);
        textureLocation = null;
    }
    
    public TextureButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction)
    {
        super(x, y, width, height, title, pressedAction);
        textureLocation = null;
    }
    
    public TextureButton setTexture(ResourceLocation textureLocation, int texX, int texY, int texW, int texH)
    {
        this.textureLocation = textureLocation;
        this.texX = texX;
        this.texY = texY;
        this.texW = texW;
        this.texH = texH;
        return this;
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
        
        if(textureLocation != null)
        {
            minecraft.getTextureManager().bind(textureLocation);
            YdmBlitUtil.blit(ms, x, y, width, height, texX, texY, texW, texH, 256, 256);
        }
        
        if(isHovered())
        {
            renderToolTip(ms, mouseX, mouseY);
        }
    }
}
