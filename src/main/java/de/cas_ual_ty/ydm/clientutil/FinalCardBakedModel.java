package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class FinalCardBakedModel implements BakedModel
{
    private BakedModel mainModel;
    private ItemStack activeItemStack;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    
    private List<BakedQuad> singleBackList = null;
    private List<BakedQuad> partneredBackList = null;
    private List<BakedQuad> blancList = null;
    
    private HashMap<Properties, List<BakedQuad>> quadsMap;
    
    public FinalCardBakedModel(BakedModel mainModel)
    {
        this.mainModel = mainModel;
        setActiveItemStack(ItemStack.EMPTY);
        textureGetter = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        quadsMap = new HashMap<>(YdmDatabase.PROPERTIES_LIST.size());
    }
    
    public FinalCardBakedModel setActiveItemStack(ItemStack itemStack)
    {
        activeItemStack = itemStack;
        return this;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom)
    {
        if(ClientProxy.itemsUseCardImagesActive)
        {
            CardHolder card = YdmItems.CARD.get().getCardHolder(activeItemStack);
            
            if(card != null)
            {
                List<BakedQuad> list = new ArrayList<>();
                list.addAll(getPartneredBackList());
                
                if(card.getCard() != null)
                {
                    ResourceLocation front = card.getItemImageResourceLocation();
                    TextureAtlasSprite spriteFront = textureGetter.apply(front);
                    
                    if(!quadsMap.containsKey(card.getCard()))
                    {
                        ModelState modelState = new SimpleModelState(Transformation.identity());
                        List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, spriteFront);
                        List<BakedQuad> baked = UnbakedGeometryHelper.bakeElements(unbaked, m -> spriteFront, modelState, front);
                        
                        List<BakedQuad> textureQuads = new ArrayList<>(0);
                        textureQuads.addAll(baked);
                        
                        quadsMap.put(card.getCard(), textureQuads);
                    }
                    
                    list.addAll(quadsMap.get(card.getCard()));
                }
                else
                {
                    list.addAll(getBlancList());
                }
                
                return list;
            }
        }
        
        return getSingleBackList();
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
        
        return this;
    }
    
    private List<BakedQuad> getSingleBackList()
    {
        if(singleBackList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("card_back"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            singleBackList = new ArrayList<>(0);
            singleBackList.addAll(convertTexture(Transformation.identity(), sprite, 0.5F, Direction.SOUTH, 0xFFFFFFFF, 1, rl));
            singleBackList.addAll(convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl));
        }
        
        return singleBackList;
    }
    
    private List<BakedQuad> getPartneredBackList()
    {
        if(partneredBackList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("card_back"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            partneredBackList = convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl);
        }
        
        return partneredBackList;
    }
    
    private List<BakedQuad> getBlancList()
    {
        if(blancList == null)
        {
            ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "item/" + YDM.proxy.addCardItemTag("blanc_card"));
            TextureAtlasSprite sprite = textureGetter.apply(rl);
            blancList = convertTexture(Transformation.identity(), sprite, 0.5F, Direction.NORTH, 0xFFFFFFFF, 1, rl);
        }
        
        return blancList;
    }
    
    public static List<BakedQuad> convertTexture(Transformation t, TextureAtlasSprite sprite, float off, Direction direction, int color, int layerIdx, ResourceLocation rl)
    {
        ModelState modelState = new SimpleModelState(new Transformation(Vector3f.ZERO, Quaternion.ONE, new Vector3f(1F, 1F, 1F), Quaternion.ONE));
        
        List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(layerIdx, sprite);
        unbaked.forEach(e ->
        {
            for(Direction d : Direction.values())
            {
                if(d != direction)
                {
                    e.faces.remove(d);
                }
            }
            
            float z = (e.from.z() + e.to.z()) * 0.5F;
            e.from.setZ(z);
            e.to.setZ(z);
        });
        
        return UnbakedGeometryHelper.bakeElements(unbaked, m -> sprite, modelState, rl);
    }
}
