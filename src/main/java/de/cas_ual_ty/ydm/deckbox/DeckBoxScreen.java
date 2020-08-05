package de.cas_ual_ty.ydm.deckbox;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DeckBoxScreen extends ContainerScreen<DeckBoxContainer>
{
    private static final ResourceLocation DECK_BOX_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/deck_box.png");
    
    public DeckBoxScreen(DeckBoxContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.xSize = 256;
        this.ySize = 238;
    }
    
    @Override
    public void init(Minecraft mc, int mouseX, int mouseY)
    {
        super.init(mc, mouseX, mouseY);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        Slot s;
        int amount;
        
        // main deck
        
        amount = 0;
        for(int i = 0; i < IDeckHolder.MAIN_DECK_SIZE; ++i)
        {
            s = this.getContainer().getSlot(i);
            
            if(s != null && s.getHasStack())
            {
                amount++;
            }
        }
        
        String main = new TranslationTextComponent("container.ydm.deck_box.main").getFormattedText() + " " + amount + "/" + IDeckHolder.MAIN_DECK_SIZE;
        this.font.drawString(main, 8F, 6F, 0x404040);
        
        // extra deck
        
        amount = 0;
        for(int i = IDeckHolder.MAIN_DECK_SIZE; i < IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE; ++i)
        {
            s = this.getContainer().getSlot(i);
            
            if(s != null && s.getHasStack())
            {
                amount++;
            }
        }
        
        String extra = new TranslationTextComponent("container.ydm.deck_box.extra").getFormattedText() + " " + amount + "/" + IDeckHolder.EXTRA_DECK_SIZE;
        this.font.drawString(extra, 8F, 84F, 0x404040);
        
        // side deck
        
        amount = 0;
        for(int i = IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE; i < IDeckHolder.TOTAL_DECK_SIZE; ++i)
        {
            s = this.getContainer().getSlot(i);
            
            if(s != null && s.getHasStack())
            {
                amount++;
            }
        }
        
        String side = new TranslationTextComponent("container.ydm.deck_box.side").getFormattedText() + " " + amount + "/" + IDeckHolder.SIDE_DECK_SIZE;
        this.font.drawString(side, 8F, 114F, 0x404040);
        
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8F, (float)(this.ySize - 96 + 2), 0x404040);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(DeckBoxScreen.DECK_BOX_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(x, y, 0, 0, this.xSize, this.ySize);
        
        Slot s;
        String text;
        int width;
        
        for(int i = 0; i < IDeckHolder.MAIN_DECK_SIZE; ++i)
        {
            s = this.getContainer().getSlot(i);
            
            if(s != null && !s.getHasStack())
            {
                text = String.valueOf(s.getSlotIndex() + 1);
                width = this.font.getStringWidth(text);
                this.font.drawString(text, this.guiLeft + s.xPos + (16 - width) / 2, this.guiTop + s.yPos + (16 - this.font.FONT_HEIGHT) / 2 + 1, 0x404040);
            }
        }
        
        for(int i = 0; i < IDeckHolder.EXTRA_DECK_SIZE; ++i)
        {
            s = this.getContainer().getSlot(IDeckHolder.MAIN_DECK_SIZE + i);
            
            if(s != null && !s.getHasStack())
            {
                text = String.valueOf(s.getSlotIndex() + 1 - IDeckHolder.MAIN_DECK_SIZE);
                width = this.font.getStringWidth(text);
                this.font.drawString(text, this.guiLeft + s.xPos + (16 - width) / 2, this.guiTop + s.yPos + (16 - this.font.FONT_HEIGHT) / 2 + 1, 0x404040);
            }
        }
        
        for(int i = 0; i < IDeckHolder.SIDE_DECK_SIZE; ++i)
        {
            s = this.getContainer().getSlot(IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE + i);
            
            if(s != null && !s.getHasStack())
            {
                text = String.valueOf(s.getSlotIndex() + 1 - IDeckHolder.MAIN_DECK_SIZE - IDeckHolder.EXTRA_DECK_SIZE);
                width = this.font.getStringWidth(text);
                this.font.drawString(text, this.guiLeft + s.xPos + (16 - width) / 2, this.guiTop + s.yPos + (16 - this.font.FONT_HEIGHT) / 2 + 1, 0x404040);
            }
        }
    }
}
