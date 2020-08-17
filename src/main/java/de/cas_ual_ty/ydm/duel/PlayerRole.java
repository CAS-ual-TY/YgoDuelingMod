package de.cas_ual_ty.ydm.duel;

import javax.annotation.Nullable;

public enum PlayerRole
{
    PLAYER1(Player.PLAYER1), PLAYER2(Player.PLAYER2), SPECTATOR(null), JUDGE(null)
    {
        @Override
        public boolean hasAccess(Player player)
        {
            return true;
        }
    };
    
    public static final PlayerRole[] VALUES = PlayerRole.values();
    
    public static PlayerRole getFromIndex(byte index)
    {
        return PlayerRole.VALUES[index];
    }
    
    static
    {
        byte index = 0;
        for(PlayerRole playerRole : PlayerRole.VALUES)
        {
            playerRole.index = index++;
        }
    }
    
    public final Player player;
    private byte index;
    
    private PlayerRole(@Nullable Player player)
    {
        this.player = player;
    }
    
    public boolean hasAccess(Player player)
    {
        return player == this.player;
    }
    
    public byte getIndex()
    {
        return this.index;
    }
}
