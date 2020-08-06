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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

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
        bus.addListener(this::renderGameOverlay);
    }
    
    @Override
    public void preInit()
    {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new YdmResourcePackFinder());
        ModLoadingContext.get().registerConfig(Type.CLIENT, Configuration.CLIENT_SPEC);
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
            }
        }
        
        ScreenManager.registerFactory(YdmContainerTypes.CARD_BINDER, CardBinderScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.PLAYMAT, PlaymatScreen::new);
        ScreenManager.registerFactory(YdmContainerTypes.DECK_BOX, DeckBoxScreen::new);
    }
    
    private void textureStitch(TextureStitchEvent.Pre event)
    {
        if(YDM.itemsUseCardImages)
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
    
    @SuppressWarnings("resource")
    private void renderGameOverlay(TickEvent.RenderTickEvent event)
    {
        if(event.type != TickEvent.Type.RENDER || event.phase != TickEvent.Phase.END)
        {
            return;
        }
        
        CardHolder card = null;
        
        Screen screen = Minecraft.getInstance().currentScreen;
        ContainerScreen<?> containerScreen = null;
        
        if(screen instanceof ContainerScreen)
        {
            containerScreen = (ContainerScreen<?>)screen;
            
            if(containerScreen.getSlotUnderMouse() != null && !containerScreen.getSlotUnderMouse().getStack().isEmpty() && containerScreen.getSlotUnderMouse().getStack().getItem() == YdmItems.CARD)
            {
                card = YdmItems.CARD.getCardHolder(containerScreen.getSlotUnderMouse().getStack());
            }
        }
        else if(Minecraft.getInstance().player != null && screen == null)
        {
            PlayerEntity player = Minecraft.getInstance().player;
            
            ItemStack itemStack = ItemStack.EMPTY;
            
            if(player.getHeldItemMainhand().getItem() == YdmItems.CARD)
            {
                itemStack = player.getHeldItemMainhand();
            }
            else if(player.getHeldItemOffhand().getItem() == YdmItems.CARD)
            {
                itemStack = player.getHeldItemOffhand();
            }
            
            card = YdmItems.CARD.getCardHolder(itemStack);
        }
        
        if(card != null && card.getCard() != null)
        {
            ClientProxy.renderCardInfo(card, containerScreen);
        }
    }
    
    @SuppressWarnings("resource")
    public static void renderCardInfo(CardHolder card, @Nullable ContainerScreen<?> screen)
    {
        float f = 0.5f;
        
        // TODO make width dependent on current screen
        int maxWidth = 200;
        
        /*
        if(screen != null)
        {
            Minecraft.getInstance().getMainWindow().SC
            maxWidth = screen.getGuiLeft() - 4;
        }
        */
        
        RenderSystem.pushMatrix();
        //        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        
        RenderSystem.translatef(2, 2, 0);
        RenderSystem.scalef(f, f, f);
        
        {
            RenderSystem.pushMatrix();
            
            RenderSystem.scalef(f, f, f);
            
            Minecraft.getInstance().getTextureManager().bindTexture(card.getInfoImageResourceLocation());
            
            // 256 somehow needs to be hardcoded
            // still uses YDM.activeInfoImageSize as size for pictures
            // and renders them properly
            // I believe the scalef calls do it but I dont know
            final int size = 256;
            AbstractGui.blit(0, 0, 0, 0, 0, size, size, 256, 256);
            
            RenderSystem.popMatrix();
        }
        
        {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            
            List<ITextComponent> list = new LinkedList<>();
            card.getProperties().addInformation(list);
            
            String string = list.stream().map((t) -> t.getFormattedText()).collect(Collectors.joining("\n"));
            fontRenderer.drawSplitString(string, 0, 139, maxWidth, 0xFFFFFF);
        }
        
        //        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
    
    @SuppressWarnings("resource")
    @Override
    public PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }
}
