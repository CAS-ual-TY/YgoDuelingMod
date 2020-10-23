package de.cas_ual_ty.ydm.duel;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
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
}
