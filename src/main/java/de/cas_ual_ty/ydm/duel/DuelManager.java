package de.cas_ual_ty.ydm.duel;

import net.minecraft.entity.player.PlayerEntity;

public class DuelManager
{
    public DuelState duelState;
    
    public int player1Id;
    public int player2Id;
    
    public PlayerEntity player1;
    public PlayerEntity player2;
    
    public PlayField playField;
    
    public void reset()
    {
        this.duelState = DuelState.IDLE;
        this.player1Id = -1;
        this.player2Id = -1;
        this.player1 = null;
        this.player2 = null;
        this.playField = new PlayField(this);
    }
}
