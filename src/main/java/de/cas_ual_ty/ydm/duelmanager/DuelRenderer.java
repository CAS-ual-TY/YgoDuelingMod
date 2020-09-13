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
        this.updateZoneCoordinates();
        this.updateZoneSizes();
        
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
        
        this.updateZoneCoordinates();
    }
    
    protected void updateZoneCoordinates()
    {
        // From ZoneOwner class
        
        /*
         * offset player1: 0 (0-16)
         * offset player2: 17: (17-33)
         * - 1 hand
         * - 1 deck
         * - 5 spell/trap (right to left)
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
        
        this.zones[0].setCoords(33, 211); // hand
        this.zones[1].setCoords(203, 177); // deck
        this.zones[2].setCoords(169, 177); // s/t right
        this.zones[3].setCoords(135, 177); // ...
        this.zones[4].setCoords(101, 177); // s/t middle
        this.zones[5].setCoords(67, 177); // ...
        this.zones[6].setCoords(33, 177); // s/t left
        this.zones[7].setCoords(7, 177); // exra deck
        this.zones[8].setCoords(203, 143); // gy
        this.zones[9].setCoords(169, 143); // monster right
        this.zones[10].setCoords(135, 143); // ...
        this.zones[11].setCoords(101, 143); // monster middle
        this.zones[12].setCoords(67, 143); // ...
        this.zones[13].setCoords(33, 143); // monster left
        this.zones[14].setCoords(7, 143); // field spell
        this.zones[15].setCoords(203, 109); // banished
        this.zones[16].setCoords(7, 211); // extra
        
        this.zones[17].setCoords(7, 7); // hand
        this.zones[18].setCoords(7, 41); // deck
        this.zones[19].setCoords(33, 41); // s/t left (opponent's right)
        this.zones[20].setCoords(67, 41); // ...
        this.zones[21].setCoords(101, 41); // s/t middle
        this.zones[22].setCoords(135, 41); // ...
        this.zones[23].setCoords(169, 41); // s/t right
        this.zones[24].setCoords(203, 41); // exra deck
        this.zones[25].setCoords(7, 75); // gy
        this.zones[26].setCoords(33, 75); // monster left
        this.zones[27].setCoords(67, 75); // ...
        this.zones[28].setCoords(101, 75); // monster middle
        this.zones[29].setCoords(135, 75); // ...
        this.zones[30].setCoords(169, 75); // monster right
        this.zones[31].setCoords(203, 75); // field spell
        this.zones[32].setCoords(7, 109); // banished
        this.zones[33].setCoords(203, 7); // extra
        
        this.zones[34].setCoords(67, 109); // left extra monster (opponent's right)
        this.zones[35].setCoords(135, 109); // right extra monster
    }
    
    protected void updateZoneSizes()
    {
        for(ZoneWrapper zone : this.zones)
        {
            if(zone.zone.getType() == ZoneType.HAND)
            {
                zone.setSizes(194, 32);
            }
            else if(zone.zone.getType() == ZoneType.DECK
                || zone.zone.getType() == ZoneType.EXTRA_DECK
                || zone.zone.getType() == ZoneType.GRAVEYARD
                || zone.zone.getType() == ZoneType.FIELD_SPELL
                || zone.zone.getType() == ZoneType.BANISHED
                || zone.zone.getType() == ZoneType.EXTRA)
            {
                zone.setSizes(24, 32);
            }
            else /*if(zone.zone.getType() == ZoneType.SPELL_TRAP
                 || zone.zone.getType() == ZoneType.MONSTER
                 || zone.zone.getType() == ZoneType.EXTRA_MONSTER)*/
            {
                zone.setSizes(32, 32);
            }
        }
    }
    
    // must be called every render tick
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        // TODO
        
        for(ZoneWrapper zone : this.zones)
        {
            if(zone.isMouseOver(mouseX, mouseY))
            {
                this.provider.renderHoverRect(zone.x, zone.y, zone.width, zone.height);
                break;
            }
        }
    }
    
    public static class ZoneWrapper
    {
        public final Zone zone;
        
        public int x;
        public int y;
        public int width;
        public int height;
        
        public ZoneWrapper(Zone zone)
        {
            this.zone = zone;
        }
        
        public void setCoords(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        
        public void setSizes(int width, int height)
        {
            this.width = width;
            this.height = height;
        }
        
        public void render(DuelRenderingProvider provider, float partial)
        {
        }
        
        public boolean isMouseOver(int mouseX, int mouseY)
        {
            return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }
}
