package de.cas_ual_ty.ydm.clientutil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

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
import de.cas_ual_ty.ydm.util.Configuration;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ClientProxy implements ISidedProxy
{
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
        ClientProxy.getMinecraft().getResourcePackList().addPackFinder(new YdmResourcePackFinder());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_SPEC);
    }
    
    @Override
    public void init()
    {
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
                YDM.itemsUseCardImagesFailed = true;
            }
        }
        
        ScreenManager.registerFactory(YdmContainerTypes.CARD_BINDER, CardBinderScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.PLAYMAT, PlaymatScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.DECK_BOX, DeckBoxScreen::new);
    }
    
    @SuppressWarnings("resource")
    @Override
    public PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }
    
    private void textureStitch(TextureStitchEvent.Pre event)
    {
        if(YDM.itemsUseCardImages && !YDM.itemsUseCardImagesFailed && !YDM.itemsUseCardImagesActive)
        {
            // sometimes this gets done before YDM.itemsUseCardImagesActive is set to true
            // so lets wait 3 seconds to make sure the value is correct
            
            YDM.log("Sleeping for 3 seconds to give the worker enough time to check the images...");
            
            try
            {
                TimeUnit.SECONDS.sleep(3);
                YDM.log("A W A K E N I N G from 3 seconds sleep.");
            }
            catch (InterruptedException e)
            {
                YDM.log("Tried sleeping to give textures enough time... It didnt work :(");
                e.printStackTrace();
            }
        }
        
        if(YDM.itemsUseCardImagesActive)
        {
            YDM.log("Stitching card item textures!");
            
            for(Card card : YdmDatabase.CARDS_LIST)
            {
                event.addSprite(card.getItemImageResourceLocation());
            }
        }
    }
    
    private void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory"));
        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory"));
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        event.getModelRegistry().put(new ModelResourceLocation(YdmItems.BLANC_CARD.getRegistryName(), "inventory"),
            event.getModelRegistry().get(
                new ModelResourceLocation(
                    new ResourceLocation(YdmItems.BLANC_CARD.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory")));
        
        event.getModelRegistry().put(new ModelResourceLocation(YdmItems.CARD_BACK.getRegistryName(), "inventory"),
            event.getModelRegistry().get(
                new ModelResourceLocation(
                    new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory")));
        
        ModelResourceLocation key = new ModelResourceLocation(YdmItems.CARD.getRegistryName(), "inventory");
        event.getModelRegistry().put(key, new CardBakedModel(event.getModelRegistry().get(key)));
    }
    
    private void modConfig(final ModConfig.ModConfigEvent event)
    {
        if(event.getConfig().getSpec() == Configuration.CLIENT_SPEC)
        {
            Configuration.bakeClient();
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
    
    @SuppressWarnings("resource")
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
            maxWidth = (screen.width - screen.getXSize()) / 2 - margin * 2;
        }
        
        RenderSystem.pushMatrix();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        
        {
            // card texture
            Minecraft.getInstance().getTextureManager().bindTexture(card.getInfoImageResourceLocation());
            AbstractGui.blit(margin /*(maxWidth - imageSize) / 2 + margin <- do draw it centered */, margin, imageSize, imageSize, 0, 0, YDM.activeInfoImageSize, YDM.activeInfoImageSize, YDM.activeInfoImageSize, YDM.activeInfoImageSize);
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
    
    public static Minecraft getMinecraft()
    {
        return Minecraft.getInstance();
    }
}
