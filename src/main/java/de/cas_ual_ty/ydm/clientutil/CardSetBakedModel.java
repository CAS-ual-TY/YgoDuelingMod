package de.cas_ual_ty.ydm.clientutil;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

@SuppressWarnings("deprecation")
public class CardSetBakedModel implements IBakedModel
{
    private IBakedModel mainModel;
    private ItemOverrideList overrideList;
    
    public CardSetBakedModel(IBakedModel mainModel)
    {
        this.mainModel = mainModel;
        this.overrideList = new CardSetOverrideList(new FinalCardSetBakedModel(mainModel));
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
    public boolean isSideLit()
    {
        return this.mainModel.isSideLit();
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
    
    private static class CardSetOverrideList extends ItemOverrideList
    {
        private FinalCardSetBakedModel finalModel;
        
        public CardSetOverrideList(FinalCardSetBakedModel finalModel)
        {
            this.finalModel = finalModel;
        }
        
        @Override
        public IBakedModel getOverrideModel(IBakedModel model, ItemStack stack, ClientWorld worldIn, LivingEntity entityIn)
        {
            return this.finalModel.setActiveItemStack(stack);
        }
    }
}
