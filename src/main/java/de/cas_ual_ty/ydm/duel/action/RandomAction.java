package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public abstract class RandomAction extends Action implements IAnnouncedAction
{
    public RandomAction(ActionType actionType)
    {
        super(actionType);
    }
    
    public RandomAction(ActionType actionType, FriendlyByteBuf buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public abstract void writeToBuf(FriendlyByteBuf buf);
    
    @Override
    public abstract void initServer(PlayField playField);
    
    @Override
    public void doAction()
    {
    }
    
    @Override
    public void undoAction()
    {
        doAction();
    }
    
    @Override
    public void redoAction()
    {
        doAction();
    }
    
    @Override
    public String getAnnouncementLocalKey()
    {
        return actionType.getLocalKey();
    }
    
    @Override
    public abstract MutableComponent getAnnouncement(Component playerName);
}
