package de.cas_ual_ty.ydm.duel.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.network.chat.Component;


import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MonsterZoneWidget extends ZoneWidget
{
    public MonsterZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, Component title, Consumer<ZoneWidget> onPress, OnTooltip onTooltip)
    {
        super(zone, context, width, height, title, onPress, onTooltip);
    }
    
    @Override
    @Nullable
    public DuelCard renderCards(PoseStack ms, int mouseX, int mouseY)
    {
        if(zone.getCardsAmount() <= 0)
        {
            return null;
        }
        else if(zone.getCardsAmount() == 1)
        {
            return super.renderCards(ms, mouseX, mouseY);
        }
        
        final int cardsWidth = DuelScreenDueling.CARDS_WIDTH * height / DuelScreenDueling.CARDS_HEIGHT;
        final int cardsHeight = height;
        final int offset = (cardsHeight - cardsWidth);
        final int cardsTextureSize = cardsHeight;
        
        DuelCard hoveredCard = null;
        float hoverX = x;
        float hoverY = y;
        float hoverWidth = cardsWidth;
        float hoverHeight = cardsHeight;
        
        boolean isOwner = zone.getOwner() == context.getZoneOwner();
        boolean isOpponentView = zone.getOwner() != context.getView();
        
        final int renderX = x;
        final int renderY = y;
        final int renderWidth = cardsTextureSize;
        final int renderHeight = cardsTextureSize;
        
        final boolean topCardInDef = !zone.getTopCard().position.isStraight;
        final int topCardIndex = topCardInDef ? 1 : 0;
        final int cardsAmount = zone.getCardsAmount() - topCardIndex;
        
        // If the top card is in DEF, cardsAmount is 1 less than zone.getCardsAmount
        // If not, its the same amount
        // We need to distinguish to not divide by 0 in else block
        
        // if this is true, there are exactly 2 cards in
        if(cardsAmount == 1)
        {
            DuelCard c = zone.getCardUnsafe(topCardIndex); // we get the 2nd card
            int newHoverX = renderX + offset / 2; // and put it in the middle
            
            if(drawCard(ms, c, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
            {
                hoveredCard = c;
                hoverX = newHoverX;
            }
        }
        else
        {
            float margin = cardsWidth - (cardsAmount * cardsWidth - width) / (float) (cardsAmount - 1);
            
            float hoverXBase = x;
            
            boolean renderLeftToRight = !isOpponentView;
            
            if(!renderLeftToRight)
            {
                margin *= -1F;
                hoverXBase += width - hoverWidth;
            }
            
            DuelCard c = null;
            float newHoverX;
            float newRenderX;
            
            for(int i = zone.getCardsAmount() - 1; i >= topCardIndex; --i)
            {
                c = zone.getCardUnsafe(i);
                
                newHoverX = hoverXBase + (i - topCardIndex) * margin;
                newRenderX = newHoverX - offset / 2;
                
                // if this is the top rendered card
                // and the card is sideways
                // adjust the hover rect
                // and also render it centered again
                if(drawCard(ms, c, newRenderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, hoverY, hoverWidth, hoverHeight))
                {
                    hoveredCard = c;
                    hoverX = newHoverX;
                }
            }
        }
        
        // if true we did not render this card yet
        if(topCardInDef)
        {
            DuelCard c = zone.getTopCard();
            float newHoverX = renderX;
            float newHoverY = renderY + offset / 2;
            float newHoverWidth = cardsHeight;
            float newHoverHeight = cardsWidth;
            
            if(drawCard(ms, c, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, newHoverX, newHoverY, newHoverWidth, newHoverHeight))
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
            if(hoveredCard.getCardPosition().isFaceUp || (isOwner && !zone.getType().getIsSecret()))
            {
                context.renderCardInfo(ms, hoveredCard);
            }
            
            if(active)
            {
                ScreenUtil.renderHoverRect(ms, hoverX, hoverY, hoverWidth, hoverHeight);
            }
        }
        
        if(!active)
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
        if(zone.getCardsAmount() <= 0 || zone.getType().getIsSecret())
        {
            return false;
        }
        else
        {
            int threshold = 3;
            
            if(!zone.getTopCard().getCardPosition().isStraight)
            {
                threshold++;
            }
            
            return zone.getCardsAmount() > threshold;
        }
    }
}
