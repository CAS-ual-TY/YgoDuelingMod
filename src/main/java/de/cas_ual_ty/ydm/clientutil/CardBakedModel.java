package de.cas_ual_ty.ydm.clientutil;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CardBakedModel implements IBakedModel
{
    private IBakedModel mainModel;
    private ItemOverrideList overrideList;
    
    public CardBakedModel(IBakedModel mainModel)
    {
        this.mainModel = mainModel;
        overrideList = new CardOverrideList(new FinalCardBakedModel(mainModel));
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return mainModel.getQuads(state, side, rand);
    }
    
    @Override
    public boolean useAmbientOcclusion()
    {
        return mainModel.useAmbientOcclusion();
    }
    
    @Override
    public boolean isGui3d()
    {
        return mainModel.isGui3d();
    }
    
    @Override
    public boolean usesBlockLight()
    {
        return mainModel.usesBlockLight();
    }
    
    @Override
    public boolean isCustomRenderer()
    {
        return mainModel.isCustomRenderer();
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return mainModel.getParticleIcon();
    }
    
    @Override
    public ItemOverrideList getOverrides()
    {
        return overrideList;
    }
    
    private static class CardOverrideList extends ItemOverrideList
    {
        private FinalCardBakedModel finalModel;
        
        public CardOverrideList(FinalCardBakedModel finalModel)
        {
            this.finalModel = finalModel;
        }
        
        @Override
        public IBakedModel resolve(IBakedModel model, ItemStack stack, ClientWorld worldIn, LivingEntity entityIn)
        {
            return finalModel.setActiveItemStack(stack);
        }
    }
}
