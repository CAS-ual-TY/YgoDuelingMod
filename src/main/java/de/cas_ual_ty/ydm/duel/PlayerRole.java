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
    
    public final Player player;
    
    private PlayerRole(Player player)
    {
        this.player = player;
    }
    
    public boolean hasAccess(Player player)
    {
        return player == this.player;
    }
}
