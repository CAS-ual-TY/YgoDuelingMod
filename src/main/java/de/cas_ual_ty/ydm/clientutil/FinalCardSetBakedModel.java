package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.set.CardSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class FinalCardSetBakedModel implements BakedModel
{
    private BakedModel mainModel;
    private ItemStack activeItemStack;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    
    private List<BakedQuad> setList = null;
    private List<BakedQuad> openedList = null;
    
    private HashMap<CardSet, List<BakedQuad>> quadsMap;
    
    private final float distance = 0.002F;
    
    public FinalCardSetBakedModel(BakedModel mainModel)
    {
        this.mainModel = mainModel;
        setActiveItemStack(ItemStack.EMPTY);
        textureGetter = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        quadsMap = new HashMap<>(YdmDatabase.SETS_LIST.size());
    }
    
    public FinalCardSetBakedModel setActiveItemStack(ItemStack itemStack)
    {
        activeItemStack = itemStack;
        return this;
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        List<BakedQuad> list = new ArrayList<>(0);
        list.addAll(getSetList());
        
        if(activeItemStack.getItem() == YdmItems.OPENED_SET.get())
        {
            list.addAll(getOpenedList());
        }
        
        if(ClientProxy.itemsUseSetImagesActive)
        {
            CardSet set = YdmItems.SET.get().getCardSet(activeItemStack);
            
            if(set != CardSet.DUMMY)
            {
                ResourceLocation front = set.getItemImageResourceLocation();
                TextureAtlasSprite spriteFront = textureGetter.apply(front);
                
                if(!quadsMap.containsKey(set))
                {
                    List<BakedQuad> textureQuads = new ArrayList<>(0);
                    textureQuads.addAll(FinalCardBakedModel.convertTexture(Transformation.identity(), spriteFront, 0.5F + distance, Direction.SOUTH, 0xFFFFFFFF, 1, front));
                    textureQuads.addAll(FinalCardBakedModel.convertTexture(Transformation.identity(), spriteFront, 0.5F - distance, Direction.NORTH, 0xFFFFFFFF, 1, front));
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
    public ItemOverrides getOverrides()
    {
        return mainModel.getOverrides();
    }
    
    @Override
    public BakedModel applyTransform(ItemTransforms.TransformType t, PoseStack mat, boolean applyLeftHandTransform)
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
        
        BakedModel.super.applyTransform(t, mat, applyLeftHandTransform);
        
        mat.popPose();
        
        return this;
    }
    
    private List<BakedQuad> getSetList()
    {
        if(setList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addSetItemTag("blanc_set"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            setList = new ArrayList<>(0);
            setList.addAll(FinalCardBakedModel.convertTexture(Transformation.identity(), sprite, 0.5F, Direction.SOUTH, 0xFFFFFFFF, 1, rl));
            setList.addAll(FinalCardBakedModel.convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl));
        }
        
        return setList;
    }
    
    private List<BakedQuad> getOpenedList()
    {
        if(openedList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YdmItems.OPENED_SET.getId().getPath());
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            openedList = new ArrayList<>(0);
            openedList.addAll(FinalCardBakedModel.convertTexture(Transformation.identity(), sprite, 0.5F + 2 * distance, Direction.SOUTH, 0xFFFFFFFF, 1, rl));
            openedList.addAll(FinalCardBakedModel.convertTexture(Transformation.identity(), sprite, 0.5F - 2 * distance, Direction.NORTH, 0xFFFFFFFF, 1, rl));
        }
        
        return openedList;
    }
}
