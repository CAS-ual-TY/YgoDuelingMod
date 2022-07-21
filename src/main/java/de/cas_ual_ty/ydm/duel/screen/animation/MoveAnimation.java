package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.widget.ZoneWidget;
import net.minecraft.util.math.vector.Quaternion;

public class MoveAnimation extends Animation
{
    public final ZoneOwner view;
    
    public final DuelCard duelCard;
    public final ZoneWidget sourceZone;
    public final ZoneWidget destinationZone;
    
    public final CardPosition sourcePosition;
    public final CardPosition destinationPosition;
    
    public int sourceX;
    public int sourceY;
    public int destX;
    public int destY;
    
    public MoveAnimation(ZoneOwner view, DuelCard duelCard, ZoneWidget sourceZone, ZoneWidget destinationZone, CardPosition sourcePosition, CardPosition destinationPosition)
    {
        super(ClientProxy.moveAnimationLength);
        
        this.view = view;
        
        this.duelCard = duelCard;
        this.sourceZone = sourceZone;
        this.destinationZone = destinationZone;
        this.sourcePosition = sourcePosition;
        this.destinationPosition = destinationPosition;
        
        sourceX = this.sourceZone.getAnimationSourceX();
        sourceY = this.sourceZone.getAnimationSourceY();
        destX = this.destinationZone.getAnimationDestX();
        destY = this.destinationZone.getAnimationDestY();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        double relativeTickTime = (double) (tickTime + partialTicks) / maxTickTime;
        float relativePositionRotation;
        float relativeScale;
        
        // [1pi, 2pi]
        double cosTime1 = Math.PI * relativeTickTime + Math.PI;
        // [0, 1]
        relativePositionRotation = (float) ((Math.cos(cosTime1) + 1) * 0.5D);
        
        if(sourcePosition.isFaceUp != destinationPosition.isFaceUp)
        {
            // [0pi, 2pi]
            double cosTime2 = 2 * Math.PI * relativeTickTime;
            // [0, 1]
            relativeScale = (float) ((Math.cos(cosTime2) + 1) * 0.5D);
        }
        else
        {
            relativeScale = 1;
        }
        
        final int cardSize = 32;
        float cardWidth = relativeScale * cardSize;
        float cardHeight = cardSize;
        
        float posX = sourceX;
        float posY = sourceY;
        
        posX += (destX - sourceX) * relativePositionRotation;
        posY += (destY - sourceY) * relativePositionRotation;
        
        CardPosition cardPosition;
        CardSleevesType sleeves;
        
        if(tickTime >= maxTickTime / 2)
        {
            cardPosition = destinationPosition;
            sleeves = destinationZone.zone.getSleeves();
        }
        else
        {
            cardPosition = sourcePosition;
            sleeves = sourceZone.zone.getSleeves();
        }
        
        float sourceRotation = MoveAnimation.getRotationForPositionAndView(view == sourceZone.zone.getOwner() || !sourceZone.zone.hasOwner(), sourcePosition);
        float targetRotation = MoveAnimation.getRotationForPositionAndView(view == destinationZone.zone.getOwner() || !destinationZone.zone.hasOwner(), destinationPosition);
        
        if(Math.abs(targetRotation - sourceRotation) > Math.abs(targetRotation - sourceRotation + 360))
        {
            sourceRotation -= 360;
        }
        else if(Math.abs(targetRotation - sourceRotation) > Math.abs(targetRotation - sourceRotation - 360))
        {
            targetRotation -= 360;
        }
        
        float rotation = sourceRotation + (targetRotation - sourceRotation) * relativePositionRotation;
        
        while(rotation < 0)
        {
            rotation += 360;
        }
        
        while(rotation >= 360)
        {
            rotation -= 360;
        }
        
        ms.pushPose();
        
        ms.translate(posX, posY, 0);
        ms.mulPose(new Quaternion(0, 0, rotation, true));
        
        // we always render the card position straight and manually rotate it, thats why we use fullBlit here
        CardRenderUtil.renderDuelCardAdvanced(ms, sleeves, mouseX, mouseY, -cardWidth / 2, -cardHeight / 2, cardWidth, cardHeight, duelCard, cardPosition, YdmBlitUtil::fullBlit);
        
        ms.popPose();
    }
    
    public static float getRotationForPositionAndView(boolean isOpponentView, CardPosition position)
    {
        if(position.isStraight)
        {
            if(!isOpponentView)
            {
                return 180;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            if(!isOpponentView)
            {
                return 90;
            }
            else
            {
                return 270;
            }
        }
    }
}
