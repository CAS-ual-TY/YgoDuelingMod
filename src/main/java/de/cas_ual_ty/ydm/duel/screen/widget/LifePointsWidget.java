package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.widget.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;


import java.util.function.Supplier;

public class LifePointsWidget extends AbstractWidget
{
    public static final ResourceLocation DUEL_WIDGETS = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png");
    
    public Supplier<Integer> lpGetter;
    public int maxLP;
    public ITooltip tooltip;
    
    public LifePointsWidget(int x, int y, int width, int height, Supplier<Integer> lpGetter, int maxLP, ITooltip tooltip)
    {
        super(x, y, width, height, Component.empty());
        this.lpGetter = lpGetter;
        this.maxLP = maxLP;
        this.tooltip = tooltip;
    }
    
    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
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
        
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        RenderSystem.setShaderTexture(0, LifePointsWidget.DUEL_WIDGETS);
        blit(ms, x, y, 0, 1 * 8, width, height);
        blit(ms, x, y, 0, 0, Mth.ceil(width * relativeLP), height);
        blit(ms, x, y, 0, 2 * 8, width, height);
        renderBg(ms, minecraft, mouseX, mouseY);
        
        x = this.x + width / 2;
        y = this.y + height / 2;
        
        ms.pushPose();
        
        ms.scale(0.5F, 0.5F, 1F);
        
        int j = getFGColor();
        Screen.drawCenteredString(ms, fontrenderer, Component.literal(String.valueOf(lp)), x * 2, y * 2 - fontrenderer.lineHeight / 2, j | Mth.ceil(alpha * 255.0F) << 24);
        
        ms.popPose();
        
        if(isHoveredOrFocused())
        {
            tooltip.onTooltip(this, ms, mouseX, mouseY);
        }
    }
    
    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
    
    }
}
