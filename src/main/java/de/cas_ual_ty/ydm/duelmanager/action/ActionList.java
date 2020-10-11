package de.cas_ual_ty.ydm.duelmanager.action;

import java.util.List;

import de.cas_ual_ty.ydm.duelmanager.DuelMessages;
import net.minecraft.network.PacketBuffer;

public class ActionList extends Action
{
    public List<Action> actions;
    
    public ActionList(ActionType actionType, List<Action> actions)
    {
        super(actionType);
        this.actions = actions;
    }
    
    public ActionList(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, DuelMessages.decodeList(buf, DuelMessages::decodeAction));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessages.encodeList(this.actions, buf, DuelMessages::encodeAction);
    }
    
    @Override
    public void doAction()
    {
        for(Action action : this.actions)
        {
            action.doAction();
        }
    }
    
    @Override
    public void undoAction()
    {
        for(int i = this.actions.size() - 1; i >= 0; --i)
        {
            this.actions.get(i).undoAction();
        }
    }
    
    @Override
    public void redoAction()
    {
        for(Action action : this.actions)
        {
            action.redoAction();
        }
    }
}
