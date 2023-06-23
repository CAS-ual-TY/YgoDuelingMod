package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class DiceRollAction extends RandomAction
{
    public int result;
    
    public DiceRollAction(ActionType actionType, int result)
    {
        super(actionType);
        this.result = result;
    }
    
    public DiceRollAction(ActionType actionType)
    {
        this(actionType, -1);
    }
    
    public DiceRollAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readInt());
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        buf.writeInt(result);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        result = playField.getDuelManager().getRandom().nextInt(6) + 1;
    }
    
    @Override
    public MutableComponent getAnnouncement(Component playerName)
    {
        return Component.translatable(getAnnouncementLocalKey()).append(": " + result);
    }
}
