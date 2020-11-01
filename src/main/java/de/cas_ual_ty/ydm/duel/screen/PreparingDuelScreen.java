package de.cas_ual_ty.ydm.duel.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class PreparingDuelScreen extends DuelContainerScreen<DuelContainer>
{
    public static final ResourceLocation DUEL_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_foreground.png");
    public static final ResourceLocation DUEL_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_background.png");
    
    public static final ResourceLocation DECK_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/deck_box.png");
    
    public static final ResourceLocation DUEL_ACTIONS_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_actions.png");
    public static final ResourceLocation DUEL_ACTIONS_LARGE_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_actions_large.png");
    
    protected AbstractButton player1Button;
    protected AbstractButton player2Button;
    protected AbstractButton spectatorButton;
    
    protected AbstractButton prevDeckButton;
    protected AbstractButton nextDeckButton;
    protected AbstractButton chooseDeckButton;
    
    protected ItemStackWidget prevDeckWidget;
    protected ItemStackWidget activeDeckWidget;
    protected ItemStackWidget nextDeckWidget;
    
    protected List<DeckWrapper> deckWrappers;
    protected int activeDeckWrapperIdx;
    
    public PreparingDuelScreen(DuelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.activeDeckWrapperIdx = 0;
    }
    
    @Override
    public void reInit()
    {
        this.init(this.getMinecraft(), this.width, this.height);
    }
    
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
    
    public void receiveDeck(int index, DeckHolder deck)
    {
        if(index >= 0 && index < this.deckWrappers.size())
        {
            this.deckWrappers.get(index).deck = deck;
            this.setActiveDeckWrapper(this.activeDeckWrapperIdx);
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
        
        int x = width / 2;
        int y = height / 2;
        
        if(this.getState() == DuelState.IDLE)
        {
            this.addButton(this.player1Button = new RoleButton(x - 100, y - 40, 100, 20, new StringTextComponent("Player 1"), this::roleButtonClicked, () -> this.getDuelManager().player1 == null && this.getPlayerRole() != PlayerRole.PLAYER1, PlayerRole.PLAYER1));
            this.addButton(this.player2Button = new RoleButton(x - 100, y - 10, 100, 20, new StringTextComponent("Player 2"), this::roleButtonClicked, () -> this.getDuelManager().player2 == null && this.getPlayerRole() != PlayerRole.PLAYER2, PlayerRole.PLAYER2));
            this.addButton(this.spectatorButton = new RoleButton(x - 100, y + 20, 100, 20, new StringTextComponent("Spectators"), this::roleButtonClicked, () -> this.getPlayerRole() != PlayerRole.SPECTATOR, PlayerRole.SPECTATOR));
            this.addButton(new RoleOccupants(x, y - 40, 80, 20, this::getRoleDescription, PlayerRole.PLAYER1));
            this.addButton(new RoleOccupants(x, y - 10, 80, 20, this::getRoleDescription, PlayerRole.PLAYER2));
            this.addButton(new RoleOccupants(x, y + 20, 100, 20, this::getRoleDescription, PlayerRole.SPECTATOR));
            this.addButton(new ReadyCheckbox(x + 80, y - 40, 20, 20, "Ready 1", (button) -> this.ready1ButtonClicked(), () -> this.getDuelManager().player1Ready, this::getPlayerRole, PlayerRole.PLAYER1));
            this.addButton(new ReadyCheckbox(x + 80, y - 10, 20, 20, "Ready 2", (button) -> this.ready2ButtonClicked(), () -> this.getDuelManager().player2Ready, this::getPlayerRole, PlayerRole.PLAYER2));
        }
        else if(this.getState() == DuelState.PREPARING)
        {
            if(this.renderDeckChoosing())
            {
                //without x+1 its technically not centered, i dont get why :(
                this.addButton(this.prevDeckButton = new Button(x - 16 - 16 - 10 - 5 - 10, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 10, 20, 20, new StringTextComponent("<"), (button) -> this.prevDeckClicked()));
                this.addButton(this.nextDeckButton = new Button(x - 16 + 32 + 16 + 5, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 10, 20, 20, new StringTextComponent(">"), (button) -> this.nextDeckClicked()));
                this.addButton(this.chooseDeckButton = new Button(x - 50, this.guiTop + this.ySize - 20 - 10, 100, 20, new StringTextComponent("Choose Deck"), (button) -> this.chooseDeckClicked()));
                
                this.addButton(this.prevDeckWidget = new ItemStackWidget(x - 16 - 16, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 8, 16, this.itemRenderer, ClientProxy.getInfoCardBack()));
                this.addButton(this.activeDeckWidget = new ItemStackWidget(x - 16, this.guiTop + this.ySize - 20 - 10 - 5 - 32, 32, this.itemRenderer, ClientProxy.getInfoCardBack()));
                this.addButton(this.nextDeckWidget = new ItemStackWidget(x - 16 + 32, this.guiTop + this.ySize - 20 - 10 - 5 - 16 - 8, 16, this.itemRenderer, ClientProxy.getInfoCardBack()));
                this.prevDeckWidget.visible = false;
                this.activeDeckWidget.visible = false;
                this.nextDeckWidget.visible = false;
                
                this.setActiveDeckWrapper(this.activeDeckWrapperIdx);
            }
        }
        else if(this.getState() == DuelState.DUELING)
        {
            this.switchScreen(new DuelingDuelScreen(this.container, this.playerInventory, this.getTitle()));
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
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY)
    {
        if(this.getState() == DuelState.IDLE)
        {
            this.font.drawString(ms, "Waiting for players...", 8.0F, 6.0F, 0x404040);
        }
        else if(this.getState() == DuelState.PREPARING)
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
        else if(this.getState() == DuelState.DUELING)
        {
            //TODO no access to partial ticks here ??? moved to background
            //this.duelRenderer.render(mouseX, mouseY, 1F);
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.minecraft.getTextureManager().bindTexture(PreparingDuelScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.getState() == DuelState.PREPARING)
        {
            if(this.renderDeckChoosing())
            {
                this.drawActiveDeckBackground(ms, partialTicks, mouseX, mouseY);
            }
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
                            ClientProxy.bindMainResourceLocation(c);
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
                        ClientProxy.bindMainResourceLocation(c);
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
                        ClientProxy.bindMainResourceLocation(c);
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
                
                this.minecraft.getTextureManager().bindTexture(PreparingDuelScreen.DECK_BACKGROUND_GUI_TEXTURE);
                YdmBlitUtil.blit(ms, guiLeft, guiTop, xSize, ySize, 0, 0, xSize, ySize, 512, 256);
                YdmBlitUtil.blit(ms, guiLeft, guiTop + ySize, xSize, 7, 0, 243, xSize, 7, 512, 256);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public void renderCardInfoForeground(MatrixStack ms, CardHolder c)
    {
        this.renderCardInfoForeground(ms, c, this.guiLeft);
    }
    
    public void renderCardInfoForeground(MatrixStack ms, CardHolder c, int width)
    {
        ms.push();
        
        ms.translate(-this.guiLeft, -this.guiTop, 0D);
        ClientProxy.renderCardInfo(ms, c, width);
        
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
    
    public ITextComponent getRoleDescription(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return new StringTextComponent(this.getDuelManager().player1 != null ? this.getDuelManager().player1.getScoreboardName() : "");
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return new StringTextComponent(this.getDuelManager().player2 != null ? this.getDuelManager().player2.getScoreboardName() : "");
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            int size = this.getDuelManager().spectators.size();
            
            if(this.getPlayerRole() == PlayerRole.SPECTATOR)
            {
                if(size == 1)
                {
                    return new StringTextComponent(ClientProxy.getPlayer().getScoreboardName());
                }
                else
                {
                    return new StringTextComponent(ClientProxy.getPlayer().getScoreboardName() + " + " + (size - 1));
                }
            }
            else
            {
                return new StringTextComponent("" + size);
            }
        }
        
        return StringTextComponent.EMPTY;
    }
    
    public void roleButtonClicked(Button button)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SelectRole(this.getHeader(), ((RoleButton)button).role));
    }
    
    public void ready1ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getPlayerRole() == PlayerRole.PLAYER1)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(this.getHeader(), !this.getDuelManager().player1Ready));
        }
    }
    
    public void ready2ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getPlayerRole() == PlayerRole.PLAYER2)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(this.getHeader(), !this.getDuelManager().player2Ready));
        }
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
    
    public static class DeckWrapper
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
    
    public static class ItemStackWidget extends Widget
    {
        public ItemStack itemStack;
        public ItemRenderer itemRenderer;
        public ResourceLocation replacement;
        
        public ItemStackWidget(int xIn, int yIn, int size, ItemRenderer itemRenderer, ResourceLocation replacement)
        {
            super(xIn, yIn, size, size, StringTextComponent.EMPTY);
            this.itemStack = ItemStack.EMPTY;
            this.itemRenderer = itemRenderer;
            this.replacement = replacement;
        }
        
        public ItemStackWidget setItemStack(ItemStack itemStack)
        {
            this.itemStack = itemStack;
            return this;
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            Minecraft minecraft = Minecraft.getInstance();
            ResourceLocation rl = this.replacement;
            
            if(!this.itemStack.isEmpty())
            {
                if(this.itemStack.getItem() == YdmItems.CARD)
                {
                    CardHolder c = YdmItems.CARD.getCardHolder(this.itemStack);
                    
                    if(c.getCard() != null)
                    {
                        rl = c.getMainImageResourceLocation();
                    }
                }
                else
                {
                    // do custom rendering and return so the bottom code isnt executed
                    
                    // from ItemRenderer#renderItemModelIntoGUI
                    
                    IBakedModel bakedmodel = this.itemRenderer.getItemModelWithOverrides(this.itemStack, (World)null, (LivingEntity)null);
                    
                    RenderSystem.pushMatrix();
                    minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                    minecraft.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
                    RenderSystem.enableRescaleNormal();
                    RenderSystem.enableAlphaTest();
                    RenderSystem.defaultAlphaFunc();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.translatef((float)this.x, (float)this.y, 100.0F + this.itemRenderer.zLevel);
                    RenderSystem.translatef(this.width / 2F, this.height / 2F, 0.0F);
                    RenderSystem.scalef(1.0F, -1.0F, 1.0F);
                    RenderSystem.scalef(this.width, this.height, 16.0F);
                    MatrixStack matrixstack = new MatrixStack();
                    IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                    boolean flag = !bakedmodel.isSideLit();
                    if(flag)
                    {
                        RenderHelper.setupGuiFlatDiffuseLighting();
                    }
                    
                    this.itemRenderer.renderItem(this.itemStack, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                    irendertypebuffer$impl.finish();
                    RenderSystem.enableDepthTest();
                    if(flag)
                    {
                        RenderHelper.setupGui3DDiffuseLighting();
                    }
                    
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableRescaleNormal();
                    RenderSystem.popMatrix();
                    
                    return;
                }
            }
            
            minecraft.getTextureManager().bindTexture(rl);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            
            YdmBlitUtil.fullBlit(ms, this.x, this.y, this.width, this.height);
        }
    }
    
    private static class RoleButton extends Button
    {
        public Supplier<Boolean> available;
        public PlayerRole role;
        
        public RoleButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent text, IPressable onPress, Supplier<Boolean> available, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, text, onPress);
            this.available = available;
            this.role = role;
        }
        
        @Override
        public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            this.active = this.available.get();
            super.render(ms, mouseX, mouseY, partial);
        }
    }
    
    private static class RoleOccupants extends Widget
    {
        public Function<PlayerRole, ITextComponent> nameGetter;
        public PlayerRole role;
        
        public RoleOccupants(int xIn, int yIn, int widthIn, int heightIn, Function<PlayerRole, ITextComponent> nameGetter, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY);
            this.nameGetter = nameGetter;
            this.role = role;
            this.active = false;
        }
        
        @Override
        public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            this.setMessage(this.nameGetter.apply(this.role));
            super.render(ms, mouseX, mouseY, partial);
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
            super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY, onPress);
            this.isChecked = isChecked;
            this.activeRole = activeRole;
            this.role = role;
        }
        
        public boolean isRoleAssigned()
        {
            return this.role == this.activeRole.get();
        }
        
        @Override
        public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            this.active = this.isRoleAssigned();
            super.render(ms, mouseX, mouseY, partial);
        }
        
        @Override
        public void renderButton(MatrixStack ms, int mouseX, int mouseY, float p_renderButton_3_)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(ReadyCheckbox.TEXTURE);
            RenderSystem.enableDepthTest();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            AbstractGui.blit(ms, this.x, this.y, 0.0F, this.isChecked.get() ? 20.0F : 0.0F, 20, this.height, 64, 64);
            AbstractGui.drawString(ms, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
