package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import de.cas_ual_ty.ydm.duelmanager.playfield.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import net.minecraft.util.text.ITextComponent;

public class MonsterZoneWidget extends ZoneWidget
{
    public MonsterZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(zone, context, width, height, title, onPress, onTooltip);
    }
    
    @Override
    @Nullable
    public DuelCard renderCards(MatrixStack ms, int mouseX, int mouseY)
    {
        if(this.zone.getCardsAmount() <= 0)
        {
            return null;
        }
        else if(this.zone.getCardsAmount() == 1)
        {
            return super.renderCards(ms, mouseX, mouseY);
        }
        
        final int cardsWidth = DuelScreenDueling.CARDS_WIDTH * this.height / DuelScreenDueling.CARDS_HEIGHT;
        final int cardsHeight = this.height;
        final int offset = (cardsHeight - cardsWidth);
        final int cardsTextureSize = cardsHeight;
        
        DuelCard hoveredCard = null;
        int hoverX = this.x;
        int hoverY = this.y;
        int hoverWidth = cardsWidth;
        int hoverHeight = cardsHeight;
        
        boolean isOwner = this.zone.getOwner() == this.context.getZoneOwner();
        boolean isOpponentView = this.zone.getOwner() != this.context.getView();
        
        final int renderX = this.x;
        final int renderY = this.y;
        final int renderWidth = cardsTextureSize;
        final int renderHeight = cardsTextureSize;
        
        final boolean topCardInDef = !this.zone.getTopCard().position.isStraight;
        final int topCardIndex = topCardInDef ? 1 : 0;
        final int cardsAmount = this.zone.getCardsAmount() - topCardIndex;
        
        // If the top card is in DEF, cardsAmount is 1 less than zone.getCardsAmount
        // If not, its the same amount
        // We need to distinguish to not divide by 0 in else block
        
        // if this is true, there are exactly 2 cards in
        if(cardsAmount == 1)
        {
            DuelCard c = this.zone.getCardUnsafe(topCardIndex); // we get the 2nd card
            int newHoverX = renderX + offset / 2; // and put it in the middle
            
            if(this.drawCard(ms, c, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
            {
                hoveredCard = c;
                hoverX = newHoverX;
            }
        }
        else
        {
            float margin = cardsWidth - (cardsAmount * cardsWidth - this.width) / (float)(cardsAmount - 1);
            
            int hoverXBase = this.x;
            
            boolean renderLeftToRight = !isOpponentView;
            //            boolean renderFrontToBack = false;
            
            if(!renderLeftToRight)
            {
                margin *= -1F;
                hoverXBase += this.width - hoverWidth;
            }
            
            DuelCard c = null;
            int newHoverX;
            int newRenderX;
            
            for(int i = this.zone.getCardsAmount() - 1; i >= topCardIndex; --i)
            {
                //                if(renderFrontToBack)
                //                {
                //                    c = this.zone.getCardUnsafe(i);
                //                }
                //                else
                //                {
                c = this.zone.getCardUnsafe(i);
                //                }
                
                newHoverX = hoverXBase + Math.round((i - topCardIndex) * margin);
                newRenderX = newHoverX - offset / 2;
                
                // if this is the top rendered card
                // and the card is sideways
                // adjust the hover rect
                // and also render it centered again
                if(this.drawCard(ms, c, newRenderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
                {
                    hoveredCard = c;
                    hoverX = newHoverX;
                }
            }
        }
        
        // if true we did not render this card yet
        if(topCardInDef)
        {
            DuelCard c = this.zone.getTopCard();
            int newHoverX = renderX;
            int newHoverY = renderY + offset / 2;
            int newHoverWidth = cardsHeight;
            int newHoverHeight = cardsWidth;
            
            if(this.drawCard(ms, c, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, newHoverY, newHoverWidth, newHoverHeight))
            {
                hoveredCard = c;
                hoverX = newHoverX;
                hoverY = newHoverY;
                hoverWidth = newHoverWidth;
                hoverHeight = newHoverHeight;
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
                DuelScreenDueling.renderHoverRect(ms, hoverX, hoverY, hoverWidth, hoverHeight);
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
}
