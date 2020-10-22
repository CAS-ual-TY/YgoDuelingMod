package de.cas_ual_ty.ydm.duelmanager.playfield;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.action.ActionIcon;

public class ZoneInteraction
{
    public final Zone interactor;
    public final DuelCard interactorCard;
    public final Zone interactee;
    public final Action action;
    public final ActionIcon icon;
    
    public ZoneInteraction(Zone interactor, @Nullable DuelCard interactorCard, Zone interactee, Action action, ActionIcon icon)
    {
        this.interactor = interactor;
        this.interactorCard = interactorCard;
        this.interactee = interactee;
        this.action = action;
        this.icon = icon;
    }
}
