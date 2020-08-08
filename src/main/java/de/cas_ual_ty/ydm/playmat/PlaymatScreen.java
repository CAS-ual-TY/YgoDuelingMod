package de.cas_ual_ty.ydm.playmat;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PlaymatScreen extends ContainerScreen<PlaymatContainer>
{
    private static final ResourceLocation PLAYMAT_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/playmat.png");
    
    public PlaymatScreen(PlaymatContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
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
        this.xSize = 234;
        this.ySize = 250;
        super.init();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String text = this.getContainer().getDuelManager().duelState.name();
        int width = this.font.getStringWidth(text);
        int height = this.font.FONT_HEIGHT;
        this.font.drawString(text, (this.xSize - width) / 2F, (this.ySize - height) / 2F, 0x404040);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(PlaymatScreen.PLAYMAT_GUI_TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
