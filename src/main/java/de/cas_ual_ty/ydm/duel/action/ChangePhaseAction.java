package de.cas_ual_ty.ydm.duel.action;

import java.util.function.Consumer;

import de.cas_ual_ty.ydm.duel.DuelPhase;
import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;

public class ChangePhaseAction extends Action
{
    public DuelPhase phase;
    
    public Consumer<DuelPhase> phaseSetter;
    public DuelPhase prevPhase;
    
    public ChangePhaseAction(ActionType actionType, DuelPhase phase)
    {
        super(actionType);
        this.phase = phase;
    }
    
    public ChangePhaseAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, DuelMessageUtility.decodePhase(buf));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodePhase(this.phase, buf);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        this.prevPhase = playField.getPhase();
        this.phaseSetter = playField::setPhase;
    }
    
    @Override
    public void doAction()
    {
        this.phaseSetter.accept(this.phase);
    }
    
    @Override
    public void undoAction()
    {
        this.phaseSetter.accept(this.prevPhase);
    }
    
    @Override
    public void redoAction()
    {
        this.doAction();
    }
}
