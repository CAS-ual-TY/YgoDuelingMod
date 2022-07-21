package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;

public class InitSleevesAction extends Action
{
    public CardSleevesType player1Sleeves;
    public CardSleevesType player2Sleeves;
    
    public InitSleevesAction(ActionType actionType, CardSleevesType player1Sleeves, CardSleevesType player2Sleeves)
    {
        super(actionType);
        this.player1Sleeves = player1Sleeves;
        this.player2Sleeves = player2Sleeves;
    }
    
    public InitSleevesAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, CardSleevesType.getFromIndex(buf.readByte()), CardSleevesType.getFromIndex(buf.readByte()));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeByte(player1Sleeves.getIndex());
        buf.writeByte(player2Sleeves.getIndex());
    }
    
    @Override
    public void initClient(PlayField playField)
    {
        playField.initSleeves(player1Sleeves, player2Sleeves);
    }
    
    @Override
    public void doAction()
    {
        
    }
    
    @Override
    public void undoAction()
    {
        
    }
    
    @Override
    public void redoAction()
    {
        
    }
}
