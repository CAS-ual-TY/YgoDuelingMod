package de.cas_ual_ty.ydm.clientutil;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("deprecation")
public class CardSetBakedModel implements BakedModel
{
    private BakedModel mainModel;
    private ItemOverrides overrideList;
    
    public CardSetBakedModel(BakedModel mainModel)
    {
        this.mainModel = mainModel;
        overrideList = new CardSetOverrideList(new FinalCardSetBakedModel(mainModel));
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
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
    public ItemOverrides getOverrides()
    {
        return overrideList;
    }
    
    private static class CardSetOverrideList extends ItemOverrides
    {
        private FinalCardSetBakedModel finalModel;
        
        public CardSetOverrideList(FinalCardSetBakedModel finalModel)
        {
            this.finalModel = finalModel;
        }
        
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, ClientLevel worldIn, LivingEntity entityIn, int seed)
        {
            return finalModel.setActiveItemStack(stack);
        }
    }
}
