package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.SwitchableContainerScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.*;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.widget.DisplayChatWidget;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

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
    
    protected Button duelChatButton;
    protected Button worldChatButton;
    protected boolean duelChat;
    
    protected List<ITextComponent> worldChatMessages;
    
    @SuppressWarnings("unchecked")
    public DuelContainerScreen(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        imageWidth = 234;
        imageHeight = 250;
        
        worldChatMessages = new ArrayList<>(32);
        textFieldWidget = null;
        duelChat = true;
        
        //default
        screensForEachState = new DuelScreenConstructor[DuelState.VALUES.length];
        screensForEachState[DuelState.IDLE.getIndex()] = DuelScreenIdle::new;
        screensForEachState[DuelState.PREPARING.getIndex()] = DuelScreenPreparing::new;
        screensForEachState[DuelState.END.getIndex()] = DuelScreenPreparing::new;
        screensForEachState[DuelState.DUELING.getIndex()] = DuelScreenDueling::new;
        screensForEachState[DuelState.SIDING.getIndex()] = DuelScreenDueling::new;
    }
    
    public DuelContainerScreen<E> setScreenForState(DuelState state, DuelScreenConstructor<E> screen)
    {
        screensForEachState[state.getIndex()] = screen;
        return this;
    }
    
    protected DuelContainerScreen<E> createNewScreenForState(DuelState state)
    {
        return screensForEachState[state.getIndex()].construct(menu, inventory, title);
    }
    
    public final void duelStateChanged()
    {
        switchScreen(createNewScreenForState(getState()));
    }
    
    public final void reInit()
    {
        init(minecraft, width, height);
    }
    
    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.renderDisabledRect(ms, 0, 0, width, height);
        
        ScreenUtil.white();
        minecraft.getTextureManager().bind(DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    @Override
    public void switchScreen(ContainerScreen<E> s)
    {
        super.switchScreen(s);
        
        if(s instanceof DuelContainerScreen)
        {
            DuelContainerScreen<E> screen = (DuelContainerScreen<E>) s;
            screen.screensForEachState = screensForEachState;
            screen.worldChatMessages = worldChatMessages;
        }
    }
    
    @Override
    protected void onGuiClose()
    {
        super.onGuiClose();
        getDuelManager().reset();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(textFieldWidget != null && textFieldWidget.isFocused() && !textFieldWidget.isMouseOver(mouseX, mouseY))
        {
            textFieldWidget.setFocus(false);
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(textFieldWidget != null && textFieldWidget.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                sendChat();
                return true;
            }
            else
            {
                return textFieldWidget.keyPressed(keyCode, scanCode, modifiers);
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
        ms.pushPose();
        ms.translate(0, 0, 10D);
        super.renderTooltip(ms, tooltips, mouseX, mouseY);
        ms.popPose();
    }
    
    @Override
    public void renderTooltip(MatrixStack ms, ITextComponent text, int mouseX, int mouseY)
    {
        ms.pushPose();
        ms.translate(0, 0, 10D);
        super.renderTooltip(ms, text, mouseX, mouseY);
        ms.popPose();
    }
    
    public void renderDisabledTooltip(MatrixStack ms, List<IReorderingProcessor> tooltips, int mouseX, int mouseY)
    {
        tooltips.add(new StringTextComponent("DISABLED").withStyle((s) -> s.applyFormat(TextFormatting.ITALIC).applyFormat(TextFormatting.RED)).getVisualOrderText());
        tooltips.add(new StringTextComponent("COMING SOON").withStyle((s) -> s.applyFormat(TextFormatting.ITALIC).applyFormat(TextFormatting.RED)).getVisualOrderText());
        renderTooltip(ms, tooltips, mouseX, mouseY);
    }
    
    public void renderDisabledTooltip(MatrixStack ms, @Nullable ITextComponent text, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> tooltips = new LinkedList<>();
        
        if(text != null)
        {
            tooltips.add(text.getVisualOrderText());
        }
        
        renderDisabledTooltip(ms, tooltips, mouseX, mouseY);
    }
    
    protected void initDefaultChat(int width, int height)
    {
        final int margin = 4;
        final int buttonHeight = 20;
        
        int x = leftPos + imageWidth + margin;
        int y = topPos + margin;
        
        int maxWidth = Math.min(160, (this.width - imageWidth) / 2 - 2 * margin);
        int maxHeight = imageHeight;
        
        int chatWidth = maxWidth;
        int chatHeight = (maxHeight - 4 * (buttonHeight + margin) - 2 * margin);
        
        initChat(width, height, x, y, maxWidth, maxHeight, chatWidth, chatHeight, margin, buttonHeight);
    }
    
    protected void initChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin, int buttonHeight)
    {
        final int offset = buttonHeight + margin;
        
        int halfW = w / 2;
        int extraOff = halfW % 2;
        
        addButton(duelChatButton = new Button(x, y, halfW, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.duel_chat"), (b) -> switchChat()));
        addButton(worldChatButton = new Button(x + halfW - extraOff, y, halfW + extraOff, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.world_chat"), (b) -> switchChat()));
        y += offset;
        
        addButton(chatUpButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.up_arrow"), this::chatScrollButtonClicked, this::chatScrollButtonHovered));
        y += offset;
        
        addButton(chatWidget = new DisplayChatWidget(x, y - (chatHeight % font.lineHeight) / 2, chatWidth, chatHeight, StringTextComponent.EMPTY));
        y += chatHeight + margin;
        
        addButton(chatDownButton = new Button(x, y, w, buttonHeight, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.down_arrow"), this::chatScrollButtonClicked, this::chatScrollButtonHovered));
        y += offset;
        
        addButton(textFieldWidget = new TextFieldWidget(font, x + 1, y + 1, w - 2, buttonHeight - 2, StringTextComponent.EMPTY));
        textFieldWidget.setMaxLength(64);
        y += offset;
        
        appendToInitChat(width, height, extraOff, y, w, halfW, chatWidth, chatHeight, margin);
        
        duelChat = !duelChat;
        switchChat();
        
        chatUpButton.active = false;
        chatDownButton.active = false; //TODO remove these
        
        makeChatVisible();
    }
    
    protected void appendToInitChat(int width, int height, int x, int y, int w, int h, int chatWidth, int chatHeight, int margin)
    {
        
    }
    
    protected void changeChatFlags(boolean flag)
    {
        chatUpButton.visible = flag;
        chatDownButton.visible = flag;
        chatWidget.visible = flag;
        textFieldWidget.visible = flag;
        duelChatButton.visible = flag;
        worldChatButton.visible = flag;
    }
    
    public void makeChatVisible()
    {
        changeChatFlags(true);
    }
    
    public void makeChatInvisible()
    {
        changeChatFlags(false);
    }
    
    protected void sendChat()
    {
        String text = textFieldWidget.getValue().trim();
        
        if(!text.isEmpty())
        {
            if(duelChat)
            {
                YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SendMessageToServer(getHeader(), new StringTextComponent(text)));
            }
            else
            {
                sendMessage(text, true);
            }
        }
        
        textFieldWidget.setValue("");
    }
    
    protected void switchChat()
    {
        if(chatWidget.visible)
        {
            Button toEnable;
            Button toDisable;
            
            if(duelChat)
            {
                toEnable = duelChatButton;
                toDisable = worldChatButton;
                chatWidget.setTextSupplier(getWorldMessagesSupplier());
            }
            else
            {
                toEnable = worldChatButton;
                toDisable = duelChatButton;
                chatWidget.setTextSupplier(getDuelMessagesSupplier());
            }
            
            toEnable.active = true;
            toDisable.active = false;
            duelChat = !duelChat;
        }
    }
    
    protected Supplier<List<ITextComponent>> getDuelMessagesSupplier()
    {
        return () -> //TODO
        {
            List<ITextComponent> list = new ArrayList<>(getDuelManager().getMessages().size());
            
            for(DuelChatMessage msg : getDuelManager().getMessages())
            {
                list.add(msg.generateStyledMessage(getPlayerRole(), TextFormatting.BLUE, TextFormatting.RED, TextFormatting.WHITE));
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
    
    protected void chatScrollButtonHovered(Widget w, MatrixStack ms, int mouseX, int mouseY)
    {
        //TODO
        renderDisabledTooltip(ms, (ITextComponent) null, mouseX, mouseY);
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
        action.initClient(getDuelManager().getPlayField());
        action.doAction();
    }
    
    public DuelManager getDuelManager()
    {
        return menu.getDuelManager();
    }
    
    public PlayField getPlayField()
    {
        return getDuelManager().getPlayField();
    }
    
    public DuelMessageHeader getHeader()
    {
        return getDuelManager().headerFactory.get();
    }
    
    public DuelState getState()
    {
        return getDuelManager().getDuelState();
    }
    
    public PlayerRole getPlayerRole()
    {
        return getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }
    
    public ZoneOwner getZoneOwner()
    {
        PlayerRole role = getPlayerRole();
        
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
    
    public interface DuelScreenConstructor<E extends DuelContainer>
    {
        DuelContainerScreen<E> construct(E container, PlayerInventory inv, ITextComponent title);
    }
}
