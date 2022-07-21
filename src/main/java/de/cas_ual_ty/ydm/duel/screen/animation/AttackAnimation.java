package de.cas_ual_ty.ydm.duel.screen.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.widget.ZoneWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;

public class AttackAnimation extends Animation
{
    public final ZoneOwner view;
    public final ZoneWidget sourceZone;
    public final ZoneWidget destinationZone;
    
    public int sourceX;
    public int sourceY;
    public int destX;
    public int destY;
    
    public AttackAnimation(ZoneOwner view, ZoneWidget sourceZone, ZoneWidget destinationZone)
    {
        super(ClientProxy.attackAnimationLength);
        
        this.view = view;
        this.sourceZone = sourceZone;
        this.destinationZone = destinationZone;
        
        sourceX = this.sourceZone.getAnimationSourceX();
        sourceY = this.sourceZone.getAnimationSourceY();
        destX = this.destinationZone.getAnimationDestX();
        destY = this.destinationZone.getAnimationDestY();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        int halfTime = maxTickTime / 2;
        
        double relativeTickTime = (double) ((tickTime % halfTime) + partialTicks) / halfTime;
        
        // [1pi, 2pi]
        double cosTime1 = Math.PI * relativeTickTime + Math.PI;
        // [0, 1]
        float relativePositionRotation = (float) ((Math.cos(cosTime1) + 1) * 0.5D);
        
        float deltaX = destX - sourceX;
        float deltaY = destY - sourceY;
        
        float maxSize = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        float rotation;
        
        if(deltaX != 0)
        {
            rotation = (float) (Math.atan(deltaY / deltaX) + 0.5D * Math.PI);
        }
        else
        {
            if(deltaY > 0)
            {
                rotation = 0F;
            }
            else
            {
                rotation = (float) Math.PI;
            }
        }
        
        if(deltaX > 0)
        {
            rotation += Math.PI;
        }
        
        float posX;
        float posY;
        
        if(tickTime < halfTime)
        {
            posX = sourceX;
            posY = sourceY;
        }
        else
        {
            posX = destX;
            posY = destY;
            rotation += Math.PI;
            relativePositionRotation = 1 - relativePositionRotation;
        }
        
        ms.pushPose();
        
        ms.translate(posX, posY, 0);
        ms.mulPose(new Quaternion(0, 0, rotation, false));
        
        ScreenUtil.drawRect(ms, -2, 0, 4, maxSize * relativePositionRotation, 1F, 0, 0, 0.5F);
        
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
