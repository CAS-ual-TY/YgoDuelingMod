package de.cas_ual_ty.ydm.duel.action;

import java.util.LinkedList;
import java.util.List;

import de.cas_ual_ty.ydm.duel.DuelState;
import net.minecraft.network.PacketBuffer;

public class DuelMessages
{
    public static void encodeActions(List<Action> actions, PacketBuffer buf)
    {
        buf.writeInt(actions.size());
        
        for(Action action : actions)
        {
            DuelMessages.encodeAction(action, buf);
        }
    }
    
    public static List<Action> decodeActions(PacketBuffer buf)
    {
        int size = buf.readInt();
        List<Action> actions = new LinkedList<>();
        
        for(int i = 0; i < size; ++i)
        {
            actions.add(DuelMessages.decodeAction(buf));
        }
        
        return actions;
    }
    
    public static void encodeAction(Action action, PacketBuffer buf)
    {
        action.writeToBuf(buf);
    }
    
    public static Action decodeAction(PacketBuffer buf)
    {
        ActionType actionType = ActionType.getFromIndex(buf.readByte());
        return actionType.factory.create(actionType, buf);
    }
    
    public static void encodeDuelState(DuelState duelState, PacketBuffer buf)
    {
        buf.writeByte(duelState.getIndex());
    }
    
    public static DuelState decodeDuelState(PacketBuffer buf)
    {
        return DuelState.getFromIndex(buf.readByte());
    }
}
