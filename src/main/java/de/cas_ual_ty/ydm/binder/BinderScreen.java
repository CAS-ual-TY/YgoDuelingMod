package de.cas_ual_ty.ydm.binder;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BinderScreen extends ContainerScreen<BinderContainer> implements IHasContainer<BinderContainer>
{
    private static final ResourceLocation BINDER_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/card_binder.png");
    
    public BinderScreen(BinderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        //        ChestScreen
        this.ySize = 114 + 6 * 18;
    }
    
    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_)
    {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String title = this.title.getFormattedText();
        
        this.font.drawString(title, 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
        
        if(!this.getContainer().loaded)
        {
            this.font.drawString(new TranslationTextComponent("container.ydm.card_binder.loading").getFormattedText(), 8.0F + this.font.getStringWidth(title + " "), 6.0F, 4210752);
        }
        else
        {
            this.font.drawString(this.container.page + "/" + this.container.clientMaxPage, 8.0F + this.font.getStringWidth(title + " "), 6F, 4210752);
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BinderScreen.BINDER_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, 6 * 18 + 17);
        this.blit(i, j + 6 * 18 + 17, 0, 126, this.xSize, 96);
    }
}
