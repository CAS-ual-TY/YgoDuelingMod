package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.SwitchableContainerScreen;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duelmanager.DuelChatMessage;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessages;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class DuelContainerScreen<E extends DuelContainer> extends SwitchableContainerScreen<E>
{
    protected Button chatUpButton;
    protected Button chatDownButton;
    protected DisplayChatWidget chatWidget;
    protected TextFieldWidget textFieldWidget;
    protected Button sendChatButton;
    
    protected Button duelChatButton;
    protected Button worldChatButton;
    protected boolean duelChat;
    
    protected List<ITextComponent> worldChatMessages;
    
    public DuelContainerScreen(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.worldChatMessages = new ArrayList<>(32);
        this.textFieldWidget = null;
        this.duelChat = true;
    }
    
    public void reInit()
    {
        this.init(this.minecraft, this.width, this.height);
    }
    
    protected void initDefaultChat(int width, int height)
    {
        int centerY = height / 2 - 12;
        int chatHeight = Math.max(32, ((height - 5 * (20 + 4)) / 32) * 32);
        this.initDefaultChat(width, height, centerY, chatHeight);
    }
    
    protected void initDefaultChat(int width, int height, int centerY, int chatHeight)
    {
        int chatWidth = (width - this.xSize) / 2 - 2 * 4;
        this.initChat(width, height, centerY, chatWidth, chatHeight);
    }
    
    protected void initChat(int width, int height, int centerY, int chatWidth, int chatHeight)
    {
        // actual height will be different, as you need to add the buttons and margin as well
        // in total you need to consider another 20 + 4 on each side, so +48 height more in total
        
        int rightMargin = (width - this.xSize) / 2;
        int x = (width) - (rightMargin + chatWidth) / 2;
        int y = centerY - chatHeight / 2;
        int w = chatWidth;
        int h = chatHeight;
        
        int verticalButtonsOff = 4;
        this.addButton(this.chatUpButton = new Button(x, y - 20 - verticalButtonsOff, w, 20, new StringTextComponent("Up"), this::chatScrollButtonClicked));
        this.chatUpButton.active = false;
        this.addButton(this.chatDownButton = new Button(x, y + chatHeight + verticalButtonsOff, w, 20, new StringTextComponent("Down"), this::chatScrollButtonClicked));
        this.chatDownButton.active = false;
        this.addButton(this.chatWidget = new DisplayChatWidget(x, y - (chatHeight % this.font.FONT_HEIGHT) / 2, chatWidth, chatHeight, StringTextComponent.EMPTY));
        this.addButton(this.textFieldWidget = new TextFieldWidget(this.font, x, y + chatHeight + 20 + 2 * verticalButtonsOff, w, 20, StringTextComponent.EMPTY));
        this.addButton(this.sendChatButton = new Button(x, y + chatHeight + 2 * 20 + 3 * verticalButtonsOff, w, 20, new StringTextComponent(">"), (b) -> this.sendChat()));
        //        this.addButton(this.sendChatButton = new Button(4, 4, 20, 20, new StringTextComponent(">"), (b) -> this.sendChat()));
        
        int halfW = w / 2;
        this.addButton(this.duelChatButton = new Button(x, y - 2 * 20 - 2 * verticalButtonsOff, halfW, 20, new StringTextComponent("Duel"), (b) -> this.switchChat()));
        this.addButton(this.worldChatButton = new Button(x + chatWidth - halfW, y - 2 * 20 - 2 * verticalButtonsOff, halfW, 20, new StringTextComponent("World"), (b) -> this.switchChat()));
        
        this.duelChat = !this.duelChat;
        this.switchChat();
        
        this.makeChatVisible();
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int x, int y)
    {
        DuelingDuelScreen.renderDisabledRect(ms, 0, 0, this.width, this.height);
    }
    
    protected void changeChatFlags(boolean flag)
    {
        this.chatUpButton.visible = flag;
        this.chatDownButton.visible = flag;
        this.chatWidget.visible = flag;
        this.textFieldWidget.visible = flag;
        this.sendChatButton.visible = flag;
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
            
            this.textFieldWidget.setText("");
        }
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
    
    @Override
    public void switchScreen(ContainerScreen<E> screen)
    {
        super.switchScreen(screen);
        
        if(screen instanceof DuelContainerScreen)
        {
            ((DuelContainerScreen<? extends DuelContainer>)screen).worldChatMessages = this.worldChatMessages;
        }
    }
    
    @Override
    protected void onGuiClose()
    {
        super.onGuiClose();
        this.getDuelManager().reset();
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
    
    public DuelManager getDuelManager()
    {
        return this.container.getDuelManager();
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
}
