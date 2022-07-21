package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Supplier;

public class LifePointsWidget extends Widget
{
    public static final ResourceLocation DUEL_WIDGETS = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png");
    
    public Supplier<Integer> lpGetter;
    public int maxLP;
    public ITooltip tooltip;
    
    public LifePointsWidget(int x, int y, int width, int height, Supplier<Integer> lpGetter, int maxLP, ITooltip tooltip)
    {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.lpGetter = lpGetter;
        this.maxLP = maxLP;
        this.tooltip = tooltip;
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        int lp = lpGetter.get();
        float relativeLP = Math.min(1F, lp / (float) maxLP);
        
        final int margin = 1;
        int x = this.x;
        int y = this.y;
        int w = width;
        int h = height;
        
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        minecraft.getTextureManager().bind(LifePointsWidget.DUEL_WIDGETS);
        blit(ms, x, y, 0, 1 * 8, width, height);
        blit(ms, x, y, 0, 0, MathHelper.ceil(width * relativeLP), height);
        blit(ms, x, y, 0, 2 * 8, width, height);
        renderBg(ms, minecraft, mouseX, mouseY);
        
        x = this.x + width / 2;
        y = this.y + height / 2;
        
        ms.pushPose();
        
        ms.scale(0.5F, 0.5F, 1F);
        
        int j = getFGColor();
        AbstractGui.drawCenteredString(ms, fontrenderer, new StringTextComponent(String.valueOf(lp)), x * 2, y * 2 - fontrenderer.lineHeight / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
        
        ms.popPose();
        
        if(isHovered())
        {
            tooltip.onTooltip(this, ms, mouseX, mouseY);
        }
    }
}
