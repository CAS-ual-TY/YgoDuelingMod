package de.cas_ual_ty.ydm.cardbinder;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import de.cas_ual_ty.ydm.clientutil.widget.TextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardBinderScreen extends ContainerScreen<CardBinderContainer> implements IHasContainer<CardBinderContainer>
{
    private static final ResourceLocation CARD_BINDER_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/card_binder.png");
    
    // https://www.glfw.org/docs/latest/group__keys.html
    private static final int LEFT_SHIFT = 340;
    private static final int Q = 81;
    
    protected CardButton[] cardButtons;
    
    protected Button reloadButton;
    protected Button prevButton;
    protected Button nextButton;
    
    protected boolean shiftDown;
    
    protected int centerX;
    protected int centerY;
    
    protected TextFieldWidget cardSearch;
    
    public CardBinderScreen(CardBinderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        shiftDown = false;
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
        
        int index;
        CardButton button;
        cardButtons = new CardButton[CardInventory.DEFAULT_CARDS_PER_PAGE];
        
        for(int y = 0; y < CardInventory.DEFAULT_PAGE_ROWS; ++y)
        {
            for(int x = 0; x < CardInventory.DEFAULT_PAGE_COLUMNS; ++x)
            {
                index = x + y * 9;
                button = new CardButton(leftPos + 7 + x * 18, topPos + 17 + y * 18, 18, 18, index, this::onCardClicked, this::getCard);
                cardButtons[index] = button;
                addButton(button);
            }
        }
    
        addButton(prevButton = new ImprovedButton(leftPos + imageWidth - 24 - 8 - 27, topPos + 4, 12, 12, new TranslationTextComponent("generic.ydm.left_arrow"), this::onButtonClicked));
        addButton(nextButton = new ImprovedButton(leftPos + imageWidth - 12 - 8 - 27, topPos + 4, 12, 12, new TranslationTextComponent("generic.ydm.right_arrow"), this::onButtonClicked));
    
        addButton(reloadButton = new TextureButton(leftPos + imageWidth - 12 - 8 - 27, topPos + imageHeight - 96, 12, 12, StringTextComponent.EMPTY, this::onButtonClicked)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 64, 0, 16, 16));
        addButton(cardSearch = new TextFieldWidget(font, leftPos + imageWidth - 12 - 8 - 27 - 82, topPos + imageHeight - 96, 80, 12, StringTextComponent.EMPTY));
    }
    
    @Override
    protected void init()
    {
        imageWidth = 176;
        imageHeight = 114 + CardInventory.DEFAULT_PAGE_ROWS * 18; //222
        super.init();
        imageWidth += 27; //insertion slot on the right; gui is not centered
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
        
        for(CardButton button : cardButtons)
        {
            if(button.isHovered())
            {
                if(button.getCard() != null)
                {
                    CardRenderUtil.renderCardInfo(ms, button.getCard(), this);
                    
                    List<ITextComponent> list = new LinkedList<>();
                    button.getCard().addInformation(list);
                    
                    List<ITextComponent> tooltip = new ArrayList<>(list.size());
                    for(ITextComponent t : list)
                    {
                        tooltip.add(t);
                    }
                    
                    //renderTooltip
                    renderComponentTooltip(ms, tooltip, mouseX, mouseY);
                }
                
                break;
            }
        }
    }
    
    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY)
    {
        IFormattableTextComponent title = new StringTextComponent(this.title.getString());
        
        if(!getMenu().loaded)
        {
            title = title.append(" ").append(new TranslationTextComponent("container.ydm.card_binder.loading"));
        }
        else
        {
            title = title.append(" ").append(new StringTextComponent(menu.page + "/" + menu.clientMaxPage));
        }
        
        font.draw(ms, title, 8.0F, 6.0F, 0x404040);
        
        font.draw(ms, inventory.getDisplayName(), 8.0F, (float) (imageHeight - 96 + 2), 0x404040);
    }
    
    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.white();
        minecraft.getTextureManager().bind(CardBinderScreen.CARD_BINDER_GUI_TEXTURE);
        blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    protected void onButtonClicked(Button button)
    {
        if(!getMenu().loaded)
        {
            return;
        }
        
        if(button == prevButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.ChangePage(false));
        }
        else if(button == nextButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.ChangePage(true));
        }
        else if(button == reloadButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.ChangeSearch(this.cardSearch.getValue()));
        }
    }
    
    protected void onCardClicked(CardButton button, int index)
    {
        if(!getMenu().loaded)
        {
            return;
        }
        
        if(button.getCard() != null && YDM.proxy.getClientPlayer().inventory.getCarried().isEmpty())
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.IndexClicked(index, shiftDown));
            
            if(!shiftDown)
            {
                ItemStack itemStack = YdmItems.CARD.createItemForCardHolder(button.getCard());
                YDM.proxy.getClientPlayer().inventory.setCarried(itemStack);
            }
        }
    }
    
    protected CardHolder getCard(int index)
    {
        return index < getMenu().clientList.size() ? getMenu().clientList.get(index) : null;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(this.cardSearch != null && this.cardSearch.isFocused())
        {
            return this.cardSearch.keyPressed(keyCode, scanCode, modifiers);
        }
        else if(getMenu().loaded)
        {
            if(keyCode == CardBinderScreen.LEFT_SHIFT)
            {
                shiftDown = true;
            }
            else if(keyCode == CardBinderScreen.Q)
            {
                for(CardButton button : cardButtons)
                {
                    if(button.isHovered() && button.getCard() != null)
                    {
                        YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.IndexDropped(button.index));
                        break;
                    }
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
            shiftDown = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}
