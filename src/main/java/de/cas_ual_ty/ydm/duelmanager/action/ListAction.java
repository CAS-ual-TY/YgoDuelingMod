package de.cas_ual_ty.ydm.duelmanager.action;

import java.util.List;

import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageUtility;
import net.minecraft.network.PacketBuffer;

public class ListAction extends Action
{
    public List<Action> actions;
    
    public ListAction(ActionType actionType, List<Action> actions)
    {
        super(actionType);
        this.actions = actions;
    }
    
    public ListAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeAction));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeList(this.actions, buf, DuelMessageUtility::encodeAction);
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
