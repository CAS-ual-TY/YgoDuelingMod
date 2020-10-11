package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.action.Action;

public class ZoneInteraction
{
    public final Zone interactor;
    public final Zone interactee;
    public final Action action;
    public final ZoneInteractionIcon icon;
    
    public ZoneInteraction(Zone interactor, Zone interactee, Action action, ZoneInteractionIcon icon)
    {
        this.interactor = interactor;
        this.interactee = interactee;
        this.action = action;
        this.icon = icon;
    }
}
