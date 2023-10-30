package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;


public class ItemStackWidget extends AbstractWidget
{
    public ItemStack itemStack;
    public ItemRenderer itemRenderer;
    public ResourceLocation replacement;
    
    public ItemStackWidget(int xIn, int yIn, int size, ItemRenderer itemRenderer, ResourceLocation replacement)
    {
        super(xIn, yIn, size, size, Component.empty());
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
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partial)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation rl = replacement;
        
        if(!itemStack.isEmpty())
        {
            if(itemStack.getItem() == YdmItems.CARD.get())
            {
                CardHolder c = YdmItems.CARD.get().getCardHolder(itemStack);
                
                if(c.getCard() != null)
                {
                    rl = c.getMainImageResourceLocation();
                }
            }
            else
            {
                // do custom rendering and return so the bottom code isnt executed
                
                // from ItemRenderer#renderGuiItem
                
                BakedModel bakedmodel = itemRenderer.getModel(itemStack, null, null, 0); //FIXME 0 correct?
                
                minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                PoseStack posestack = RenderSystem.getModelViewStack();
                posestack.pushPose();
                posestack.translate((float) x, (float) y, 100.0F + itemRenderer.blitOffset);
                posestack.translate(width / 2F, height / 2F, 0.0D);
                posestack.scale(1.0F, -1.0F, 1.0F);
                posestack.scale(width, height, 16.0F);
                RenderSystem.applyModelViewMatrix();
                PoseStack posestack1 = new PoseStack();
                MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
                boolean flag = !bakedmodel.usesBlockLight();
                if(flag)
                {
                    Lighting.setupForFlatItems();
                }
                
                itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                multibuffersource$buffersource.endBatch();
                RenderSystem.enableDepthTest();
                if(flag)
                {
                    Lighting.setupFor3DItems();
                }
                
                posestack.popPose();
                RenderSystem.applyModelViewMatrix();
                
                return;
            }
        }
        
        RenderSystem.setShaderTexture(0, rl);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        YdmBlitUtil.fullBlit(ms, x, y, width, height);
    }
    
    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
    
    }
}