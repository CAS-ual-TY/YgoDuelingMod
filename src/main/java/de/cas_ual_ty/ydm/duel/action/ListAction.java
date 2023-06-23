package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class ListAction extends Action
{
    public List<Action> actions;
    
    public ListAction(ActionType actionType, List<Action> actions)
    {
        super(actionType);
        this.actions = actions;
    }
    
    public ListAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeAction));
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        DuelMessageUtility.encodeList(actions, buf, DuelMessageUtility::encodeAction);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        for(Action action : actions)
        {
            action.initServer(playField);
        }
    }
    
    @Override
    public void initClient(PlayField playField)
    {
        for(Action action : actions)
        {
            action.initClient(playField);
        }
    }
    
    @Override
    public void doAction()
    {
        for(Action action : actions)
        {
            action.doAction();
        }
    }
    
    @Override
    public void undoAction()
    {
        for(int i = actions.size() - 1; i >= 0; --i)
        {
            actions.get(i).undoAction();
        }
    }
    
    @Override
    public void redoAction()
    {
        for(Action action : actions)
        {
            action.redoAction();
        }
    }
}
