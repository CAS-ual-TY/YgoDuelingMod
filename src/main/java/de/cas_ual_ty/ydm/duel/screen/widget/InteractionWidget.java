package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import de.cas_ual_ty.ydm.duel.action.ActionIcon;
import de.cas_ual_ty.ydm.duel.playfield.ZoneInteraction;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class InteractionWidget extends Button
{
    public final ZoneInteraction interaction;
    public final IDuelScreenContext context;
    
    public InteractionWidget(ZoneInteraction interaction, IDuelScreenContext context, int x, int y, int width, int height, ITextComponent title, Consumer<InteractionWidget> onPress, ITooltip onTooltip)
    {
        super(x, y, width, height, title, (w) -> onPress.accept((InteractionWidget) w), onTooltip);
        this.interaction = interaction;
        this.context = context;
    }
    
    public InteractionWidget(ZoneInteraction interaction, IDuelScreenContext context, int x, int y, int width, int height, Consumer<InteractionWidget> onPress, ITooltip onTooltip)
    {
        super(x, y, width, height, interaction.icon.getLocal(), (w) -> onPress.accept((InteractionWidget) w), onTooltip);
        this.interaction = interaction;
        this.context = context;
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        ms.pushPose();
        ms.translate(0, 0, 5);
        
        ActionIcon icon = interaction.icon;
        
        int iconWidth = icon.iconWidth;
        int iconHeight = icon.iconHeight;
        
        if(iconHeight >= height)
        {
            iconWidth = height * iconWidth / iconHeight;
            iconHeight = height;
        }
        
        if(iconWidth >= width)
        {
            iconHeight = width * iconHeight / iconWidth;
            iconWidth = width;
        }
        
        ScreenUtil.white();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        ClientProxy.getMinecraft().getTextureManager().bind(icon.sourceFile);
        YdmBlitUtil.blit(ms, x + (width - iconWidth) / 2, y + (height - iconHeight) / 2, iconWidth, iconHeight, icon.iconX, icon.iconY, icon.iconWidth, icon.iconHeight, icon.fileSize, icon.fileSize);
        
        if(isHovered() && active)
        {
            ScreenUtil.renderHoverRect(ms, x, y, width, height);
            renderToolTip(ms, mouseX, mouseY);
        }
        
        ms.popPose();
    }
}