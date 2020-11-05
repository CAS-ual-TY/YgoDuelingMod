package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.screen.widget.HandZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.InteractionWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.MonsterZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ViewCardStackWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ZoneWidget;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessages;
import de.cas_ual_ty.ydm.duelmanager.playfield.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelScreenDueling<E extends DuelContainer> extends DuelContainerScreen<E> implements IDuelScreenContext
{
    public static final ResourceLocation DUEL_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_foreground.png");
    public static final ResourceLocation DUEL_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_background.png");
    
    public static final int CARDS_WIDTH = 24;
    public static final int CARDS_HEIGHT = 32;
    
    protected ViewCardStackWidget viewCardStackWidget;
    protected Button scrollUpButton;
    protected Button scrollDownButton;
    
    protected ZoneWidget clickedZoneWidget;
    protected DuelCard clickedCard;
    
    protected List<ZoneWidget> zoneWidgets;
    protected List<InteractionWidget> interactionWidgets;
    
    protected ZoneOwner view;
    
    public DuelScreenDueling(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.xSize = 234;
        this.ySize = 250;
        this.interactionWidgets = new ArrayList<>(); // Need to temporarily initialize with placeholder this to make sure no clear() call gets NPEd
        this.viewCardStackWidget = null;
        this.clickedZoneWidget = null;
        this.clickedCard = null;
        this.view = this.getZoneOwner();
        if(this.view == ZoneOwner.NONE)
        {
            this.view = ZoneOwner.PLAYER1;
        }
    }
    
    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        
        this.zoneWidgets = new ArrayList<>(this.getDuelManager().getPlayField().getZones().size());
        this.interactionWidgets.clear();
        
        ZoneWidget widget;
        
        for(Zone zone : this.getDuelManager().getPlayField().getZones())
        {
            this.addButton(widget = this.createZoneWidgetForZone(zone));
            
            if(this.getPlayerRole() == ZoneOwner.PLAYER2.player)
            {
                widget.setPositionRelativeFlipped(zone.x, zone.y, width, height);
            }
            else
            {
                widget.setPositionRelative(zone.x, zone.y, width, height);
            }
            
            this.zoneWidgets.add(widget);
        }
        
        int maxWidth = (this.width - this.xSize) / 2;
        int maxHeight = this.height - 40;
        int x = this.width - maxWidth / 2;
        int y = this.height / 2;
        int cardsSize = 32;
        
        int centerY = height / 2 - 12;
        int chatHeight = Math.max(32, ((height - 5 * (20 + 4)) / cardsSize) * cardsSize);
        int chatWidth = Math.max(32, maxWidth - 8);
        
        int columns = chatWidth / cardsSize;
        int rows = chatHeight / cardsSize;
        
        y = centerY;
        int w = columns * 32;
        int h = rows * 32;
        
        this.initChat(width, height, centerY, chatWidth, chatHeight);
        
        ViewCardStackWidget previousViewStack = this.viewCardStackWidget;
        this.addButton(this.viewCardStackWidget = new ViewCardStackWidget(this, x - w / 2, y - h / 2, w, h, StringTextComponent.EMPTY, this::viewCardStackClicked, this::viewCardStackTooltip)
            .setRowsAndColumns(cardsSize, rows, columns));
        //TODO
        
        w = chatWidth;
        
        int verticalButtonsOff = 4;
        this.addButton(this.scrollUpButton = new Button(x - w / 2, y - h / 2 - 20 - verticalButtonsOff, w, 20, new StringTextComponent("Up"), this::scrollButtonClicked));
        this.addButton(this.scrollDownButton = new Button(x - w / 2, y + h / 2 + verticalButtonsOff, w, 20, new StringTextComponent("Down"), this::scrollButtonClicked));
        
        // in case we init again, buttons is cleared, thus all interaction widgets are removed
        // just act like we click on the last widget again
        if(this.clickedZoneWidget != null)
        {
            for(ZoneWidget match : this.zoneWidgets)
            {
                if(match.zone == this.clickedZoneWidget.zone)
                {
                    this.clickedZoneWidget = match;
                    break;
                }
            }
            
            this.clickedZoneWidget.hoverCard = this.clickedCard;
            this.zoneClicked(this.clickedZoneWidget);
        }
        
        if(previousViewStack != null && previousViewStack.getCards() != null)
        {
            this.viewCards(previousViewStack.getCards(), previousViewStack.getForceFaceUp());
        }
        
        this.updateButtonStatus();
    }
    
    protected ZoneWidget createZoneWidgetForZone(Zone zone)
    {
        if(zone.getType() == ZoneTypes.MONSTER ||
            zone.getType() == ZoneTypes.EXTRA_MONSTER_RIGHT ||
            zone.getType() == ZoneTypes.EXTRA_MONSTER_LEFT)
        {
            return new MonsterZoneWidget(zone, this, zone.width, zone.height, StringTextComponent.EMPTY, this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.HAND)
        {
            return new HandZoneWidget(zone, this, zone.width, zone.height, StringTextComponent.EMPTY, this::zoneClicked, this::zoneTooltip);
        }
        return new ZoneWidget(zone, this, zone.width, zone.height, StringTextComponent.EMPTY, this::zoneClicked, this::zoneTooltip);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(ms, partialTicks, mouseX, mouseY);
        
        ScreenUtil.white();
        this.minecraft.getTextureManager().bindTexture(DuelScreenDueling.DUEL_BACKGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.minecraft.getTextureManager().bindTexture(DuelScreenDueling.DUEL_FOREGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int x, int y)
    {
        
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(button == 1)
        {
            this.resetToNormalZoneWidgets();
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public void flip()
    {
        if(this.view == ZoneOwner.PLAYER1)
        {
            this.view = ZoneOwner.PLAYER2;
        }
        else
        {
            this.view = ZoneOwner.PLAYER1;
        }
        
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.flip(this.width, this.height);
        }
    }
    
    public void resetToNormalZoneWidgets()
    {
        this.removeClickedZone();
        this.removeInteractionWidgets();
        
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.active = true;
        }
        
        this.makeChatVisible();
    }
    
    protected void viewZone(ZoneWidget widget, boolean forceFaceUp)
    {
        this.viewCards(widget.zone.getCardsList(), forceFaceUp);
    }
    
    protected void viewCards(List<DuelCard> cards, boolean forceFaceUp)
    {
        this.viewCardStackWidget.activate(cards, forceFaceUp);
        this.updateButtonStatus();
        this.makeChatInvisible();
    }
    
    protected void updateButtonStatus()
    {
        this.scrollUpButton.active = false;
        this.scrollUpButton.visible = false;
        this.scrollDownButton.active = false;
        this.scrollDownButton.visible = false;
        
        if(this.viewCardStackWidget.active)
        {
            this.scrollUpButton.visible = true;
            this.scrollDownButton.visible = true;
            
            if(this.viewCardStackWidget.getCurrentRow() > 0)
            {
                this.scrollUpButton.active = true;
            }
            
            if(this.viewCardStackWidget.getCurrentRow() < this.viewCardStackWidget.getMaxRows())
            {
                this.scrollDownButton.active = true;
            }
        }
    }
    
    @Override
    public void viewZone(Zone zone)
    {
        for(ZoneWidget w : this.zoneWidgets)
        {
            if(w.zone == zone)
            {
                this.resetToNormalZoneWidgets();
                
                w.hoverCard = null;
                
                this.zoneClicked(w);
                this.viewZone(w, true);
                return;
            }
        }
    }
    
    @Override
    public void viewCards(Zone zone, List<DuelCard> cards)
    {
        for(ZoneWidget w : this.zoneWidgets)
        {
            if(w.zone == zone)
            {
                this.resetToNormalZoneWidgets();
                
                w.hoverCard = null;
                
                this.zoneClicked(w);
                this.viewCards(cards, true);
                return;
            }
        }
    }
    
    protected void zoneClicked(ZoneWidget widget)
    {
        if(!widget.active)
        {
            return;
        }
        
        ZoneOwner owner = this.getZoneOwner();
        
        if(owner == ZoneOwner.NONE)
        {
            return;
        }
        
        this.clickedZoneWidget = widget;
        this.clickedCard = widget.hoverCard;
        
        this.removeInteractionWidgets();
        
        this.interactionWidgets = new ArrayList<>();
        
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.addInteractionWidgets(owner, this.clickedZoneWidget.zone, this.clickedCard, this.getDuelManager(), this.interactionWidgets, this::interactionClicked, this::interactionTooltip);
            w.active = false;
        }
        
        this.buttons.addAll(this.interactionWidgets);
        this.children.addAll(this.interactionWidgets);
        
        if(!widget.zone.getType().getIsSecret())
        {
            this.viewZone(widget, owner == widget.zone.getOwner() && widget.zone.type.getShowFaceDownCardsToOwner());
        }
    }
    
    protected void interactionClicked(InteractionWidget widget)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestDuelAction(this.getDuelManager().headerFactory.get(), widget.interaction.action));
        this.resetToNormalZoneWidgets();
    }
    
    protected void viewCardStackClicked(ViewCardStackWidget widget)
    {
        ZoneWidget w = this.clickedZoneWidget;
        boolean forceFaceUp = widget.getForceFaceUp();
        
        if(w != null)
        {
            w.active = true;
            w.hoverCard = widget.hoverCard;
            this.zoneClicked(w);
            
            if(forceFaceUp)
            {
                widget.forceFaceUp();
            }
        }
    }
    
    protected void scrollButtonClicked(Button button)
    {
        if(this.viewCardStackWidget.active)
        {
            if(button == this.scrollUpButton)
            {
                this.viewCardStackWidget.decreaseCurrentRow();
            }
            else if(button == this.scrollDownButton)
            {
                this.viewCardStackWidget.increaseCurrentRow();
            }
        }
        
        this.updateButtonStatus();
    }
    
    protected void zoneTooltip(Widget w0, MatrixStack ms, int mouseX, int mouseY)
    {
        ZoneWidget w = (ZoneWidget)w0;
        this.renderTooltip(ms, new StringTextComponent(w.zone.getType().getRegistryName().getPath() + " " + w.zone.getOwner().name()), mouseX, mouseY);
    }
    
    protected void interactionTooltip(Widget w0, MatrixStack ms, int mouseX, int mouseY)
    {
        InteractionWidget w = (InteractionWidget)w0;
        this.renderTooltip(ms, new StringTextComponent(w.interaction.icon.getRegistryName().getPath()), mouseX, mouseY);
    }
    
    protected void viewCardStackTooltip(Widget w0, MatrixStack ms, int mouseX, int mouseY)
    {
    }
    
    protected void removeInteractionWidgets()
    {
        this.buttons.removeIf((w) -> w instanceof InteractionWidget);
        this.children.removeIf((w) -> w instanceof InteractionWidget);
    }
    
    protected void removeClickedZone()
    {
        this.clickedZoneWidget = null;
        this.clickedCard = null;
        this.viewCardStackWidget.deactivate();
        this.updateButtonStatus();
    }
    
    @Override
    public Zone getClickedZone()
    {
        return this.clickedZoneWidget != null ? this.clickedZoneWidget.zone : null;
    }
    
    @Override
    public DuelCard getClickedDuelCard()
    {
        return this.clickedCard;
    }
    
    @Override
    public ZoneOwner getView()
    {
        return this.view;
    }
    
    @Override
    public void renderCardInfo(MatrixStack ms, DuelCard card)
    {
        CardRenderUtil.renderCardInfo(ms, card.getCardHolder(), (this.width - this.xSize) / 2);
    }
    
    public static void renderSelectedRect(MatrixStack ms, int x, int y, int w, int h)
    {
        RenderSystem.disableDepthTest();
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 0, 0, 1F, 1F);
        RenderSystem.enableDepthTest();
    }
    
    public static void renderEnemySelectedRect(MatrixStack ms, int x, int y, int w, int h)
    {
        RenderSystem.disableDepthTest();
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 1F, 0, 0, 1F);
        RenderSystem.enableDepthTest();
    }
}
