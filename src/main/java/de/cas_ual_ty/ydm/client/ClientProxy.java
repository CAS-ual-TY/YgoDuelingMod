package de.cas_ual_ty.ydm.client;

import java.util.List;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.ISidedProxy;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.Card;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientProxy implements ISidedProxy
{
    @Override
    public void registerModEventListeners(IEventBus bus)
    {
        if(YDM.itemsUseCardImages)
        {
            List<Card> list = ImageHandler.getMissingItemImages();
            
            if(list.size() == 0)
            {
                bus.addListener(this::textureStitch);
                bus.addListener(this::modelBake);
            }
            else
            {
                ImageHandler.downloadAllCardImages();
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
        for(Card card : Database.CARDS_LIST)
        {
            event.addSprite(card.getItemImageResourceLocation());
        }
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        ModelResourceLocation key = new ModelResourceLocation(YdmItems.CARD.getRegistryName(), "inventory");
        event.getModelRegistry().put(key, new CardBakedModel(event.getModelRegistry().get(key)));
    }
}
