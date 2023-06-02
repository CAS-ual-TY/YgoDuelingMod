package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class DuelEntityRenderer extends EntityRenderer
{
    protected DuelEntityRenderer(EntityRendererManager p_i46179_1_)
    {
        super(p_i46179_1_);
    }
    
    @Override
    public boolean shouldRender(Entity pLivingEntity, ClippingHelper pCamera, double pCamX, double pCamY, double pCamZ)
    {
        return false;
    }
    
    @Override
    public void render(Entity pEntity, float pEntityYaw, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pPackedLight)
    {
    }
    
    @Override
    public ResourceLocation getTextureLocation(Entity pEntity)
    {
        return null;
    }
}
