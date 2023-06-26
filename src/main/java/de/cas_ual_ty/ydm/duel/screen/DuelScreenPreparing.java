package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import de.cas_ual_ty.ydm.clientutil.widget.ItemStackWidget;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DeckSource;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

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
    protected boolean deckChosen;
    
    public DuelScreenPreparing(E screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        activeDeckWrapperIdx = 0;
        deckChosen = false;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        int x = width / 2;
        
        if(!deckChosen && getZoneOwner() != ZoneOwner.NONE)
        {
            // Faking this to make the chat smaller on the right side
            int prevXSize = imageWidth;
            int prevGuiLeft = leftPos;
            imageWidth = 284;
            leftPos = (width - imageWidth) / 2;
            initDefaultChat(width, height);
            imageWidth = prevXSize;
            leftPos = prevGuiLeft;
            
            //without x+1 its technically not centered, i dont get why :(
            int chooseWidth = imageWidth - 20;
            addRenderableWidget(prevDeckButton = new ImprovedButton(x - 16 - 16 - 10 - 5 - 10, topPos + imageHeight - 20 - 10 - 5 - 16 - 10, 20, 20, Component.translatable("container." + YDM.MOD_ID + ".duel.left_arrow"), (button) -> prevDeckClicked()));
            addRenderableWidget(nextDeckButton = new ImprovedButton(x - 16 + 32 + 16 + 5, topPos + imageHeight - 20 - 10 - 5 - 16 - 10, 20, 20, Component.translatable("container." + YDM.MOD_ID + ".duel.right_arrow"), (button) -> nextDeckClicked()));
            addRenderableWidget(chooseDeckButton = new ImprovedButton(x - chooseWidth / 2, topPos + imageHeight - 20 - 10, chooseWidth, 20, Component.translatable("container." + YDM.MOD_ID + ".duel.choose_deck"), (button) -> chooseDeckClicked(), this::chooseDeckTooltip));
            
            addRenderableWidget(prevDeckWidget = new ItemStackWidget(x - 16 - 16, topPos + imageHeight - 20 - 10 - 5 - 16 - 8, 16, itemRenderer, CardRenderUtil.getInfoCardBack()));
            addRenderableWidget(activeDeckWidget = new ItemStackWidget(x - 16, topPos + imageHeight - 20 - 10 - 5 - 32, 32, itemRenderer, CardRenderUtil.getInfoCardBack()));
            addRenderableWidget(nextDeckWidget = new ItemStackWidget(x - 16 + 32, topPos + imageHeight - 20 - 10 - 5 - 16 - 8, 16, itemRenderer, CardRenderUtil.getInfoCardBack()));
            
            prevDeckWidget.visible = false;
            activeDeckWidget.visible = false;
            nextDeckWidget.visible = false;
            
            setActiveDeckWrapper(activeDeckWrapperIdx);
        }
        else
        {
            initDefaultChat(width, height);
        }
    }
    
    @Override
    protected void renderLabels(PoseStack ms, int mouseX, int mouseY)
    {
        font.draw(ms, "Choose your decks...", 8.0F, 6.0F, 0x404040);
        
        PlayerRole role = getPlayerRole();
        
        if(role == PlayerRole.PLAYER1 || role == PlayerRole.PLAYER2)
        {
            if(renderDeckChoosing())
            {
                drawActiveDeckForeground(ms, mouseX, mouseY);
            }
            else
            {
                String text = "Waiting for other player...";
                int width = font.width(text);
                int height = font.lineHeight;
                font.draw(ms, text, (imageWidth - width) / 2F, (this.height - height) / 2F, 0x404040);
            }
        }
        else
        {
            String text = "Waiting for players...";
            int width = font.width(text);
            int height = font.lineHeight;
            font.draw(ms, text, (imageWidth - width) / 2F, (imageHeight - height) / 2F, 0x404040);
        }
    }
    
    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(ms, partialTicks, mouseX, mouseY);
        
        if(renderDeckChoosing())
        {
            drawActiveDeckBackground(ms, partialTicks, mouseX, mouseY);
        }
    }
    
    protected void drawActiveDeckForeground(PoseStack ms, int mouseX, int mouseY)
    {
        DeckWrapper h = getActiveDeckWrapper();
        
        if(h != DeckWrapper.DUMMY)
        {
            DeckHolder d = h.deck;
            
            if(d != null && d != DeckHolder.DUMMY)
            {
                // coordinates from #drawActiveDeckBackground
                int xSize = 284;
                //                int ySize = 153;
                int actualGuiLeft = (width - xSize) / 2;
                int guiLeft = actualGuiLeft - leftPos;
                int guiTop = topPos + 6 + 5 + font.lineHeight - topPos;
                
                // from DeckBoxScreen#drawGuiContainerForegroundLayer
                
                mouseX -= (leftPos + guiLeft) - 1;
                mouseY -= (topPos + guiTop) - 1;
                
                // main deck
                //drawString
                font.draw(ms, Component.translatable("container.ydm.deck_box.main").append(" " + d.getMainDeckSize() + "/" + DeckHolder.MAIN_DECK_SIZE), guiLeft + 8F, guiTop + 6F, 0x404040);
                
                // extra deck
                //drawString
                font.draw(ms, Component.translatable("container.ydm.deck_box.extra").append(" " + d.getExtraDeckSize() + "/" + DeckHolder.EXTRA_DECK_SIZE), guiLeft + 8F, guiTop + 92F, 0x404040);
                
                // side deck
                //drawString
                font.draw(ms, Component.translatable("container.ydm.deck_box.side").append(" " + d.getSideDeckSize() + "/" + DeckHolder.SIDE_DECK_SIZE), guiLeft + 8F, guiTop + 124F, 0x404040);
                
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
                            CardRenderUtil.bindMainResourceLocation(c);
                            YdmBlitUtil.fullBlit(ms, guiLeft + offX, guiTop + offY, 16, 16);
                            
                            if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                            {
                                ScreenUtil.renderHoverRect(ms, guiLeft + offX, guiTop + offY, 16, 16);
                                renderCardInfoForeground(ms, c, actualGuiLeft);
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
                        CardRenderUtil.bindMainResourceLocation(c);
                        YdmBlitUtil.fullBlit(ms, guiLeft + offX, guiTop + offY, 16, 16);
                        
                        if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                        {
                            ScreenUtil.renderHoverRect(ms, guiLeft + offX, guiTop + offY, 16, 16);
                            renderCardInfoForeground(ms, c, actualGuiLeft);
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
                        CardRenderUtil.bindMainResourceLocation(c);
                        YdmBlitUtil.fullBlit(ms, guiLeft + offX, guiTop + offY, 16, 16);
                        
                        if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                        {
                            ScreenUtil.renderHoverRect(ms, guiLeft + offX, guiTop + offY, 16, 16);
                            renderCardInfoForeground(ms, c, actualGuiLeft);
                        }
                    }
                    
                    offX += size;
                }
            }
        }
    }
    
    protected void drawActiveDeckBackground(PoseStack ms, float partialTicks, int mouseX, int mouseY)
    {
        DeckWrapper h = getActiveDeckWrapper();
        
        if(h != DeckWrapper.DUMMY)
        {
            DeckHolder d = h.deck;
            
            if(d != null && d != DeckHolder.DUMMY)
            {
                int xSize = 284;
                int ySize = 153;
                int guiLeft = (width - xSize) / 2;
                int guiTop = topPos + 6 + 5 + font.lineHeight;
                
                RenderSystem.setShaderTexture(0, DuelContainerScreen.DECK_BACKGROUND_GUI_TEXTURE);
                YdmBlitUtil.blit(ms, guiLeft, guiTop, xSize, ySize, 0, 0, xSize, ySize, 512, 256);
                YdmBlitUtil.blit(ms, guiLeft, guiTop + ySize, xSize, 7, 0, 243, xSize, 7, 512, 256);
            }
        }
    }
    
    @Override
    public void populateDeckSources(List<DeckSource> deckSources)
    {
        deckWrappers = new ArrayList<>(deckSources.size());
        activeDeckWrapperIdx = 0;
        
        for(int index = 0; index < deckSources.size(); ++index)
        {
            deckWrappers.add(new DeckWrapper(deckSources.get(index), index));
        }
        
        setActiveDeckWrapper(0);
    }
    
    @Override
    public void receiveDeck(int index, DeckHolder deck)
    {
        if(index >= 0 && index < deckWrappers.size())
        {
            deckWrappers.get(index).deck = deck;
            setActiveDeckWrapper(activeDeckWrapperIdx);
        }
    }
    
    @Override
    public void deckAccepted(PlayerRole role)
    {
        if(role == getPlayerRole())
        {
            deckChosen = true;
            reInit();
        }
    }
    
    public void setActiveDeckWrapper(int index)
    {
        if(deckWrappers == null)
        {
            return;
        }
        
        if(index >= deckWrappers.size())
        {
            index = 0;
        }
        else if(index < 0)
        {
            index = deckWrappers.size() - 1;
        }
        
        int prev = index - 1;
        
        if(prev < 0)
        {
            prev = deckWrappers.size() - 1;
        }
        
        int next = index + 1;
        
        if(next >= deckWrappers.size())
        {
            next = 0;
        }
        
        activeDeckWrapperIdx = index;
        
        DeckWrapper dPrev = deckWrappers.get(prev);
        dPrev.index = prev;
        prevDeckWidget.setItemStack(dPrev.source);
        
        DeckWrapper dActive = deckWrappers.get(index);
        dActive.index = index;
        activeDeckWidget.setItemStack(dActive.source);
        
        DeckWrapper dNext = deckWrappers.get(next);
        dNext.index = next;
        nextDeckWidget.setItemStack(dNext.source);
        
        prevDeckWidget.visible = true;
        activeDeckWidget.visible = true;
        nextDeckWidget.visible = true;
        
        if(!dActive.hasDeck())
        {
            requestDeck(dActive.index);
        }
        
        chooseDeckButton.setMessage(dActive.name);
    }
    
    // when true, deck choosing must be rendered, otherwise dont render it
    public boolean renderDeckChoosing()
    {
        return getPlayerRole() == PlayerRole.PLAYER1 ? getDuelManager().player1Deck == null : (getPlayerRole() == PlayerRole.PLAYER2 ? getDuelManager().player2Deck == null : false);
    }
    
    public void renderCardInfoForeground(PoseStack ms, CardHolder c)
    {
        renderCardInfoForeground(ms, c, leftPos);
    }
    
    public void renderCardInfoForeground(PoseStack ms, CardHolder c, int width)
    {
        ms.pushPose();
        
        ms.translate(-leftPos, -topPos, 0D);
        CardRenderUtil.renderCardInfo(ms, c, width);
        
        ms.popPose();
    }
    
    protected void prevDeckClicked()
    {
        setActiveDeckWrapper(activeDeckWrapperIdx - 1);
    }
    
    protected void nextDeckClicked()
    {
        setActiveDeckWrapper(activeDeckWrapperIdx + 1);
    }
    
    protected void chooseDeckTooltip(AbstractWidget w, PoseStack ms, int mouseX, int mouseY)
    {
        renderTooltip(ms, Component.translatable("container.ydm.duel.choose_deck"), mouseX, mouseY);
    }
    
    public DeckWrapper getActiveDeckWrapper()
    {
        if(deckWrappers == null || deckWrappers.size() <= activeDeckWrapperIdx)
        {
            return DeckWrapper.DUMMY;
        }
        else
        {
            return deckWrappers.get(activeDeckWrapperIdx);
        }
    }
    
    protected void chooseDeckClicked()
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.ChooseDeck(getHeader(), getActiveDeckWrapper().index));
    }
    
    public void requestDeck(int index)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestDeck(getHeader(), index));
    }
    
    protected static class DeckWrapper
    {
        public static final DeckWrapper DUMMY = new DeckWrapper(new DeckSource(DeckHolder.DUMMY, new ItemStack(YdmItems.BLANC_CARD.get())), -1);
        
        public ItemStack source;
        public Component name;
        public DeckHolder deck;
        public int index;
        
        public DeckWrapper(DeckSource source, int index)
        {
            this.source = source.source;
            name = source.name;
            deck = source.deck; //should be null
        }
        
        public boolean hasDeck()
        {
            return deck != null;
        }
        
        public ItemStack getShownItemStack()
        {
            return source;
        }
    }
}
