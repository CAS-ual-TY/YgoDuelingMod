package de.cas_ual_ty.ydm.client;

import java.util.List;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.ISidedProxy;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.Card;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientProxy implements ISidedProxy
{
    @Override
    public void registerModEventListeners(IEventBus bus)
    {
        bus.addListener(this::textureStitch);
        bus.addListener(this::modelRegistry);
        bus.addListener(this::modelBake);
        
        if(YDM.itemsUseCardImages)
        {
            List<Card> list = ImageHandler.getMissingItemImages();
            
            if(list.size() == 0)
            {
                YDM.log("Items will use card images!");
                YDM.itemsUseCardImagesActive = true;
            }
            else
            {
                YDM.log("Items will not use card images, still missing " + list.size() + " images. Fetching...");
                ImageHandler.downloadCardImages(list);
            }
        }
    }
    
    @Override
    public void preInit()
    {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new YdmResourcePackFinder());
    }
    
    private void textureStitch(TextureStitchEvent.Pre event)
    {
        if(YDM.itemsUseCardImagesActive)
        {
            for(Card card : Database.CARDS_LIST)
            {
                event.addSprite(card.getItemImageResourceLocation());
            }
        }
    }
    
    private void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory"));
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        YdmItems.CARD.getRegistryName();
        
        YdmItems.CARD_BACK.getRegistryName();
        
        event.getModelRegistry().put(new ModelResourceLocation(YdmItems.CARD_BACK.getRegistryName(), "inventory"),
            event.getModelRegistry().get(
                new ModelResourceLocation(
                    new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory")));
        
        if(YDM.itemsUseCardImages)
        {
            ModelResourceLocation key = new ModelResourceLocation(YdmItems.CARD.getRegistryName(), "inventory");
            event.getModelRegistry().put(key, new CardBakedModel(event.getModelRegistry().get(key)));
        }
    }
}
