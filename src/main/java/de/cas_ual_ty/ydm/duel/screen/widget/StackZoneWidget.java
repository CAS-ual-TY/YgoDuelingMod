package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class StackZoneWidget extends ZoneWidget
{
    // this does not render counters
    
    public StackZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, Component title, Consumer<ZoneWidget> onPress, OnTooltip onTooltip)
    {
        super(zone, context, width, height, title, onPress, onTooltip);
    }
    
    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        
        renderZoneSelectRect(ms, zone, x, y, width, height);
        
        hoverCard = renderCards(ms, mouseX, mouseY);
        
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        
        if(zone.getCardsAmount() > 0)
        {
            // see font renderer, top static Vector3f
            // white is translated in front by that
            ms.pushPose();
            ms.translate(0, 0, 0.03F);
            Screen.drawCenteredString(ms, fontrenderer, Component.literal(String.valueOf(zone.getCardsAmount())),
                    x + width / 2, y + height / 2 - fontrenderer.lineHeight / 2,
                    16777215 | Mth.ceil(alpha * 255.0F) << 24);
            ms.popPose();
        }
        
        if(active)
        {
            if(isHoveredOrFocused())
            {
                if(zone.getCardsAmount() == 0)
                {
                    ScreenUtil.renderHoverRect(ms, x, y, width, height);
                }
                
                renderToolTip(ms, mouseX, mouseY);
            }
        }
        else
        {
            ScreenUtil.renderDisabledRect(ms, x, y, width, height);
        }
    }
    
    @Override
    public boolean openAdvancedZoneView()
    {
        return !zone.getType().getIsSecret() && zone.getCardsAmount() > 0;
    }
}
