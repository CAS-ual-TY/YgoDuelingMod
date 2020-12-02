package de.cas_ual_ty.ydm.duel.action;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.duel.DuelPhase;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;

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
        this.turnSwapper = playField::endTurn;
        this.phaseSetter = playField::setPhase;
        this.phaseGetter = playField::getPhase;
    }
    
    @Override
    public void doAction()
    {
        this.prevPhase = this.phaseGetter.get();
        this.turnSwapper.run();
        this.nextPhase = this.phaseGetter.get();
    }
    
    @Override
    public void undoAction()
    {
        this.turnSwapper.run();
        this.phaseSetter.accept(this.prevPhase);
    }
    
    @Override
    public void redoAction()
    {
        this.turnSwapper.run();
        this.phaseSetter.accept(this.nextPhase);
    }
}
