package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ColoredButton;
import de.cas_ual_ty.ydm.clientutil.widget.ColoredTextWidget;
import de.cas_ual_ty.ydm.clientutil.widget.SmallTextWidget;
import de.cas_ual_ty.ydm.clientutil.widget.TextWidget;
import de.cas_ual_ty.ydm.clientutil.widget.TextureButton;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.DuelPhase;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionTypes;
import de.cas_ual_ty.ydm.duel.action.AttackAction;
import de.cas_ual_ty.ydm.duel.action.ChangeCountersAction;
import de.cas_ual_ty.ydm.duel.action.ChangeLPAction;
import de.cas_ual_ty.ydm.duel.action.ChangePhaseAction;
import de.cas_ual_ty.ydm.duel.action.ChangePositionAction;
import de.cas_ual_ty.ydm.duel.action.CoinFlipAction;
import de.cas_ual_ty.ydm.duel.action.CreateTokenAction;
import de.cas_ual_ty.ydm.duel.action.DiceRollAction;
import de.cas_ual_ty.ydm.duel.action.EndTurnAction;
import de.cas_ual_ty.ydm.duel.action.IAnnouncedAction;
import de.cas_ual_ty.ydm.duel.action.ListAction;
import de.cas_ual_ty.ydm.duel.action.MoveAction;
import de.cas_ual_ty.ydm.duel.action.MoveTopAction;
import de.cas_ual_ty.ydm.duel.action.RemoveTokenAction;
import de.cas_ual_ty.ydm.duel.action.SelectAction;
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
import de.cas_ual_ty.ydm.duel.screen.animation.DummyAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.MoveAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.ParallelListAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.QueueAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.RemoveTokenAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.SpecialSummonAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.SpecialSummonOverlayAnimation;
import de.cas_ual_ty.ydm.duel.screen.animation.SpecialSummonTokenAnimation;
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
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
    protected boolean isAdvanced;
    
    protected Button coinFlipButton;
    protected Button diceRollButton;
    protected Button addCounterButton;
    protected Button removeCounterButton;
    protected Button advancedOptionsButton;
    
    protected Button reloadButton;
    protected Button flipViewButton;
    protected Button offerDrawButton;
    protected Button admitDefeatButton;
    protected LPTextFieldWidget lifePointsWidget;
    
    protected ColoredButton prevPhaseButton;
    protected ColoredButton nextPhaseButton;
    protected ColoredTextWidget phaseWidget;
    
    protected ZoneOwner view;
    protected AnimationsWidget animationsWidget;
    
    protected DuelCard cardInfo;
    
    // need to store these seperately
    // to make sure that we keep them
    // in case a player leaves
    protected IFormattableTextComponent player1Name;
    protected IFormattableTextComponent player2Name;
    
    public DuelScreenDueling(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.interactionWidgets = new ArrayList<>(); // Need to temporarily initialize with placeholder this to make sure no clear() call gets NPEd
        this.isAdvanced = false;
        
        this.viewCardStackWidget = null;
        this.nameShown = null;
        this.clickedZoneWidget = null;
        this.clickedCard = null;
        
        this.coinFlipButton = null;
        this.diceRollButton = null;
        this.addCounterButton = null;
        this.removeCounterButton = null;
        this.advancedOptionsButton = null;
        this.reloadButton = null;
        this.flipViewButton = null;
        this.offerDrawButton = null;
        this.admitDefeatButton = null;
        this.lifePointsWidget = null;
        this.prevPhaseButton = null;
        this.nextPhaseButton = null;
        this.phaseWidget = null;
        
        this.view = this.getZoneOwner();
        if(this.view == ZoneOwner.NONE)
        {
            this.view = ZoneOwner.PLAYER1;
        }
        this.animationsWidget = null;
        this.cardInfo = null;
        this.player1Name = null;
        this.player2Name = null;
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
        
        //middle
        x = (width - zoneSize) / 2;
        y = (height - zoneSize) / 2;
        
        this.addButton(this.reloadButton = new TextureButton(x, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.reload"), this::middleButtonClicked, this::middleButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 64, 0, 16, 16));
        this.addButton(this.flipViewButton = new TextureButton(x + quarterSize, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.flip_view"), this::middleButtonClicked, this::middleButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 80, 0, 16, 16));
        this.addButton(this.offerDrawButton = new TextureButton(x + 2 * quarterSize, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.offer_draw"), this::middleButtonClicked, this::middleButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 96, 0, 16, 16));
        this.addButton(this.admitDefeatButton = new TextureButton(x + 3 * quarterSize, y, quarterSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.admit_defeat"), this::middleButtonClicked, this::middleButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 112, 0, 16, 16));
        
        // lp text field for players, "Spectator" text for spectators
        if(this.getZoneOwner() != ZoneOwner.NONE)
        {
            this.addButton(this.lifePointsWidget = new LPTextFieldWidget(this.font, x, y + 3 * quarterSize, zoneSize, quarterSize, this::lpTextFieldWidget));
        }
        else
        {
            this.addButton(new SmallTextWidget(x, y + 3 * quarterSize, zoneSize, quarterSize, () -> new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.spectating")));
        }
        
        if(this.getZoneOwner() == ZoneOwner.NONE)
        {
            this.admitDefeatButton.active = false;
            this.offerDrawButton.active = false;
        }
        
        this.addButton(new LifePointsWidget(x, y + quarterSize, zoneSize, quarterSize,
            () -> this.getPlayField().getLifePoints(this.getView().opponent()), this.getPlayField().playFieldType.startingLifePoints, this::lpTooltipViewOpponent));
        this.addButton(new LifePointsWidget(x, y + 2 * quarterSize, zoneSize, quarterSize,
            () -> this.getPlayField().getLifePoints(this.getView()), this.getPlayField().playFieldType.startingLifePoints, this::lpTooltipView));
        
        //left
        x = (width - zoneSize) / 2 - (zoneSize + zonesMargin) * 2;
        
        this.addButton(this.coinFlipButton = new TextureButton(x, y, halfSize, halfSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.coin_flip"), this::leftButtonClicked, this::leftButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 32, 0, 16, 16));
        this.addButton(this.diceRollButton = new TextureButton(x + halfSize, y, halfSize, halfSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.dice_roll"), this::leftButtonClicked, this::leftButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 48, 0, 16, 16));
        this.addButton(this.addCounterButton = new TextureButton(x, y + halfSize, halfSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.add_counter"), this::leftButtonClicked, this::leftButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 128, 0, 16, 8));
        this.addButton(this.removeCounterButton = new TextureButton(x, y + halfSize + quarterSize, halfSize, quarterSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.remove_counter"), this::leftButtonClicked, this::leftButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 128, 8, 16, 8));
        this.addButton(this.advancedOptionsButton = new TextureButton(x + halfSize, y + halfSize, halfSize, halfSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.advanced_options"), this::leftButtonClicked, this::leftButtonHovered)
            .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 144, 0, 16, 16));
        
        if(this.getZoneOwner() == ZoneOwner.NONE)
        {
            this.coinFlipButton.active = false;
            this.diceRollButton.active = false;
            this.addCounterButton.active = false;
            this.removeCounterButton.active = false;
            this.advancedOptionsButton.active = false;
        }
        
        // right
        x = (width - zoneSize) / 2 + (zoneSize + zonesMargin) * 2;
        
        this.addButton(this.phaseWidget = new ColoredTextWidget(x, y, zoneSize, halfSize, this::getPhaseShort, this::phaseWidgetHovered));
        this.phaseWidget.active = false;
        this.addButton(this.prevPhaseButton = new ColoredButton(x, y + halfSize, halfSize, halfSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.left_arrow"), this::rightButtonClicked, this::rightButtonHovered));
        this.addButton(this.nextPhaseButton = new ColoredButton(x + halfSize, y + halfSize, halfSize, halfSize, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.right_arrow"), this::rightButtonClicked, this::rightButtonHovered));
        
        if(this.getZoneOwner() == ZoneOwner.NONE)
        {
            this.prevPhaseButton.active = false;
            this.nextPhaseButton.active = false;
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
                    this.setClickedZoneWidgetAndCard(match, this.clickedCard);
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
        
        this.updateScrollButtonStatus();
        this.updateLeftButtonStatus();
        this.updateRightButtonStatus();
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
        
        this.addButton(this.scrollUpButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.up_arrow"), this::scrollButtonClicked, this::scrollButtonHovered));
        y += offset;
        
        int columns = chatWidth / cardsSize;
        int rows = chatHeight / cardsSize;
        this.addButton(this.viewCardStackWidget = new ViewCardStackWidget(this, x + (w - widgetWidth) / 2, y + (chatHeight - widgetHeight) / 2, chatWidth, chatHeight, StringTextComponent.EMPTY, this::viewCardStackClicked, this::viewCardStackTooltip)
            .setRowsAndColumns(cardsSize, rows, columns));
        y += chatHeight + margin;
        
        this.addButton(this.scrollDownButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.down_arrow"), this::scrollButtonClicked, this::scrollButtonHovered));
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
            CardRenderUtil.renderCardInfo(ms, this.cardInfo.getCardHolder(), this.cardInfo.getIsToken(), (this.width - this.xSize) / 2);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int x, int y)
    {
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.forceFinishAnimations(mouseX, mouseY);
        
        if(this.lifePointsWidget != null && this.lifePointsWidget.isFocused() && !this.lifePointsWidget.isMouseOver(mouseX, mouseY))
        {
            this.lifePointsWidget.setFocused2(false);
        }
        
        if(button == GLFW.GLFW_MOUSE_BUTTON_2)
        {
            this.resetToNormalZoneWidgets();
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void handleAction(Action action)
    {
        action.initClient(this.getDuelManager().getPlayField());
        this.getDuelManager().actions.add(action);
        
        // all actions must return an animation
        // otherwise, their order might be disrupted
        // eg Action1 still in animation, then Action2 (without animation) gets done before Action1 finishes
        // so, by default a dummy animation is returned, doing nothing, lasting 1 tick, just doing the Action
        Animation animation = this.getAnimationForAction(action);
        this.playAnimation(animation);
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
        this.isAdvanced = false;
    }
    
    protected void viewZone(ZoneWidget w, boolean forceFaceUp)
    {
        IFormattableTextComponent t = new StringTextComponent("").appendSibling(w.getMessage());
        
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
        
        this.updateScrollButtonStatus();
        this.makeChatInvisible();
    }
    
    protected void updateScrollButtonStatus()
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
    
    protected void updateLeftButtonStatus()
    {
        if(this.clickedZoneWidget != null &&
            this.clickedZoneWidget.zone.getType().getCanHaveCounters() &&
            this.clickedZoneWidget.zone.getCardsAmount() > 0 &&
            this.clickedZoneWidget.zone.getOwner() == this.getZoneOwner())
        {
            this.addCounterButton.active = true;
            this.removeCounterButton.active = true;
        }
        else
        {
            this.addCounterButton.active = false;
            this.removeCounterButton.active = false;
        }
        
        if(this.clickedZoneWidget != null)
        {
            this.advancedOptionsButton.active = true;
        }
        else
        {
            this.advancedOptionsButton.active = false;
        }
    }
    
    protected void updateRightButtonStatus()
    {
        boolean isTurn;
        
        if(this.getZoneOwner() == ZoneOwner.NONE)
        {
            isTurn = this.getPlayField().isPlayerTurn(ZoneOwner.PLAYER1);
        }
        else
        {
            isTurn = this.getPlayField().isPlayerTurn(this.getZoneOwner());
        }
        
        if(isTurn)
        {
            this.phaseWidget.setBlue();
            this.prevPhaseButton.setBlue();
            this.nextPhaseButton.setBlue();
        }
        else
        {
            this.phaseWidget.setRed();
            this.prevPhaseButton.setRed();
            this.nextPhaseButton.setRed();
        }
        
        isTurn = this.getZoneOwner() != ZoneOwner.NONE && this.getPlayField().isPlayerTurn(this.getZoneOwner());
        
        if(isTurn)
        {
            this.prevPhaseButton.active = !this.getPlayField().getPhase().isFirst();
            this.nextPhaseButton.active = true;
            // next phase button is always active
            // if last phase: we end turn
        }
        else
        {
            this.prevPhaseButton.active = false;
            this.nextPhaseButton.active = false;
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
                        DuelScreenDueling.this.repopulateInteractions();
                    });
            
            if(action.actionType == ActionTypes.SPECIAL_SUMMON)
            {
                ZoneWidget w = this.getZoneWidget(action.destinationZone);
                
                int size = Math.max(w.getWidth(), w.getHeight());
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
                            action.sourceZone.removeCardKeepCounters(action.sourceCardIndex);
                        })
                        .setOnEnd(() ->
                        {
                            action.sourceZone.addCard(owner, action.card, action.sourceCardIndex);
                            action.sourceZone.getCard(action.sourceCardIndex).setPosition(action.destinationCardPosition);
                            DuelScreenDueling.this.repopulateInteractions();
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
                    
                    int size = Math.max(w.getWidth(), w.getHeight());
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
        else if(action0 instanceof CreateTokenAction)
        {
            CreateTokenAction action = (CreateTokenAction)action0;
            
            ZoneWidget w = this.getZoneWidget(action.destinationZone);
            
            int size = Math.max(w.getWidth(), w.getHeight());
            return new SpecialSummonTokenAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2)
                .setOnStart(() ->
                {
                    action.doAction();
                    DuelScreenDueling.this.repopulateInteractions();
                });
        }
        else if(action0 instanceof RemoveTokenAction)
        {
            RemoveTokenAction action = (RemoveTokenAction)action0;
            
            ZoneWidget w = this.getZoneWidget(action.destinationZone);
            
            int size = Math.max(w.getWidth(), w.getHeight());
            return new RemoveTokenAnimation(w.getAnimationDestX(), w.getAnimationDestY(), size, size + size / 2)
                .setOnEnd(() ->
                {
                    action.doAction();
                    DuelScreenDueling.this.repopulateInteractions();
                });
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
        else if(action0.actionType == ActionTypes.CHANGE_PHASE || action0.actionType == ActionTypes.END_TURN)
        {
            Animation a = this.getDefaultAnimation(action0);
            
            a.setOnEnd(() ->
            {
                this.updateRightButtonStatus();
            });
            
            return a;
        }
        
        return this.getDefaultAnimation(action0);
    }
    
    protected Animation getDefaultAnimation(Action action)
    {
        return new DummyAnimation().setOnStart(() ->
        {
            action.doAction();
        });
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
    
    protected void forceFinishAnimations(double mouseX, double mouseY)
    {
        this.animationsWidget.forceFinish();
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
            this.setClickedZoneWidgetAndCard(widget, widget.hoverCard);
            this.findAndPopulateInteractions(widget, false);
        }
        
        if(widget.openAdvancedZoneView())
        {
            this.viewZone(widget, owner == widget.zone.getOwner() && widget.zone.type.getShowFaceDownCardsToOwner());
        }
        
        this.updateLeftButtonStatus();
    }
    
    protected void findAndPopulateInteractions(ZoneWidget widget, boolean isAdvanced)
    {
        ZoneOwner owner = this.getZoneOwner();
        
        this.removeInteractionWidgets();
        
        this.interactionWidgets = new ArrayList<>();
        
        for(ZoneWidget w : this.zoneWidgets)
        {
            w.addInteractionWidgets(owner, this.clickedZoneWidget.zone, this.clickedCard, this.getDuelManager(), this.interactionWidgets, this::interactionClicked, this::interactionTooltip, isAdvanced);
            w.active = false;
        }
        
        this.buttons.addAll(this.interactionWidgets);
        this.children.addAll(this.interactionWidgets);
    }
    
    protected void interactionClicked(InteractionWidget widget)
    {
        Action action = widget.interaction.action;
        
        ZoneType interactorType = widget.interaction.interactor.getType();
        
        if(interactorType.getKeepFocusedAfterInteraction() &&
            (interactorType.getIsSecret() ? this.viewCardStackWidget.active : true))
        {
            this.clickedCard = null;
            this.repopulateInteractions();
        }
        else if(this.shouldRepopulateInteractions(widget))
        {
            this.repopulateInteractions();
        }
        else
        {
            this.resetToNormalZoneWidgets();
        }
        
        this.requestDuelAction(action);
    }
    
    protected boolean shouldRepopulateInteractions(InteractionWidget clickedWidget)
    {
        return clickedWidget.interaction.action.getActionType() == ActionTypes.CREATE_TOKEN;
    }
    
    protected void repopulateInteractions()
    {
        if(this.clickedZoneWidget != null)
        {
            this.findAndPopulateInteractions(this.clickedZoneWidget, this.isAdvanced);
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
        
        this.updateScrollButtonStatus();
    }
    
    protected void middleButtonClicked(Widget w)
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
    
    protected void leftButtonClicked(Button button)
    {
        if(button == this.coinFlipButton)
        {
            this.requestDuelAction(new CoinFlipAction(ActionTypes.COIN_FLIP));
        }
        else if(button == this.diceRollButton)
        {
            this.requestDuelAction(new DiceRollAction(ActionTypes.DICE_ROLL));
        }
        else if(this.getClickedZone() != null && this.clickedZoneWidget.zone.getOwner() == this.getZoneOwner() && button == this.addCounterButton)
        {
            this.requestDuelAction(new ChangeCountersAction(ActionTypes.CHANGE_COUNTERS, this.getClickedZone().index, +1));
        }
        else if(this.getClickedZone() != null && this.clickedZoneWidget.zone.getOwner() == this.getZoneOwner() && button == this.removeCounterButton)
        {
            this.requestDuelAction(new ChangeCountersAction(ActionTypes.CHANGE_COUNTERS, this.getClickedZone().index, -1));
        }
        else if(button == this.advancedOptionsButton)
        {
            this.isAdvanced = !this.isAdvanced;
            this.repopulateInteractions();
        }
    }
    
    protected void rightButtonClicked(Widget w)
    {
        DuelPhase phase = this.getPlayField().getPhase();
        
        if(w == this.prevPhaseButton)
        {
            if(!phase.isFirst())
            {
                DuelPhase prevPhase = DuelPhase.getFromIndex((byte)(phase.getIndex() - 1));
                this.requestDuelAction(new ChangePhaseAction(ActionTypes.CHANGE_PHASE, prevPhase));
            }
        }
        else if(w == this.nextPhaseButton)
        {
            if(phase.isLast())
            {
                this.requestDuelAction(new EndTurnAction(ActionTypes.END_TURN));
            }
            else
            {
                DuelPhase nextPhase = DuelPhase.getFromIndex((byte)(phase.getIndex() + 1));
                this.requestDuelAction(new ChangePhaseAction(ActionTypes.CHANGE_PHASE, nextPhase));
            }
        }
    }
    
    protected void zoneTooltip(Widget w0, MatrixStack ms, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> tooltip = new LinkedList<>();
        
        ZoneWidget w = (ZoneWidget)w0;
        
        IFormattableTextComponent t = new StringTextComponent("").appendSibling(w.getMessage());
        
        if(w.zone.getCardsAmount() > 0)
        {
            t.appendString(" (" + w.zone.getCardsAmount() + ")");
        }
        
        tooltip.add(t.func_241878_f());
        
        if(w.zone.getType().getCanHaveCounters() && w.zone.getCounters() > 0)
        {
            tooltip.add(new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.counters").appendString(": " + w.zone.getCounters()).func_241878_f());
        }
        
        this.renderTooltip(ms, tooltip, mouseX, mouseY);
    }
    
    protected void interactionTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.renderTooltip(ms, w.getMessage(), mouseX, mouseY);
    }
    
    protected void viewCardStackTooltip(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
    }
    
    protected void lpTooltip(ZoneOwner owner, @Nullable IFormattableTextComponent playerName, Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> list = new LinkedList<>();
        
        list.add(new StringTextComponent(String.valueOf(this.getPlayField().getLifePoints(owner))).func_241878_f());
        
        if(playerName != null)
        {
            list.add(playerName.func_241878_f());
        }
        else
        {
            list.add(this.getUnknownPlayerName().func_241878_f());
        }
        
        this.renderTooltip(ms, list, mouseX, mouseY);
    }
    
    protected void lpTooltipView(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.lpTooltip(this.getView(), this.getViewName(), w, ms, mouseX, mouseY);
    }
    
    protected void lpTooltipViewOpponent(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.lpTooltip(this.getView().opponent(), this.getViewOpponentName(), w, ms, mouseX, mouseY);
    }
    
    protected void lpTextFieldWidget(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> list = new LinkedList<>();
        
        list.add(new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.change_lp_tooltip1").func_241878_f());
        list.add(new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.change_lp_tooltip2").func_241878_f());
        list.add(new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.change_lp_tooltip3").func_241878_f());
        
        this.renderTooltip(ms, list, mouseX, mouseY);
    }
    
    protected void phaseWidgetHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        this.renderTooltip(ms, this.getCurrentPhaseTooltip(), mouseX, mouseY);
    }
    
    protected void scrollButtonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
    }
    
    protected void middleButtonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        if(w == this.reloadButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.reload"), mouseX, mouseY);
        }
        else if(w == this.flipViewButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.flip_view"), mouseX, mouseY);
        }
        else if(w == this.offerDrawButton)
        {
            this.renderDisabledTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.offer_draw"), mouseX, mouseY);
        }
        else if(w == this.admitDefeatButton)
        {
            this.renderDisabledTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.admit_defeat"), mouseX, mouseY);
        }
    }
    
    protected void leftButtonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        if(w == this.coinFlipButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.coin_flip"), mouseX, mouseY);
        }
        else if(w == this.diceRollButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.dice_roll"), mouseX, mouseY);
        }
        else if(w == this.addCounterButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.add_counter"), mouseX, mouseY);
        }
        else if(w == this.removeCounterButton)
        {
            this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.remove_counter"), mouseX, mouseY);
        }
        else if(w == this.advancedOptionsButton)
        {
            if(!this.isAdvanced)
            {
                this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.advanced_options"), mouseX, mouseY);
            }
            else
            {
                this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.basic_options"), mouseX, mouseY);
            }
        }
    }
    
    protected void rightButtonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        DuelPhase phase = this.getPlayField().getPhase();
        
        if(w == this.prevPhaseButton)
        {
            if(!phase.isFirst())
            {
                DuelPhase prevPhase = DuelPhase.getFromIndex((byte)(phase.getIndex() - 1));
                this.renderTooltip(ms, (this.getPhaseTooltip(prevPhase).appendString(" ").appendSibling(new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.left_arrow"))), mouseX, mouseY);
            }
        }
        else if(w == this.nextPhaseButton)
        {
            if(phase.isLast())
            {
                this.renderTooltip(ms, new TranslationTextComponent("action." + YDM.MOD_ID + ".end_turn"), mouseX, mouseY);
            }
            else
            {
                DuelPhase nextPhase = DuelPhase.getFromIndex((byte)(phase.getIndex() + 1));
                this.renderTooltip(ms, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.right_arrow").appendString(" ").appendSibling(this.getPhaseTooltip(nextPhase)), mouseX, mouseY);
            }
        }
    }
    
    public IFormattableTextComponent getPhaseShort()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".duel." + this.getPlayField().getPhase().local + ".short");
    }
    
    public IFormattableTextComponent getCurrentPhaseTooltip()
    {
        return this.getPhaseTooltip(this.getPlayField().getPhase());
    }
    
    public IFormattableTextComponent getPhaseTooltip(DuelPhase phase)
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".duel." + phase.local);
    }
    
    protected void removeInteractionWidgets()
    {
        this.buttons.removeIf((w) -> w instanceof InteractionWidget);
        this.children.removeIf((w) -> w instanceof InteractionWidget);
    }
    
    protected void removeClickedZone()
    {
        this.setClickedZoneWidgetAndCard(null, null);
        this.viewCardStackWidget.deactivate();
        this.nameShown = null;
        this.updateScrollButtonStatus();
        this.updateLeftButtonStatus();
    }
    
    protected void setClickedZoneWidgetAndCard(ZoneWidget zone, DuelCard card)
    {
        this.clickedZoneWidget = zone;
        this.clickedCard = card;
        
        if(this.getZoneOwner().isPlayer())
        {
            this.getPlayField().setClickedForPlayer(this.getZoneOwner(), zone != null ? zone.zone : null, card);
            this.requestDuelAction(new SelectAction(ActionTypes.SELECT, this.getClickedZone(), this.getClickedCard(), this.getZoneOwner()));
        }
    }
    
    protected IFormattableTextComponent getUnknownPlayerName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.unknown_player")
            .modifyStyle((style) -> style.applyFormatting(TextFormatting.ITALIC))
            .modifyStyle((style) -> style.applyFormatting(TextFormatting.RED));
    }
    
    protected IFormattableTextComponent getViewName()
    {
        return this.getView() == ZoneOwner.PLAYER1 ? this.getPlayer1Name() : this.getPlayer2Name();
    }
    
    protected IFormattableTextComponent getViewOpponentName()
    {
        return this.getView() == ZoneOwner.PLAYER1 ? this.getPlayer2Name() : this.getPlayer1Name();
    }
    
    protected IFormattableTextComponent getPlayer1Name()
    {
        if(this.getDuelManager().player1 != null)
        {
            return (IFormattableTextComponent)this.getDuelManager().player1.getName();
        }
        else
        {
            if(!this.fetchPlayer1Name() && this.player1Name == null)
            {
                // we have never fetched the name and the player isnt here
                return null;
            }
            else
            {
                return this.player1Name.modifyStyle((style) -> style.applyFormatting(TextFormatting.RED));
            }
        }
    }
    
    protected IFormattableTextComponent getPlayer2Name()
    {
        if(this.getDuelManager().player2 != null)
        {
            return (IFormattableTextComponent)this.getDuelManager().player2.getName();
        }
        else
        {
            if(!this.fetchPlayer2Name() && this.player2Name == null)
            {
                // we have never fetched the name and the player isnt here
                return null;
            }
            else
            {
                return this.player2Name.modifyStyle((style) -> style.applyFormatting(TextFormatting.RED));
            }
        }
    }
    
    // return true if player 1 is still in the same dimension
    protected boolean fetchPlayer1Name()
    {
        // TODO sync UUIDs to client, instead of setting roles only for uuid-fetchable players
        
        if(this.getDuelManager().player1Id == null)
        {
            return false;
        }
        
        PlayerEntity p = this.minecraft.world.getPlayerByUuid(this.getDuelManager().player1Id);
        if(p != null)
        {
            this.player1Name = (IFormattableTextComponent)p.getName();
            return true;
        }
        else
        {
            return false;
        }
    }
    
    // return true if player 2 is still in the same dimension
    protected boolean fetchPlayer2Name()
    {
        if(this.getDuelManager().player2Id == null)
        {
            return false;
        }
        
        PlayerEntity p = this.minecraft.world.getPlayerByUuid(this.getDuelManager().player2Id);
        if(p != null)
        {
            this.player2Name = (IFormattableTextComponent)p.getName();
            return true;
        }
        else
        {
            return false;
        }
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
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 0, 0, 1F, 1F);
    }
    
    public static void renderEnemySelectedRect(MatrixStack ms, float x, float y, float w, float h)
    {
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 1F, 0, 0, 1F);
    }
    
    public static void renderBothSelectedRect(MatrixStack ms, float x, float y, float w, float h)
    {
        ScreenUtil.drawLineRect(ms, x - 1, y - 1, w + 2, h + 2, 2, 1F, 0, 1F, 1F);
    }
}
