package de.cas_ual_ty.ydm.duelmanager;

import javax.annotation.Nullable;

import net.minecraft.util.math.MathHelper;

public class DuelRenderer
{
    public final DuelRenderingProvider provider;
    public final DuelManager manager;
    public final ZoneWrapper[] zones;
    
    // for 4-tick animations
    // values from 0-3, incrementing every tick
    public int animationPhase;
    
    // save this to render the cards in the link zones the correct way
    private PlayerRole activeView;
    
    public ZoneWrapper mouseHoverZone;
    public DuelCard mouseHoverCard;
    
    public DuelRenderer(DuelRenderingProvider provider, DuelManager manager)
    {
        this.provider = provider;
        this.manager = manager;
        
        this.zones = new ZoneWrapper[manager.getPlayField().zones.length];
        
        Zone zone;
        for(byte i = 0; i < this.zones.length; ++i)
        {
            zone = manager.getPlayField().getZone(i);
            this.zones[i] = new ZoneWrapper(zone, this, this.renderZoneCardsSpread(zone.getType()), this.getZoneAllegiance(zone));
        }
        
        this.activeView = PlayerRole.PLAYER1;
        this.updateZoneCoordinates();
        this.updateZoneSizes();
        
        if(this.provider.getPlayerRole() == PlayerRole.PLAYER2)
        {
            this.forceFlipBoard();
        }
        
        this.animationPhase = 0;
        
        this.mouseHoverZone = null;
        this.mouseHoverCard = null;
    }
    
    private boolean renderZoneCardsSpread(ZoneType type)
    {
        return type == ZoneType.HAND || type == ZoneType.MONSTER;
    }
    
    private Boolean getZoneAllegiance(Zone zone)
    {
        return true;
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
        
        this.mouseHoverZone = null;
        this.mouseHoverCard = null;
        
        for(ZoneWrapper zone : this.zones)
        {
            zone.render(this.provider, partialTicks);
            
            if(zone.isMouseOver(mouseX, mouseY))
            {
                this.mouseHoverZone = zone;
            }
        }
        
        if(this.mouseHoverZone != null)
        {
            this.provider.renderHoverRect(this.mouseHoverZone.x, this.mouseHoverZone.y, this.mouseHoverZone.width, this.mouseHoverZone.height);
            
            this.mouseHoverCard = this.mouseHoverZone.getHoverCard(mouseX, mouseY);
        }
    }
    
    // must be called every tick
    public void tick()
    {
        this.animationPhase = (this.animationPhase + 1) % 4;
    }
    
    public static class ZoneWrapper
    {
        public static final int SINGLE_CARDS_HEIGHT = 28;
        public static final int SINGLE_CARDS_WIDTH = 20;
        
        public static final int SPREAD_CARDS_MARGIN = 2;
        
        public final Zone zone;
        public final DuelRenderer renderer;
        public final boolean renderCardsSpread;
        public boolean isOpponent;
        
        public int x;
        public int y;
        public int width;
        public int height;
        
        public ZoneWrapper(Zone zone, DuelRenderer renderer, boolean renderCardsSpread, boolean isOpponent)
        {
            this.zone = zone;
            this.renderer = renderer;
            this.renderCardsSpread = renderCardsSpread;
            this.isOpponent = isOpponent;
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
            if(this.renderCardsSpread && this.zone.getCardsAmount() > 1)
            {
                int left = ZoneWrapper.SPREAD_CARDS_MARGIN;
                int right = this.width - ZoneWrapper.SPREAD_CARDS_MARGIN - ZoneWrapper.SINGLE_CARDS_WIDTH;
                float area = (right - left) / (this.zone.getCardsAmount() - 1);
                
                // most left = 2
                // most right = 10
                
                // two:
                // 2 = 2 + 0 * 8
                // 10 = 2 + 1 * 8
                // 8 / (n-1)
                
                // three:
                // 2 = 2 + 0 * 4
                // 6 = 2 + 1 * 4
                // 10 = 2 + 2 * 4
                // 8 / (n-1)
                
                // four:
                // 2 + 0 * 2.66
                // 2 + 1 * 2.66 -> 4.66 -> 6
                // 2 + 2 * 2.66 -> 7.33 -> 8
                // 2 + 3 * 2.66 -> 10
                // 8 / (n-1)
                
                // five:
                // 2 = 2 + 0 * 2
                // 4 = 2 + 1 * 2
                // 6 = 2 + 2 * 2
                // 8 = 2 + 3 * 2
                // 10 = 2 + 4 * 2
                // 8 / (n-1)
                
                // ...
                
                int x1;
                
                for(short i = 0; i < this.zone.getCardsAmount(); ++i)
                {
                    x1 = left + MathHelper.ceil(i * area);
                    
                    if(!this.isOpponent)
                    {
                        this.renderer.provider.renderCardCentered(this.x + x1, this.y, this.width, this.height, this.zone.getCard(i));
                    }
                    else
                    {
                        this.renderer.provider.renderCardReversedCentered(this.x + x1, this.y, this.width, this.height, this.zone.getCard(i));
                    }
                }
            }
            else if(this.zone.getCardsAmount() > 0)
            {
                DuelCard card = this.zone.getTopCard();
                
                if(!this.isOpponent)
                {
                    this.renderer.provider.renderCardCentered(this.x, this.y, this.width, this.height, card);
                }
                else
                {
                    this.renderer.provider.renderCardReversedCentered(this.x, this.y, this.width, this.height, card);
                }
            }
        }
        
        @Nullable
        public DuelCard getHoverCard(int mouseX, int mouseY)
        {
            return null;
        }
        
        public boolean isMouseOver(int mouseX, int mouseY)
        {
            return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }
}
