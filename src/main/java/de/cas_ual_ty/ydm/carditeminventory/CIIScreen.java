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
        passEvents = false;
        //int i = 222;
        //int j = 114;
        inventoryRows = 6;
        imageHeight = 114 + inventoryRows * 18;
        inventoryLabelY = imageHeight - 94;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        addButton(prevButton = new ImprovedButton(leftPos + imageWidth - 24 - 8, topPos + 4, 12, 12, new TranslationTextComponent("generic.ydm.left_arrow"), this::onButtonClicked));
        addButton(nextButton = new ImprovedButton(leftPos + imageWidth - 12 - 8, topPos + 4, 12, 12, new TranslationTextComponent("generic.ydm.right_arrow"), this::onButtonClicked));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(MatrixStack ms, int x, int y)
    {
        IFormattableTextComponent title = new StringTextComponent(this.title.getString());
        title = title.append(" ").append(new StringTextComponent((menu.getPage() + 1) + "/" + menu.getMaxPage()));
        font.draw(ms, title, 8.0F, 6.0F, 0x404040);
    }
    
    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int x, int y)
    {
        ScreenUtil.white();
        minecraft.getTextureManager().bind(CIIScreen.CHEST_GUI_TEXTURE);
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        blit(ms, i, j, 0, 0, imageWidth, inventoryRows * 18 + 17);
        blit(ms, i, j + inventoryRows * 18 + 17, 0, 126, imageWidth, 96);
    }
    
    protected void onButtonClicked(Button button)
    {
        if(button == prevButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CIIMessages.ChangePage(false));
        }
        else if(button == nextButton)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new CIIMessages.ChangePage(true));
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(keyCode <= 57 && keyCode >= 49)
        {
            return false;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
