package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.util.text.ITextComponent;

public class HandZoneWidget extends ZoneWidget
{
    public HandZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(zone, context, width, height, title, onPress, onTooltip);
    }
    
    @Override
    @Nullable
    public DuelCard renderCards(MatrixStack ms, int mouseX, int mouseY)
    {
        if(this.zone.getCardsAmount() <= 0)
        {
            return super.renderCards(ms, mouseX, mouseY);
        }
        
        final int cardsWidth = DuelScreenDueling.CARDS_WIDTH * this.height / DuelScreenDueling.CARDS_HEIGHT;
        final int cardsHeight = this.height;
        final int offset = (cardsHeight - cardsWidth);
        final int cardsTextureSize = cardsHeight;
        
        DuelCard hoveredCard = null;
        float hoverX = this.x;
        float hoverY = this.y;
        float hoverWidth = cardsWidth;
        float hoverHeight = cardsHeight;
        
        boolean isOwner = this.zone.getOwner() == this.context.getZoneOwner();
        boolean isOpponentView = this.zone.getOwner() != this.context.getView();
        
        final int renderX = this.x;
        final int renderY = this.y;
        final int renderWidth = cardsTextureSize;
        final int renderHeight = cardsTextureSize;
        
        DuelCard c = null;
        hoverWidth = cardsWidth;
        
        int totalW = this.zone.getCardsAmount() * cardsWidth;
        
        if(totalW <= this.width || this.zone.getCardsAmount() == 1)
        {
            int newHoverX = renderX + (this.width - totalW) / 2;
            int newRenderX = newHoverX - (cardsTextureSize - cardsWidth) / 2; // Cards are 24x32, but the textures are still 32x32, so we must account for that
            
            for(int i = 0; i < this.zone.getCardsAmount(); ++i)
            {
                if(!isOpponentView)
                {
                    c = this.zone.getCardUnsafe(i);
                }
                else
                {
                    c = this.zone.getCardUnsafe(this.zone.getCardsAmount() - i - 1);
                }
                
                if(this.drawCard(ms, c, newRenderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
                {
                    hoveredCard = c;
                    hoverX = newHoverX;
                }
                
                newRenderX += cardsWidth;
                newHoverX += cardsWidth;
            }
        }
        else
        {
            float hoverXBase = this.x;
            
            float newRenderX;
            float newHoverX;
            
            float margin = cardsWidth - (this.zone.getCardsAmount() * cardsWidth - this.width) / (float)(this.zone.getCardsAmount() - 1);
            
            boolean renderLeftToRight = false;
            boolean renderFrontToBack = isOpponentView;
            
            if(!renderLeftToRight)
            {
                margin *= -1F;
                hoverXBase += this.width - cardsWidth;
            }
            
            for(int i = 0; i < this.zone.getCardsAmount(); ++i)
            {
                if(renderFrontToBack)
                {
                    c = this.zone.getCardUnsafe(i);
                }
                else
                {
                    c = this.zone.getCardUnsafe(this.zone.getCardsAmount() - i - 1);
                }
                
                newHoverX = hoverXBase + i * margin;
                newRenderX = newHoverX - offset / 2;
                
                if(this.drawCard(ms, c, newRenderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
                {
                    hoveredCard = c;
                    hoverX = newHoverX;
                }
            }
        }
        
        if(hoveredCard != null)
        {
            if(hoveredCard.getCardPosition().isFaceUp || (isOwner && !this.zone.getType().getIsSecret()))
            {
                this.context.renderCardInfo(ms, hoveredCard);
            }
            
            if(this.active)
            {
                ScreenUtil.renderHoverRect(ms, hoverX, hoverY, hoverWidth, hoverHeight);
            }
        }
        
        if(!this.active)
        {
            return null;
        }
        else
        {
            return hoveredCard;
        }
    }
    
    @Override
    public boolean openAdvancedZoneView()
    {
        return this.zone.getCardsAmount() > 12;
    }
}
