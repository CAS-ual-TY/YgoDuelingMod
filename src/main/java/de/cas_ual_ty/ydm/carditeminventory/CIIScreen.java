package de.cas_ual_ty.ydm.carditeminventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ImprovedButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

public class CIIScreen<T extends CIIContainer> extends AbstractContainerScreen<T>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    
    private final int inventoryRows;
    
    protected Button prevButton;
    protected Button nextButton;
    
    public CIIScreen(T container, Inventory playerInventory, Component title)
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
        
        addRenderableWidget(prevButton = new ImprovedButton(leftPos + imageWidth - 24 - 8, topPos + 4, 12, 12, Component.translatable("generic.ydm.left_arrow"), this::onButtonClicked));
        addRenderableWidget(nextButton = new ImprovedButton(leftPos + imageWidth - 12 - 8, topPos + 4, 12, 12, Component.translatable("generic.ydm.right_arrow"), this::onButtonClicked));
    }
    
    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(PoseStack);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
        renderTooltip(PoseStack, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack ms, int x, int y)
    {
        MutableComponent title = Component.literal(this.title.getString());
        title = title.append(" ").append(Component.literal((menu.getPage() + 1) + "/" + menu.getMaxPage()));
        font.draw(ms, title, 8.0F, 6.0F, 0x404040);
    }
    
    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int x, int y)
    {
        ScreenUtil.white();
        RenderSystem.setShaderTexture(0, CIIScreen.CHEST_GUI_TEXTURE);
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
