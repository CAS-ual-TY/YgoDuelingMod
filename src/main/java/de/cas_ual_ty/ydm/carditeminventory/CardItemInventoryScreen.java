package de.cas_ual_ty.ydm.carditeminventory;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CardItemInventoryScreen<T extends CardItemInventoryContainer> extends ContainerScreen<T>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    
    private final int inventoryRows;
    
    public CardItemInventoryScreen(T container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.passEvents = false;
        //int i = 222;
        //int j = 114;
        this.inventoryRows = 6;
        this.ySize = 114 + this.inventoryRows * 18;
        this.playerInventoryTitleY = this.ySize - 94;
        
        //TODO next/prev page buttons
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        ScreenUtil.white();
        this.minecraft.getTextureManager().bindTexture(CardItemInventoryScreen.CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
