package de.cas_ual_ty.ydm.duel;

public enum ZoneOwner
{
    /*
     * offset player1: 0 (0-16)
     * offset player2: 17: (17-33)
     * - 1 hand
     * - 1 deck
     * - 5 spell/trap
     * - 1 extra deck
     * - 1 gy
     * - 5 monster
     * - 1 field spell
     * - 1 banished
     * - 1 extra
     * = 17
     * offset extra monsters: 32 (34-35)
     * - player 1 zones: 17
     * - player 2 zones: 17
     * = 34
     */
    
    PLAYER1(Player.PLAYER1, 0), PLAYER2(Player.PLAYER1, 17), NONE(null, PLAYER2.offset * 2);
    
    public static final int ZONES_PER_PLAYER = PLAYER2.offset;
    
    public final Player player;
    public final int offset;
    
    private ZoneOwner(Player player, int offset)
    {
        this.player = player;
        this.offset = offset;
    }
    
    public Player getPlayer()
    {
        return this.player;
    }
    
    public boolean hasAccess(PlayerRole player)
    {
        return this.getPlayer() == null || player.hasAccess(this.getPlayer());
    }
    
    public static int convertIndex(int index)
    {
        if(index >= ZoneOwner.ZONES_PER_PLAYER * 2)
        {
            return index;
        }
        
        if(index >= ZoneOwner.ZONES_PER_PLAYER)
        {
            return index - ZoneOwner.ZONES_PER_PLAYER;
        }
        else
        {
            return index + ZoneOwner.ZONES_PER_PLAYER;
        }
    }
}
