package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.SwitchableContainerScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DeckSource;
import de.cas_ual_ty.ydm.duel.DuelChatMessage;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.widget.DisplayChatWidget;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class DuelContainerScreen<E extends DuelContainer> extends SwitchableContainerScreen<E>
{
    public static final ResourceLocation DUEL_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_foreground.png");
    public static final ResourceLocation DUEL_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_background.png");
    
    public static final ResourceLocation DECK_BACKGROUND_GUI_TEXTURE = DeckBoxScreen.DECK_BOX_GUI_TEXTURE;
    
    protected DuelScreenConstructor<E>[] screensForEachState;
    
    protected Button chatUpButton;
    protected Button chatDownButton;
    protected DisplayChatWidget chatWidget;
    protected TextFieldWidget textFieldWidget;
    //    protected Button sendChatButton;
    
    protected Button duelChatButton;
    protected Button worldChatButton;
    protected boolean duelChat;
    
    protected List<ITextComponent> worldChatMessages;
    
    @SuppressWarnings("unchecked")
    public DuelContainerScreen(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.xSize = 234;
        this.ySize = 250;
        
        this.worldChatMessages = new ArrayList<>(32);
        this.textFieldWidget = null;
        this.duelChat = true;
        
        //default
        this.screensForEachState = new DuelScreenConstructor[DuelState.VALUES.length];
        this.screensForEachState[DuelState.IDLE.getIndex()] = DuelScreenIdle::new;
        this.screensForEachState[DuelState.PREPARING.getIndex()] = DuelScreenPreparing::new;
        this.screensForEachState[DuelState.END.getIndex()] = DuelScreenPreparing::new;
        this.screensForEachState[DuelState.DUELING.getIndex()] = DuelScreenDueling::new;
        this.screensForEachState[DuelState.SIDING.getIndex()] = DuelScreenDueling::new;
    }
    
    public DuelContainerScreen<E> setScreenForState(DuelState state, DuelScreenConstructor<E> screen)
    {
        this.screensForEachState[state.getIndex()] = screen;
        return this;
    }
    
    protected DuelContainerScreen<E> createNewScreenForState(DuelState state)
    {
        return this.screensForEachState[state.getIndex()].construct(this.container, this.playerInventory, this.title);
    }
    
    public final void duelStateChanged()
    {
        this.switchScreen(this.createNewScreenForState(this.getState()));
    }
    
    public final void reInit()
    {
        this.init(this.minecraft, this.width, this.height);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.renderDisabledRect(ms, 0, 0, this.width, this.height);
        
        ScreenUtil.white();
        this.minecraft.getTextureManager().bindTexture(DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    public void switchScreen(ContainerScreen<E> s)
    {
        super.switchScreen(s);
        
        if(s instanceof DuelContainerScreen)
        {
            DuelContainerScreen<E> screen = (DuelContainerScreen<E>)s;
            screen.screensForEachState = this.screensForEachState;
            screen.worldChatMessages = this.worldChatMessages;
        }
    }
    
    @Override
    protected void onGuiClose()
    {
        super.onGuiClose();
        this.getDuelManager().reset();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.textFieldWidget != null && this.textFieldWidget.isFocused() && !this.textFieldWidget.isMouseOver(mouseX, mouseY))
        {
            this.textFieldWidget.setFocused2(false);
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(this.textFieldWidget != null && this.textFieldWidget.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                this.sendChat();
                return true;
            }
            else
            {
                return this.textFieldWidget.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
    
    protected void initDefaultChat(int width, int height)
    {
        final int margin = 4;
        final int buttonHeight = 20;
        
        int x = this.guiLeft + this.xSize + margin;
        int y = this.guiTop + margin;
        
        int maxWidth = Math.min(160, (this.width - this.xSize) / 2 - 2 * margin);
        int maxHeight = this.ySize;
        
        int chatWidth = maxWidth;
        int chatHeight = (maxHeight - 4 * (buttonHeight + margin) - 2 * margin);
        
        this.initChat(width, height, x, y, maxWidth, maxHeight, chatWidth, chatHeight, margin, buttonHeight);
    }
    
    protected void initChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin, int buttonHeight)
    {
        final int offset = buttonHeight + margin;
        
        int halfW = w / 2;
        int extraOff = halfW % 2;
        
        this.addButton(this.duelChatButton = new Button(x, y, halfW, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.duel_chat"), (b) -> this.switchChat()));
        this.addButton(this.worldChatButton = new Button(x + halfW - extraOff, y, halfW + extraOff, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.world_chat"), (b) -> this.switchChat()));
        y += offset;
        
        this.addButton(this.chatUpButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.up_arrow"), this::chatScrollButtonClicked));
        y += offset;
        
        this.addButton(this.chatWidget = new DisplayChatWidget(x, y - (chatHeight % this.font.FONT_HEIGHT) / 2, chatWidth, chatHeight, StringTextComponent.EMPTY));
        y += chatHeight + margin;
        
        this.addButton(this.chatDownButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.down_arrow"), this::chatScrollButtonClicked));
        y += offset;
        
        this.addButton(this.textFieldWidget = new TextFieldWidget(this.font, x + 1, y + 1, w - 2, buttonHeight - 2, StringTextComponent.EMPTY));
        this.textFieldWidget.setMaxStringLength(64);
        y += offset;
        
        //        this.addButton(this.sendChatButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.send_chat"), (b) -> this.sendChat()));
        //        y += offset;
        
        this.appendToInitChat(width, height, extraOff, y, w, halfW, chatWidth, chatHeight, margin);
        
        this.duelChat = !this.duelChat;
        this.switchChat();
        
        this.chatUpButton.active = false;
        this.chatDownButton.active = false; //TODO remove these
        
        this.makeChatVisible();
    }
    
    protected void appendToInitChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin)
    {
        
    }
    
    protected void changeChatFlags(boolean flag)
    {
        this.chatUpButton.visible = flag;
        this.chatDownButton.visible = flag;
        this.chatWidget.visible = flag;
        this.textFieldWidget.visible = flag;
        //        this.sendChatButton.visible = flag;
        this.duelChatButton.visible = flag;
        this.worldChatButton.visible = flag;
    }
    
    public void makeChatVisible()
    {
        this.changeChatFlags(true);
    }
    
    public void makeChatInvisible()
    {
        this.changeChatFlags(false);
    }
    
    protected void sendChat()
    {
        String text = this.textFieldWidget.getText().trim();
        
        if(!text.isEmpty())
        {
            if(this.duelChat)
            {
                YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SendMessageToServer(this.getHeader(), new StringTextComponent(text)));
            }
            else
            {
                this.sendMessage(text, true);
            }
        }
        
        this.textFieldWidget.setText("");
    }
    
    protected void switchChat()
    {
        if(this.chatWidget.visible)
        {
            Button toEnable;
            Button toDisable;
            
            if(this.duelChat)
            {
                toEnable = this.duelChatButton;
                toDisable = this.worldChatButton;
                this.chatWidget.setTextSupplier(this.getWorldMessagesSupplier());
            }
            else
            {
                toEnable = this.worldChatButton;
                toDisable = this.duelChatButton;
                this.chatWidget.setTextSupplier(this.getDuelMessagesSupplier());
            }
            
            toEnable.active = true;
            toDisable.active = false;
            this.duelChat = !this.duelChat;
        }
    }
    
    protected Supplier<List<ITextComponent>> getDuelMessagesSupplier()
    {
        return () -> //TODO
        {
            List<ITextComponent> list = new ArrayList<>(DuelContainerScreen.this.getDuelManager().getMessages().size());
            
            for(DuelChatMessage msg : DuelContainerScreen.this.getDuelManager().getMessages())
            {
                list.add(msg.generateStyledMessage(DuelContainerScreen.this.getPlayerRole(), TextFormatting.BLUE, TextFormatting.RED, TextFormatting.WHITE));
            }
            
            return list;
        };
    }
    
    protected Supplier<List<ITextComponent>> getWorldMessagesSupplier()
    {
        return () -> ClientProxy.chatMessages;
    }
    
    protected void chatScrollButtonClicked(Button button)
    {
        //TODO
    }
    
    public void populateDeckSources(List<DeckSource> deckSources)
    {
    }
    
    public void receiveDeck(int index, DeckHolder deck)
    {
    }
    
    public void deckAccepted(PlayerRole role)
    {
    }
    
    public void handleAction(Action action)
    {
        action.init(this.getDuelManager().getPlayField());
        action.doAction();
    }
    
    public DuelManager getDuelManager()
    {
        return this.container.getDuelManager();
    }
    
    public PlayField getPlayField()
    {
        return this.getDuelManager().getPlayField();
    }
    
    public DuelMessageHeader getHeader()
    {
        return this.getDuelManager().headerFactory.get();
    }
    
    public DuelState getState()
    {
        return this.getDuelManager().getDuelState();
    }
    
    public PlayerRole getPlayerRole()
    {
        return this.getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }
    
    public ZoneOwner getZoneOwner()
    {
        PlayerRole role = this.getPlayerRole();
        
        if(ZoneOwner.PLAYER1.player == role)
        {
            return ZoneOwner.PLAYER1;
        }
        else if(ZoneOwner.PLAYER2.player == role)
        {
            return ZoneOwner.PLAYER2;
        }
        else
        {
            return ZoneOwner.NONE;
        }
    }
    
    public static interface DuelScreenConstructor<E extends DuelContainer>
    {
        public DuelContainerScreen<E> construct(E container, PlayerInventory inv, ITextComponent title);
    }
}
