package de.cas_ual_ty.ydm.duelmanager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duelmanager.action.ActionIcon;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneInteraction;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneOwner;
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
    
    public ZoneWrapper mouseClickedZone;
    public DuelCard mouseClickedCard;
    
    public DuelRenderer(DuelRenderingProvider provider, DuelManager manager)
    {
        this.provider = provider;
        this.manager = manager;
        
        this.zones = new ZoneWrapper[manager.getPlayField().zones.size()];
        
        Zone zone;
        ZoneWrapper wrapper;
        for(byte i = 0; i < this.zones.length; ++i)
        {
            zone = manager.getPlayField().getZone(i);
            wrapper = new ZoneWrapper(zone, this, this.getZoneAllegiance(zone));
            
            wrapper.setCoords(zone.x, zone.y);
            wrapper.setSizes(zone.width, zone.height);
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
        this.mouseClickedZone = null;
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
        
        for(ZoneWrapper wrapper : this.zones)
        {
            wrapper.setCoords(wrapper.x + wrapper.width / 2, wrapper.y + wrapper.height / 2); // moving to top left corner
            wrapper.setCoords(-wrapper.x, -wrapper.y);
            wrapper.setCoords(wrapper.x - wrapper.width / 2, wrapper.y - wrapper.height / 2); // moving to top left corner
        }
    }
    
    // must be called every render tick
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        this.mouseHoverZone = null;
        this.mouseHoverCard = null;
        
        for(ZoneWrapper zone : this.zones)
        {
            // TODO
            //            ClientProxy.drawRect(ms, zone.x, zone.y, zone.width, zone.height, 1F, 0F, 0F, 0.5F);
            
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
        
        if(this.mouseClickedZone != null)
        {
            this.provider.renderLineRect(ms, this.mouseClickedZone.x, this.mouseClickedZone.y, this.mouseClickedZone.width, this.mouseClickedZone.height, 2, 0, 0, 1F, 1F);
        }
    }
    
    public void mouseClick(double mouseX, double mouseY)
    {
        if(this.mouseHoverZone == null)
        {
            this.mouseClickedZone = null;
            this.mouseClickedCard = null;
            
            this.resetInteractions();
        }
        else if(this.mouseClickedZone == null)
        {
            this.mouseClickedZone = this.mouseHoverZone; //not null
            this.mouseClickedCard = this.mouseHoverCard;
            
            this.populateInteractions();
        }
        else
        {
            //mouse clicked zone not null
            //mouse hover zone not null
            // => do action
            
            ZoneInteraction interaction = this.mouseHoverZone.getHoverInteraction(mouseX, mouseY);
            
            if(interaction != null)
            {
                this.doInteraction(interaction);
            }
            this.resetInteractions();
            
            this.mouseClickedZone = null;
            this.mouseClickedCard = null;
        }
    }
    
    public void doInteraction(ZoneInteraction interaction)
    {
        this.provider.sendActionToServer(interaction.action);
    }
    
    public void resetInteractions()
    {
        for(ZoneWrapper z : this.zones)
        {
            z.clearInteractions();
        }
    }
    
    public void populateInteractions()
    {
        for(ZoneWrapper z : this.zones)
        {
            z.setInteractions(this.manager.getActionsFor(this.activeView, this.mouseClickedZone.zone, this.mouseClickedCard, z.zone));
        }
    }
    
    public PlayerRole getRole()
    {
        return this.provider.getPlayerRole();
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
        
        public List<ZoneInteractionWrapper> interactions;
        
        public int x;
        public int y;
        public int width;
        public int height;
        
        public ZoneWrapper(Zone zone, DuelRenderer renderer, boolean isOpponent)
        {
            this.zone = zone;
            this.renderer = renderer;
            this.isOpponent = isOpponent;
            this.interactions = null;
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
        
        public void clearInteractions()
        {
            this.interactions = null;
        }
        
        public void setInteractions(List<ZoneInteraction> interactions)
        {
            this.interactions = new ArrayList<>(interactions.size());
            
            if(interactions.size() == 1)
            {
                this.interactions.add(new ZoneInteractionWrapper(interactions.get(0), this.x, this.y, this.width, this.height));
            }
        }
        
        public void render(MatrixStack ms, DuelRenderingProvider provider, float partial)
        {
            boolean forceFaceUp = this.zone.getType().getShowFaceDownCardsToOwner() && (this.zone.getOwner().player == this.renderer.getRole());
            
            if(this.zone.getType().getRenderCardsSpread() && this.zone.getCardsAmount() > 1)
            {
                int totalW = this.zone.getCardsAmount() * ZoneWrapper.SINGLE_CARDS_WIDTH + (this.zone.getCardsAmount() - 1) * ZoneWrapper.SPREAD_CARDS_MARGIN;
                
                if(totalW <= this.width)
                {
                    int left = this.x + (this.width - totalW) / 2;
                    
                    if(!this.isOpponent)
                    {
                        for(short i = 0; i < this.zone.getCardsAmount(); ++i)
                        {
                            this.renderer.provider.renderCardCentered(ms, left, this.y, this.height, this.height, this.zone.getCard(i), forceFaceUp);
                            left += ZoneWrapper.SINGLE_CARDS_WIDTH + ZoneWrapper.SPREAD_CARDS_MARGIN;
                        }
                    }
                    else
                    {
                        for(short i = (short)(this.zone.getCardsAmount() - 1); i >= 0; --i)
                        {
                            this.renderer.provider.renderCardReversedCentered(ms, left, this.y, this.height, this.height, this.zone.getCard(i), forceFaceUp);
                            left += ZoneWrapper.SINGLE_CARDS_WIDTH + ZoneWrapper.SPREAD_CARDS_MARGIN;
                        }
                    }
                }
                else
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
                    
                    if(!this.isOpponent)
                    {
                        for(short i = 0; i < this.zone.getCardsAmount(); ++i)
                        {
                            x1 = left + MathHelper.ceil(i * area);
                            this.renderer.provider.renderCardCentered(ms, this.x + x1, this.y, this.height, this.height, this.zone.getCard(i), forceFaceUp);
                        }
                    }
                    else
                    {
                        for(short i = (short)(this.zone.getCardsAmount() - 1); i >= 0; --i)
                        {
                            x1 = left + MathHelper.ceil(i * area);
                            this.renderer.provider.renderCardReversedCentered(ms, this.x + x1, this.y, this.height, this.height, this.zone.getCard(i), forceFaceUp);
                        }
                    }
                }
            }
            else if(this.zone.getCardsAmount() > 0)
            {
                DuelCard card = this.zone.getTopCard();
                
                if(!this.isOpponent)
                {
                    this.renderer.provider.renderCardCentered(ms, this.x, this.y, this.width, this.height, card, forceFaceUp);
                }
                else
                {
                    this.renderer.provider.renderCardReversedCentered(ms, this.x, this.y, this.width, this.height, card, forceFaceUp);
                }
            }
            
            if(this.interactions != null)
            {
                ActionIcon icon;
                
                int iconWidth;
                int iconHeight;
                
                for(ZoneInteractionWrapper w : this.interactions)
                {
                    icon = w.zoneInteraction.icon;
                    
                    iconWidth = icon.iconWidth;
                    iconHeight = icon.iconHeight;
                    
                    if(iconHeight >= w.height)
                    {
                        iconWidth = (int)((double)(w.height * iconWidth) / (double)iconHeight);
                        iconHeight = w.height;
                    }
                    
                    if(iconWidth >= w.width)
                    {
                        iconHeight = (int)((double)(w.width * iconHeight) / (double)iconWidth);
                        iconWidth = w.width;
                    }
                    
                    this.renderer.provider.renderAction(ms, w.x + (w.width - iconWidth) / 2, w.y + (w.height - iconHeight) / 2, iconWidth, iconHeight, icon);
                }
                
                /*
                if(this.interactions.size() == 1)
                {
                    
                    
                    ZoneInteraction interaction = this.interactions.get(0).zoneInteraction;
                    this.renderer.provider.renderAction(ms, this.x + (this.width - interaction.icon.iconWidth) / 2, this.y + (this.height - interaction.icon.iconHeight) / 2, interaction.icon.iconWidth, interaction.icon.iconHeight, interaction.icon);
                }
                else if(this.interactions.size() == 2)
                {
                    this.renderer.provider.renderAction(ms, this.x, this.y, this.width, this.height / 2, this.interactions.get(0).zoneInteraction.icon);
                    this.renderer.provider.renderAction(ms, this.x, this.y + this.height / 2, this.width, this.height / 2, this.interactions.get(1).zoneInteraction.icon);
                }
                */
            }
        }
        
        @Nullable
        public DuelCard getHoverCard(int mouseX, int mouseY)
        {
            if(this.zone.getType().getRenderCardsSpread())
            {
                return null; //TODO
            }
            else
            {
                return this.zone.getTopCardSafely();
            }
        }
        
        @Nullable
        public ZoneInteraction getHoverInteraction(double mouseX, double mouseY)
        {
            for(ZoneInteractionWrapper w : this.interactions)
            {
                if(w.isMouseOver(mouseX, mouseY))
                {
                    return w.zoneInteraction;
                }
            }
            
            return null;
        }
        
        public boolean isMouseOver(double mouseX, double mouseY)
        {
            return DuelRenderer.isMouseOver(mouseX, mouseY, this.x, this.y, this.width, this.height);
        }
    }
    
    public static class ZoneInteractionWrapper
    {
        public ZoneInteraction zoneInteraction;
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        
        public ZoneInteractionWrapper(ZoneInteraction zoneInteraction, int x, int y, int width, int height)
        {
            this.zoneInteraction = zoneInteraction;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public boolean isMouseOver(double mouseX, double mouseY)
        {
            return DuelRenderer.isMouseOver(mouseX, mouseY, this.x, this.y, this.width, this.height);
        }
    }
    
    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
