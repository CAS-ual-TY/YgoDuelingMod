package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.AttackAction;
import de.cas_ual_ty.ydm.duel.action.ChangePositionAction;
import de.cas_ual_ty.ydm.duel.action.ListAction;
import de.cas_ual_ty.ydm.duel.action.MoveAction;
import de.cas_ual_ty.ydm.duel.action.ShowCardAction;
import de.cas_ual_ty.ydm.duel.action.ShowZoneAction;
import de.cas_ual_ty.ydm.duel.action.ViewZoneAction;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.playfield.ZoneTypes;
import de.cas_ual_ty.ydm.duel.screen.animation.Animation;
import de.cas_ual_ty.ydm.duel.screen.animation.AttackAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.ListAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.MoveAnimation;
import de.cas_ual_ty.ydm.duel.screen.widget.AnimationsWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.HandZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.InteractionWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.MonsterZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ViewCardStackWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ZoneWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
    
    protected Button admitDefeatButton;
    protected Button offerDrawButton;
    
    protected ZoneOwner view;
    
    protected AnimationsWidget animationsWidget;
    
    public DuelScreenDueling(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.interactionWidgets = new ArrayList<>(); // Need to temporarily initialize with placeholder this to make sure no clear() call gets NPEd
        this.viewCardStackWidget = null;
        this.clickedZoneWidget = null;
        this.clickedCard = null;
        this.view = this.getZoneOwner();
        if(this.view == ZoneOwner.NONE)
        {
            this.view = ZoneOwner.PLAYER1;
        }
        this.animationsWidget = null;
    }
    
    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        
        final int cardsSize = 32;
        final int margin = 4;
        final int buttonHeight = 20;
        final int offset = buttonHeight + margin;
        
        int x = this.guiLeft + this.xSize + margin;
        int y = this.guiTop + margin;
        
        int maxWidth = Math.min(160, (this.width - this.xSize) / 2 - 2 * margin);
        int maxHeight = this.ySize - 2 * buttonHeight;
        
        int maxChatHeight = (maxHeight - 4 * (buttonHeight + margin));
        
        int chatWidth = Math.max(cardsSize, (maxWidth / cardsSize) * cardsSize);
        int chatHeight = Math.max(cardsSize, (maxChatHeight / cardsSize) * cardsSize);
        
        this.initChat(width, height, x, y, maxWidth, maxHeight, chatWidth, chatHeight, margin, buttonHeight);
        
        //buttons from top
        
        // addButton
        y += offset;
        
        this.addButton(this.scrollUpButton = new Button(x, y, maxWidth, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.up_arrow"), this::scrollButtonClicked));
        y += offset;
        
        int columns = chatWidth / cardsSize;
        int rows = chatHeight / cardsSize;
        ViewCardStackWidget previousViewStack = this.viewCardStackWidget;
        this.addButton(this.viewCardStackWidget = new ViewCardStackWidget(this, x + (maxWidth - chatWidth) / 2, y, chatWidth, chatHeight, StringTextComponent.EMPTY, this::viewCardStackClicked, this::viewCardStackTooltip)
            .setRowsAndColumns(cardsSize, rows, columns));
        y += chatHeight + margin;
        
        this.addButton(this.scrollDownButton = new Button(x, y, maxWidth, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.down_arrow"), this::scrollButtonClicked));
        y += offset;
        
        // butttons from bottom
        y = this.guiTop + this.ySize - margin - buttonHeight;
        
        this.addButton(this.admitDefeatButton = new Button(x, y, maxWidth, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.admit_defeat"), (b) -> this.admitDefeatClicked()));
        y -= offset;
        
        this.addButton(this.offerDrawButton = new Button(x, y, maxWidth, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.offer_draw"), (b) -> this.admitDefeatClicked()));
        y -= offset;
        
        this.admitDefeatButton.active = false;
        this.offerDrawButton.active = false; //TODO remove these
        
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
        
        this.zoneWidgets.sort((z1, z2) -> Byte.compare(z1.zone.index, z2.zone.index));
        
        if(this.animationsWidget != null)
        {
            this.animationsWidget.onInit();
        }
        this.addButton(this.animationsWidget = new AnimationsWidget(0, 0, 0, 0));
        
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
    
    @Override
    public void tick()
    {
        this.animationsWidget.tick();
        super.tick();
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
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
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
    
    @Override
    public void handleAction(Action action)
    {
        action.init(this.getDuelManager().getPlayField());
        
        if(action instanceof ViewZoneAction)
        {
            ViewZoneAction a = (ViewZoneAction)action;
            if(this.getZoneOwner() == a.sourceZone.getOwner())
            {
                this.viewZone(a.sourceZone);
            }
        }
        else if(action instanceof ShowZoneAction)
        {
            ShowZoneAction a = (ShowZoneAction)action;
            if(this.getZoneOwner() != a.sourceZone.getOwner())
            {
                this.viewZone(a.sourceZone);
            }
        }
        else if(action instanceof ShowCardAction)
        {
            ShowCardAction a = (ShowCardAction)action;
            if(this.getZoneOwner() != a.sourceZone.getOwner())
            {
                this.viewCards(a.sourceZone, ImmutableList.of(a.card));
            }
        }
        
        Animation animation = this.getAnimationForAction(action);
        
        if(animation != null)
        {
            this.playAnimation(animation);
        }
        else
        {
            action.doAction();
        }
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
    
    protected ZoneWidget getZoneWidget(Zone zone)
    {
        return this.zoneWidgets.get(zone.index);
    }
    
    protected void playAnimation(Animation a)
    {
        if(a != null)
        {
            this.animationsWidget.addAnimation(a);
        }
    }
    
    @Nullable
    public Animation getAnimationForAction(Action action0)
    {
        if(action0 instanceof MoveAction)
        {
            MoveAction action = (MoveAction)action0;
            
            CardPosition sourcePosition = action.sourceCardPosition;
            
            if(!sourcePosition.isFaceUp && action.sourceZone.getOwner() == this.getZoneOwner() && action.sourceZone.type.getShowFaceDownCardsToOwner())
            {
                sourcePosition = sourcePosition.flip();
            }
            
            CardPosition destinationPosition = action.destinationCardPosition;
            
            if(!destinationPosition.isFaceUp && action.destinationZone.getOwner() == this.getZoneOwner() && action.destinationZone.type.getShowFaceDownCardsToOwner())
            {
                destinationPosition = destinationPosition.flip();
            }
            
            return new MoveAnimation(
                this.getView(),
                action.card,
                this.getZoneWidget(action.sourceZone),
                this.getZoneWidget(action.destinationZone),
                sourcePosition,
                destinationPosition)
                    .setOnStart(action::removeCardFromZone)
                    .setOnEnd(() ->
                    {
                        action.addCard();
                        action.finish();
                    });
        }
        else if(action0 instanceof ChangePositionAction)
        {
            ChangePositionAction action = (ChangePositionAction)action0;
            
            if(action.card == action.sourceZone.getTopCardSafely())
            {
                ZoneOwner owner = action.sourceZone.getOwner();
                
                return new MoveAnimation(
                    this.getView(),
                    action.card,
                    this.getZoneWidget(action.sourceZone),
                    this.getZoneWidget(action.sourceZone),
                    action.sourceCardPosition,
                    action.destinationCardPosition)
                        .setOnStart(() ->
                        {
                            action.removeCardFromZone();
                        })
                        .setOnEnd(() ->
                        {
                            action.sourceZone.addCard(owner, action.card, action.sourceCardIndex);
                            action.sourceZone.getCard(action.sourceCardIndex).setPosition(action.destinationCardPosition);
                        });
            }
        }
        else if(action0 instanceof ListAction)
        {
            ListAction action = (ListAction)action0;
            
            if(!action.actions.isEmpty())
            {
                List<Animation> animations = new ArrayList<>(action.actions.size());
                
                Animation animation;
                for(Action a : action.actions)
                {
                    animation = this.getAnimationForAction(a);
                    
                    if(animation != null)
                    {
                        animations.add(animation);
                    }
                }
                
                return new ListAnimation(animations);
            }
        }
        else if(action0 instanceof AttackAction)
        {
            AttackAction action = (AttackAction)action0;
            
            return new AttackAnimation(this.getView(), this.getZoneWidget(action.sourceZone), this.getZoneWidget(action.attackedZone));
        }
        
        return null;
    }
    
    protected void viewZone(Zone zone)
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
    
    protected void viewCards(Zone zone, List<DuelCard> cards)
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
    
    protected void admitDefeatClicked()
    {
    }
    
    protected void offerDrawClicked()
    {
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
    
    public static void renderSelectedRect(MatrixStack ms, float x, float y, float w, float h)
    {
        RenderSystem.disableDepthTest();
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 0, 0, 1F, 1F);
        RenderSystem.enableDepthTest();
    }
    
    public static void renderEnemySelectedRect(MatrixStack ms, float x, float y, float w, float h)
    {
        RenderSystem.disableDepthTest();
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 1F, 0, 0, 1F);
        RenderSystem.enableDepthTest();
    }
}
