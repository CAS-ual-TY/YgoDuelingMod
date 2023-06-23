package de.cas_ual_ty.ydm.deckbox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;


public class DeckBoxScreen extends AbstractContainerScreen<DeckBoxContainer>
{
    public static final ResourceLocation DECK_BOX_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/deck_box.png");
    
    public DeckBoxScreen(DeckBoxContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected void init()
    {
        imageWidth = 284;
        imageHeight = 250;
        super.init();
    }
    
    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack ms, int mouseX, int mouseY)
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
        font.draw(ms, Component.translatable("container.ydm.deck_box.main").append(" " + amount + "/" + DeckHolder.MAIN_DECK_SIZE), 8F, 6F, 0x404040);
        
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
        font.draw(ms, Component.translatable("container.ydm.deck_box.extra").append(" " + amount + "/" + DeckHolder.EXTRA_DECK_SIZE), 8F, 92F, 0x404040);
        
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
        font.draw(ms, Component.translatable("container.ydm.deck_box.side").append(" " + amount + "/" + DeckHolder.SIDE_DECK_SIZE), 8F, 124F, 0x404040);
        
        font.draw(ms, Component.translatable("container.ydm.deck_box.sleeves"), 224F, (float) (imageHeight - 96 + 2), 0x404040);
        
        font.draw(ms, playerInventoryTitle.getVisualOrderText(), 8F, (float) (imageHeight - 96 + 2), 0x404040);
    }
    
    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY)
    {
        ScreenUtil.white();
        RenderSystem.setShaderTexture(0, DeckBoxScreen.DECK_BOX_GUI_TEXTURE);
        YdmBlitUtil.blit(ms, leftPos, topPos, imageWidth, imageHeight, 0, 0, imageWidth, imageHeight, 512, 256);
    }
}
