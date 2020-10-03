package de.cas_ual_ty.ydm.duelmanager.playfield;

import java.util.List;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.action.Action;

public class ZoneTypeInteraction
{
    public final ZoneType interactor;
    public final ZoneType interactee;
    
    private List<ZoneInteraction> interactions;
    
    public ZoneTypeInteraction(ZoneType interactor, ZoneType interactee)
    {
        this.interactor = interactor;
        this.interactee = interactee;
    }
    
    /*
    public ZoneTypeInteraction oneAction(ZoneInteraction interaction)
    {
        
    }
    */
    
    public static interface ZoneInteraction
    {
        Action interact(Zone interactor, @Nullable DuelCard interactorCard, Zone interactee);
    }
}
