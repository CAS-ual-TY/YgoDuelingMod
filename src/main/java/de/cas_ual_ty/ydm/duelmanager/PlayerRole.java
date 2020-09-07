package de.cas_ual_ty.ydm.duelmanager;

public enum PlayerRole
{
    PLAYER1, PLAYER2, SPECTATOR, JUDGE;
    
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
    
    private byte index;
    
    public byte getIndex()
    {
        return this.index;
    }
}
