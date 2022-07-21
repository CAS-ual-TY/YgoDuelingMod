package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

public abstract class RandomAction extends Action implements IAnnouncedAction
{
    public RandomAction(ActionType actionType)
    {
        super(actionType);
    }
    
    public RandomAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public abstract void writeToBuf(PacketBuffer buf);
    
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
    public abstract IFormattableTextComponent getAnnouncement(ITextComponent playerName);
}
