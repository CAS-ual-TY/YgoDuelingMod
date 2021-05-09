package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class StackZoneWidget extends ZoneWidget
{
    // this does not render counters
    
    public StackZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(zone, context, width, height, title, onPress, onTooltip);
    }
    
    @Override
    public void renderWidget(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1F, 1F, 1F, this.alpha);
        
        this.renderZoneSelectRect(ms, this.zone, this.x, this.y, this.width, this.height);
        
        this.hoverCard = this.renderCards(ms, mouseX, mouseY);
        
        RenderSystem.color4f(1F, 1F, 1F, this.alpha);
        
        if(this.zone.getCardsAmount() > 0)
        {
            // see font renderer, top static Vector3f
            // white is translated in front by that
            ms.push();
            ms.translate(0, 0, 0.03F);
            AbstractGui.drawCenteredString(ms, fontrenderer, new StringTextComponent(String.valueOf(this.zone.getCardsAmount())),
                this.x + this.width / 2, this.y + this.height / 2 - fontrenderer.FONT_HEIGHT / 2,
                16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
            ms.pop();
        }
        
        if(this.active)
        {
            if(this.isHovered())
            {
                if(this.zone.getCardsAmount() == 0)
                {
                    ScreenUtil.renderHoverRect(ms, this.x, this.y, this.width, this.height);
                }
                
                this.renderToolTip(ms, mouseX, mouseY);
            }
        }
        else
        {
            ScreenUtil.renderDisabledRect(ms, this.x, this.y, this.width, this.height);
        }
    }
    
    @Override
    public boolean openAdvancedZoneView()
    {
        return !this.zone.getType().getIsSecret() && this.zone.getCardsAmount() > 0;
    }
}
