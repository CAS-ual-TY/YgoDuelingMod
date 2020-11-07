package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
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
        this.view = view;
        
        this.duelCard = duelCard;
        this.sourceZone = sourceZone;
        this.destinationZone = destinationZone;
        this.sourcePosition = sourcePosition;
        this.destinationPosition = destinationPosition;
        
        this.sourceX = this.sourceZone.getAnimationSourceX();
        this.sourceY = this.sourceZone.getAnimationSourceY();
        this.destX = this.destinationZone.getAnimationDestX();
        this.destY = this.destinationZone.getAnimationDestY();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        double relativeTickTime = (double)(this.tickTime + partialTicks) / this.maxTickTime;
        float relativePositionRotation;
        float relativeScale;
        
        // [1pi, 2pi]
        double cosTime1 = Math.PI * relativeTickTime + Math.PI;
        // [0, 1]
        relativePositionRotation = (float)((Math.cos(cosTime1) + 1) * 0.5D);
        
        if(this.sourcePosition.isFaceUp != this.destinationPosition.isFaceUp)
        {
            // [0pi, 2pi]
            double cosTime2 = 2 * Math.PI * relativeTickTime;
            // [0, 1]
            relativeScale = (float)((Math.cos(cosTime2) + 1) * 0.5D);
        }
        else
        {
            relativeScale = 1;
        }
        
        final int cardSize = 32;
        float cardWidth = relativeScale * cardSize;
        float cardHeight = cardSize;
        
        float posX = this.sourceX;
        float posY = this.sourceY;
        
        posX += (this.destX - this.sourceX) * relativePositionRotation;
        posY += (this.destY - this.sourceY) * relativePositionRotation;
        
        CardPosition cardPosition;
        
        if(this.tickTime >= this.maxTickTime / 2)
        {
            cardPosition = this.destinationPosition;
        }
        else
        {
            cardPosition = this.sourcePosition;
        }
        
        float sourceRotation = MoveAnimation.getRotationForPositionAndView(this.view == this.sourceZone.zone.getOwner() || !this.sourceZone.zone.hasOwner(), this.sourcePosition);
        float targetRotation = MoveAnimation.getRotationForPositionAndView(this.view == this.destinationZone.zone.getOwner() || !this.destinationZone.zone.hasOwner(), this.destinationPosition);
        
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
        
        ms.push();
        
        ms.translate(posX, posY, 0);
        ms.rotate(new Quaternion(0, 0, rotation, true));
        
        // we always render the card position straight and manually rotate it, thats why we use fullBlit here
        CardRenderUtil.renderDuelCardAdvanced(ms, -cardWidth / 2, -cardHeight / 2, cardWidth, cardHeight, this.duelCard, cardPosition, YdmBlitUtil::fullBlit);
        
        ms.pop();
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
