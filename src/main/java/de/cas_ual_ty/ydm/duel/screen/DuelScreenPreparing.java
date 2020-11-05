package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.screen.widget.ItemStackWidget;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelScreenPreparing<E extends DuelContainer> extends DuelContainerScreen<E>
{
    protected AbstractButton prevDeckButton;
    protected AbstractButton nextDeckButton;
    protected AbstractButton chooseDeckButton;
    
    protected ItemStackWidget prevDeckWidget;
    protected ItemStackWidget activeDeckWidget;
    protected ItemStackWidget nextDeckWidget;
    
    protected List<DeckWrapper> deckWrappers;
    protected int activeDeckWrapperIdx;
    
    public DuelScreenPreparing(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.xSize = 234;
        this.ySize = 250;
        this.activeDeckWrapperIdx = 0;
    }
    
    @Override
    public void populateDeckSources(List<DeckSource> deckSources)
    {
        this.deckWrappers = new ArrayList<>(deckSources.size());
        this.activeDeckWrapperIdx = 0;
        
        for(int index = 0; index < deckSources.size(); ++index)
        {
            this.deckWrappers.add(new DeckWrapper(deckSources.get(index), index));
        }
        
        this.setActiveDeckWrapper(0);
    }
    
    @Override
    public void receiveDeck(int index, DeckHolder deck)
    {
        if(index >= 0 && index < this.deckWrappers.size())
        {
            this.deckWrappers.get(index).deck = deck;
            this.setActiveDeckWrapper(this.activeDeckWrapperIdx);
        }
    }
    
    @Override
    public void deckAccepted(PlayerRole role)
    {
        if(role == this.getPlayerRole())
        {
            this.reInit();
        }
    }
    
    public void setActiveDeckWrapper(int index)
    {
        if(this.deckWrappers == null)
        {
            return;
        }
        
        if(index >= this.deckWrappers.size())
        {
            index = 0;
        }
        else if(index < 0)
        {
            index = this.deckWrappers.size() - 1;
        }
        
        int prev = index - 1;
        
        if(prev < 0)
        {
            prev = this.deckWrappers.size() - 1;
        }
        
        int next = index + 1;
        
        if(next >= this.deckWrappers.size())
        {
            next = 0;
        }
        
        this.activeDeckWrapperIdx = index;
        
        DeckWrapper dPrev = this.deckWrappers.get(prev);
        dPrev.index = prev;
        this.prevDeckWidget.setItemStack(dPrev.source);
        
        DeckWrapper dActive = this.deckWrappers.get(index);
        dActive.index = index;
        this.activeDeckWidget.setItemStack(dActive.source);
        
        DeckWrapper dNext = this.deckWrappers.get(next);
        dNext.index = next;
        this.nextDeckWidget.setItemStack(dNext.source);
        
        this.prevDeckWidget.visible = true;
        this.activeDeckWidget.visible = true;
        this.nextDeckWidget.visible = true;
        
        if(!dActive.hasDeck())
        {
            this.requestDeck(dActive.index);
        }
    }
    
    // when true, deck choosing must be rendered, otherwise dont render it
    public boolean renderDeckChoosing()
    {
        return this.getPlayerRole() == PlayerRole.PLAYER1 ? this.getDuelManager().player1Deck == null : (this.getPlayerRole() == PlayerRole.PLAYER2 ? this.getDuelManager().player2Deck == null : false);
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
        
        int margin = (this.width - this.xSize) / 2;
        
        int x = width / 2;
        int y = height / 2;
        
        this.initDefaultChat(width, height);
        
        //without x+1 its technically not centered, i dont get why :(
        this.addButton(this.prevDeckButton = new Button(x - 16 - 16 - 10 - 5 - 10, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 10, 20, 20, new StringTextComponent("<"), (button) -> this.prevDeckClicked()));
        this.addButton(this.nextDeckButton = new Button(x - 16 + 32 + 16 + 5, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 10, 20, 20, new StringTextComponent(">"), (button) -> this.nextDeckClicked()));
        this.addButton(this.chooseDeckButton = new Button(x - 50, this.guiTop + this.ySize - 20 - 10, 100, 20, new StringTextComponent("Choose Deck"), (button) -> this.chooseDeckClicked()));
        
        this.addButton(this.prevDeckWidget = new ItemStackWidget(x - 16 - 16, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 8, 16, this.itemRenderer, ScreenUtil.getInfoCardBack()));
        this.addButton(this.activeDeckWidget = new ItemStackWidget(x - 16, this.guiTop + this.ySize - 20 - 10 - 5 - 32, 32, this.itemRenderer, ScreenUtil.getInfoCardBack()));
        this.addButton(this.nextDeckWidget = new ItemStackWidget(x - 16 + 32, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 8, 16, this.itemRenderer, ScreenUtil.getInfoCardBack()));
        this.prevDeckWidget.visible = false;
        this.activeDeckWidget.visible = false;
        this.nextDeckWidget.visible = false;
        
        this.setActiveDeckWrapper(this.activeDeckWrapperIdx);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY)
    {
        this.font.drawString(ms, "Choose your decks...", 8.0F, 6.0F, 0x404040);
        
        PlayerRole role = this.getPlayerRole();
        
        if(role == PlayerRole.PLAYER1 || role == PlayerRole.PLAYER2)
        {
            if(this.renderDeckChoosing())
            {
                this.drawActiveDeckForeground(ms, mouseX, mouseY);
            }
            else
            {
                String text = "Waiting for other player...";
                int width = this.font.getStringWidth(text);
                int height = this.font.FONT_HEIGHT;
                this.font.drawString(ms, text, (this.xSize - width) / 2F, (this.height - height) / 2F, 0x404040);
            }
        }
        else
        {
            String text = "Waiting for players...";
            int width = this.font.getStringWidth(text);
            int height = this.font.FONT_HEIGHT;
            this.font.drawString(ms, text, (this.xSize - width) / 2F, (this.ySize - height) / 2F, 0x404040);
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(ms, partialTicks, mouseX, mouseY);
        
        if(this.renderDeckChoosing())
        {
            this.drawActiveDeckBackground(ms, partialTicks, mouseX, mouseY);
        }
    }
    
    protected void drawActiveDeckForeground(MatrixStack ms, int mouseX, int mouseY)
    {
        DeckWrapper h = this.getActiveDeckWrapper();
        
        if(h != DeckWrapper.DUMMY)
        {
            DeckHolder d = h.deck;
            
            if(d != null && d != DeckHolder.DUMMY)
            {
                // coordinates from #drawActiveDeckBackground
                int xSize = 284;
                //                int ySize = 153;
                int actualGuiLeft = (this.width - xSize) / 2;
                int guiLeft = actualGuiLeft - this.guiLeft;
                int guiTop = this.guiTop + 6 + 5 + this.font.FONT_HEIGHT - this.guiTop;
                
                // from DeckBoxScreen#drawGuiContainerForegroundLayer
                
                mouseX -= (this.guiLeft + guiLeft) - 1;
                mouseY -= (this.guiTop + guiTop) - 1;
                
                // main deck
                //drawString
                this.font.func_243248_b(ms, new TranslationTextComponent("container.ydm.deck_box.main").appendString(" " + d.getMainDeckSize() + "/" + DeckHolder.MAIN_DECK_SIZE), guiLeft + 8F, guiTop + 6F, 0x404040);
                
                // extra deck
                //drawString
                this.font.func_243248_b(ms, new TranslationTextComponent("container.ydm.deck_box.extra").appendString(" " + d.getExtraDeckSize() + "/" + DeckHolder.EXTRA_DECK_SIZE), guiLeft + 8F, guiTop + 92F, 0x404040);
                
                // side deck
                //drawString
                this.font.func_243248_b(ms, new TranslationTextComponent("container.ydm.deck_box.side").appendString(" " + d.getSideDeckSize() + "/" + DeckHolder.SIDE_DECK_SIZE), guiLeft + 8F, guiTop + 124F, 0x404040);
                
                int size = 18;
                CardHolder c;
                
                //following code from DeckBoxContainer#<init>
                
                final int itemsPerRow = 15;
                
                // main deck
                boolean broken = false;
                int offX = 8;
                int offY = 18;
                for(int y = 0; y < DeckHolder.MAIN_DECK_SIZE / itemsPerRow; ++y)
                {
                    for(int x = 0; x < itemsPerRow && x + y * itemsPerRow < DeckHolder.MAIN_DECK_SIZE; ++x)
                    {
                        if(d.getMainDeck().size() <= x + y * itemsPerRow)
                        {
                            broken = true;
                            break;
                        }
                        
                        c = d.getMainDeck().get(x + y * itemsPerRow);
                        
                        if(c != null && c.getCard() != null)
                        {
                            ScreenUtil.bindMainResourceLocation(c);
                            YdmBlitUtil.fullBlit(ms, guiLeft + offX, guiTop + offY, 16, 16);
                            
                            if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                            {
                                this.renderHoverRect(ms, guiLeft + offX, guiTop + offY, 16, 16);
                                this.renderCardInfoForeground(ms, c, actualGuiLeft);
                            }
                        }
                        
                        offX += size;
                    }
                    
                    if(broken)
                    {
                        break;
                    }
                    
                    offX = 8;
                    offY += size;
                }
                
                // extra deck
                offX = 8;
                offY = 104;
                for(int x = 0; x < DeckHolder.EXTRA_DECK_SIZE; ++x)
                {
                    if(d.getExtraDeck().size() <= x)
                    {
                        break;
                    }
                    
                    c = d.getExtraDeck().get(x);
                    
                    if(c != null && c.getCard() != null)
                    {
                        ScreenUtil.bindMainResourceLocation(c);
                        YdmBlitUtil.fullBlit(ms, guiLeft + offX, guiTop + offY, 16, 16);
                        
                        if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                        {
                            this.renderHoverRect(ms, guiLeft + offX, guiTop + offY, 16, 16);
                            this.renderCardInfoForeground(ms, c, actualGuiLeft);
                        }
                    }
                    
                    offX += size;
                }
                
                // side deck
                offX = 8;
                offY = 136;
                for(int x = 0; x < DeckHolder.SIDE_DECK_SIZE; ++x)
                {
                    if(d.getSideDeck().size() <= x)
                    {
                        break;
                    }
                    
                    c = d.getSideDeck().get(x);
                    
                    if(c != null && c.getCard() != null)
                    {
                        ScreenUtil.bindMainResourceLocation(c);
                        YdmBlitUtil.fullBlit(ms, guiLeft + offX, guiTop + offY, 16, 16);
                        
                        if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                        {
                            this.renderHoverRect(ms, guiLeft + offX, guiTop + offY, 16, 16);
                            this.renderCardInfoForeground(ms, c, actualGuiLeft);
                        }
                    }
                    
                    offX += size;
                }
            }
        }
    }
    
    protected void drawActiveDeckBackground(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        DeckWrapper h = this.getActiveDeckWrapper();
        
        if(h != DeckWrapper.DUMMY)
        {
            DeckHolder d = h.deck;
            
            if(d != null && d != DeckHolder.DUMMY)
            {
                int xSize = 284;
                int ySize = 153;
                int guiLeft = (this.width - xSize) / 2;
                int guiTop = this.guiTop + 6 + 5 + this.font.FONT_HEIGHT;
                
                this.minecraft.getTextureManager().bindTexture(DuelContainerScreen.DECK_BACKGROUND_GUI_TEXTURE);
                YdmBlitUtil.blit(ms, guiLeft, guiTop, xSize, ySize, 0, 0, xSize, ySize, 512, 256);
                YdmBlitUtil.blit(ms, guiLeft, guiTop + ySize, xSize, 7, 0, 243, xSize, 7, 512, 256);
            }
        }
    }
    
    public void renderCardInfoForeground(MatrixStack ms, CardHolder c)
    {
        this.renderCardInfoForeground(ms, c, this.guiLeft);
    }
    
    public void renderCardInfoForeground(MatrixStack ms, CardHolder c, int width)
    {
        ms.push();
        
        ms.translate(-this.guiLeft, -this.guiTop, 0D);
        ScreenUtil.renderCardInfo(ms, c, width);
        
        ms.pop();
    }
    
    public void renderHoverRect(MatrixStack ms, int x, int y, int w, int h)
    {
        // from ContainerScreen#render
        
        RenderSystem.disableDepthTest();
        int j1 = x;
        int k1 = y;
        RenderSystem.colorMask(true, true, true, false);
        int slotColor = -2130706433; // from ContainerScreen#slotColor
        this.fillGradient(ms, j1, k1, j1 + w, k1 + h, slotColor, slotColor);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
    
    public void prevDeckClicked()
    {
        this.setActiveDeckWrapper(this.activeDeckWrapperIdx - 1);
    }
    
    public void nextDeckClicked()
    {
        this.setActiveDeckWrapper(this.activeDeckWrapperIdx + 1);
    }
    
    public DeckWrapper getActiveDeckWrapper()
    {
        if(this.deckWrappers == null || this.deckWrappers.size() <= this.activeDeckWrapperIdx)
        {
            return DeckWrapper.DUMMY;
        }
        else
        {
            return this.deckWrappers.get(this.activeDeckWrapperIdx);
        }
    }
    
    public void chooseDeckClicked()
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.ChooseDeck(this.getHeader(), this.getActiveDeckWrapper().index));
    }
    
    public void requestDeck(int index)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestDeck(this.getHeader(), index));
    }
    
    protected static class DeckWrapper
    {
        public static final DeckWrapper DUMMY = new DeckWrapper(new DeckSource(DeckHolder.DUMMY, new ItemStack(YdmItems.BLANC_CARD)), -1);
        
        public ItemStack source;
        public DeckHolder deck;
        public int index;
        
        public DeckWrapper(DeckSource source, int index)
        {
            this.source = source.source;
            this.deck = source.deck; //should be null
        }
        
        public boolean hasDeck()
        {
            return this.deck != null;
        }
        
        public ItemStack getShownItemStack()
        {
            return this.source;
        }
    }
}
