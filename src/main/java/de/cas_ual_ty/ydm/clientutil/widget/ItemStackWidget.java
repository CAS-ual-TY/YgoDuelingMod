package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ItemStackWidget extends Widget
{
    public ItemStack itemStack;
    public ItemRenderer itemRenderer;
    public ResourceLocation replacement;
    
    public ItemStackWidget(int xIn, int yIn, int size, ItemRenderer itemRenderer, ResourceLocation replacement)
    {
        super(xIn, yIn, size, size, StringTextComponent.EMPTY);
        itemStack = ItemStack.EMPTY;
        this.itemRenderer = itemRenderer;
        this.replacement = replacement;
    }
    
    public ItemStackWidget setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
        return this;
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partial)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation rl = replacement;
        
        if(!itemStack.isEmpty())
        {
            if(itemStack.getItem() == YdmItems.CARD)
            {
                CardHolder c = YdmItems.CARD.getCardHolder(itemStack);
                
                if(c.getCard() != null)
                {
                    rl = c.getMainImageResourceLocation();
                }
            }
            else
            {
                // do custom rendering and return so the bottom code isnt executed
                
                // from ItemRenderer#renderItemModelIntoGUI
                
                IBakedModel bakedmodel = itemRenderer.getModel(itemStack, null, null);
                
                RenderSystem.pushMatrix();
                minecraft.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
                minecraft.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
                RenderSystem.enableRescaleNormal();
                RenderSystem.enableAlphaTest();
                RenderSystem.defaultAlphaFunc();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.translatef((float) x, (float) y, 100.0F + itemRenderer.blitOffset);
                RenderSystem.translatef(width / 2F, height / 2F, 0.0F);
                RenderSystem.scalef(1.0F, -1.0F, 1.0F);
                RenderSystem.scalef(width, height, 16.0F);
                MatrixStack matrixstack = new MatrixStack();
                IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
                boolean flag = !bakedmodel.usesBlockLight();
                if(flag)
                {
                    RenderHelper.setupForFlatItems();
                }
                
                itemRenderer.render(itemStack, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                irendertypebuffer$impl.endBatch();
                RenderSystem.enableDepthTest();
                if(flag)
                {
                    RenderHelper.setupFor3DItems();
                }
                
                RenderSystem.disableAlphaTest();
                RenderSystem.disableRescaleNormal();
                RenderSystem.popMatrix();
                
                return;
            }
        }
        
        minecraft.getTextureManager().bind(rl);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        YdmBlitUtil.fullBlit(ms, x, y, width, height);
    }
}