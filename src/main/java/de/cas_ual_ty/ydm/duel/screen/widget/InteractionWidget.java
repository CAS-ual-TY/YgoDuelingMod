package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Consumer;

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

public class InteractionWidget extends Button
{
    public final ZoneInteraction interaction;
    public final IDuelScreenContext context;
    
    public InteractionWidget(ZoneInteraction interaction, IDuelScreenContext context, int x, int y, int width, int height, ITextComponent title, Consumer<InteractionWidget> onPress, ITooltip onTooltip)
    {
        super(x, y, width, height, title, (w) -> onPress.accept((InteractionWidget)w), onTooltip);
        this.interaction = interaction;
        this.context = context;
    }
    
    public InteractionWidget(ZoneInteraction interaction, IDuelScreenContext context, int x, int y, int width, int height, Consumer<InteractionWidget> onPress, ITooltip onTooltip)
    {
        super(x, y, width, height, interaction.icon.getLocal(), (w) -> onPress.accept((InteractionWidget)w), onTooltip);
        this.interaction = interaction;
        this.context = context;
    }
    
    @Override
    public void renderWidget(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        ms.push();
        ms.translate(0, 0, 5);
        
        ActionIcon icon = this.interaction.icon;
        
        int iconWidth = icon.iconWidth;
        int iconHeight = icon.iconHeight;
        
        if(iconHeight >= this.height)
        {
            iconWidth = this.height * iconWidth / iconHeight;
            iconHeight = this.height;
        }
        
        if(iconWidth >= this.width)
        {
            iconHeight = this.width * iconHeight / iconWidth;
            iconWidth = this.width;
        }
        
        ScreenUtil.white();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        ClientProxy.getMinecraft().getTextureManager().bindTexture(icon.sourceFile);
        YdmBlitUtil.blit(ms, this.x + (this.width - iconWidth) / 2, this.y + (this.height - iconHeight) / 2, iconWidth, iconHeight, icon.iconX, icon.iconY, icon.iconWidth, icon.iconHeight, icon.fileSize, icon.fileSize);
        
        if(this.isHovered() && this.active)
        {
            ScreenUtil.renderHoverRect(ms, this.x, this.y, this.width, this.height);
            this.renderToolTip(ms, mouseX, mouseY);
        }
        
        ms.pop();
    }
}