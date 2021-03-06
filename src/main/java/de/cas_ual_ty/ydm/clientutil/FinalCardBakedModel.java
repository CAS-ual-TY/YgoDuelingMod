package de.cas_ual_ty.ydm.clientutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.ItemTextureQuadConverter;

@SuppressWarnings("deprecation")
public class FinalCardBakedModel implements IBakedModel
{
    private IBakedModel mainModel;
    private ItemStack activeItemStack;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    
    private List<BakedQuad> singleBackList = null;
    private List<BakedQuad> partneredBackList = null;
    private List<BakedQuad> blancList = null;
    
    private HashMap<Properties, List<BakedQuad>> quadsMap;
    
    public FinalCardBakedModel(IBakedModel mainModel)
    {
        this.mainModel = mainModel;
        this.setActiveItemStack(ItemStack.EMPTY);
        this.textureGetter = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        this.quadsMap = new HashMap<>(YdmDatabase.PROPERTIES_LIST.size());
    }
    
    public FinalCardBakedModel setActiveItemStack(ItemStack itemStack)
    {
        this.activeItemStack = itemStack;
        return this;
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        if(ClientProxy.itemsUseCardImagesActive)
        {
            CardHolder card = YdmItems.CARD.getCardHolder(this.activeItemStack);
            
            if(card != null)
            {
                List<BakedQuad> list = new ArrayList<>();
                list.addAll(this.getPartneredBackList());
                
                if(card.getCard() != null)
                {
                    ResourceLocation front = card.getItemImageResourceLocation();
                    TextureAtlasSprite spriteFront = this.textureGetter.apply(front);
                    
                    if(!this.quadsMap.containsKey(card.getCard()))
                    {
                        List<BakedQuad> textureQuads = new ArrayList<>(0);
                        textureQuads.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), spriteFront, spriteFront, 0.5F, Direction.SOUTH, 0xFFFFFFFF, 1));
                        this.quadsMap.put(card.getCard(), textureQuads);
                    }
                    
                    list.addAll(this.quadsMap.get(card.getCard()));
                }
                else
                {
                    list.addAll(this.getBlancList());
                }
                
                return list;
            }
        }
        
        return this.getSingleBackList();
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
    
    private List<BakedQuad> getSingleBackList()
    {
        if(this.singleBackList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("card_back"));
            TextureAtlasSprite sprite = this.textureGetter.apply(rl);
            this.singleBackList = new ArrayList<>(0);
            this.singleBackList.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F, Direction.SOUTH, 0xFFFFFFFF, 1));
            this.singleBackList.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1));
        }
        
        return this.singleBackList;
    }
    
    private List<BakedQuad> getPartneredBackList()
    {
        if(this.partneredBackList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("card_back"));
            TextureAtlasSprite sprite = this.textureGetter.apply(rl);
            this.partneredBackList = ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1);
        }
        
        return this.partneredBackList;
    }
    
    private List<BakedQuad> getBlancList()
    {
        if(this.blancList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("blanc_card"));
            TextureAtlasSprite sprite = this.textureGetter.apply(rl);
            this.blancList = ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1);
        }
        
        return this.blancList;
    }
}
