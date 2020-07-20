package de.cas_ual_ty.ydm.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.Card;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemTextureQuadConverter;

@SuppressWarnings("deprecation")
public class FinalCardBakedModel implements IBakedModel
{
    private IBakedModel mainModel;
    private ItemStack activeItemStack;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    
    public FinalCardBakedModel(IBakedModel mainModel)
    {
        this.mainModel = mainModel;
        this.setActiveItemStack(ItemStack.EMPTY);
        this.textureGetter = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }
    
    public FinalCardBakedModel setActiveItemStack(ItemStack itemStack)
    {
        this.activeItemStack = itemStack;
        return this;
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        List<BakedQuad> list = new LinkedList<>();
        
        ResourceLocation front = new ResourceLocation(YDM.MOD_ID, "item/blanc_card");
        ResourceLocation back = new ResourceLocation(YDM.MOD_ID, "item/card_back");
        
        if(YDM.itemsUseCardImages)
        {
            Card card = YdmItems.CARD.getCardHolder(this.activeItemStack).getCard();
            
            if(card != null)
            {
                front = card.getItemImageResourceLocation();
            }
        }
        
        TextureAtlasSprite spriteFront = this.textureGetter.apply(front);
        TextureAtlasSprite spriteBack = this.textureGetter.apply(back);
        
        float distance = 0.002F;
        
        list.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), spriteBack, spriteBack, 0.5F - distance, Direction.NORTH, 0xFFFFFFFF, 1));
        list.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), spriteFront, spriteFront, 0.5F + distance, Direction.SOUTH, 0xFFFFFFFF, 1));
        
        return list;
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
        return this.mainModel.getOverrides();
    }
    
    @Override
    public IBakedModel handlePerspective(TransformType t, MatrixStack mat)
    {
        mat.push();
        
        switch(t)
        {
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                // m.setTranslation(new Vector3f(0f, 3.5f, 0f));
                
                mat.scale(0.5F, 0.5F, 0.5F);
                mat.translate(0, 0.35, 0);
                
                break;
            case GROUND:
                // m.setTranslation(new Vector3f(0f, 4f, 0f));
                mat.scale(0.5F, 0.5F, 0.5F);
                break;
            case FIXED:
                mat.rotate(new Quaternion(Direction.UP.toVector3f(), 180F, true));
                break;
            default:
                break;
        }
        
        return this;
    }
}
