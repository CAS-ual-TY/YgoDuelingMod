package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.set.CardSet;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class FinalCardSetBakedModel implements IBakedModel
{
    private IBakedModel mainModel;
    private ItemStack activeItemStack;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    
    private List<BakedQuad> setList = null;
    private List<BakedQuad> openedList = null;
    
    private HashMap<CardSet, List<BakedQuad>> quadsMap;
    
    private final float distance = 0.002F;
    
    public FinalCardSetBakedModel(IBakedModel mainModel)
    {
        this.mainModel = mainModel;
        setActiveItemStack(ItemStack.EMPTY);
        textureGetter = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS);
        quadsMap = new HashMap<>(YdmDatabase.SETS_LIST.size());
    }
    
    public FinalCardSetBakedModel setActiveItemStack(ItemStack itemStack)
    {
        activeItemStack = itemStack;
        return this;
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        List<BakedQuad> list = new ArrayList<>(0);
        list.addAll(getSetList());
        
        if(activeItemStack.getItem() == YdmItems.OPENED_SET)
        {
            list.addAll(getOpenedList());
        }
        
        if(ClientProxy.itemsUseSetImagesActive)
        {
            CardSet set = YdmItems.SET.getCardSet(activeItemStack);
            
            if(set != CardSet.DUMMY)
            {
                ResourceLocation front = set.getItemImageResourceLocation();
                TextureAtlasSprite spriteFront = textureGetter.apply(front);
                
                if(!quadsMap.containsKey(set))
                {
                    List<BakedQuad> textureQuads = new ArrayList<>(0);
                    textureQuads.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), spriteFront, spriteFront, 0.5F + distance, Direction.SOUTH, 0xFFFFFFFF, 1));
                    textureQuads.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), spriteFront, spriteFront, 0.5F - distance, Direction.NORTH, 0xFFFFFFFF, 1));
                    quadsMap.put(set, textureQuads);
                }
                
                list.addAll(quadsMap.get(set));
            }
        }
        
        return list;
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
        return mainModel.getOverrides();
    }
    
    @Override
    public IBakedModel handlePerspective(TransformType t, MatrixStack mat)
    {
        mat.pushPose();
        
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
                mat.mulPose(new Quaternion(Direction.UP.step(), 180F, true));
                break;
            default:
                break;
        }
        
        return this;
    }
    
    private List<BakedQuad> getSetList()
    {
        if(setList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addSetItemTag("blanc_set"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            setList = new ArrayList<>(0);
            setList.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F, Direction.SOUTH, 0xFFFFFFFF, 1));
            setList.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1));
        }
        
        return setList;
    }
    
    private List<BakedQuad> getOpenedList()
    {
        if(openedList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YdmItems.OPENED_SET.getRegistryName().getPath());
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            openedList = new ArrayList<>(0);
            openedList.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F + 2 * distance, Direction.SOUTH, 0xFFFFFFFF, 1));
            openedList.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), sprite, sprite, 0.5F - 2 * distance, Direction.NORTH, 0xFFFFFFFF, 1));
        }
        
        return openedList;
    }
}
