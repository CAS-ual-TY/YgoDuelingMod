package de.cas_ual_ty.ydm.carditeminventory;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CIIScreen<T extends CIIContainer> extends ContainerScreen<T>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    
    private final int inventoryRows;
    
    protected Button prevButton;
    protected Button nextButton;
    
    public CIIScreen(T container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.passEvents = false;
        //int i = 222;
        //int j = 114;
        this.inventoryRows = 6;
        this.ySize = 114 + this.inventoryRows * 18;
        this.playerInventoryTitleY = this.ySize - 94;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        this.addButton(this.prevButton = new ImprovedButton(this.guiLeft + this.xSize - 24 - 8, this.guiTop + 4, 12, 12, new TranslationTextComponent("generic.ydm.left_arrow"), this::onButtonClicked));
        this.addButton(this.nextButton = new ImprovedButton(this.guiLeft + this.xSize - 12 - 8, this.guiTop + 4, 12, 12, new TranslationTextComponent("generic.ydm.right_arrow"), this::onButtonClicked));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int x, int y)
    {
        IFormattableTextComponent title = new StringTextComponent(this.title.getString());
        title = title.appendString(" ").appendSibling(new StringTextComponent((this.container.getPage() + 1) + "/" + this.container.getMaxPage()));
        this.font.drawText(ms, title, 8.0F, 6.0F, 0x404040);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int x, int y)
    {
        ScreenUtil.white();
        this.minecraft.getTextureManager().bindTexture(CIIScreen.CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(ms, i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(ms, i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
    
    protected void onButtonClicked(Button button)
    {
        if(button == this.prevButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CIIMessages.ChangePage(false));
        }
        else if(button == this.nextButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CIIMessages.ChangePage(true));
        }
    }
}
