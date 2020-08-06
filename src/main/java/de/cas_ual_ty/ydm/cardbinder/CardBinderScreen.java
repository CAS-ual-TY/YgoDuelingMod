package de.cas_ual_ty.ydm.cardbinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CardBinderScreen extends ContainerScreen<CardBinderContainer> implements IHasContainer<CardBinderContainer>
{
    private static final ResourceLocation CARD_BINDER_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/card_binder.png");
    
    // https://www.glfw.org/docs/latest/group__keys.html
    private static final int LEFT_SHIFT = 340;
    private static final int Q = 81;
    
    protected CardButton[] cardButtons;
    
    protected Button prevButton;
    protected Button nextButton;
    
    protected boolean shiftDown;
    
    public CardBinderScreen(CardBinderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.ySize = 114 + CardInventory.DEFAULT_PAGE_ROWS * 18;
        this.shiftDown = false;
    }
    
    @Override
    public void init(Minecraft mc, int mouseX, int mouseY)
    {
        super.init(mc, mouseX, mouseY);
        
        int index;
        CardButton button;
        this.cardButtons = new CardButton[CardInventory.DEFAULT_CARDS_PER_PAGE];
        
        for(int y = 0; y < CardInventory.DEFAULT_PAGE_ROWS; ++y)
        {
            for(int x = 0; x < CardInventory.DEFAULT_PAGE_COLUMNS; ++x)
            {
                index = x + y * 9;
                button = new CardButton(this.guiLeft + 7 + x * 18, this.guiTop + 17 + y * 18, 18, 18, index, this::onCardClicked, this::getCard);
                this.cardButtons[index] = button;
                this.addButton(button);
            }
        }
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.prevButton = new Button(centerX + 40, centerY - 117, 40, 20, new TranslationTextComponent("container.ydm.card_binder.prev").getFormattedText(), this::onButtonClicked);
        this.nextButton = new Button(centerX + 80, centerY - 117, 40, 20, new TranslationTextComponent("container.ydm.card_binder.next").getFormattedText(), this::onButtonClicked);
        this.addButton(this.prevButton);
        this.addButton(this.nextButton);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        for(CardButton button : this.cardButtons)
        {
            if(button.isHovered())
            {
                if(button.getCard() != null)
                {
                    ClientProxy.renderCardInfo(button.getCard(), this);
                    
                    List<ITextComponent> list = new LinkedList<>();
                    button.getCard().addInformation(list);
                    
                    List<String> tooltip = new ArrayList<>(list.size());
                    for(ITextComponent t : list)
                    {
                        tooltip.add(t.getFormattedText());
                    }
                    
                    this.renderTooltip(tooltip, mouseX, mouseY, this.font);
                }
                
                break;
            }
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String title = this.title.getFormattedText();
        
        if(!this.getContainer().loaded)
        {
            title += " " + new TranslationTextComponent("container.ydm.card_binder.loading").getFormattedText();
        }
        else
        {
            title += " " + this.container.page + "/" + this.container.clientMaxPage;
        }
        
        this.font.drawString(title, 8.0F, 6.0F, 0x404040);
        
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 0x404040);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CardBinderScreen.CARD_BINDER_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(x, y, 0, 0, this.xSize + 27, CardInventory.DEFAULT_PAGE_ROWS * 18 + 17);
        this.blit(x, y + CardInventory.DEFAULT_PAGE_ROWS * 18 + 17, 0, 126, this.xSize + 27, 96);
    }
    
    protected void onButtonClicked(Button button)
    {
        if(button == this.prevButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.ChangePage(false));
        }
        else if(button == this.nextButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.ChangePage(true));
        }
    }
    
    protected void onCardClicked(CardButton button, int index)
    {
        if(button.getCard() != null)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.IndexClicked(index, this.shiftDown));
            
            if(!this.shiftDown)
            {
                ItemStack itemStack = YdmItems.CARD.createItemForCardHolder(button.getCard());
                YDM.proxy.getClientPlayer().inventory.setItemStack(itemStack);
            }
        }
    }
    
    protected CardHolder getCard(int index)
    {
        return index < this.getContainer().clientList.size() ? this.getContainer().clientList.get(index) : null;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(keyCode == CardBinderScreen.LEFT_SHIFT)
        {
            this.shiftDown = true;
        }
        else if(keyCode == CardBinderScreen.Q)
        {
            for(CardButton button : this.cardButtons)
            {
                if(button.isHovered() && button.getCard() != null)
                {
                    YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.IndexDropped(button.index));
                    break;
                }
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if(keyCode == CardBinderScreen.LEFT_SHIFT)
        {
            this.shiftDown = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}