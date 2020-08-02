package de.cas_ual_ty.ydm.duel;

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
    
    public static PlayerRole getFromIndex(int index)
    {
        return PlayerRole.VALUES[index];
    }
    
    static
    {
        int index = 0;
        for(PlayerRole playerRole : PlayerRole.VALUES)
        {
            playerRole.index = index++;
        }
    }
    
    public final Player player;
    private int index;
    
    private PlayerRole(Player player)
    {
        this.player = player;
    }
    
    public boolean hasAccess(Player player)
    {
        return player == this.player;
    }
    
    public int getIndex()
    {
        return this.index;
    }
}
