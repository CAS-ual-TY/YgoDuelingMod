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
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import de.cas_ual_ty.ydm.clientutil.widget.SmallTextButton;
import de.cas_ual_ty.ydm.clientutil.widget.TextWidget;
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
import de.cas_ual_ty.ydm.duel.screen.widget.InteractionWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.LPTextFieldWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.LifePointsWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.MonsterZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.NonSecretStackZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.StackZoneWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ViewCardStackWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.ZoneWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
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
    
    protected Button reloadButton;
    protected Button flipViewButton;
    protected Button offerDrawButton;
    protected Button admitDefeatButton;
    
    protected ZoneOwner view;
    
    protected AnimationsWidget animationsWidget;
    
    protected TextFieldWidget lifePointsWidget;
    
    protected Button prevPhaseButton;
    protected Button nextPhaseButton;
    protected Widget phaseWidget;
    
    protected DuelCard cardInfo;
    
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
        this.lifePointsWidget = null;
        this.prevPhaseButton = null;
        this.nextPhaseButton = null;
        this.phaseWidget = null;
        this.cardInfo = null;
        this.reloadButton = null;
        this.flipViewButton = null;
        this.offerDrawButton = null;
        this.admitDefeatButton = null;
    }
    
    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        
        ViewCardStackWidget previousViewStack = this.viewCardStackWidget;
        
        if(this.animationsWidget != null)
        {
            this.animationsWidget.forceFinish();
        }
        
        this.initDefaultChat(width, height);
        
        int x, y;
        
        final int zoneSize = 32;
        final int halfSize = zoneSize / 2;
        final int quarterSize = zoneSize / 4;
        final int zonesMargin = 2;
        
        x = (width - zoneSize) / 2;
        y = (height - zoneSize) / 2;
        
        if(this.getZoneOwner() != ZoneOwner.NONE)
        {
            this.addButton(this.reloadButton = new SmallTextButton(x, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.reload"), this::buttonClicked, this::buttonHovered));
            this.addButton(this.flipViewButton = new SmallTextButton(x + quarterSize, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.flip_view"), this::buttonClicked, this::buttonHovered));
            this.addButton(this.offerDrawButton = new SmallTextButton(x + 2 * quarterSize, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.offer_draw"), this::buttonClicked, this::buttonHovered));
            this.addButton(this.admitDefeatButton = new SmallTextButton(x + 3 * quarterSize, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.admit_defeat"), this::buttonClicked, this::buttonHovered));
            this.addButton(this.lifePointsWidget = new LPTextFieldWidget(this.font, x, y + 3 * quarterSize, zoneSize, quarterSize, this::lpTextFieldWidget));
            this.admitDefeatButton.active = false;
            this.offerDrawButton.active = false; //TODO remove these
        }
        else
        {
            this.addButton(this.reloadButton = new SmallTextButton(x, y, zoneSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.reload_tooltip"), this::buttonClicked, this::buttonHovered));
            this.addButton(this.flipViewButton = new SmallTextButton(x, y + 3 * quarterSize, zoneSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.flip_view_tooltip"), this::buttonClicked, this::buttonHovered));
        }
        this.addButton(new LifePointsWidget(x, y + quarterSize, zoneSize, quarterSize,
            () -> this.getPlayField().getLifePoints(this.getView().opponent()), this.getPlayField().playFieldType.startingLifePoints, this::lpTooltipViewOpponent));
        this.addButton(new LifePointsWidget(x, y + 2 * quarterSize, zoneSize, quarterSize,
            () -> this.getPlayField().getLifePoints(this.getView()), this.getPlayField().playFieldType.startingLifePoints, this::lpTooltipView));
        
        // TODO do all of these properly
        x += (zoneSize + zonesMargin) * 2;
        
        if(this.getZoneOwner() != ZoneOwner.NONE)
        {
            this.addButton(this.phaseWidget = new TextWidget(x, y, zoneSize, halfSize, () -> new StringTextComponent("DP"), this::phaseWidgetHovered));
            this.phaseWidget.active = false;
            this.addButton(this.prevPhaseButton = new ImprovedButton(x, y + halfSize, halfSize, halfSize, new TranslationTextComponent("container.ydm.duel.left_arrow"), this::phaseButtonClicked, this::phaseButtonHovered));
            this.prevPhaseButton.active = false;
            this.addButton(this.nextPhaseButton = new ImprovedButton(x + halfSize, y + halfSize, halfSize, halfSize, new TranslationTextComponent("container.ydm.duel.right_arrow"), this::phaseButtonClicked, this::phaseButtonHovered));
            this.nextPhaseButton.active = false;
        }
        else
        {
            this.addButton(this.phaseWidget = new TextWidget(x, y, zoneSize, zoneSize, () -> new StringTextComponent("DP"), this::phaseWidgetHovered));
            this.phaseWidget.active = false;
        }
        
        this.zoneWidgets = new ArrayList<>(this.getDuelManager().getPlayField().getZones().size());
        this.interactionWidgets.clear();
        
        ZoneWidget widget;
        
        for(Zone zone : this.getDuelManager().getPlayField().getZones())
        {
            this.addButton(widget = this.createZoneWidgetForZone(zone));
            
            if(this.getView() == ZoneOwner.PLAYER2)
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
    protected void initChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin, int buttonHeight)
    {
        super.initChat(width, height, x, y, w, h, chatWidth, chatHeight, margin, buttonHeight);
        
        // 4* -> 3*
        // because we dont have a text box at the bottom
        // so more space for cards
        chatHeight = (h - 3 * (buttonHeight + margin) - 2 * margin);
        
        final int cardsSize = 32;
        final int offset = buttonHeight + margin;
        
        int widgetWidth = Math.max(cardsSize, (chatWidth / cardsSize) * cardsSize);
        int widgetHeight = Math.max(cardsSize, (chatHeight / cardsSize) * cardsSize);
        
        this.addButton(this.cardStackNameWidget = new TextWidget(x, y, w, buttonHeight, this::getShownZoneName));
        y += offset;
        
        this.addButton(this.scrollUpButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.up_arrow"), this::scrollButtonClicked));
        y += offset;
        
        int columns = chatWidth / cardsSize;
        int rows = chatHeight / cardsSize;
        this.addButton(this.viewCardStackWidget = new ViewCardStackWidget(this, x + (w - widgetWidth) / 2, y + (chatHeight - widgetHeight) / 2, chatWidth, chatHeight, StringTextComponent.EMPTY, this::viewCardStackClicked, this::viewCardStackTooltip)
            .setRowsAndColumns(cardsSize, rows, columns));
        y += chatHeight + margin;
        
        this.addButton(this.scrollDownButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.down_arrow"), this::scrollButtonClicked));
        y += offset;
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
            zone.getType() == ZoneTypes.BANISHED ||
            zone.getType() == ZoneTypes.EXTRA)
        {
            return new NonSecretStackZoneWidget(zone, this, zone.width, zone.height, zone.getType().getLocal(), this::zoneClicked, this::zoneTooltip);
        }
        else if(zone.getType() == ZoneTypes.DECK)
        {
            return new StackZoneWidget(zone, this, zone.width, zone.height, zone.getType().getLocal(), this::zoneClicked, this::zoneTooltip);
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
        
        if(this.cardInfo != null)
        {
            CardRenderUtil.renderCardInfo(ms, this.cardInfo.getCardHolder(), (this.width - this.xSize) / 2);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int x, int y)
    {
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.animationsWidget.forceFinish();
        
        if(this.lifePointsWidget != null && this.lifePointsWidget.isFocused() && !this.lifePointsWidget.isMouseOver(mouseX, mouseY))
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
        this.getDuelManager().actions.add(action);
        
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
    
    @Override
    public void renderTooltip(MatrixStack ms, List<? extends IReorderingProcessor> tooltips, int mouseX, int mouseY)
    {
        ms.push();
        ms.translate(0, 0, 1.5);
        super.renderTooltip(ms, tooltips, mouseX, mouseY);
        ms.pop();
    }
    
    public void flip()
    {
        this.view = this.view.opponent();
        
        /*//re-init anyways, this is not needed
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.flip(this.width, this.height);
        }
        */
        
        this.reInit();
    }
    
    public void reload()
    {
        this.getContainer().requestFullUpdate();
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
    
    protected void viewZone(ZoneWidget w, boolean forceFaceUp)
    {
        IFormattableTextComponent t = new StringTextComponent("").append(w.getMessage());
        
        if(w.zone.getCardsAmount() > 0)
        {
            t.appendString(" (" + w.zone.getCardsAmount() + ")");
        }
        
        this.viewCards(w.zone.getCardsList(), t, forceFaceUp);
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
        
        if(owner != ZoneOwner.NONE)
        {
            this.clickedZoneWidget = widget;
            this.clickedCard = widget.hoverCard;
            this.findAndPopulateInteractions(widget);
        }
        
        if(widget.openAdvancedZoneView())
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
            (interactorType.getIsSecret() ? this.viewCardStackWidget.active : true))
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
    
    protected void buttonClicked(Widget w)
    {
        if(w == this.reloadButton)
        {
            this.reload();
        }
        else if(w == this.flipViewButton)
        {
            this.flip();
        }
        // TODO offer draw
        // TODO admit defeat
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
    
    protected void zoneTooltip(Widget w0, MatrixStack ms, int mouseX, int mouseY)
    {
        ZoneWidget w = (ZoneWidget)w0;
        
        IFormattableTextComponent t = new StringTextComponent("").append(w.getMessage());
        
        if(w.zone.getCardsAmount() > 0)
        {
            t.appendString(" (" + w.zone.getCardsAmount() + ")");
        }
        
        this.renderTooltip(ms, t, mouseX, mouseY);
    }
    
    protected void interactionTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.renderTooltip(ms, w.getMessage(), mouseX, mouseY);
    }
    
    protected void viewCardStackTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
    }
    
    protected void phaseButtonClicked(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
    }
    
    protected void lpTooltip(ZoneOwner owner, Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> list = new LinkedList<>();
        
        list.add(new StringTextComponent(String.valueOf(this.getPlayField().getLifePoints(this.getView()))).func_241878_f());
        
        PlayerEntity player = this.getDuelManager().getPlayer(owner);
        
        if(player != null)
        {
            list.add(player.getName().func_241878_f());
        }
        
        this.renderTooltip(ms, list, mouseX, mouseY);
    }
    
    protected void lpTooltipView(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.lpTooltip(this.getView(), w, ms, mouseX, mouseY);
    }
    
    protected void lpTooltipViewOpponent(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.lpTooltip(this.getView().opponent(), w, ms, mouseX, mouseY);
    }
    
    protected void lpTextFieldWidget(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> list = new LinkedList<>();
        
        list.add(new TranslationTextComponent("container.ydm.duel.change_lp_tooltip1").func_241878_f());
        list.add(new TranslationTextComponent("container.ydm.duel.change_lp_tooltip2").func_241878_f());
        list.add(new TranslationTextComponent("container.ydm.duel.change_lp_tooltip3").func_241878_f());
        
        this.renderTooltip(ms, list, mouseX, mouseY);
    }
    
    protected void phaseWidgetHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.renderTooltip(ms, new StringTextComponent("Draw Phase"), mouseX, mouseY);
    }
    
    protected void phaseButtonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> list = new LinkedList<>();
        
        if(w == this.prevPhaseButton)
        {
            //            list.add(new StringTextComponent("-").func_241878_f());
            return;
        }
        else if(w == this.nextPhaseButton)
        {
            list.add(new StringTextComponent("to Standby Phase").func_241878_f());
        }
        
        this.renderTooltip(ms, list, mouseX, mouseY);
    }
    
    protected void buttonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        if(w == this.reloadButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container.ydm.duel.reload_tooltip"), mouseX, mouseY);
        }
        else if(w == this.flipViewButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container.ydm.duel.flip_view_tooltip"), mouseX, mouseY);
        }
        else if(w == this.offerDrawButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container.ydm.duel.offer_draw_tooltip"), mouseX, mouseY);
        }
        else if(w == this.admitDefeatButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container.ydm.duel.admit_defeat_tooltip"), mouseX, mouseY);
        }
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
        this.cardInfo = card;
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
