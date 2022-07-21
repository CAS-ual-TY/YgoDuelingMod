package de.cas_ual_ty.ydm.datagen;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.clientutil.ImageHandler;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.io.IOException;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class YDMDataGen
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        try
        {
            ImageHandler.createCustomSleevesImages(CardSleevesType.P_2, "jpg");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new YDMItemModels(generator, YDM.MOD_ID, event.getExistingFileHelper()));
    }
}