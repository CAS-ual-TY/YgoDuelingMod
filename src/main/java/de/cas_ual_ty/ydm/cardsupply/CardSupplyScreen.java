package de.cas_ual_ty.ydm.cardsupply;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.Rarity;
import de.cas_ual_ty.ydm.cardbinder.CardButton;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardSupplyScreen extends ContainerScreen<CardSupplyContainer>
{
    private static final ResourceLocation CARD_SUPPLY_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/card_supply.png");
    
    public static final int ROWS = 6;
    public static final int COLUMNS = 9;
    public static final int PAGE = CardSupplyScreen.ROWS * CardSupplyScreen.COLUMNS;
    
    public List<CardHolder> cardsList;
    public TextFieldWidget textField;
    protected Button prevButton;
    protected Button nextButton;
    public int page;
    
    public CardButton[] cardButtons;
    
    public CardSupplyScreen(CardSupplyContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        imageWidth = 176;
        imageHeight = 114 + 6 * 18; //222
        cardsList = new ArrayList<>(YdmDatabase.getTotalCardsAndVariants());
    }
    
    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        
        addButton(textField = new TextFieldWidget(font, leftPos + imageWidth - 80 - 8 - 1, topPos + 6 - 1, 80 + 2, font.lineHeight + 2, StringTextComponent.EMPTY));
        
        int index;
        CardButton button;
        cardButtons = new CardButton[CardSupplyScreen.PAGE];
        
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
        
        addButton(prevButton = new ImprovedButton(leftPos + imageWidth - 80 - 8, topPos + imageHeight - 96, 40, 12, new TranslationTextComponent("container.ydm.card_supply.prev"), this::onButtonClicked));
        addButton(nextButton = new ImprovedButton(leftPos + imageWidth - 40 - 8, topPos + imageHeight - 96, 40, 12, new TranslationTextComponent("container.ydm.card_supply.next"), this::onButtonClicked));
        
        applyName();
        updateCards();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
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
    protected void renderBg(MatrixStack ms, float partialTicks, int x, int y)
    {
        minecraft.getTextureManager().bind(CardSupplyScreen.CARD_SUPPLY_GUI_TEXTURE);
        blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY)
    {
        font.draw(ms, title, 8.0F, 6.0F, 0x404040);
        font.draw(ms, inventory.getDisplayName(), 8.0F, (float) (imageHeight - 96 + 2), 0x404040);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(textField != null && textField.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                applyName();
                return true;
            }
            else
            {
                return textField.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
    
    protected void onButtonClicked(Button button)
    {
        int minPage = 0;
        int maxPage = cardsList.size() / CardSupplyScreen.PAGE + 1;
        
        if(button == prevButton)
        {
            --page;
            
            if(page < minPage)
            {
                page = maxPage;
            }
        }
        else if(button == nextButton)
        {
            ++page;
            
            if(page > maxPage)
            {
                page = minPage;
            }
        }
    }
    
    public void applyName()
    {
        String name = textField.getValue().toLowerCase();
        
        cardsList.clear();
        page = 0;
        
        YdmDatabase.forAllCardVariants((card, imageIndex) ->
        {
            if(card.getName().toLowerCase().contains(name))
            {
                cardsList.add(new CardHolder(card, imageIndex, Rarity.SUPPLY.name));
            }
        });
    }
    
    public void updateCards()
    {
        page = 0;
        cardsList.clear();
        
        YdmDatabase.forAllCardVariants((card, imageIndex) ->
        {
            cardsList.add(new CardHolder(card, imageIndex, Rarity.SUPPLY.name));
        });
    }
    
    protected void onCardClicked(CardButton button, int index)
    {
        if(button.getCard() != null && button.getCard().getCard() != null)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardSupplyMessages.RequestCard(button.getCard().getCard(), button.getCard().getImageIndex()));
        }
    }
    
    protected CardHolder getCard(int index0)
    {
        int index = scopeIndex(index0);
        return index < cardsList.size() ? cardsList.get(index) : null;
    }
    
    protected int scopeIndex(int cardButtonIndex)
    {
        return page * CardSupplyScreen.PAGE + cardButtonIndex;
    }
}
