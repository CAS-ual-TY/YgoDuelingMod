package de.cas_ual_ty.ydm.deckbox;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DeckBoxScreen extends ContainerScreen<DeckBoxContainer>
{
    public static final ResourceLocation DECK_BOX_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/deck_box.png");
    
    public DeckBoxScreen(DeckBoxContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
    }
    
    @Override
    protected void init()
    {
        imageWidth = 284;
        imageHeight = 250;
        super.init();
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY)
    {
        Slot s;
        int amount;
        
        // main deck
        
        amount = 0;
        for(int i = DeckHolder.MAIN_DECK_INDEX_START; i < DeckHolder.MAIN_DECK_INDEX_END; ++i)
        {
            s = getMenu().getSlot(i);
            
            if(s != null && s.hasItem())
            {
                amount++;
            }
        }
        
        //drawString
        font.draw(ms, new TranslationTextComponent("container.ydm.deck_box.main").append(" " + amount + "/" + DeckHolder.MAIN_DECK_SIZE), 8F, 6F, 0x404040);
        
        // extra deck
        
        amount = 0;
        for(int i = DeckHolder.EXTRA_DECK_INDEX_START; i < DeckHolder.EXTRA_DECK_INDEX_END; ++i)
        {
            s = getMenu().getSlot(i);
            
            if(s != null && s.hasItem())
            {
                amount++;
            }
        }
        
        //drawString
        font.draw(ms, new TranslationTextComponent("container.ydm.deck_box.extra").append(" " + amount + "/" + DeckHolder.EXTRA_DECK_SIZE), 8F, 92F, 0x404040);
        
        // side deck
        
        amount = 0;
        for(int i = DeckHolder.SIDE_DECK_INDEX_START; i < DeckHolder.SIDE_DECK_INDEX_END; ++i)
        {
            s = getMenu().getSlot(i);
            
            if(s != null && s.hasItem())
            {
                amount++;
            }
        }
        
        //drawString
        font.draw(ms, new TranslationTextComponent("container.ydm.deck_box.side").append(" " + amount + "/" + DeckHolder.SIDE_DECK_SIZE), 8F, 124F, 0x404040);
        
        font.draw(ms, new TranslationTextComponent("container.ydm.deck_box.sleeves"), 224F, (float) (imageHeight - 96 + 2), 0x404040);
        
        font.draw(ms, inventory.getDisplayName(), 8F, (float) (imageHeight - 96 + 2), 0x404040);
    }
    
    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.white();
        minecraft.getTextureManager().bind(DeckBoxScreen.DECK_BOX_GUI_TEXTURE);
        YdmBlitUtil.blit(ms, leftPos, topPos, imageWidth, imageHeight, 0, 0, imageWidth, imageHeight, 512, 256);
    }
}
