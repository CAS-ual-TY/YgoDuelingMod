package de.cas_ual_ty.ydm.util;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class CardBakedModel implements IBakedModel
{
    private IBakedModel mainModel;
    private ItemOverrideList overrideList;
    
    public CardBakedModel(IBakedModel mainModel)
    {
        this.mainModel = mainModel;
        this.overrideList = new CardOverrideList(new FinalCardBakedModel(mainModel));
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return this.mainModel.getQuads(state, side, rand);
    }
    
    @Override
    public boolean isAmbientOcclusion()
    {
        return this.mainModel.isAmbientOcclusion();
    }
    
    @Override
    public boolean isGui3d()
    {
        return this.mainModel.isGui3d();
    }
    
    @Override
    public boolean func_230044_c_()
    {
        return this.mainModel.func_230044_c_();
    }
    
    @Override
    public boolean isBuiltInRenderer()
    {
        return this.mainModel.isBuiltInRenderer();
    }
    
    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return this.mainModel.getParticleTexture();
    }
    
    @Override
    public ItemOverrideList getOverrides()
    {
        return this.overrideList;
    }
    
    private static class CardOverrideList extends ItemOverrideList
    {
        private FinalCardBakedModel finalModel;
        
        public CardOverrideList(FinalCardBakedModel finalModel)
        {
            this.finalModel = finalModel;
        }
        
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, World worldIn, LivingEntity entityIn)
        {
            return this.finalModel.setActiveItemStack(stack);
        }
    }
}
