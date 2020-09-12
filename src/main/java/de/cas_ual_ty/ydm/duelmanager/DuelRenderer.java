package de.cas_ual_ty.ydm.duelmanager;

public class DuelRenderer
{
    public final DuelRenderingProvider provider;
    public final DuelManager manager;
    public final ZoneWrapper[] zones;
    
    // save this to render the cards in the link zones the correct way
    private PlayerRole activeView;
    
    public DuelRenderer(DuelRenderingProvider provider, DuelManager manager)
    {
        this.provider = provider;
        this.manager = manager;
        
        this.zones = new ZoneWrapper[manager.getPlayField().zones.length];
        for(byte i = 0; i < this.zones.length; ++i)
        {
            this.zones[i] = new ZoneWrapper(manager.getPlayField().getZone(i));
        }
        
        this.activeView = PlayerRole.PLAYER1;
        
        if(this.provider.getPlayerRole() == PlayerRole.PLAYER2)
        {
            this.forceFlipBoard();
        }
    }
    
    // can be called any time, eg. via button
    public void flipBoard()
    {
        // dont allow it for players
        if(this.provider.getPlayerRole() != PlayerRole.PLAYER1 && this.provider.getPlayerRole() != PlayerRole.PLAYER2)
        {
            this.forceFlipBoard();
        }
    }
    
    // swaps player zones from top to bottom, also swaps both link zones
    private void forceFlipBoard()
    {
        if(this.activeView == PlayerRole.PLAYER1)
        {
            this.activeView = PlayerRole.PLAYER2;
        }
        else
        {
            this.activeView = PlayerRole.PLAYER1;
        }
        
        ZoneWrapper z;
        
        // swap player zones vertically (actually: point symmetrically, not vertically)
        for(int i = ZoneOwner.PLAYER1.offset, j = ZoneOwner.PLAYER2.offset; i < ZoneOwner.PLAYER2.offset; ++i, ++j)
        {
            z = this.zones[i];
            this.zones[i] = this.zones[j];
            this.zones[j] = z;
        }
        
        // swap link zones horizontally
        for(int i = ZoneOwner.NONE.offset, j = this.zones.length - 1; i < this.zones.length; ++i, --j)
        {
            z = this.zones[i];
            this.zones[i] = this.zones[j];
            this.zones[j] = z;
            
            if(i >= j)
            {
                break;
            }
        }
    }
    
    // must be called every render tick
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        
    }
    
    public static class ZoneWrapper
    {
        public final Zone zone;
        
        public ZoneWrapper(Zone zone)
        {
            this.zone = zone;
        }
    }
}
