package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionTypes;
import de.cas_ual_ty.ydm.duel.action.AttackAction;
import de.cas_ual_ty.ydm.duel.action.ChangeLPAction;
import de.cas_ual_ty.ydm.duel.action.ChangePositionAction;
import de.cas_ual_ty.ydm.duel.action.IAnnouncedAction;
import de.cas_ual_ty.ydm.duel.action.ListAction;
import de.cas_ual_ty.ydm.duel.action.MoveAction;
import de.cas_ual_ty.ydm.duel.action.MoveTopAction;
import de.cas_ual_ty.ydm.duel.action.ShowCardAction;
import de.cas_ual_ty.ydm.duel.action.ShowZoneAction;
import de.cas_ual_ty.ydm.duel.action.ShuffleAction;
import de.cas_ual_ty.ydm.duel.action.ViewZoneAction;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.playfield.ZoneType;
import de.cas_ual_ty.ydm.duel.playfield.ZoneTypes;
import de.cas_ual_ty.ydm.duel.screen.animation.Animation;
import de.cas_ual_ty.ydm.duel.screen.animation.AttackAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.MoveAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.ParallelListAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.QueueAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.SpecialSummonAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.SpecialSummonOverlayAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.TextAnimation;
import de.cas_ual_ty.ydm.duel.screen.widget.AnimationsWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.HandZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ImprovedButton;
import de.cas_ual_ty.ydm.duel.screen.widget.InteractionWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.LPTextFieldWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.LifePointsWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.MonsterZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.NonSecretStackZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.TextWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ViewCardStackWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ZoneWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelScreenDueling<E extends DuelContainer> extends DuelContainerScreen<E> implements IDuelScreenContext
{
    public static final int CARDS_WIDTH = 24;
    public static final int CARDS_HEIGHT = 32;
    
    protected TextWidget cardStackNameWidget;
    protected ITextComponent nameShown;
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
    
    protected TextFieldWidget lifePointsWidget;
    
    public DuelScreenDueling(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.interactionWidgets = new ArrayList<>(); // Need to temporarily initialize with placeholder this to make sure no clear() call gets NPEd
        this.viewCardStackWidget = null;
        this.nameShown = null;
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
        
        this.addButton(this.cardStackNameWidget = new TextWidget(x, y, maxWidth, buttonHeight, this::getShownZoneName));
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
        
        final int zoneSize = 32;
        final int halfSize = zoneSize / 2;
        final int quarterSize = zoneSize / 4;
        final int zonesMargin = 2;
        
        x = (width - zoneSize) / 2;
        y = (height - zoneSize) / 2;
        // show match points in between top LP and lp-textfield (y+lpHeight)
        this.addButton(new LifePointsWidget(x, y + quarterSize, zoneSize, quarterSize,
            () -> this.getPlayField().getLifePoints(this.getView().opponent()), this.getPlayField().playFieldType.startingLifePoints));
        this.addButton(new LifePointsWidget(x, y + 2 * quarterSize, zoneSize, quarterSize,
            () -> this.getPlayField().getLifePoints(this.getView()), this.getPlayField().playFieldType.startingLifePoints));
        this.addButton(this.lifePointsWidget = new LPTextFieldWidget(this.font, x, y + 3 * quarterSize, zoneSize, quarterSize, this::lpTextFieldWidget));
        
        // TODO do all of these properly
        x += (zoneSize + zonesMargin) * 2;
        Widget w;
        this.addButton(w = new ImprovedButton(x, y, halfSize, halfSize, new TranslationTextComponent("container.ydm.duel.left_arrow"), this::phaseButtonClicked));
        w.active = false;
        this.addButton(w = new ImprovedButton(x + halfSize, y, halfSize, halfSize, new TranslationTextComponent("container.ydm.duel.right_arrow"), this::phaseButtonClicked));
        w.active = false;
        this.addButton(w = new TextWidget(x, y + halfSize, zoneSize, halfSize, () -> new StringTextComponent("BP")));
        w.active = false;
        
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
            this.viewCards(previousViewStack.getCards(), this.nameShown, previousViewStack.getForceFaceUp());
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
            return new MonsterZoneWidget(zone, this, zone.width, zone.height, zone.getType().getLocal(), this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.HAND)
        {
            return new HandZoneWidget(zone, this, zone.width, zone.height, zone.getType().getLocal(), this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.EXTRA_DECK ||
            zone.getType() == ZoneTypes.GRAVEYARD ||
            zone.getType() == ZoneTypes.GRAVEYARD ||
            zone.getType() == ZoneTypes.GRAVEYARD)
        {
            return new NonSecretStackZoneWidget(zone, this, zone.width, zone.height, zone.getType().getLocal(), this::zoneClicked, this::zoneTooltip);
        }
        else
        {
            return new ZoneWidget(zone, this, zone.width, zone.height, zone.getType().getLocal(), this::zoneClicked, this::zoneTooltip);
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(ms, partialTicks, mouseX, mouseY);
        
        ScreenUtil.white();
        this.minecraft.getTextureManager().bindTexture(DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.minecraft.getTextureManager().bindTexture(DuelContainerScreen.DUEL_FOREGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
    {
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.animationsWidget.forceFinish();
        
        if(this.lifePointsWidget.isFocused() && !this.lifePointsWidget.isMouseOver(mouseX, mouseY))
        {
            this.lifePointsWidget.setFocused2(false);
        }
        
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
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(this.lifePointsWidget != null && this.lifePointsWidget.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                this.parseAndSendLPChange();
                return true;
            }
            else
            {
                return this.lifePointsWidget.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
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
        this.viewCards(widget.zone.getCardsList(), widget.getMessage(), forceFaceUp);
    }
    
    protected void viewCards(List<DuelCard> cards, ITextComponent name, boolean forceFaceUp)
    {
        this.viewCardStackWidget.activate(cards, forceFaceUp);
        this.nameShown = name;
        
        this.updateButtonStatus();
        this.makeChatInvisible();
    }
    
    protected void updateButtonStatus()
    {
        this.scrollUpButton.active = false;
        this.scrollUpButton.visible = false;
        this.scrollDownButton.active = false;
        this.scrollDownButton.visible = false;
        this.cardStackNameWidget.visible = false;
        
        if(this.viewCardStackWidget.active)
        {
            this.scrollUpButton.visible = true;
            this.scrollDownButton.visible = true;
            this.cardStackNameWidget.visible = true;
            
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
            
            Animation moveAnimation = new MoveAnimation(
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
            
            if(action.actionType == ActionTypes.SPECIAL_SUMMON)
            {
                ZoneWidget w = this.getZoneWidget(action.destinationZone);
                
                int size = Math.max(w.getWidth(), w.getHeightRealms());
                Animation ringAnimation = new SpecialSummonAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2);
                
                Queue<Animation> queue = new LinkedList<>();
                queue.add(moveAnimation);
                queue.add(ringAnimation);
                
                return new QueueAnimation(queue);
            }
            else
            {
                return moveAnimation;
            }
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
                
                ParallelListAnimation listAnimation = new ParallelListAnimation(animations);
                
                if(action.actionType == ActionTypes.SPECIAL_SUMMON_OVERLAY)
                {
                    Queue<Animation> queue = new LinkedList<>();
                    queue.add(listAnimation);
                    
                    MoveTopAction moveAction = (MoveTopAction)action.actions.get(action.actions.size() - 1);
                    ZoneWidget w = this.getZoneWidget(moveAction.destinationZone);
                    int size = Math.max(w.getWidth(), w.getHeightRealms());
                    queue.add(new SpecialSummonOverlayAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2));
                    
                    return new QueueAnimation(queue);
                }
                else
                {
                    return listAnimation;
                }
                
            }
        }
        else if(action0 instanceof AttackAction)
        {
            AttackAction action = (AttackAction)action0;
            
            return new AttackAnimation(this.getView(), this.getZoneWidget(action.sourceZone), this.getZoneWidget(action.attackedZone));
        }
        else if(action0 instanceof IAnnouncedAction)
        {
            IAnnouncedAction action = (IAnnouncedAction)action0;
            
            if(action.announceOnField())
            {
                ZoneWidget w = this.getZoneWidget(action.getFieldAnnouncementZone());
                
                return new TextAnimation(action0.getActionType().getLocal(), w.getAnimationDestX(), w.getAnimationDestY())
                    .setOnStart(() -> this.handleAnnouncedAction(action0));
            }
        }
        
        return null;
    }
    
    protected void handleAnnouncedAction(Action action)
    {
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
        else if(action instanceof ShuffleAction)
        {
            ShuffleAction a = (ShuffleAction)action;
            
            // if we have a zone selected/viewed and it is shuffled, we gotta deselect it / stop viewing it
            if(this.clickedZoneWidget != null &&
                this.clickedZoneWidget.zone == a.sourceZone)
            {
                this.resetToNormalZoneWidgets();
            }
            
            a.doAction();
        }
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
                this.clickedCard = null;
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
                this.viewCards(cards, w.getMessage(), true);
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
        
        this.findAndPopulateInteractions(widget);
        
        if(!widget.zone.getType().getIsSecret())
        {
            this.viewZone(widget, owner == widget.zone.getOwner() && widget.zone.type.getShowFaceDownCardsToOwner());
        }
    }
    
    protected void findAndPopulateInteractions(ZoneWidget widget)
    {
        ZoneOwner owner = this.getZoneOwner();
        
        this.removeInteractionWidgets();
        
        this.interactionWidgets = new ArrayList<>();
        
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.addInteractionWidgets(owner, this.clickedZoneWidget.zone, this.clickedCard, this.getDuelManager(), this.interactionWidgets, this::interactionClicked, this::interactionTooltip);
            w.active = false;
        }
        
        this.buttons.addAll(this.interactionWidgets);
        this.children.addAll(this.interactionWidgets);
    }
    
    protected void interactionClicked(InteractionWidget widget)
    {
        this.requestDuelAction(widget.interaction.action);
        
        ZoneType interactorType = widget.interaction.interactor.getType();
        
        if(interactorType.getKeepFocusedAfterInteraction() &&
            interactorType.getIsSecret() ? this.viewCardStackWidget.active : true)
        {
            this.clickedCard = null;
            this.findAndPopulateInteractions(this.getZoneWidget(widget.interaction.interactor));
        }
        else
        {
            this.resetToNormalZoneWidgets();
        }
    }
    
    protected void requestDuelAction(Action action)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestDuelAction(this.getDuelManager().headerFactory.get(), action));
    }
    
    protected ITextComponent getShownZoneName()
    {
        return this.nameShown == null ? StringTextComponent.EMPTY : this.nameShown;
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
    
    protected void parseAndSendLPChange()
    {
        if(this.getZoneOwner().isPlayer())
        {
            String text = this.lifePointsWidget.getText();
            this.lifePointsWidget.setText("");
            
            if(text.length() > 1)
            {
                if(text.startsWith("+"))
                {
                    text = text.substring(1);
                }
                
                int lp = Integer.valueOf(text);
                this.requestDuelAction(new ChangeLPAction(ActionTypes.CHANGE_LP, lp, this.getZoneOwner()));
            }
        }
    }
    
    protected void phaseButtonClicked(Button button)
    {
    }
    
    protected void zoneTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.renderTooltip(ms, w.getMessage(), mouseX, mouseY);
    }
    
    protected void interactionTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.renderTooltip(ms, w.getMessage(), mouseX, mouseY);
    }
    
    protected void viewCardStackTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
    }
    
    protected void lpTextFieldWidget(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        //        this.renderTooltip(ms, new TranslationTextComponent("container.ydm.duel.lp_tooltip"), mouseX, mouseY);
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
        this.nameShown = null;
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
