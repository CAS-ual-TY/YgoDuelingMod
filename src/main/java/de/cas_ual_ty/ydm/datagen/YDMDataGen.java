package de.cas_ual_ty.ydm.datagen;

import java.io.IOException;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.clientutil.ImageHandler;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class YDMDataGen
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        try
        {
            for(int i = 13; i < CardSleevesType.VALUES.length; ++i)
            {
                ImageHandler.createCustomSleevesImages(CardSleevesType.VALUES[i], "png");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new YDMItemModels(generator, YDM.MOD_ID, event.getExistingFileHelper()));
    }
}