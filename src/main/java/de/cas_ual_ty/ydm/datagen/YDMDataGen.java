package de.cas_ual_ty.ydm.datagen;

import de.cas_ual_ty.ydm.YDM;
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
        //        try
        //        {
        //            ImageHandler.createCustomSleevesImages(CardSleevesType.VFD, "jpg");
        //            ImageHandler.createCustomSleevesImages(CardSleevesType.OLD_ENTITY, "jpg");
        //            ImageHandler.createCustomSleevesImages(CardSleevesType.MASTER_PEACE, "jpg");
        //            ImageHandler.createCustomSleevesImages(CardSleevesType.HERO, "png");
        //            ImageHandler.createCustomSleevesImages(CardSleevesType.DESTINY_HERO, "png");
        //            ImageHandler.createCustomSleevesImages(CardSleevesType.P_1, "png");
        //        }
        //        catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }
        
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new YDMItemModels(generator, YDM.MOD_ID, event.getExistingFileHelper()));
    }
}