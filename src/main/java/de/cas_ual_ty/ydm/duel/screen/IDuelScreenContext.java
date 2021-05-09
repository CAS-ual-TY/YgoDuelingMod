package de.cas_ual_ty.ydm.duel.screen;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;

public interface IDuelScreenContext
{
    @Nullable
    public default Zone getClickedZone()
    {
        return this.getPlayField().getClickedZoneForPlayer(this.getViewOwner());
    }
    
    @Nullable
    public default DuelCard getClickedCard()
    {
        return this.getPlayField().getClickedCardForPlayer(this.getViewOwner());
    }
    
    @Nullable
    public default Zone getOpponentClickedZone()
    {
        return this.getPlayField().getClickedZoneForPlayer(this.getViewOwner().opponent());
    }
    
    @Nullable
    public default DuelCard getOpponentClickedCard()
    {
        return this.getPlayField().getClickedCardForPlayer(this.getViewOwner().opponent());
    }
    
    public default ZoneOwner getViewOwner()
    {
        return this.getZoneOwner().isPlayer() ? this.getZoneOwner() : ZoneOwner.PLAYER1;
    }
    
    public PlayField getPlayField();
    
    public ZoneOwner getView();
    
    public ZoneOwner getZoneOwner();
    
    public void renderCardInfo(MatrixStack ms, DuelCard card);
}
