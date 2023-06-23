package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


import net.minecraft.ChatFormatting;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
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
    protected EditBox textFieldWidget;
    
    protected Button duelChatButton;
    protected Button worldChatButton;
    protected boolean duelChat;
    
    protected List<Component> worldChatMessages;
    
    protected Inventory playerInv;
    
    @SuppressWarnings("unchecked")
    public DuelContainerScreen(E screenContainer, Inventory inv, Component titleIn)
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
        
        this.playerInv = inv;
    }
    
    public DuelContainerScreen<E> setScreenForState(DuelState state, DuelScreenConstructor<E> screen)
    {
        screensForEachState[state.getIndex()] = screen;
        return this;
    }
    
    protected DuelContainerScreen<E> createNewScreenForState(DuelState state)
    {
        return screensForEachState[state.getIndex()].construct(menu, playerInv, title);
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
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.renderDisabledRect(ms, 0, 0, width, height);
        
        ScreenUtil.white();
        RenderSystem.setShaderTexture(0, DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    @Override
    public void switchScreen(AbstractContainerScreen<E> s)
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
    public void renderTooltip(PoseStack ms, List<? extends FormattedCharSequence> tooltips, int mouseX, int mouseY)
    {
        ms.pushPose();
        ms.translate(0, 0, 10D);
        super.renderTooltip(ms, tooltips, mouseX, mouseY);
        ms.popPose();
    }
    
    @Override
    public void renderTooltip(PoseStack ms, Component text, int mouseX, int mouseY)
    {
        ms.pushPose();
        ms.translate(0, 0, 10D);
        super.renderTooltip(ms, text, mouseX, mouseY);
        ms.popPose();
    }
    
    public void renderDisabledTooltip(PoseStack ms, List<FormattedCharSequence> tooltips, int mouseX, int mouseY)
    {
        tooltips.add(Component.literal("DISABLED").withStyle((s) -> s.applyFormat(ChatFormatting.ITALIC).applyFormat(ChatFormatting.RED)).getVisualOrderText());
        tooltips.add(Component.literal("COMING SOON").withStyle((s) -> s.applyFormat(ChatFormatting.ITALIC).applyFormat(ChatFormatting.RED)).getVisualOrderText());
        renderTooltip(ms, tooltips, mouseX, mouseY);
    }
    
    public void renderDisabledTooltip(PoseStack ms, @Nullable Component text, int mouseX, int mouseY)
    {
        List<FormattedCharSequence> tooltips = new LinkedList<>();
        
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
        
        addRenderableWidget(duelChatButton = new Button(x, y, halfW, buttonHeight, Component.translatable("container." + YDM.MOD_ID + ".duel.duel_chat"), (b) -> switchChat()));
        addRenderableWidget(worldChatButton = new Button(x + halfW - extraOff, y, halfW + extraOff, buttonHeight, Component.translatable("container." + YDM.MOD_ID + ".duel.world_chat"), (b) -> switchChat()));
        y += offset;
        
        addRenderableWidget(chatUpButton = new Button(x, y, w, buttonHeight, Component.translatable("container." + YDM.MOD_ID + ".duel.up_arrow"), this::chatScrollButtonClicked, this::chatScrollButtonHovered));
        y += offset;
        
        addRenderableWidget(chatWidget = new DisplayChatWidget(x, y - (chatHeight % font.lineHeight) / 2, chatWidth, chatHeight, Component.empty()));
        y += chatHeight + margin;
        
        addRenderableWidget(chatDownButton = new Button(x, y, w, buttonHeight, Component.translatable("container." + YDM.MOD_ID + ".duel.down_arrow"), this::chatScrollButtonClicked, this::chatScrollButtonHovered));
        y += offset;
        
        addRenderableWidget(textFieldWidget = new EditBox(font, x + 1, y + 1, w - 2, buttonHeight - 2, Component.empty()));
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
                YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SendMessageToServer(getHeader(), Component.literal(text)));
            }
            else
            {
                minecraft.player.chatSigned(text, null);
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
                chatWidget.setTextSupplier(getLevelMessagesSupplier());
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
    
    protected Supplier<List<Component>> getDuelMessagesSupplier()
    {
        return () -> //TODO
        {
            List<Component> list = new ArrayList<>(getDuelManager().getMessages().size());
            
            for(DuelChatMessage msg : getDuelManager().getMessages())
            {
                list.add(msg.generateStyledMessage(getPlayerRole(), ChatFormatting.BLUE, ChatFormatting.RED, ChatFormatting.WHITE));
            }
            
            return list;
        };
    }
    
    protected Supplier<List<Component>> getLevelMessagesSupplier()
    {
        return () -> ClientProxy.chatMessages;
    }
    
    protected void chatScrollButtonClicked(Button button)
    {
        //TODO
    }
    
    protected void chatScrollButtonHovered(AbstractWidget w, PoseStack ms, int mouseX, int mouseY)
    {
        //TODO
        renderDisabledTooltip(ms, (Component) null, mouseX, mouseY);
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
        DuelContainerScreen<E> construct(E container, Inventory inv, Component title);
    }
}
