package de.cas_ual_ty.ydm.clientutil;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CardBakedModel implements IDynamicBakedModel
{
    private BakedModel mainModel;
    private ItemOverrides overrideList;
    
    public CardBakedModel(BakedModel mainModel)
    {
        this.mainModel = mainModel;
        overrideList = new CardOverrideList(new FinalCardBakedModel(mainModel));
    }
    
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType)
    {
        return mainModel.getQuads(state, side, rand, extraData, renderType);
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
    
    private static class CardOverrideList extends ItemOverrides
    {
        private FinalCardBakedModel finalModel;
        
        public CardOverrideList(FinalCardBakedModel finalModel)
        {
            this.finalModel = finalModel;
        }
    
        @Override
        public BakedModel resolve(BakedModel pModel, ItemStack stack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed)
        {
            return finalModel.setActiveItemStack(stack);
        }
    }
}
