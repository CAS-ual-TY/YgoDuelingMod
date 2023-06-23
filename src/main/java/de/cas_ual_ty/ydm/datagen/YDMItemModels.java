package de.cas_ual_ty.ydm.datagen;

import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class YDMItemModels extends ItemModelProvider
{
    public YDMItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
    {
        super(generator, modid, existingFileHelper);
    }
    
    @Override
    protected void registerModels()
    {
        for(CardSleevesType sleeves : CardSleevesType.VALUES)
        {
            if(!sleeves.isCardBack())
            {
                String main = modid + ":item/" + sleeves.getResourceName();
                
                getBuilder(main)
                        .parent(new UncheckedModelFile("item/generated"))
                        .texture("layer0", modLoc("item/" + 16 + "/" + sleeves.getResourceName()));
                
                for(int i = 4; i <= 10; ++i)
                {
                    int size = YdmUtil.getPow2(i);
                    
                    getBuilder(sleeves.getItemModelRL(size).toString())
                            .parent(new UncheckedModelFile(main))
                            .texture("layer0", modLoc("item/" + size + "/" + sleeves.getResourceName()));
                }
            }
        }
    }
    
    public void defaultSizedModel(Item item, int size)
    {
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
        getBuilder(rl.toString() + "_" + size)
                .parent(new UncheckedModelFile(rl.toString()))
                .texture("layer0", modLoc("item/" + rl.getPath() + "_" + size));
    }
}
