package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;

import javax.annotation.Nullable;

public interface IDuelScreenContext
{
    @Nullable
    default Zone getClickedZone()
    {
        return getPlayField().getClickedZoneForPlayer(getViewOwner());
    }
    
    @Nullable
    default DuelCard getClickedCard()
    {
        return getPlayField().getClickedCardForPlayer(getViewOwner());
    }
    
    @Nullable
    default Zone getOpponentClickedZone()
    {
        return getPlayField().getClickedZoneForPlayer(getViewOwner().opponent());
    }
    
    @Nullable
    default DuelCard getOpponentClickedCard()
    {
        return getPlayField().getClickedCardForPlayer(getViewOwner().opponent());
    }
    
    default ZoneOwner getViewOwner()
    {
        return getZoneOwner().isPlayer() ? getZoneOwner() : ZoneOwner.PLAYER1;
    }
    
    PlayField getPlayField();
    
    ZoneOwner getView();
    
    ZoneOwner getZoneOwner();
    
    void renderCardInfo(PoseStack ms, DuelCard card);
}
