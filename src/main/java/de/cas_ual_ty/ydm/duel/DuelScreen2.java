package de.cas_ual_ty.ydm.duel;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneOwner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DuelScreen2 extends ContainerScreen<DuelContainer> implements IDuelScreen
{
    public static final ResourceLocation DUEL_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_foreground.png");
    public static final ResourceLocation DUEL_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_background.png");
    
    public DuelScreen2(DuelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.xSize = 234;
        this.ySize = 250;
    }
    
    @Override
    public void reInit()
    {
        this.init(this.minecraft, this.width, this.height);
    }
    
    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        
        ZoneWidget w;
        
        for(Zone zone : this.getDuelManager().getPlayField().getZones())
        {
            this.addButton(w = new ZoneWidget(zone, zone.width, zone.height, StringTextComponent.EMPTY, this::zoneClicked, this::zoneTooltip));
            
            if(this.getPlayerRole() == ZoneOwner.PLAYER2.player)
            {
                w.setPositionRelativeFlipped(zone.x, zone.y, width, height);
            }
            else
            {
                w.setPositionRelative(zone.x, zone.y, width, height);
            }
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int x, int y)
    {
        this.minecraft.getTextureManager().bindTexture(DuelScreen2.DUEL_BACKGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.minecraft.getTextureManager().bindTexture(DuelScreen2.DUEL_FOREGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int x, int y)
    {
        
    }
    
    protected void zoneClicked(ZoneWidget w)
    {
        
    }
    
    protected void zoneTooltip(Widget w0, MatrixStack ms, int mouseX, int mouseY)
    {
        ZoneWidget w = (ZoneWidget)w0;
        this.renderTooltip(ms, new StringTextComponent(w.zone.getType().getRegistryName().getPath() + " " + w.zone.getOwner().name()), mouseX, mouseY);
    }
    
    public DuelManager getDuelManager()
    {
        return this.getContainer().getDuelManager();
    }
    
    public DuelState getState()
    {
        return this.getDuelManager().getDuelState();
    }
    
    public PlayerRole getPlayerRole()
    {
        return this.getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }
    
    public static void renderHoverRect(MatrixStack ms, int x, int y, int w, int h)
    {
        // from ContainerScreen#render
        
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        ClientProxy.drawRect(ms, x, y, w, h, 1F, 1F, 1F, 0.5F);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
    
    public static class ZoneWidget extends Button
    {
        public final Zone zone;
        
        public ZoneWidget(Zone zone, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
        {
            super(0, 0, width, height, title, (w) -> onPress.accept((ZoneWidget)w), onTooltip);
            this.zone = zone;
            this.shift();
        }
        
        protected void shift()
        {
            this.x -= this.width / 2;
            this.y -= this.height / 2;
        }
        
        protected void unshift()
        {
            this.x += this.width / 2;
            this.y += this.height / 2;
        }
        
        public ZoneWidget flip(int guiWidth, int guiHeight)
        {
            guiWidth /= 2;
            guiHeight /= 2;
            
            this.unshift();
            
            this.x -= guiWidth;
            this.y -= guiHeight;
            
            this.x = -this.x;
            this.y = -this.y;
            
            this.x += guiWidth;
            this.y += guiHeight;
            
            this.shift();
            
            return this;
        }
        
        public ZoneWidget setPositionRelative(int x, int y, int guiWidth, int guiHeight)
        {
            this.x = x + guiWidth / 2;
            this.y = y + guiHeight / 2;
            
            this.shift();
            
            return this;
        }
        
        public ZoneWidget setPositionRelativeFlipped(int x, int y, int guiWidth, int guiHeight)
        {
            this.x = guiWidth / 2 - x;
            this.y = guiHeight / 2 - y;
            
            this.shift();
            
            return this;
        }
        
        @Override
        public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
        {
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            
            if(this.isHovered())
            {
                DuelScreen2.renderHoverRect(ms, this.x, this.y, this.width, this.height);
                this.renderToolTip(ms, mouseX, mouseY);
            }
            
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            
            ClientProxy.drawLineRect(ms, this.x, this.y, this.width, this.height, 1, 1, 0, 0, 1);
            
            int j = this.getFGColor();
            AbstractGui.drawCenteredString(ms, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
