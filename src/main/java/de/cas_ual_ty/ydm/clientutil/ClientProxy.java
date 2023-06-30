package de.cas_ual_ty.ydm.clientutil;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.*;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.card.InspectCardScreen;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.cardbinder.CardBinderScreen;
import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.carditeminventory.CIIScreen;
import de.cas_ual_ty.ydm.cardsupply.CardSupplyScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.screen.DuelContainerScreen;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenBase;
import de.cas_ual_ty.ydm.rarity.RarityLayer;
import de.cas_ual_ty.ydm.set.CardSet;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public static File rarityImagesFolder;
    public static File rarityMainImagesFolder;
    public static File rarityInfoImagesFolder;
    public static File rawCardImagesFolder;
    public static File rawSetImagesFolder;
    public static File rawRarityImagesFolder;
    private static File cardInfoImagesFolder;
    private static File cardItemImagesFolder;
    private static File cardMainImagesFolder;
    private static File setInfoImagesFolder;
    private static File setItemImagesFolder;
    
    @Override
    public void registerModEventListeners(IEventBus bus)
    {
        bus.addListener(this::entityRenderers);
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
            ClientProxy.getMinecraft().getResourcePackRepository().addPackFinder(new YdmResourcePackFinder());
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
            catch(Exception e)
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
            catch(Exception e)
            {
                YDM.log("Failed checking missing set images!");
                e.printStackTrace();
                ClientProxy.itemsUseSetImagesFailed = true;
            }
        }
        
        MenuScreens.register(YdmContainerTypes.CARD_BINDER.get(), CardBinderScreen::new);
        MenuScreens.register(YdmContainerTypes.DECK_BOX.get(), DeckBoxScreen::new);
        MenuScreens.<DuelContainer, DuelContainerScreen<DuelContainer>>register(YdmContainerTypes.DUEL_BLOCK_CONTAINER.get(), DuelScreenBase::new);
        MenuScreens.<DuelContainer, DuelContainerScreen<DuelContainer>>register(YdmContainerTypes.DUEL_ENTITY_CONTAINER.get(), DuelScreenBase::new);
        MenuScreens.register(YdmContainerTypes.CARD_SUPPLY.get(), CardSupplyScreen::new);
        MenuScreens.register(YdmContainerTypes.CARD_SET.get(), (MenuScreens.ScreenConstructor<CIIContainer, CIIScreen<CIIContainer>>) (CIIScreen::new));
        MenuScreens.register(YdmContainerTypes.CARD_SET_CONTENTS.get(), (MenuScreens.ScreenConstructor<CIIContainer, CIIScreen<CIIContainer>>) (CIIScreen::new));
        MenuScreens.register(YdmContainerTypes.SIMPLE_BINDER.get(), (MenuScreens.ScreenConstructor<CIIContainer, CIIScreen<CIIContainer>>) (CIIScreen::new));
        
        ImageHandler.prepareRarityImages(ClientProxy.activeCardMainImageSize);
        ImageHandler.prepareRarityImages(ClientProxy.activeCardInfoImageSize);
        CardRenderUtil.init(ClientProxy.maxInfoImages, ClientProxy.maxMainImages);
    }
    
    @Override
    public void initFolders()
    {
        ClientProxy.imagesParentFolder = new File("ydm_db_images");
        ClientProxy.cardImagesFolder = new File(ClientProxy.imagesParentFolder, "cards");
        ClientProxy.setImagesFolder = new File(ClientProxy.imagesParentFolder, "sets");
        ClientProxy.rarityImagesFolder = new File(ClientProxy.imagesParentFolder, "rarities");
        ClientProxy.rawCardImagesFolder = new File(ClientProxy.cardImagesFolder, "raw");
        ClientProxy.rawSetImagesFolder = new File(ClientProxy.setImagesFolder, "raw");
        ClientProxy.rawRarityImagesFolder = new File(YDM.mainFolder, "rarity_images");
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.setImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rarityImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawCardImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawSetImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawRarityImagesFolder);
    }
    
    @Override
    public void initFiles() // done before #init
    {
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        ClientProxy.cardInfoImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeCardInfoImageSize);
        ClientProxy.cardItemImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeCardItemImageSize);
        ClientProxy.cardMainImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeCardMainImageSize);
        
        ClientProxy.rarityMainImagesFolder = new File(rarityImagesFolder, "" + ClientProxy.activeCardMainImageSize);
        ClientProxy.rarityInfoImagesFolder = new File(rarityImagesFolder, "" + ClientProxy.activeCardInfoImageSize);
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardItemImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardMainImagesFolder);
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rarityMainImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rarityInfoImagesFolder);
        
        // change this depending on resolution (64/128/256)
        ClientProxy.setInfoImagesFolder = new File(ClientProxy.setImagesFolder, "" + ClientProxy.activeSetInfoImageSize);
        ClientProxy.setItemImagesFolder = new File(ClientProxy.setImagesFolder, "" + ClientProxy.activeSetItemImageSize);
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.setInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.setItemImagesFolder);
    }
    
    @Override
    public Player getClientPlayer()
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
    
    @Override
    public String getRarityMainImage(RarityLayer layer)
    {
        return ImageHandler.getRarityMainImage(layer);
    }
    
    @Override
    public String getRarityInfoImage(RarityLayer layer)
    {
        return ImageHandler.getRarityInfoImage(layer);
    }
    
    @Override
    public void openCardInspectScreen(CardHolder card)
    {
        Minecraft.getInstance().setScreen(new InspectCardScreen(card));
    }
    
    private void entityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(YdmEntityTypes.DUEL.get(), DuelEntityRenderer::new);
    }
    
    @SuppressWarnings("deprecation")
    private void textureStitchPre(TextureStitchEvent.Pre event)
    {
        if(event.getAtlas().location() != TextureAtlas.LOCATION_BLOCKS)
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
            catch(InterruptedException e)
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
    
    private void modelRegistry(ModelEvent.RegisterAdditional event)
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
            catch(InterruptedException e)
            {
                YDM.log("Tried sleeping to give textures enough time... It didnt work :(");
                e.printStackTrace();
                break;
            }
        }
        
        YDM.log("Registering models (size: " + ClientProxy.activeCardItemImageSize + ") for " + YdmItems.BLANC_CARD.getId().toString() + " and " + YdmItems.CARD_BACK.getId().toString());
        
        // 16 is default texture; no need to do anything special in that case
        if(ClientProxy.activeCardItemImageSize != 16)
        {
            
            event.register(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_CARD.getId().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory"));
            event.register(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getId().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory"));
            
            for(CardSleevesType sleeves : CardSleevesType.VALUES)
            {
                if(!sleeves.isCardBack())
                {
                    event.register(new ModelResourceLocation(sleeves.getItemModelRL(ClientProxy.activeCardItemImageSize), "inventory"));
                }
            }
        }
        
        YDM.log("Registering models (size: " + ClientProxy.activeSetItemImageSize + ") for " + YdmItems.BLANC_SET.getId().toString());
        
        if(ClientProxy.activeSetItemImageSize != 16)
        {
            event.register(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_SET.getId().toString() + "_" + ClientProxy.activeSetItemImageSize), "inventory"));
        }
    }
    
    private void modelBake(ModelEvent.BakingCompleted event)
    {
        YDM.log("Baking models (size: " + ClientProxy.activeCardItemImageSize + ") for " + YdmItems.BLANC_CARD.getId().toString() + " and " + YdmItems.CARD_BACK.getId().toString());
        
        // 16 is default texture; no need to do anything special in that case
        if(ClientProxy.activeCardItemImageSize != 16)
        {
            event.getModels().put(new ModelResourceLocation(YdmItems.BLANC_CARD.getId(), "inventory"),
                    event.getModelManager().getModel(
                            new ModelResourceLocation(
                                    new ResourceLocation(YdmItems.BLANC_CARD.getId().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory")));
            
            event.getModels().put(new ModelResourceLocation(YdmItems.CARD_BACK.getId(), "inventory"),
                    event.getModelManager().getModel(
                            new ModelResourceLocation(
                                    new ResourceLocation(YdmItems.CARD_BACK.getId().toString() + "_" + ClientProxy.activeCardItemImageSize), "inventory")));
            
            for(CardSleevesType sleeves : CardSleevesType.VALUES)
            {
                if(!sleeves.isCardBack())
                {
                    event.getModels().put(new ModelResourceLocation(new ResourceLocation(YDM.MOD_ID, "sleeves_" + sleeves.name), "inventory"),
                            event.getModelManager().getModel(
                                    new ModelResourceLocation(
                                            sleeves.getItemModelRL(ClientProxy.activeCardItemImageSize), "inventory")));
                }
            }
        }
        
        if(ClientProxy.activeSetItemImageSize != 16)
        {
            event.getModels().put(new ModelResourceLocation(YdmItems.BLANC_SET.getId(), "inventory"),
                    event.getModelManager().getModel(
                            new ModelResourceLocation(
                                    new ResourceLocation(YdmItems.BLANC_SET.getId().toString() + "_" + ClientProxy.activeSetItemImageSize), "inventory")));
        }
        
        ModelResourceLocation key = new ModelResourceLocation(YdmItems.CARD.getId(), "inventory");
        event.getModels().put(key, new CardBakedModel(event.getModelManager().getModel(key)));
        
        key = new ModelResourceLocation(YdmItems.SET.getId(), "inventory");
        event.getModels().put(key, new CardSetBakedModel(event.getModelManager().getModel(key)));
        key = new ModelResourceLocation(YdmItems.OPENED_SET.getId(), "inventory");
        event.getModels().put(key, new CardSetBakedModel(event.getModelManager().getModel(key)));
    }
    
    private void modConfig(ModConfigEvent event)
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
    
    private void guiScreenDrawScreenPost(ScreenEvent.Render.Post event)
    {
        if(event.getScreen() instanceof AbstractContainerScreen)
        {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) event.getScreen();
            
            if(containerScreen.getSlotUnderMouse() != null && !containerScreen.getSlotUnderMouse().getItem().isEmpty())
            {
                ItemStack itemStack = containerScreen.getSlotUnderMouse().getItem();
                
                if(itemStack.getItem() == YdmItems.CARD.get())
                {
                    CardRenderUtil.renderCardInfo(event.getPoseStack(), YdmItems.CARD.get().getCardHolder(itemStack), containerScreen);
                }
                else if(itemStack.getItem() == YdmItems.SET.get())
                {
                    renderSetInfo(event.getPoseStack(), YdmItems.SET.get().getCardSet(itemStack), containerScreen.getGuiLeft());
                }
                else if(itemStack.getItem() == YdmItems.OPENED_SET.get())
                {
                    renderSetInfo(event.getPoseStack(), YdmItems.OPENED_SET.get().getCardSet(itemStack), containerScreen.getGuiLeft());
                }
                else if(itemStack.getItem() instanceof CardSleevesItem)
                {
                    renderSleevesInfo(event.getPoseStack(), ((CardSleevesItem) itemStack.getItem()).sleeves, containerScreen.getGuiLeft());
                }
            }
        }
    }
    
    private void renderGameOverlayPost(RenderGuiOverlayEvent.Post event)
    {
        if(event.getOverlay() != VanillaGuiOverlay.HOTBAR.type())
        {
            return;
        }
        
        if(getClientPlayer() != null && ClientProxy.getMinecraft().screen == null)
        {
            Player player = getClientPlayer();
            
            if(player.getMainHandItem().getItem() == YdmItems.CARD.get())
            {
                CardRenderUtil.renderCardInfo(event.getPoseStack(), YdmItems.CARD.get().getCardHolder(player.getMainHandItem()));
            }
            else if(player.getMainHandItem().getItem() == YdmItems.SET.get())
            {
                renderSetInfo(event.getPoseStack(), YdmItems.SET.get().getCardSet(player.getMainHandItem()));
            }
            else if(player.getMainHandItem().getItem() == YdmItems.OPENED_SET.get())
            {
                renderSetInfo(event.getPoseStack(), YdmItems.OPENED_SET.get().getCardSet(player.getMainHandItem()));
            }
            else if(player.getMainHandItem().getItem() instanceof CardSleevesItem)
            {
                renderSleevesInfo(event.getPoseStack(), ((CardSleevesItem) player.getMainHandItem().getItem()).sleeves);
            }
            else if(player.getOffhandItem().getItem() == YdmItems.CARD.get())
            {
                CardRenderUtil.renderCardInfo(event.getPoseStack(), YdmItems.CARD.get().getCardHolder(player.getOffhandItem()));
            }
            else if(player.getOffhandItem().getItem() == YdmItems.SET.get())
            {
                renderSetInfo(event.getPoseStack(), YdmItems.SET.get().getCardSet(player.getOffhandItem()));
            }
            else if(player.getOffhandItem().getItem() == YdmItems.OPENED_SET.get())
            {
                renderSetInfo(event.getPoseStack(), YdmItems.OPENED_SET.get().getCardSet(player.getOffhandItem()));
            }
            else if(player.getOffhandItem().getItem() instanceof CardSleevesItem)
            {
                renderSleevesInfo(event.getPoseStack(), ((CardSleevesItem) player.getMainHandItem().getItem()).sleeves);
            }
        }
    }
    
    private void renderSetInfo(PoseStack ms, CardSet set)
    {
        renderSetInfo(ms, set, 150);
    }
    
    private void renderSetInfo(PoseStack ms, CardSet set, int width)
    {
        if(set == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.pushPose();
        ScreenUtil.white();
        
        int x = margin;
        
        if(maxWidth < imageSize)
        {
            // draw it centered if the space we got is limited
            // to make sure the image is NOT rendered more to the right of the center
            x = (maxWidth - imageSize) / 2 + margin;
        }
        
        // card texture
        
        RenderSystem.setShaderTexture(0, set.getInfoImageResourceLocation());
        YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        ms.scale(f, f, f);
        
        // card description text
        
        Font fontRenderer = ClientProxy.getMinecraft().font;
        
        List<Component> list = new LinkedList<>();
        set.addInformation(list);
        
        ScreenUtil.drawSplitString(ms, fontRenderer, list, margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        
        ms.popPose();
    }
    
    private void renderSleevesInfo(PoseStack ms, CardSleevesType sleeves)
    {
        renderSleevesInfo(ms, sleeves, 150);
    }
    
    private void renderSleevesInfo(PoseStack ms, CardSleevesType sleeves, int width)
    {
        if(sleeves == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.pushPose();
        ScreenUtil.white();
        
        int x = margin;
        
        if(maxWidth < imageSize)
        {
            // draw it centered if the space we got is limited
            // to make sure the image is NOT rendered more to the right of the center
            x = (maxWidth - imageSize) / 2 + margin;
        }
        
        // card texture
        
        RenderSystem.setShaderTexture(0, sleeves.getMainRL(ClientProxy.activeCardInfoImageSize));
        YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        ms.scale(f, f, f);
        
        // card description text
        
        @SuppressWarnings("resource")
        Font fontRenderer = ClientProxy.getMinecraft().font;
        
        ScreenUtil.drawSplitString(ms, fontRenderer, ImmutableList.of(Component.translatable("item.ydm." + sleeves.getResourceName())), margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        
        ms.popPose();
    }
    
    public static int maxMessages = 50; //TODO make configurable
    public static List<Component> chatMessages = new ArrayList<>(50);
    
    private void clientChatReceived(ClientChatReceivedEvent event)
    {
        if(!event.isCanceled() && event.getMessage() != null && !event.getMessage().getString().isEmpty() && !ClientProxy.getMinecraft().isBlocked(event.getMessageSigner().profileId()))
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
    public static Player getPlayer()
    {
        return ClientProxy.getMinecraft().player;
    }
}
