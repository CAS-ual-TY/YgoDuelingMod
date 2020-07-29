package de.cas_ual_ty.ydm.client;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.ISidedProxy;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.binder.BinderScreen;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.config.Configuration;
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
        
        ScreenManager.registerFactory(YdmContainerTypes.CARD_BINDER, BinderScreen::new);
    }
    
    private void textureStitch(TextureStitchEvent.Pre event)
    {
        if(YDM.itemsUseCardImagesActive)
        {
            YDM.log("Stitching card item textures!");
            
            for(Card card : Database.CARDS_LIST)
            {
                event.addSprite(card.getItemImageResourceLocation());
            }
        }
    }
    
    private void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(YdmItems.CARD_BACK.getRegistryName().toString() + "_" + YDM.activeItemImageSize), "inventory"));
    }
    
    private void modelBake(ModelBakeEvent event)
    {
        YdmItems.CARD.getRegistryName();
        
        YdmItems.CARD_BACK.getRegistryName();
        
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
    
    private void renderGameOverlay(TickEvent.RenderTickEvent event)
    {
        if(event.type != TickEvent.Type.RENDER || event.phase != TickEvent.Phase.END)
        {
            return;
        }
        
        CardHolder card = null;
        
        Screen screen = Minecraft.getInstance().currentScreen;
        
        if(screen instanceof ContainerScreen)
        {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>)screen;
            
            if(containerScreen.getSlotUnderMouse() != null && !containerScreen.getSlotUnderMouse().getStack().isEmpty() && containerScreen.getSlotUnderMouse().getStack().getItem() == YdmItems.CARD)
            {
                card = YdmItems.CARD.getCardHolder(containerScreen.getSlotUnderMouse().getStack());
            }
        }
        else if(Minecraft.getInstance().player != null)
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
            ClientProxy.renderCardInfo(card);
        }
    }
    
    public static void renderCardInfo(CardHolder card)
    {
        float f = 0.5f;
        
        // TODO make width dependent on current screen
        int maxWidth = 200;
        
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
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
        
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
    
    @Override
    public PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }
}
