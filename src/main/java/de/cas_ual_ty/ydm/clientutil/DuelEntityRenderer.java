package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class DuelEntityRenderer extends EntityRenderer<Entity>
{
    protected DuelEntityRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }
    
    @Override
    public boolean shouldRender(Entity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ)
    {
        return false;
    }
    
    @Override
    public void render(Entity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight)
    {
    }
    
    @Override
    public ResourceLocation getTextureLocation(Entity pEntity)
    {
        return null;
    }
}
