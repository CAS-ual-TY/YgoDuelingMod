package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.DuelPhase;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EndTurnAction extends Action
{
    public Runnable turnSwapper;
    public Consumer<DuelPhase> phaseSetter;
    public Supplier<DuelPhase> phaseGetter;
    
    public DuelPhase nextPhase;
    public DuelPhase prevPhase;
    
    public EndTurnAction(ActionType actionType)
    {
        super(actionType);
    }
    
    public EndTurnAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        turnSwapper = playField::endTurn;
        phaseSetter = playField::setPhase;
        phaseGetter = playField::getPhase;
    }
    
    @Override
    public void doAction()
    {
        prevPhase = phaseGetter.get();
        turnSwapper.run();
        nextPhase = phaseGetter.get();
    }
    
    @Override
    public void undoAction()
    {
        turnSwapper.run();
        phaseSetter.accept(prevPhase);
    }
    
    @Override
    public void redoAction()
    {
        turnSwapper.run();
        phaseSetter.accept(nextPhase);
    }
}
