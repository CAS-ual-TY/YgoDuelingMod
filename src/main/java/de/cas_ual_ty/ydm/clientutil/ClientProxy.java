package de.cas_ual_ty.ydm.clientutil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.cardbinder.CardBinderScreen;
import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.carditeminventory.CIIScreen;
import de.cas_ual_ty.ydm.cardsupply.CardSupplyScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.screen.DuelContainerScreen;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenBase;
import de.cas_ual_ty.ydm.set.CardSet;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ClientProxy implements ISidedProxy
{
    public static ForgeConfigSpec clientConfigSpec;
    public static ClientConfig clientConfig;
    
    public static int activeCardInfoImageSize;
    public static volatile int activeCardItemImageSize;
    public static int activeCardMainImageSize;
    public static int activeSetInfoImageSize;
    public static volatile int activeSetItemImageSize;
    public static boolean keepCachedImages;
    public static boolean itemsUseCardImages;
    public static boolean itemsUseSetImages;
    public static boolean showBinderId;
    public static int maxInfoImages;
    public static int maxMainImages;
    public static double duelChatSize;
    public static int moveAnimationLength;
    public static int specialAnimationLength;
    public static int attackAnimationLength;
    public static int announcementAnimationLength;
    
    public static volatile boolean itemsUseCardImagesActive;
    public static volatile boolean itemsUseCardImagesFailed;
    public static volatile boolean itemsUseSetImagesActive;
    public static volatile boolean itemsUseSetImagesFailed;
    
    public static File imagesParentFolder;
    public static File cardImagesFolder;
    public static File setImagesFolder;
    public static File rawCardImagesFolder;
    public static File rawSetImagesFolder;
    private static File cardInfoImagesFolder;
    private static File cardItemImagesFolder;
    private static File cardMainImagesFolder;
    private static File setInfoImagesFolder;
    private static File setItemImagesFolder;
    
    @Override
    public void registerModEventListeners(IEventBus bus)
    {
        bus.addListener(this::textureStitchPre);
        bus.addListener(this::modelRegistry);
        bus.addListener(this::modelBake);
        bus.addListener(this::modConfig);
    }
    
    @Override
    public void registerForgeEventListeners(IEventBus bus)
    {
        bus.addListener(this::guiScreenDrawScreenPost);
        bus.addListener(this::renderGameOverlayPost);
        bus.addListener(this::clientChatReceived);
    }
    
    @Override
    public void preInit()
    {
        ClientProxy.itemsUseCardImagesActive = false;
        ClientProxy.itemsUseCardImagesFailed = false;
        ClientProxy.itemsUseSetImagesActive = false;
        ClientProxy.itemsUseSetImagesFailed = false;
        
        Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        ClientProxy.clientConfig = client.getLeft();
        ClientProxy.clientConfigSpec = client.getRight();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientProxy.clientConfigSpec);
        
        if(ClientProxy.getMinecraft() != null)
        {
            ClientProxy.getMinecraft().getResourcePackList().addPackFinder(new YdmResourcePackFinder());
        }
    }
    
    @Override
    public void init()
    {
        YDM.log("Sizes from client config (info/item/main): " + ClientProxy.activeCardInfoImageSize + " / " + ClientProxy.activeCardItemImageSize + " (" + ClientProxy.itemsUseCardImages + ") / " + ClientProxy.activeCardMainImageSize);
        
        if(ClientProxy.itemsUseCardImages)
        {
            try
            {
                List<CardHolder> list = ImageHandler.getMissingItemImages();
                
                if(list.size() == 0)
                {
                    YDM.log("Items will use card images!");
                    ClientProxy.itemsUseCardImagesActive = true;
                }
                else
                {
                    YDM.log("Items will not use card images, still missing " + list.size() + " images. Fetching...");
                    ImageHandler.downloadCardImages(list);
                    ClientProxy.itemsUseCardImagesFailed = true;
                }
            }
            catch (Exception e)
            {
                YDM.log("Failed checking missing item images!");
                e.printStackTrace();
                ClientProxy.itemsUseCardImagesFailed = true;
            }
        }
        
        if(ClientProxy.itemsUseSetImages)
        {
            try
            {
                List<CardSet> list = ImageHandler.getMissingSetImages();
                
                if(list.size() == 0)
                {
                    YDM.log("Items will use set images!");
                    ClientProxy.itemsUseSetImagesActive = true;
                }
                else
                {
                    YDM.log("Items will not use set images, still missing " + list.size() + " images. Fetching...");
                    ImageHandler.downloadSetImages(list);
                    ClientProxy.itemsUseSetImagesFailed = true;
                }
            }
            catch (Exception e)
            {
                YDM.log("Failed checking missing set images!");
                e.printStackTrace();
                ClientProxy.itemsUseSetImagesFailed = true;
            }
        }
        
        ScreenManager.registerFactory(YdmContainerTypes.CARD_BINDER, CardBinderScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.DECK_BOX, DeckBoxScreen::new);
        ScreenManager.<DuelContainer, DuelContainerScreen<DuelContainer>>registerFactory(YdmContainerTypes.DUEL_BLOCK_CONTAINER, DuelScreenBase::new);
        ScreenManager.registerFactory(YdmContainerTypes.CARD_SUPPLY, CardSupplyScreen::new);
        ScreenManager.<CIIContainer, CIIScreen<CIIContainer>>registerFactory(YdmContainerTypes.CARD_SET, CIIScreen::new);
        ScreenManager.<CIIContainer, CIIScreen<CIIContainer>>registerFactory(YdmContainerTypes.CARD_SET_CONTENTS, CIIScreen::new);
        ScreenManager.<CIIContainer, CIIScreen<CIIContainer>>registerFactory(YdmContainerTypes.SIMPLE_BINDER, CIIScreen::new);
        
        CardRenderUtil.init(ClientProxy.maxInfoImages, ClientProxy.maxMainImages);
    }
    
    @Override
    public void initFolders()
    {
        ClientProxy.imagesParentFolder = new File("ydm_db_images");
        ClientProxy.cardImagesFolder = new File(ClientProxy.imagesParentFolder, "cards");
        ClientProxy.setImagesFolder = new File(ClientProxy.imagesParentFolder, "sets");
        ClientProxy.rawCardImagesFolder = new File(ClientProxy.cardImagesFolder, "raw");
        ClientProxy.rawSetImagesFolder = new File(ClientProxy.setImagesFolder, "raw");
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.setImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawCardImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawSetImagesFolder);
    }
    
    @Override
    public void initFiles() // done before #init
    {
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        ClientProxy.cardInfoImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeCardInfoImageSize);
        ClientProxy.cardItemImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeCardItemImageSize);
        ClientProxy.cardMainImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeCardMainImageSize);
        
        // change this depending on resolution (64/128/256)
        ClientProxy.setInfoImagesFolder = new File(ClientProxy.setImagesFolder, "" + ClientProxy.activeSetInfoImageSize);
        ClientProxy.setItemImagesFolder = new File(ClientProxy.setImagesFolder, "" + ClientProxy.activeSetItemImageSize);
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardItemImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardMainImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.setInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.setItemImagesFolder);
    }
    
    @Override
    public PlayerEntity getClientPlayer()
    {
        return ClientProxy.getPlayer();
    }
    
    @Override
    public String addCardInfoTag(String imageName)
    {
        return ClientProxy.activeCardInfoImageSize + "/" + imageName;
    }
    
    @Override
    public String addCardItemTag(String imageName)
    {
        return ClientProxy.activeCardItemImageSize + "/" + imageName;
    }
    
    @Override
    public String addCardMainTag(String imageName)
    {
        return ClientProxy.activeCardMainImageSize + "/" + imageName;
    }
    
    @Override
    public String addSetInfoTag(String imageName)
    {
        return ClientProxy.activeSetInfoImageSize + "/" + imageName;
    }
    
    @Override
    public String addSetItemTag(String imageName)
    {
        return ClientProxy.activeSetItemImageSize + "/" + imageName;
    }
    
    @Override
    public String getCardInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getInfoReplacementImage(properties, imageIndex);
    }
    
    @Override
    public String getCardMainReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getMainReplacementImage(properties, imageIndex);
    }
    
    @Override
    public String getSetInfoReplacementImage(CardSet set)
    {
        return ImageHandler.getInfoReplacementImage(set);
    }
    
    @SuppressWarnings("deprecation")
    private void textureStitchPre(TextureStitchEvent.Pre event)
    {
        if(event.getMap().getTextureLocation() != AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        {
            return;
        }
        
        boolean flag = false;
        int i = 0;
        
        while((ClientProxy.itemsUseCardImages && !ClientProxy.itemsUseCardImagesFailed && !ClientProxy.itemsUseCardImagesActive)
            || (ClientProxy.itemsUseSetImages && !ClientProxy.itemsUseSetImagesFailed && !ClientProxy.itemsUseSetImagesActive))
        {
            if(!flag)
            {
                flag = true;
                YDM.log("Sleeping for a couple seconds to give the worker enough time to check the item and set images...");
            }
            
            // sometimes this gets done before YDM.itemsUseCardImagesActive is set to true
            // so lets wait a bit to make sure the value is correct
            
            ++i;
            
            try
            {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e)
            {
                YDM.log("Tried sleeping to give textures enough time... It didnt work :(");
                e.printStackTrace();
                break;
            }
        }
        
        if(i > 0)
        {
            YDM.log("Slept for " + i + " seconds.");
        }
        
        if(ClientProxy.itemsUseCardImagesActive)
        {
            YDM.log("Stitching " + YdmDatabase.getTotalCardsAndVariants() + " card item textures!");
            
            YdmDatabase.forAllCardVariants((card, imageIndex) ->
            {
                event.addSprite(card.getItemImageResourceLocation(imageIndex));
            });
        }
        
        if(ClientProxy.itemsUseSetImagesActive)
        {
            YDM.log("Stitching " + YdmDatabase.SETS_LIST.size() + " set item textures!");
            
            for(CardSet set : YdmDatabase.SETS_LIST)
            {
                if(set.isIndependentAndItem())
                {
                    event.addSprite(set.getItemImageResourceLocation());
                }
            }
        }
    }
    
    private void modelRegistry(ModelRegistryEvent event)
    {
        boolean flag = false;
        
        while((ClientProxy.activeCardItemImageSize == 0 || ClientProxy.activeSetItemImageSize == 0))
        {
            if(!flag)
            {
                flag = true;
                YDM.log("Sleeping for a couple seconds to give the worker enough time to read the config...");
            }
            
            // sometimes this gets done before YDM.itemsUseCardImagesActive is set to true
            // so lets wait a bit to make sure the value is correct
            
            //++i;
            
            try
            {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e)
            {
                YDM.log("Tried sleeping to give textures enough time... It didnt work :(");
                e.printStackTrace();
                break;
            }
        }
        
        YDM.log("Registering models (size: " + ClientProxy.activeCardItemImageSize + ") for " + YdmItems.BLANC_CARD.getRegistryName().toString() + " and " + YdmItems.CARD_BACK.getRegistryName().toString());
        
        // 16 is default texture; no need to do anything special in that case
        if(ClientProxy.activeCardItemImageSize != 16)
        {
            ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory"));
            ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory"));
            
            for(CardSleevesType sleeves : CardSleevesType.VALUES)
            {
                if(!sleeves.isCardBack())
                {
                    ModelLoader.addSpecialModel(new ModelResourceLocation(sleeves.getItemModelRL(ClientProxy.activeCardItemImageSize), "inventory"));
                }
            }
        }
        
        YDM.log("Registering models (size: " + ClientProxy.activeSetItemImageSize + ") for " + YdmItems.BLANC_SET.getRegistryName().toString());
        
        if(ClientProxy.activeSetItemImageSize != 16)
        {
            ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_SET.getRegistryName().toString() + "_" + ClientProxy.activeSetItemImageSize), "inventory"));
        }
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        YDM.log("Baking models (size: " + ClientProxy.activeCardItemImageSize + ") for " + YdmItems.BLANC_CARD.getRegistryName().toString() + " and " + YdmItems.CARD_BACK.getRegistryName().toString());
        
        // 16 is default texture; no need to do anything special in that case
        if(ClientProxy.activeCardItemImageSize != 16)
        {
            event.getModelRegistry().put(new ModelResourceLocation(YdmItems.BLANC_CARD.getRegistryName(), "inventory"),
                event.getModelRegistry().get(
                    new ModelResourceLocation(
                        new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory")));
            
            event.getModelRegistry().put(new ModelResourceLocation(YdmItems.CARD_BACK.getRegistryName(), "inventory"),
                event.getModelRegistry().get(
                    new ModelResourceLocation(
                        new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory")));
            
            for(CardSleevesType sleeves : CardSleevesType.VALUES)
            {
                if(!sleeves.isCardBack())
                {
                    event.getModelRegistry().put(new ModelResourceLocation(new ResourceLocation(YDM.MOD_ID, "sleeves_" + sleeves.name), "inventory"),
                        event.getModelRegistry().get(
                            new ModelResourceLocation(
                                sleeves.getItemModelRL(ClientProxy.activeCardItemImageSize), "inventory")));
                }
            }
        }
        
        if(ClientProxy.activeSetItemImageSize != 16)
        {
            event.getModelRegistry().put(new ModelResourceLocation(YdmItems.BLANC_SET.getRegistryName(), "inventory"),
                event.getModelRegistry().get(
                    new ModelResourceLocation(
                        new ResourceLocation(YdmItems.BLANC_SET.getRegistryName().toString() + "_" + ClientProxy.activeSetItemImageSize), "inventory")));
        }
        
        ModelResourceLocation key = new ModelResourceLocation(YdmItems.CARD.getRegistryName(), "inventory");
        event.getModelRegistry().put(key, new CardBakedModel(event.getModelRegistry().get(key)));
        
        key = new ModelResourceLocation(YdmItems.SET.getRegistryName(), "inventory");
        event.getModelRegistry().put(key, new CardSetBakedModel(event.getModelRegistry().get(key)));
        key = new ModelResourceLocation(YdmItems.OPENED_SET.getRegistryName(), "inventory");
        event.getModelRegistry().put(key, new CardSetBakedModel(event.getModelRegistry().get(key)));
    }
    
    private void modConfig(final ModConfig.ModConfigEvent event)
    {
        if(event.getConfig().getSpec() == ClientProxy.clientConfigSpec)
        {
            YDM.log("Baking client config!");
            ClientProxy.activeCardInfoImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeCardInfoImageSize.get(), 4);
            ClientProxy.activeCardItemImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeCardItemImageSize.get(), 4);
            ClientProxy.activeCardMainImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeCardMainImageSize.get(), 4);
            ClientProxy.activeSetInfoImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeSetInfoImageSize.get(), 4);
            ClientProxy.activeSetItemImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeSetItemImageSize.get(), 4);
            ClientProxy.keepCachedImages = ClientProxy.clientConfig.keepCachedImages.get();
            ClientProxy.itemsUseCardImages = ClientProxy.clientConfig.itemsUseCardImages.get();
            ClientProxy.itemsUseSetImages = ClientProxy.clientConfig.itemsUseSetImages.get();
            ClientProxy.showBinderId = ClientProxy.clientConfig.showBinderId.get();
            ClientProxy.maxInfoImages = ClientProxy.clientConfig.maxInfoImages.get();
            ClientProxy.maxMainImages = ClientProxy.clientConfig.maxMainImages.get();
            ClientProxy.duelChatSize = ClientProxy.clientConfig.duelChatSize.get();
            ClientProxy.moveAnimationLength = ClientProxy.clientConfig.moveAnimationLength.get();
            ClientProxy.attackAnimationLength = ClientProxy.clientConfig.attackAnimationLength.get();
            ClientProxy.specialAnimationLength = ClientProxy.clientConfig.specialAnimationLength.get();
            ClientProxy.announcementAnimationLength = ClientProxy.clientConfig.announcementAnimationLength.get();
        }
    }
    
    private void guiScreenDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        if(event.getGui() instanceof ContainerScreen)
        {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>)event.getGui();
            
            if(containerScreen.getSlotUnderMouse() != null && !containerScreen.getSlotUnderMouse().getStack().isEmpty())
            {
                ItemStack itemStack = containerScreen.getSlotUnderMouse().getStack();
                
                if(itemStack.getItem() == YdmItems.CARD)
                {
                    CardRenderUtil.renderCardInfo(event.getMatrixStack(), YdmItems.CARD.getCardHolder(itemStack), containerScreen);
                }
                else if(itemStack.getItem() == YdmItems.SET)
                {
                    this.renderSetInfo(event.getMatrixStack(), YdmItems.SET.getCardSet(itemStack), containerScreen.getGuiLeft());
                }
                else if(itemStack.getItem() == YdmItems.OPENED_SET)
                {
                    this.renderSetInfo(event.getMatrixStack(), YdmItems.OPENED_SET.getCardSet(itemStack), containerScreen.getGuiLeft());
                }
                else if(itemStack.getItem() instanceof CardSleevesItem)
                {
                    this.renderSleevesInfo(event.getMatrixStack(), ((CardSleevesItem)itemStack.getItem()).sleeves, containerScreen.getGuiLeft());
                }
            }
        }
    }
    
    private void renderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL)
        {
            return;
        }
        
        if(this.getClientPlayer() != null && ClientProxy.getMinecraft().currentScreen == null)
        {
            PlayerEntity player = this.getClientPlayer();
            
            if(player.getHeldItemMainhand().getItem() == YdmItems.CARD)
            {
                CardRenderUtil.renderCardInfo(event.getMatrixStack(), YdmItems.CARD.getCardHolder(player.getHeldItemMainhand()));
            }
            else if(player.getHeldItemMainhand().getItem() == YdmItems.SET)
            {
                this.renderSetInfo(event.getMatrixStack(), YdmItems.SET.getCardSet(player.getHeldItemMainhand()));
            }
            else if(player.getHeldItemMainhand().getItem() == YdmItems.OPENED_SET)
            {
                this.renderSetInfo(event.getMatrixStack(), YdmItems.OPENED_SET.getCardSet(player.getHeldItemMainhand()));
            }
            else if(player.getHeldItemMainhand().getItem() instanceof CardSleevesItem)
            {
                this.renderSleevesInfo(event.getMatrixStack(), ((CardSleevesItem)player.getHeldItemMainhand().getItem()).sleeves);
            }
            else if(player.getHeldItemOffhand().getItem() == YdmItems.CARD)
            {
                CardRenderUtil.renderCardInfo(event.getMatrixStack(), YdmItems.CARD.getCardHolder(player.getHeldItemOffhand()));
            }
            else if(player.getHeldItemOffhand().getItem() == YdmItems.SET)
            {
                this.renderSetInfo(event.getMatrixStack(), YdmItems.SET.getCardSet(player.getHeldItemOffhand()));
            }
            else if(player.getHeldItemOffhand().getItem() == YdmItems.OPENED_SET)
            {
                this.renderSetInfo(event.getMatrixStack(), YdmItems.OPENED_SET.getCardSet(player.getHeldItemOffhand()));
            }
            else if(player.getHeldItemOffhand().getItem() instanceof CardSleevesItem)
            {
                this.renderSleevesInfo(event.getMatrixStack(), ((CardSleevesItem)player.getHeldItemMainhand().getItem()).sleeves);
            }
        }
    }
    
    private void renderSetInfo(MatrixStack ms, CardSet set)
    {
        this.renderSetInfo(ms, set, 150);
    }
    
    private void renderSetInfo(MatrixStack ms, CardSet set, int width)
    {
        if(set == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.push();
        ScreenUtil.white();
        
        {
            int x = margin;
            
            if(maxWidth < imageSize)
            {
                // draw it centered if the space we got is limited
                // to make sure the image is NOT rendered more to the right of the center
                x = (maxWidth - imageSize) / 2 + margin;
            }
            
            // card texture
            
            Minecraft.getInstance().textureManager.bindTexture(set.getInfoImageResourceLocation());
            YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        }
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        ms.scale(f, f, f);
        
        {
            // card description text
            
            @SuppressWarnings("resource")
            FontRenderer fontRenderer = ClientProxy.getMinecraft().fontRenderer;
            
            List<ITextComponent> list = new LinkedList<>();
            set.addInformation(list);
            
            ScreenUtil.drawSplitString(ms, fontRenderer, list, margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        }
        
        ms.pop();
    }
    
    private void renderSleevesInfo(MatrixStack ms, CardSleevesType sleeves)
    {
        this.renderSleevesInfo(ms, sleeves, 150);
    }
    
    private void renderSleevesInfo(MatrixStack ms, CardSleevesType sleeves, int width)
    {
        if(sleeves == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.push();
        ScreenUtil.white();
        
        {
            int x = margin;
            
            if(maxWidth < imageSize)
            {
                // draw it centered if the space we got is limited
                // to make sure the image is NOT rendered more to the right of the center
                x = (maxWidth - imageSize) / 2 + margin;
            }
            
            // card texture
            
            Minecraft.getInstance().textureManager.bindTexture(sleeves.getMainRL(ClientProxy.activeCardInfoImageSize));
            YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        }
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        ms.scale(f, f, f);
        
        {
            // card description text
            
            @SuppressWarnings("resource")
            FontRenderer fontRenderer = ClientProxy.getMinecraft().fontRenderer;
            
            ScreenUtil.drawSplitString(ms, fontRenderer, ImmutableList.<ITextComponent>of(new TranslationTextComponent("item.ydm." + sleeves.getResourceName())), margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        }
        
        ms.pop();
    }
    
    public static int maxMessages = 50; //TODO make configurable
    public static List<ITextComponent> chatMessages = new ArrayList<>(50);
    
    private void clientChatReceived(ClientChatReceivedEvent event)
    {
        if(!event.isCanceled() && event.getMessage() != null && !event.getMessage().getString().isEmpty() && !ClientProxy.getMinecraft().cannotSendChatMessages(event.getSenderUUID()))
        {
            if(ClientProxy.chatMessages.size() >= ClientProxy.maxMessages)
            {
                ClientProxy.chatMessages.remove(0);
            }
            
            ClientProxy.chatMessages.add(event.getMessage());
        }
    }
    
    public static Minecraft getMinecraft()
    {
        return Minecraft.getInstance();
    }
    
    @SuppressWarnings("resource")
    public static PlayerEntity getPlayer()
    {
        return ClientProxy.getMinecraft().player;
    }
}
