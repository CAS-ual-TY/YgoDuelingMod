package de.cas_ual_ty.ydm.duel.screen;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duelmanager.playfield.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneOwner;

public interface IDuelScreenContext
{
    @Nullable
    public Zone getClickedZone();
    
    @Nullable
    public DuelCard getClickedDuelCard();
    
    public ZoneOwner getView();
    
    public ZoneOwner getZoneOwner();
    
    public void renderCardInfo(MatrixStack ms, DuelCard card);
}
