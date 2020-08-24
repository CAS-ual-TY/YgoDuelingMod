package de.cas_ual_ty.ydm.playmat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDeckProviders;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.deckbox.DeckProvider;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelMessages;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PlaymatScreen extends ContainerScreen<PlaymatContainer>
{
    private static final ResourceLocation PLAYMAT_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/playmat_foreground.png");
    private static final ResourceLocation PLAYMAT_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/playmat_background.png");
    
    private static final ResourceLocation DECK_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/deck_box.png");
    private static final ResourceLocation DECK_REPLACEMENT = new ResourceLocation(YDM.MOD_ID, "textures/item/card_back_" + ClientProxy.activeInfoImageSize + ".png");
    
    protected AbstractButton player1Button;
    protected AbstractButton player2Button;
    protected AbstractButton spectatorButton;
    
    protected AbstractButton prevDeckButton;
    protected AbstractButton nextDeckButton;
    protected AbstractButton chooseDeckButton;
    
    protected SimpleWidget prevDeckWidget;
    protected SimpleWidget activeDeckWidget;
    protected SimpleWidget nextDeckWidget;
    
    protected List<DeckProviderHolder> deckProviderHolders;
    protected int activeDeckProviderIndex;
    
    public PlaymatScreen(PlaymatContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.activeDeckProviderIndex = 0;
    }
    
    public void reInit()
    {
        this.init(this.getMinecraft(), this.width, this.height);
    }
    
    public void populateDeckProviders(List<ResourceLocation> deckProviderRLs)
    {
        this.deckProviderHolders = new ArrayList<>(deckProviderRLs.size());
        this.activeDeckProviderIndex = 0;
        
        DeckProviderHolder p;
        for(ResourceLocation rl : deckProviderRLs)
        {
            this.deckProviderHolders.add(p = new DeckProviderHolder(rl));
            
            if(p.hasDeckProvider())
            {
                p.populate();
            }
            else
            {
                this.requestDeckProviderPop(p.deckProviderRL);
            }
        }
        
        this.activateDeckProviders(0);
    }
    
    public void synchDeckProvider(ResourceLocation deckProviderRL, DeckHolder deck)
    {
        for(DeckProviderHolder provider : this.deckProviderHolders)
        {
            if(provider.deckProviderRL.equals(deckProviderRL))
            {
                provider.deckHolder = deck;
                break;
            }
        }
        
        this.activateDeckProviders(0);
    }
    
    public void activateDeckProviders(int index)
    {
        if(this.deckProviderHolders == null)
        {
            return;
        }
        
        if(index >= this.deckProviderHolders.size())
        {
            index = 0;
        }
        else if(index < 0)
        {
            index = this.deckProviderHolders.size() - 1;
        }
        
        int prev = index - 1;
        
        if(prev < 0)
        {
            prev = this.deckProviderHolders.size() - 1;
        }
        
        int next = index + 1;
        
        if(next >= this.deckProviderHolders.size())
        {
            next = 0;
        }
        
        this.activeDeckProviderIndex = index;
        
        DeckProviderHolder dPrev = this.deckProviderHolders.get(prev);
        this.prevDeckWidget.setRL(dPrev.getShownTexture() != null ? () -> dPrev.getShownTexture() : () -> PlaymatScreen.DECK_REPLACEMENT);
        
        DeckProviderHolder dActive = this.deckProviderHolders.get(index);
        this.activeDeckWidget.setRL(dActive.getShownTexture() != null ? () -> dActive.getShownTexture() : () -> PlaymatScreen.DECK_REPLACEMENT);
        
        DeckProviderHolder dNext = this.deckProviderHolders.get(next);
        this.nextDeckWidget.setRL(dNext.getShownTexture() != null ? () -> dNext.getShownTexture() : () -> PlaymatScreen.DECK_REPLACEMENT);
        
        this.prevDeckWidget.visible = true;
        this.activeDeckWidget.visible = true;
        this.nextDeckWidget.visible = true;
    }
    
    // when true, deck choosing must be rendered, otherwise dont render it
    public boolean renderDeckChoosing()
    {
        return this.getRole() == PlayerRole.PLAYER1 ? this.getDuelManager().player1Deck == null : (this.getRole() == PlayerRole.PLAYER2 ? this.getDuelManager().player2Deck == null : false);
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
        
        int x = width / 2;
        int y = height / 2;
        
        if(this.getState() == DuelState.IDLE)
        {
            this.addButton(this.player1Button = new RoleButton(x - 100, y - 40, 100, 20, "Player 1", this::roleButtonClicked, () -> this.getDuelManager().player1 == null && this.getRole() != PlayerRole.PLAYER1, PlayerRole.PLAYER1));
            this.addButton(this.player2Button = new RoleButton(x - 100, y - 10, 100, 20, "Player 2", this::roleButtonClicked, () -> this.getDuelManager().player2 == null && this.getRole() != PlayerRole.PLAYER2, PlayerRole.PLAYER2));
            this.addButton(this.spectatorButton = new RoleButton(x - 100, y + 20, 100, 20, "Spectators", this::roleButtonClicked, () -> this.getRole() != PlayerRole.SPECTATOR, PlayerRole.SPECTATOR));
            this.addButton(new RoleDescriptions(x, y - 40, 80, 20, this::getRoleDescription, PlayerRole.PLAYER1));
            this.addButton(new RoleDescriptions(x, y - 10, 80, 20, this::getRoleDescription, PlayerRole.PLAYER2));
            this.addButton(new RoleDescriptions(x, y + 20, 100, 20, this::getRoleDescription, PlayerRole.SPECTATOR));
            this.addButton(new ReadyCheckbox(x + 80, y - 40, 20, 20, "Ready 1", (button) -> this.ready1ButtonClicked(), () -> this.getDuelManager().player1Ready, this::getRole, PlayerRole.PLAYER1));
            this.addButton(new ReadyCheckbox(x + 80, y - 10, 20, 20, "Ready 2", (button) -> this.ready2ButtonClicked(), () -> this.getDuelManager().player2Ready, this::getRole, PlayerRole.PLAYER2));
        }
        else if(this.getState() == DuelState.PREPARING)
        {
            if(this.renderDeckChoosing())
            {
                /*
                this.addButton(this.prevDeckButton = new Button(x - 64 - 32, height - 20 - 10 - 10 - 32 - 10, 20, 20, "<", (button) -> this.prevDeckClicked()));
                this.addButton(this.nextDeckButton = new Button(x + 32 + 32 + 10, height - 20 - 10 - 10 - 32 - 10, 20, 20, ">", (button) -> this.nextDeckClicked()));
                this.addButton(this.chooseDeckButton = new Button(x - 50, height - 20 - 10, 100, 20, "Choose Deck", (button) -> this.chooseDeckClicked()));
                
                this.addButton(this.prevDeckWidget = new SimpleWidget(x - 64, height - 20 - 10 - 10 - 32 - 16, 32));
                this.addButton(this.activeDeckWidget = new SimpleWidget(x - 32, height - 20 - 10 - 10 - 64, 64));
                this.addButton(this.nextDeckWidget = new SimpleWidget(x + 32, height - 20 - 10 - 10 - 32 - 16, 32));
                */
                
                //without x+1 its technically not centered, i dont get why :(
                this.addButton(this.prevDeckButton = new Button(x - 16 - 16 - 10 - 5 - 10, height - 20 - 10 - 5 - 16 - 10, 20, 20, "<", (button) -> this.prevDeckClicked()));
                this.addButton(this.nextDeckButton = new Button(x - 16 + 32 + 16 + 5, height - 20 - 10 - 5 - 16 - 10, 20, 20, ">", (button) -> this.nextDeckClicked()));
                this.addButton(this.chooseDeckButton = new Button(x - 50, height - 20 - 5, 100, 20, "Choose Deck", (button) -> this.chooseDeckClicked()));
                
                this.addButton(this.prevDeckWidget = new SimpleWidget(x - 16 - 16, height - 20 - 10 - 5 - 16 - 8, 16));
                this.addButton(this.activeDeckWidget = new SimpleWidget(x - 16, height - 20 - 10 - 5 - 32, 32));
                this.addButton(this.nextDeckWidget = new SimpleWidget(x - 16 + 32, height - 20 - 10 - 5 - 16 - 8, 16));
                this.prevDeckWidget.visible = false;
                this.activeDeckWidget.visible = false;
                this.nextDeckWidget.visible = false;
                
                this.activateDeckProviders(0);
            }
        }
    }
    
    @Override
    protected void init()
    {
        this.xSize = 234;
        this.ySize = 250;
        super.init();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if(this.getState() == DuelState.IDLE)
        {
            this.font.drawString("Waiting for players...", 8.0F, 6.0F, 0x404040);
        }
        else if(this.getState() == DuelState.PREPARING)
        {
            this.font.drawString("Choose your decks...", 8.0F, 6.0F, 0x404040);
            
            PlayerRole role = this.getRole();
            
            if(role == PlayerRole.PLAYER1 || role == PlayerRole.PLAYER2)
            {
                if(this.renderDeckChoosing())
                {
                    this.drawActiveDeckForeground(mouseX, mouseY);
                }
                else
                {
                    String text = "Waiting for other player...";
                    int width = this.font.getStringWidth(text);
                    int height = this.font.FONT_HEIGHT;
                    this.font.drawString(text, (this.xSize - width) / 2F, (this.height - height) / 2F, 0x404040);
                }
            }
            else
            {
                String text = "Waiting for players...";
                int width = this.font.getStringWidth(text);
                int height = this.font.FONT_HEIGHT;
                this.font.drawString(text, (this.xSize - width) / 2F, (this.ySize - height) / 2F, 0x404040);
            }
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.minecraft.getTextureManager().bindTexture(PlaymatScreen.PLAYMAT_BACKGROUND_GUI_TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.getState() == DuelState.PREPARING)
        {
            this.drawActiveDeckBackground(partialTicks, mouseX, mouseY);
        }
        else if(this.getState() == DuelState.DUELING)
        {
            this.minecraft.getTextureManager().bindTexture(PlaymatScreen.PLAYMAT_FOREGROUND_GUI_TEXTURE);
            this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        }
    }
    
    protected void drawActiveDeckForeground(int mouseX, int mouseY)
    {
        DeckProviderHolder h = this.getActiveDeckProviderHolder();
        
        if(h != DeckProviderHolder.DUMMY)
        {
            DeckHolder d = h.deckHolder;
            
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
                String main = new TranslationTextComponent("container.ydm.deck_box.main").getFormattedText() + " " + d.getMainDeckSize() + "/" + DeckHolder.MAIN_DECK_SIZE;
                this.font.drawString(main, guiLeft + 8F, guiTop + 6F, 0x404040);
                
                // extra deck
                String extra = new TranslationTextComponent("container.ydm.deck_box.extra").getFormattedText() + " " + d.getExtraDeckSize() + "/" + DeckHolder.EXTRA_DECK_SIZE;
                this.font.drawString(extra, guiLeft + 8F, guiTop + 92F, 0x404040);
                
                // side deck
                String side = new TranslationTextComponent("container.ydm.deck_box.side").getFormattedText() + " " + d.getSideDeckSize() + "/" + DeckHolder.SIDE_DECK_SIZE;
                this.font.drawString(side, guiLeft + 8F, guiTop + 124F, 0x404040);
                
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
                            this.minecraft.getTextureManager().bindTexture(c.getMainImageResourceLocation());
                            ClientProxy.blit(guiLeft + offX, guiTop + offY, 16, 16, 0, 0, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize);
                            
                            if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                            {
                                this.renderHoverRect(guiLeft + offX, guiTop + offY, 16, 16);
                                this.renderCardInfoForeground(c, actualGuiLeft);
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
                        this.minecraft.getTextureManager().bindTexture(c.getMainImageResourceLocation());
                        ClientProxy.blit(guiLeft + offX, guiTop + offY, 16, 16, 0, 0, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize);
                        
                        if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                        {
                            this.renderHoverRect(guiLeft + offX, guiTop + offY, 16, 16);
                            this.renderCardInfoForeground(c, actualGuiLeft);
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
                        this.minecraft.getTextureManager().bindTexture(c.getMainImageResourceLocation());
                        ClientProxy.blit(guiLeft + offX, guiTop + offY, 16, 16, 0, 0, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize, ClientProxy.activeMainImageSize);
                        
                        if(mouseX >= offX && mouseX < offX + size && mouseY >= offY && mouseY < offY + size)
                        {
                            this.renderHoverRect(guiLeft + offX, guiTop + offY, 16, 16);
                            this.renderCardInfoForeground(c, actualGuiLeft);
                        }
                    }
                    
                    offX += size;
                }
            }
        }
    }
    
    protected void drawActiveDeckBackground(float partialTicks, int mouseX, int mouseY)
    {
        DeckProviderHolder h = this.getActiveDeckProviderHolder();
        
        if(h != DeckProviderHolder.DUMMY)
        {
            DeckHolder d = h.deckHolder;
            
            if(d != null && d != DeckHolder.DUMMY)
            {
                int xSize = 284;
                int ySize = 153;
                int guiLeft = (this.width - xSize) / 2;
                int guiTop = this.guiTop + 6 + 5 + this.font.FONT_HEIGHT;
                
                this.minecraft.getTextureManager().bindTexture(PlaymatScreen.DECK_BACKGROUND_GUI_TEXTURE);
                ClientProxy.blit(guiLeft, guiTop, xSize, ySize, 0, 0, xSize, ySize, 512, 256);
                ClientProxy.blit(guiLeft, guiTop + ySize, xSize, 7, 0, 243, xSize, 7, 512, 256);
            }
        }
    }
    
    public void renderCardInfoForeground(CardHolder c)
    {
        this.renderCardInfoForeground(c, this.guiLeft);
    }
    
    public void renderCardInfoForeground(CardHolder c, int width)
    {
        RenderSystem.pushMatrix();
        
        RenderSystem.translatef(-this.guiLeft, -this.guiTop, 0F);
        ClientProxy.renderCardInfo(c, width);
        
        RenderSystem.popMatrix();
    }
    
    public void renderHoverRect(int x, int y, int w, int h)
    {
        // from ContainerScreen#render
        
        RenderSystem.disableDepthTest();
        int j1 = x;
        int k1 = y;
        RenderSystem.colorMask(true, true, true, false);
        int slotColor = this.slotColor;
        this.fillGradient(j1, k1, j1 + w, k1 + h, slotColor, slotColor);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
    
    public DuelManager getDuelManager()
    {
        return this.getContainer().getDuelManager();
    }
    
    public DuelState getState()
    {
        return this.getDuelManager().getDuelState();
    }
    
    public PlayerRole getRole()
    {
        return this.getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }
    
    public String getRoleDescription(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return this.getDuelManager().player1 != null ? this.getDuelManager().player1.getScoreboardName() : "";
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return this.getDuelManager().player2 != null ? this.getDuelManager().player2.getScoreboardName() : "";
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            int size = this.getDuelManager().spectators.size();
            
            if(this.getRole() == PlayerRole.SPECTATOR)
            {
                if(size == 1)
                {
                    return ClientProxy.getPlayer().getScoreboardName();
                }
                else
                {
                    return ClientProxy.getPlayer().getScoreboardName() + " + " + (size - 1);
                }
            }
            else
            {
                return "" + size;
            }
        }
        
        return "";
    }
    
    public void roleButtonClicked(Button button)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SelectRole(((RoleButton)button).role));
    }
    
    public void ready1ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getRole() == PlayerRole.PLAYER1)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(!this.getDuelManager().player1Ready));
        }
    }
    
    public void ready2ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getRole() == PlayerRole.PLAYER2)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(!this.getDuelManager().player2Ready));
        }
    }
    
    public void prevDeckClicked()
    {
        this.activateDeckProviders(this.activeDeckProviderIndex - 1);
    }
    
    public void nextDeckClicked()
    {
        this.activateDeckProviders(this.activeDeckProviderIndex + 1);
    }
    
    public DeckProviderHolder getActiveDeckProviderHolder()
    {
        if(this.deckProviderHolders == null || this.deckProviderHolders.size() <= this.activeDeckProviderIndex)
        {
            return DeckProviderHolder.DUMMY;
        }
        else
        {
            return this.deckProviderHolders.get(this.activeDeckProviderIndex);
        }
    }
    
    public void chooseDeckClicked()
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.ChooseDeck(this.getActiveDeckProviderHolder().deckProviderRL));
    }
    
    public void requestDeckProviderPop(ResourceLocation rl)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestDeck(rl));
    }
    
    public static class DeckProviderHolder
    {
        public static final DeckProviderHolder DUMMY = new DeckProviderHolder(YdmDeckProviders.DUMMY.getRegistryName());
        
        public ResourceLocation deckProviderRL;
        public DeckProvider deckProvider;
        
        public DeckHolder deckHolder;
        
        public DeckProviderHolder(ResourceLocation deckProviderRL)
        {
            this.deckProviderRL = deckProviderRL;
            this.deckProvider = YDM.DECK_PROVIDERS_REGISTRY.getValue(this.deckProviderRL);
        }
        
        public void populate()
        {
            this.deckHolder = this.deckProvider.provideDeck(ClientProxy.getPlayer());
        }
        
        public boolean hasDeckProvider()
        {
            return this.deckProvider != null;
        }
        
        @Nullable
        public ResourceLocation getShownTexture()
        {
            if(this.deckProvider != null)
            {
                return this.deckProvider.getShownIcon(ClientProxy.getPlayer());
            }
            else
            {
                return null;
            }
        }
    }
    
    public static class SimpleWidget extends Widget
    {
        public Supplier<ResourceLocation> rl;
        
        public SimpleWidget(int xIn, int yIn, int size)
        {
            super(xIn, yIn, size, size, "");
            this.rl = null;
        }
        
        public SimpleWidget setRL(Supplier<ResourceLocation> rl)
        {
            this.rl = rl;
            return this;
        }
        
        public SimpleWidget setMsg(String msg)
        {
            this.setMessage(msg);
            return this;
        }
        
        @Override
        public void renderButton(int mouseX, int mouseY, float partial)
        {
            if(this.rl != null)
            {
                Minecraft minecraft = Minecraft.getInstance();
                FontRenderer fontrenderer = minecraft.fontRenderer;
                minecraft.getTextureManager().bindTexture(this.rl.get());
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                
                ClientProxy.blit(this.x, this.y, this.width, this.height, 0, 0, 256, 256, 256, 256);
                
                this.renderBg(minecraft, mouseX, mouseY);
                int j = this.getFGColor();
                this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
            }
        }
    }
    
    private static class RoleButton extends Button
    {
        public Supplier<Boolean> available;
        public PlayerRole role;
        
        public RoleButton(int xIn, int yIn, int widthIn, int heightIn, String text, IPressable onPress, Supplier<Boolean> available, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, text, onPress);
            this.available = available;
            this.role = role;
        }
        
        @Override
        public void render(int mouseX, int mouseY, float partial)
        {
            this.active = this.available.get();
            super.render(mouseX, mouseY, partial);
        }
    }
    
    private static class RoleDescriptions extends Widget
    {
        public Function<PlayerRole, String> nameGetter;
        public PlayerRole role;
        
        public RoleDescriptions(int xIn, int yIn, int widthIn, int heightIn, Function<PlayerRole, String> nameGetter, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, "");
            this.nameGetter = nameGetter;
            this.role = role;
            this.active = false;
        }
        
        @Override
        public void render(int mouseX, int mouseY, float partial)
        {
            this.setMessage(this.nameGetter.apply(this.role));
            super.render(mouseX, mouseY, partial);
        }
    }
    
    private static class ReadyCheckbox extends Button
    {
        private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
        
        public Supplier<Boolean> isChecked;
        public Supplier<PlayerRole> activeRole;
        public PlayerRole role;
        
        public ReadyCheckbox(int xIn, int yIn, int widthIn, int heightIn, String msg, IPressable onPress, Supplier<Boolean> isChecked, Supplier<PlayerRole> activeRole, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, "", onPress);
            this.isChecked = isChecked;
            this.activeRole = activeRole;
            this.role = role;
        }
        
        public boolean isRoleAssigned()
        {
            return this.role == this.activeRole.get();
        }
        
        @Override
        public void render(int mouseX, int mouseY, float partial)
        {
            this.active = this.isRoleAssigned();
            super.render(mouseX, mouseY, partial);
        }
        
        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(ReadyCheckbox.TEXTURE);
            RenderSystem.enableDepthTest();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            AbstractGui.blit(this.x, this.y, 0.0F, this.isChecked.get() ? 20.0F : 0.0F, 20, this.height, 32, 64);
            this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
            this.drawString(fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
