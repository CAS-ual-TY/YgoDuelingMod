package de.cas_ual_ty.ydm.duelmanager.action;

import net.minecraft.network.PacketBuffer;

public enum ActionType
{
    POPULATE(Populate::new), MOVE_ON_TOP(MoveTopAction::new), MOVE_TO_BOTTOM(MoveBottomAction::new), SHUFFLE(ShuffleAction::new), SHOW_CARD(ShowCardAction::new), SHOW_ZONE(ShowZoneAction::new), VIEW_ZONE(ViewZoneAction::new), ATTACK(AttackAction::new);
    
    public static final ActionType[] VALUES = ActionType.values();
    
    public static ActionType getFromIndex(byte index)
    {
        return ActionType.VALUES[index];
    }
    
    static
    {
        byte index = 0;
        for(ActionType actionType : ActionType.VALUES)
        {
            actionType.index = index++;
        }
    }
    
    public final ActionType.Factory factory;
    private byte index;
    
    private ActionType(ActionType.Factory factory)
    {
        this.factory = factory;
    }
    
    public ActionType.Factory getFactory()
    {
        return this.factory;
    }
    
    public byte getIndex()
    {
        return this.index;
    }
    
    public static interface Factory
    {
        Action create(ActionType type, PacketBuffer buf);
    }
}
