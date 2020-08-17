package de.cas_ual_ty.ydm.clientutil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardbinder.CardBinderScreen;
import de.cas_ual_ty.ydm.deckbox.DeckBoxScreen;
import de.cas_ual_ty.ydm.playmat.PlaymatScreen;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
    
    public static volatile boolean itemsUseCardImagesActive;
    public static volatile boolean itemsUseCardImagesFailed;
    
    public static File imagesParentFolder;
    public static File rawImagesFolder;
    public static File cardInfoImagesFolder;
    public static File cardItemImagesFolder;
    public static File cardMainImagesFolder;
    
    @Override
    public void registerModEventListeners(IEventBus bus)
    {
        bus.addListener(this::textureStitch);
        bus.addListener(this::modelRegistry);
        bus.addListener(this::modelBake);
        bus.addListener(this::modConfig);
    }
    
    @Override
    public void registerForgeEventListeners(IEventBus bus)
    {
        bus.addListener(this::guiScreen);
        bus.addListener(this::renderGameOverlayPost);
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
        YDM.log("Sizes from client config (info/item/main): " + ClientProxy.activeInfoImageSize + " / " + ClientProxy.activeItemImageSize + " (" + ClientProxy.itemsUseCardImages + ") / " + ClientProxy.activeMainImageSize);
        
        if(ClientProxy.itemsUseCardImages)
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
        
        ScreenManager.registerFactory(YdmContainerTypes.CARD_BINDER, CardBinderScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.PLAYMAT, PlaymatScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.DECK_BOX, DeckBoxScreen::new);
    }
    
    @Override
    public void initFiles()
    {
        ClientProxy.imagesParentFolder = new File("ydm_db_images");
        ClientProxy.rawImagesFolder = new File(ClientProxy.imagesParentFolder, "cards_raw");
        
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        ClientProxy.cardInfoImagesFolder = new File(ClientProxy.imagesParentFolder, "cards_" + ClientProxy.activeInfoImageSize);
        ClientProxy.cardItemImagesFolder = new File(ClientProxy.imagesParentFolder, "cards_" + ClientProxy.activeItemImageSize);
        ClientProxy.cardMainImagesFolder = new File(ClientProxy.imagesParentFolder, "cards_" + ClientProxy.activeMainImageSize);
        
        YdmIOUtil.createDirIfNonExistant(ClientProxy.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.rawImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardItemImagesFolder);
        YdmIOUtil.createDirIfNonExistant(ClientProxy.cardMainImagesFolder);
        
        ImageHandler.init();
    }
    
    @Override
    public PlayerEntity getClientPlayer()
    {
        return ClientProxy.getPlayer();
    }
    
    private void textureStitch(TextureStitchEvent.Pre event)
    {
        boolean flag = false;
        int i = 0;
        
        while(ClientProxy.itemsUseCardImages && !ClientProxy.itemsUseCardImagesFailed && !ClientProxy.itemsUseCardImagesActive)
        {
            if(!flag)
            {
                flag = true;
                YDM.log("Sleeping for a couple seconds to give the worker enough time to check the images...");
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
        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory"));
        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory"));
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        YDM.log("Baking models (size: " + ClientProxy.activeItemImageSize + ") for " + YdmItems.BLANC_CARD.getRegistryName().toString() + " and " + YdmItems.CARD_BACK.getRegistryName().toString());
        
        event.getModelRegistry().put(new ModelResourceLocation(YdmItems.BLANC_CARD.getRegistryName(), "inventory"),
            event.getModelRegistry().get(
                new ModelResourceLocation(
                    new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory")));
        
        event.getModelRegistry().put(new ModelResourceLocation(YdmItems.CARD_BACK.getRegistryName(), "inventory"),
            event.getModelRegistry().get(
                new ModelResourceLocation(
                    new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + ClientProxy.activeItemImageSize), "inventory")));
        
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
        }
    }
    
    private void guiScreen(GuiScreenEvent.DrawScreenEvent event)
    {
        if(event.getGui() instanceof ContainerScreen)
        {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>)event.getGui();
            
            if(containerScreen.getSlotUnderMouse() != null && !containerScreen.getSlotUnderMouse().getStack().isEmpty() && containerScreen.getSlotUnderMouse().getStack().getItem() == YdmItems.CARD)
            {
                ClientProxy.renderCardInfo(YdmItems.CARD.getCardHolder(containerScreen.getSlotUnderMouse().getStack()), containerScreen);
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
                ClientProxy.renderCardInfo(YdmItems.CARD.getCardHolder(player.getHeldItemMainhand()), null);
            }
            else if(player.getHeldItemOffhand().getItem() == YdmItems.CARD)
            {
                ClientProxy.renderCardInfo(YdmItems.CARD.getCardHolder(player.getHeldItemOffhand()), null);
            }
        }
    }
    
    public static void renderCardInfo(CardHolder card, @Nullable ContainerScreen<?> screen)
    {
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = 100;
        
        if(screen != null)
        {
            maxWidth = /*(screen.width - screen.getXSize()) / 2*/ screen.getGuiLeft() - margin * 2;
        }
        
        RenderSystem.pushMatrix();
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
            Minecraft.getInstance().getTextureManager().bindTexture(card.getInfoImageResourceLocation());
            ClientProxy.blit(x, margin, imageSize, imageSize, 0, 0, ClientProxy.activeInfoImageSize, ClientProxy.activeInfoImageSize, ClientProxy.activeInfoImageSize, ClientProxy.activeInfoImageSize);
        }
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        RenderSystem.scalef(f, f, f);
        
        {
            // card description text
            
            @SuppressWarnings("resource")
            FontRenderer fontRenderer = ClientProxy.getMinecraft().fontRenderer;
            
            List<ITextComponent> list = new LinkedList<>();
            card.getProperties().addInformation(list);
            
            String string = list.stream().map((t) -> t.getFormattedText()).collect(Collectors.joining("\n"));
            fontRenderer.drawSplitString(string, margin, imageSize * 2 + margin * 2 /* extra margin of image */, maxWidth, 0xFFFFFF);
        }
        
        RenderSystem.popMatrix();
    }
    
    /**
     * Param 1-4: Where to and how big to draw on the screen
     * Param 5-8: What part of the texture file to cut out and draw
     * Param 9-10: How big the entire texture file is in general (pow2 only)
     * 
     * @param renderX Where to draw on the screen
     * @param renderY Where to draw on the screen
     * @param renderWidth How big to draw on the screen
     * @param renderHeight How big to draw on the screen
     * @param textureX
     * @param textureY
     * @param textureWidth
     * @param textureHeight
     * @param totalTextureFileWidth The total texture file size
     * @param totalTextureFileHeight The total texture file size
     */
    public static void blit(int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        AbstractGui.blit(renderX, renderY, renderWidth, renderHeight, textureX, textureY, textureWidth, textureHeight, totalTextureFileWidth, totalTextureFileHeight);
    }
    
    public static void drawRect(int x, int y, int w, int h, float r, float g, float b, float a)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        
        // Prep time
        GlStateManager.enableBlend(); // We do need blending
        GlStateManager.disableTexture(); // We dont need textures
        
        // Make sure alpha is working
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
        // Set the color!
        RenderSystem.color4f(r, g, b, a);
        
        // Start drawing
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        // Add vertices
        bufferbuilder.pos(x, y + h, 0.0D).endVertex(); // BL
        bufferbuilder.pos(x + w, y + h, 0.0D).endVertex(); // BR
        bufferbuilder.pos(x + w, y, 0.0D).endVertex(); // TR
        bufferbuilder.pos(x, y, 0.0D).endVertex(); // TL
        
        // End drawing
        tessellator.draw();
        
        // Cleanup time
        GlStateManager.enableTexture(); // Turn textures back on
        GlStateManager.disableBlend(); // Turn blending uhh... back off?
    }
    
    public static Minecraft getMinecraft()
    {
        return Minecraft.getInstance();
    }
    
    public static PlayerEntity getPlayer()
    {
        return ClientProxy.getMinecraft().player;
    }
}
