package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionIcon;

import javax.annotation.Nullable;

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
