package de.cas_ual_ty.ydm.cardbinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import de.cas_ual_ty.ydm.clientutil.widget.TextureButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardBinderScreen extends AbstractContainerScreen<CardBinderContainer>
{
    private static final ResourceLocation CARD_BINDER_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/card_binder.png");
    
    // https://www.glfw.org/docs/latest/group__keys.html
    private static final int LEFT_SHIFT = 340;
    private static final int Q = 81;
    
    protected CardButton[] cardButtons;
    
    protected Button reloadButton;
    protected Button prevButton;
    protected Button nextButton;
    
    protected int centerX;
    protected int centerY;
    
    protected EditBox cardSearch;
    
    public CardBinderScreen(CardBinderContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected void init()
    {
        imageWidth = 176;
        imageHeight = 114 + CardInventory.DEFAULT_PAGE_ROWS * 18; //222
        super.init();
        imageWidth += 27; //insertion slot on the right; gui is not centered
        
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
                addRenderableWidget(button);
            }
        }
        
        addRenderableWidget(prevButton = new ImprovedButton(leftPos + imageWidth - 24 - 8 - 27, topPos + 4, 12, 12, Component.translatable("generic.ydm.left_arrow"), this::onButtonClicked));
        addRenderableWidget(nextButton = new ImprovedButton(leftPos + imageWidth - 12 - 8 - 27, topPos + 4, 12, 12, Component.translatable("generic.ydm.right_arrow"), this::onButtonClicked));
        
        addRenderableWidget(reloadButton = new TextureButton(leftPos + imageWidth - 12 - 8 - 27, topPos + imageHeight - 96, 12, 12, Component.empty(), this::onButtonClicked)
                .setTexture(new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png"), 64, 0, 16, 16));
        addRenderableWidget(cardSearch = new EditBox(font, leftPos + imageWidth - 12 - 8 - 27 - 82, topPos + imageHeight - 96, 80, 12, Component.empty()));
    }
    
    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
        
        for(CardButton button : cardButtons)
        {
            if(button.isHoveredOrFocused())
            {
                if(button.getCard() != null)
                {
                    CardRenderUtil.renderCardInfo(ms, button.getCard(), this);
                    
                    List<Component> list = new LinkedList<>();
                    button.getCard().addInformation(list);
                    
                    List<Component> tooltip = new ArrayList<>(list.size());
                    for(Component t : list)
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
    protected void renderLabels(PoseStack ms, int mouseX, int mouseY)
    {
        MutableComponent title = Component.literal(this.title.getString());
        
        if(!getMenu().loaded)
        {
            title = title.append(" ").append(Component.translatable("container.ydm.card_binder.loading"));
        }
        else
        {
            title = title.append(" ").append(Component.literal(menu.page + "/" + menu.clientMaxPage));
        }
        
        font.draw(ms, title, 8.0F, 6.0F, 0x404040);
        
        font.draw(ms, playerInventoryTitle.getVisualOrderText(), 8.0F, (float) (imageHeight - 96 + 2), 0x404040);
    }
    
    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.white();
        RenderSystem.setShaderTexture(0, CardBinderScreen.CARD_BINDER_GUI_TEXTURE);
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
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.ChangeSearch(cardSearch.getValue()));
        }
    }
    
    protected void onCardClicked(CardButton button, int index)
    {
        if(!getMenu().loaded)
        {
            return;
        }
        
        if(button.getCard() != null)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.IndexClicked(index));
        }
    }
    
    protected CardHolder getCard(int index)
    {
        return index < getMenu().clientList.size() ? getMenu().clientList.get(index) : null;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(cardSearch != null && cardSearch.isFocused())
        {
            return cardSearch.keyPressed(keyCode, scanCode, modifiers);
        }
        else if(getMenu().loaded)
        {
            if(keyCode == CardBinderScreen.Q)
            {
                for(CardButton button : cardButtons)
                {
                    if(button.isHoveredOrFocused() && button.getCard() != null)
                    {
                        YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardBinderMessages.IndexDropped(button.index));
                        break;
                    }
                }
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
