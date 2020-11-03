package de.cas_ual_ty.ydm.clientutil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.cardbinder.CardBinderScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.duel.screen.PreparingDuelScreen;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    
    public static int activeInfoImageSize;
    public static volatile int activeItemImageSize;
    public static int activeMainImageSize;
    public static boolean keepCachedImages;
    public static boolean itemsUseCardImages;
    public static boolean showBinderId;
    public static int maxInfoImages;
    public static int maxMainImages;
    
    public static volatile boolean itemsUseCardImagesActive;
    public static volatile boolean itemsUseCardImagesFailed;
    
    public static File imagesParentFolder;
    public static File cardImagesFolder;
    public static File rawCardImagesFolder;
    private static File cardInfoImagesFolder;
    private static File cardItemImagesFolder;
    private static File cardMainImagesFolder;
    
    public static LimitedTextureBinder infoTextureBinder;
    public static LimitedTextureBinder mainTextureBinder;
    
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
        
        Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        ClientProxy.clientConfig = client.getLeft();
        ClientProxy.clientConfigSpec = client.getRight();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientProxy.clientConfigSpec);
        
        ClientProxy.getMinecraft().getResourcePackList().addPackFinder(new YdmResourcePackFinder());
    }
    
    @Override
    public void init()
    {
        ImageHandler.init();
        
        YDM.log("Sizes from client config (info/item/main): " + ClientProxy.activeInfoImageSize + " / " + ClientProxy.activeItemImageSize + " (" + ClientProxy.itemsUseCardImages + ") / " + ClientProxy.activeMainImageSize);
        
        if(ClientProxy.itemsUseCardImages)
        {
            try
            {
                List<Card> list = ImageHandler.getMissingItemImages();
                
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
        
        ScreenManager.registerFactory(YdmContainerTypes.CARD_BINDER, CardBinderScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.DECK_BOX, DeckBoxScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.DUEL_BLOCK_CONTAINER, PreparingDuelScreen::new);
        
        ClientProxy.infoTextureBinder = new LimitedTextureBinder(ClientProxy.getMinecraft(), ClientProxy.maxInfoImages);
        ClientProxy.mainTextureBinder = new LimitedTextureBinder(ClientProxy.getMinecraft(), ClientProxy.maxMainImages);
    }
    
    @Override
    public void initFiles() // done before #init
    {
        ClientProxy.imagesParentFolder = new File("ydm_db_images");
        ClientProxy.cardImagesFolder = new File(ClientProxy.imagesParentFolder, "cards");
        ClientProxy.rawCardImagesFolder = new File(ClientProxy.cardImagesFolder, "raw");
        
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        ClientProxy.cardInfoImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeInfoImageSize);
        ClientProxy.cardItemImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeItemImageSize);
        ClientProxy.cardMainImagesFolder = new File(ClientProxy.cardImagesFolder, "" + ClientProxy.activeMainImageSize);
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawCardImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardItemImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardMainImagesFolder);
    }
    
    @Override
    public PlayerEntity getClientPlayer()
    {
        return ClientProxy.getPlayer();
    }
    
    @Override
    public String addInfoTag(String imageName)
    {
        return ClientProxy.activeInfoImageSize + "/" + imageName;
    }
    
    @Override
    public String addItemTag(String imageName)
    {
        return ClientProxy.activeItemImageSize + "/" + imageName;
    }
    
    @Override
    public String addMainTag(String imageName)
    {
        return ClientProxy.activeMainImageSize + "/" + imageName;
    }
    
    @Override
    public String getInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getInfoReplacementImage(properties, imageIndex);
    }
    
    @Override
    public String getMainReplacementImage(Properties properties, byte imageIndex)
    {
        return ImageHandler.getMainReplacementImage(properties, imageIndex);
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
        
        while(ClientProxy.itemsUseCardImages && !ClientProxy.itemsUseCardImagesFailed && !ClientProxy.itemsUseCardImagesActive)
        {
            if(!flag)
            {
                flag = true;
                YDM.log("Sleeping for a couple seconds to give the worker enough time to check the item images...");
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
            YDM.log("Stitching " + YdmDatabase.CARDS_LIST.size() + " card item textures!");
            
            for(Card card : YdmDatabase.CARDS_LIST)
            {
                event.addSprite(card.getItemImageResourceLocation());
            }
        }
    }
    
    private void modelRegistry(ModelRegistryEvent event)
    {
        YDM.log("Registering models (size: " + ClientProxy.activeItemImageSize + ") for " + YdmItems.BLANC_CARD.getRegistryName().toString() + " and " + YdmItems.CARD_BACK.getRegistryName().toString());
        
        // 16 is default texture; no need to do anything special in that case
        if(ClientProxy.activeItemImageSize != 16)
        {
            ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory"));
            ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory"));
        }
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        YDM.log("Baking models (size: " + ClientProxy.activeItemImageSize + ") for " + YdmItems.BLANC_CARD.getRegistryName().toString() + " and " + YdmItems.CARD_BACK.getRegistryName().toString());
        
        // 16 is default texture; no need to do anything special in that case
        if(ClientProxy.activeItemImageSize != 16)
        {
            event.getModelRegistry().put(new ModelResourceLocation(YdmItems.BLANC_CARD.getRegistryName(), "inventory"),
                event.getModelRegistry().get(
                    new ModelResourceLocation(
                        new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory")));
            
            event.getModelRegistry().put(new ModelResourceLocation(YdmItems.CARD_BACK.getRegistryName(), "inventory"),
                event.getModelRegistry().get(
                    new ModelResourceLocation(
                        new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory")));
        }
        
        ModelResourceLocation key = new ModelResourceLocation(YdmItems.CARD.getRegistryName(), "inventory");
        event.getModelRegistry().put(key, new CardBakedModel(event.getModelRegistry().get(key)));
    }
    
    private void modConfig(final ModConfig.ModConfigEvent event)
    {
        if(event.getConfig().getSpec() == ClientProxy.clientConfigSpec)
        {
            YDM.log("Baking client config!");
            ClientProxy.activeInfoImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeInfoImageSize.get(), 4);
            ClientProxy.activeItemImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeItemImageSize.get(), 4);
            ClientProxy.activeMainImageSize = YdmUtil.toPow2ConfigValue(ClientProxy.clientConfig.activeMainImageSize.get(), 4);
            ClientProxy.keepCachedImages = ClientProxy.clientConfig.keepCachedImages.get();
            ClientProxy.itemsUseCardImages = ClientProxy.clientConfig.itemsUseCardImages.get();
            ClientProxy.showBinderId = ClientProxy.clientConfig.showBinderId.get();
            ClientProxy.maxInfoImages = ClientProxy.clientConfig.maxInfoImages.get();
            ClientProxy.maxMainImages = ClientProxy.clientConfig.maxMainImages.get();
        }
    }
    
    private void guiScreenDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        if(event.getGui() instanceof ContainerScreen)
        {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>)event.getGui();
            
            if(containerScreen.getSlotUnderMouse() != null && !containerScreen.getSlotUnderMouse().getStack().isEmpty() && containerScreen.getSlotUnderMouse().getStack().getItem() == YdmItems.CARD)
            {
                ClientProxy.renderCardInfo(event.getMatrixStack(), YdmItems.CARD.getCardHolder(containerScreen.getSlotUnderMouse().getStack()), containerScreen);
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
                ClientProxy.renderCardInfo(event.getMatrixStack(), YdmItems.CARD.getCardHolder(player.getHeldItemMainhand()));
            }
            else if(player.getHeldItemOffhand().getItem() == YdmItems.CARD)
            {
                ClientProxy.renderCardInfo(event.getMatrixStack(), YdmItems.CARD.getCardHolder(player.getHeldItemOffhand()));
            }
        }
    }
    
    public static int maxMessages = 50; //TODO make configurable
    public static List<ITextComponent> chatMessages = new ArrayList<>(50);
    
    private void clientChatReceived(ClientChatReceivedEvent event)
    {
        if(!event.isCanceled() && event.getMessage() != null && !event.getMessage().getString().isEmpty() && !ClientProxy.getMinecraft().cannotSendChatMessages(event.getSenderUUID()))
        {
            UUID playerUUID = event.getSenderUUID();
            
            ITextComponent message = event.getMessage();
            
            if(playerUUID == ClientProxy.getPlayer().getUniqueID())
            {
                message = new StringTextComponent("<").append(ClientProxy.getPlayer().getName()).appendString("> ").append(event.getMessage());
            }
            
            if(ClientProxy.chatMessages.size() >= ClientProxy.maxMessages)
            {
                ClientProxy.chatMessages.remove(0);
            }
            
            ClientProxy.chatMessages.add(event.getMessage());
        }
    }
    
    public static void renderCardInfo(MatrixStack ms, CardHolder card, ContainerScreen<?> screen)
    {
        ClientProxy.renderCardInfo(ms, card, screen.getGuiLeft());
    }
    
    public static void renderCardInfo(MatrixStack ms, CardHolder card)
    {
        ClientProxy.renderCardInfo(ms, card, 100);
    }
    
    public static void renderCardInfo(MatrixStack ms, CardHolder card, int width)
    {
        if(card == null || card.getCard() == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.push();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        
        {
            int x = margin;
            
            if(maxWidth < imageSize)
            {
                // draw it centered if the space we got is limited
                // to make sure the image is NOT rendered more to the right of the center
                x = (maxWidth - imageSize) / 2 + margin;
            }
            
            // card texture
            
            ClientProxy.bindInfoResourceLocation(card);
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
            card.getProperties().addInformation(list);
            
            ClientProxy.drawSplitString(ms, fontRenderer, list, margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        }
        
        ms.pop();
    }
    
    public static void bindInfoResourceLocation(CardHolder c)
    {
        ClientProxy.infoTextureBinder.bind(c.getInfoImageResourceLocation());
    }
    
    public static void bindMainResourceLocation(CardHolder c)
    {
        ClientProxy.mainTextureBinder.bind(c.getMainImageResourceLocation());
    }
    
    public static void bindInfoResourceLocation(Properties p, byte imageIndex)
    {
        ClientProxy.infoTextureBinder.bind(p.getInfoImageResourceLocation(imageIndex));
    }
    
    public static void bindMainResourceLocation(Properties p, byte imageIndex)
    {
        ClientProxy.mainTextureBinder.bind(p.getMainImageResourceLocation(imageIndex));
    }
    
    public static void bindInfoResourceLocation(ResourceLocation r)
    {
        ClientProxy.infoTextureBinder.bind(r);
    }
    
    public static void bindMainResourceLocation(ResourceLocation r)
    {
        ClientProxy.mainTextureBinder.bind(r);
    }
    
    public static ResourceLocation getInfoCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeInfoImageSize + "/" + YdmItems.CARD_BACK.getRegistryName().getPath() + ".png");
    }
    
    public static ResourceLocation getMainCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeMainImageSize + "/" + YdmItems.CARD_BACK.getRegistryName().getPath() + ".png");
    }
    
    public static ResourceLocation getInfoTokenOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeInfoImageSize + "/" + "token_overlay" + ".png");
    }
    
    public static ResourceLocation getMainMainOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeMainImageSize + "/" + "token_overlay" + ".png");
    }
    
    public static void drawLineRect(MatrixStack ms, float x, float y, float w, float h, float lineWidth, float r, float g, float b, float a)
    {
        ClientProxy.drawRect(ms, x, y, w, lineWidth, r, g, b, a); //top
        ClientProxy.drawRect(ms, x, y + h - lineWidth, w, lineWidth, r, g, b, a); //bot
        ClientProxy.drawRect(ms, x, y, lineWidth, h, r, g, b, a); //left
        ClientProxy.drawRect(ms, x + w - lineWidth, y, lineWidth, h, r, g, b, a); //right
    }
    
    public static void drawRect(MatrixStack ms, float x, float y, float w, float h, float r, float g, float b, float a)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        
        // Use src_color * src_alpha
        // and dest_color * (1 - src_alpha) for colors
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
        RenderSystem.color4f(r, g, b, a);
        
        Matrix4f m = ms.getLast().getMatrix();
        
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(m, x, y + h, 0F).endVertex(); // BL
        bufferbuilder.pos(m, x + w, y + h, 0F).endVertex(); // BR
        bufferbuilder.pos(m, x + w, y, 0F).endVertex(); // TR
        bufferbuilder.pos(m, x, y, 0F).endVertex(); // TL
        tessellator.draw();
        
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public static void drawSplitString(MatrixStack ms, FontRenderer fontRenderer, List<ITextComponent> list, float x, float y, int maxWidth, int color)
    {
        for(ITextComponent t : list)
        {
            if(t.getUnformattedComponentText().isEmpty() && t.getSiblings().isEmpty())
            {
                y += fontRenderer.FONT_HEIGHT;
            }
            else
            {
                for(IReorderingProcessor p : fontRenderer.trimStringToWidth(t, maxWidth))
                {
                    fontRenderer.func_238407_a_(ms, p, x, y, color);
                    y += fontRenderer.FONT_HEIGHT;
                }
            }
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
