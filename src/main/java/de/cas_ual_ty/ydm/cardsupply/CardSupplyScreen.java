package de.cas_ual_ty.ydm.cardsupply;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CustomCards;
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
        this.xSize = 176;
        this.ySize = 114 + 6 * 18; //222
        this.cardsList = new ArrayList<>(YdmDatabase.CARDS_LIST.size());
    }
    
    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        
        this.addButton(this.textField = new TextFieldWidget(this.font, this.guiLeft + this.xSize - 80 - 8 - 1, this.guiTop + 6 - 1, 80 + 2, this.font.FONT_HEIGHT + 2, StringTextComponent.EMPTY));
        
        int index;
        CardButton button;
        this.cardButtons = new CardButton[CardSupplyScreen.PAGE];
        
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
        
        this.addButton(this.prevButton = new ImprovedButton(this.guiLeft + this.xSize - 80 - 8, this.guiTop + this.ySize - 96, 40, 12, new TranslationTextComponent("container.ydm.card_supply.prev"), this::onButtonClicked));
        this.addButton(this.nextButton = new ImprovedButton(this.guiLeft + this.xSize - 40 - 8, this.guiTop + this.ySize - 96, 40, 12, new TranslationTextComponent("container.ydm.card_supply.next"), this::onButtonClicked));
        
        this.applyName();
        this.updateCards();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(ms, mouseX, mouseY);
        
        for(CardButton button : this.cardButtons)
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
                    this.func_243308_b(ms, tooltip, mouseX, mouseY);
                }
                
                break;
            }
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int x, int y)
    {
        this.minecraft.getTextureManager().bindTexture(CardSupplyScreen.CARD_SUPPLY_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY)
    {
        this.font.func_243248_b(ms, this.title, 8.0F, 6.0F, 0x404040);
        this.font.func_243248_b(ms, this.playerInventory.getDisplayName(), 8.0F, (float)(this.ySize - 96 + 2), 0x404040);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(this.textField != null && this.textField.isFocused())
        {
            if(keyCode == GLFW.GLFW_KEY_ENTER)
            {
                this.applyName();
                return true;
            }
            else
            {
                return this.textField.keyPressed(keyCode, scanCode, modifiers);
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
        int maxPage = this.cardsList.size() / CardSupplyScreen.PAGE + 1;
        
        if(button == this.prevButton)
        {
            --this.page;
            
            if(this.page < minPage)
            {
                this.page = maxPage;
            }
        }
        else if(button == this.nextButton)
        {
            ++this.page;
            
            if(this.page > maxPage)
            {
                this.page = minPage;
            }
        }
    }
    
    public void applyName()
    {
        String name = this.textField.getText().toLowerCase();
        
        this.cardsList.clear();
        this.page = 0;
        
        for(Card c : YdmDatabase.CARDS_LIST)
        {
            if(c.getProperties().getName().toLowerCase().contains(name))
            {
                this.cardsList.add(new CardHolder(c, (byte)-1, Rarity.SUPPLY.name));
            }
        }
    }
    
    public void updateCards()
    {
        this.page = 0;
        this.cardsList.clear();
        
        for(Card c : YdmDatabase.CARDS_LIST)
        {
            if(c != CustomCards.DUMMY_CARD)
            {
                this.cardsList.add(new CardHolder(c, (byte)-1, Rarity.SUPPLY.name));
            }
        }
    }
    
    protected void onCardClicked(CardButton button, int index)
    {
        if(button.getCard() != null && button.getCard().getCard() != null)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CardSupplyMessages.RequestCard(button.getCard().getCard()));
        }
    }
    
    protected CardHolder getCard(int index0)
    {
        int index = this.scopeIndex(index0);
        return index < this.cardsList.size() ? this.cardsList.get(index) : null;
    }
    
    protected int scopeIndex(int cardButtonIndex)
    {
        return this.page * CardSupplyScreen.PAGE + cardButtonIndex;
    }
}
