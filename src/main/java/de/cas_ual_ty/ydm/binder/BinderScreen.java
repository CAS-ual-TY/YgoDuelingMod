package de.cas_ual_ty.ydm.binder;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
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
        this.ySize = 114 + 6 * 18;
    }
    
    @Override
    public void init(Minecraft mc, int mouseX, int mouseY)
    {
        super.init(mc, mouseX, mouseY);
        
        Button button;
        
        for(int y = 0; y < 6; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                button = new CardButton(this.guiLeft + 7 + x * 18, this.guiTop + 17 + y * 18, 18, 18, x + y * 9, (b) ->
                {}, this::getCard);
                this.addButton(button);
            }
        }
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
    
    public CardHolder getCard(int index)
    {
        return index < this.getContainer().clientList.size() ? this.getContainer().clientList.get(index) : null;
    }
}
