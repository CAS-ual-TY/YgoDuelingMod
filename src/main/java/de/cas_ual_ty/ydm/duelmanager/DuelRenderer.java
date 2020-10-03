package de.cas_ual_ty.ydm.duelmanager;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duelmanager.playfield.PlayField;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneType;
import net.minecraft.util.math.MathHelper;

public class DuelRenderer
{
    public final DuelRenderingProvider provider;
    public final DuelManager manager;
    public final ZoneWrapper[] zones;
    
    // for 4-tick animations
    // values from 0-3, incrementing every tick
    public int animationPhase;
    
    private ZoneOwner activeView;
    
    public ZoneWrapper mouseHoverZone;
    public DuelCard mouseHoverCard;
    
    public DuelRenderer(DuelRenderingProvider provider, DuelManager manager)
    {
        this.provider = provider;
        this.manager = manager;
        
        this.zones = new ZoneWrapper[manager.getPlayField().zones.size()];
        
        Zone zone;
        ZoneType type;
        ZoneWrapper wrapper;
        for(byte i = 0; i < this.zones.length; ++i)
        {
            zone = manager.getPlayField().getZone(i);
            type = zone.getType();
            wrapper = new ZoneWrapper(zone, this, this.getZoneAllegiance(zone));
            
            wrapper.setCoords(type.getX() + type.getXOffsetForChild(zone.childIndex), type.getY());
            
            if(zone.getOwner() == ZoneOwner.PLAYER2)
            {
                wrapper.setCoords(-wrapper.x, -wrapper.y);
            }
            
            wrapper.setSizes(type.getWidth(), type.getHeight());
            wrapper.setCoords(wrapper.x - wrapper.width / 2, wrapper.y - wrapper.height / 2); // moving to top left corner
            
            this.zones[i] = wrapper;
        }
        
        this.activeView = ZoneOwner.PLAYER1;
        
        if(this.provider.getPlayerRole() == ZoneOwner.PLAYER2.getPlayer())
        {
            this.forceFlipBoard();
        }
        
        this.animationPhase = 0;
        
        this.mouseHoverZone = null;
        this.mouseHoverCard = null;
    }
    
    private boolean getZoneAllegiance(Zone zone)
    {
        return zone.getOwner() == this.activeView;
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
        if(this.activeView == ZoneOwner.PLAYER1)
        {
            this.activeView = ZoneOwner.PLAYER2;
        }
        else
        {
            this.activeView = ZoneOwner.PLAYER1;
        }
        
        PlayField pf = this.manager.getPlayField();
        ZoneWrapper z;
        
        // swap player zones vertically (actually: point symmetrically, not vertically)
        for(int i = pf.player1Offset, j = pf.player2Offset; i < pf.player2Offset; ++i, ++j)
        {
            z = this.zones[i];
            this.zones[i] = this.zones[j];
            this.zones[j] = z;
        }
        
        // swap link zones horizontally
        for(int i = pf.extraOffset, j = this.zones.length - 1; i < this.zones.length; ++i, --j)
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
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        // TODO
        
        this.mouseHoverZone = null;
        this.mouseHoverCard = null;
        
        for(ZoneWrapper zone : this.zones)
        {
            zone.render(ms, this.provider, partialTicks);
            
            if(zone.isMouseOver(mouseX, mouseY))
            {
                this.mouseHoverZone = zone;
            }
        }
        
        if(this.mouseHoverZone != null)
        {
            this.provider.renderHoverRect(ms, this.mouseHoverZone.x, this.mouseHoverZone.y, this.mouseHoverZone.width, this.mouseHoverZone.height);
            
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
        public boolean isOpponent;
        
        public int x;
        public int y;
        public int width;
        public int height;
        
        public ZoneWrapper(Zone zone, DuelRenderer renderer, boolean isOpponent)
        {
            this.zone = zone;
            this.renderer = renderer;
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
        
        public void render(MatrixStack ms, DuelRenderingProvider provider, float partial)
        {
            if(this.zone.getType().getRenderCardsSpread() && this.zone.getCardsAmount() > 1)
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
                        this.renderer.provider.renderCardCentered(ms, this.x + x1, this.y, this.width, this.height, this.zone.getCard(i));
                    }
                    else
                    {
                        this.renderer.provider.renderCardReversedCentered(ms, this.x + x1, this.y, this.width, this.height, this.zone.getCard(i));
                    }
                }
            }
            else if(this.zone.getCardsAmount() > 0)
            {
                DuelCard card = this.zone.getTopCard();
                
                if(!this.isOpponent)
                {
                    this.renderer.provider.renderCardCentered(ms, this.x, this.y, this.width, this.height, card);
                }
                else
                {
                    this.renderer.provider.renderCardReversedCentered(ms, this.x, this.y, this.width, this.height, card);
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
