package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public enum ActionType
{
    MOVE_ON_TOP(MoveTopAction::new), MOVE_TO_BOTTOM(MoveBottomAction::new), SHUFFLE(ShuffleAction::new), SHOW_CARD(null), SHOW_ZONE(ShowZoneAction::new), VIEW_ZONE(ViewZoneAction::new), ATTACK(AttackAction::new);
    
    public static final ActionType[] VALUES = ActionType.values();
    
    public ActionType getFromIndex(int index)
    {
        return ActionType.VALUES[index];
    }
    
    static
    {
        int index = 0;
        for(ActionType actionType : ActionType.VALUES)
        {
            actionType.index = index++;
        }
    }
    
    public final ActionType.Factory factory;
    private int index;
    
    private ActionType(ActionType.Factory factory)
    {
        this.factory = factory;
    }
    
    public ActionType.Factory getFactory()
    {
        return this.factory;
    }
    
    public int getIndex()
    {
        return this.index;
    }
    
    public static interface Factory
    {
        // from and to can be the same, eg when shuffling
        // cardIndex to specify which card exactly (eg. which card from hand, which from grave etc.)
        // ... can be -1 (eg. for shuffle action)
        Action create(ActionType type, Zone from, Zone to, int cardIndex);
    }
}
