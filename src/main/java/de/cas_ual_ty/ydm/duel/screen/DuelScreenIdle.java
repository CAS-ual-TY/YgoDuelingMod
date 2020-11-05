package de.cas_ual_ty.ydm.duel.screen;

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelScreenIdle<E extends DuelContainer> extends DuelContainerScreen<E>
{
    protected AbstractButton player1Button;
    protected AbstractButton player2Button;
    protected AbstractButton spectatorButton;
    
    public DuelScreenIdle(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.xSize = 234;
        this.ySize = 250;
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
        
        int margin = (this.width - this.xSize) / 2;
        
        int x = width / 2;
        int y = height / 2;
        
        //        this.initChat(width, height, y, margin - 4*2, 3 * 32);
        this.initDefaultChat(width, height);
        
        this.addButton(this.player1Button = new RoleButton(x - 100, y - 40, 100, 20, new StringTextComponent("Player 1"), this::roleButtonClicked, () -> this.getDuelManager().player1 == null && this.getPlayerRole() != PlayerRole.PLAYER1, PlayerRole.PLAYER1));
        this.addButton(this.player2Button = new RoleButton(x - 100, y - 10, 100, 20, new StringTextComponent("Player 2"), this::roleButtonClicked, () -> this.getDuelManager().player2 == null && this.getPlayerRole() != PlayerRole.PLAYER2, PlayerRole.PLAYER2));
        this.addButton(this.spectatorButton = new RoleButton(x - 100, y + 20, 100, 20, new StringTextComponent("Spectators"), this::roleButtonClicked, () -> this.getPlayerRole() != PlayerRole.SPECTATOR, PlayerRole.SPECTATOR));
        this.addButton(new RoleOccupants(x, y - 40, 80, 20, this::getRoleDescription, PlayerRole.PLAYER1));
        this.addButton(new RoleOccupants(x, y - 10, 80, 20, this::getRoleDescription, PlayerRole.PLAYER2));
        this.addButton(new RoleOccupants(x, y + 20, 100, 20, this::getRoleDescription, PlayerRole.SPECTATOR));
        this.addButton(new ReadyCheckbox(x + 80, y - 40, 20, 20, "Ready 1", (button) -> this.ready1ButtonClicked(), () -> this.getDuelManager().player1Ready, this::getPlayerRole, PlayerRole.PLAYER1));
        this.addButton(new ReadyCheckbox(x + 80, y - 10, 20, 20, "Ready 2", (button) -> this.ready2ButtonClicked(), () -> this.getDuelManager().player2Ready, this::getPlayerRole, PlayerRole.PLAYER2));
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY)
    {
        this.font.drawString(ms, "Waiting for players...", 8.0F, 6.0F, 0x404040);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(ms, partialTicks, mouseX, mouseY);
        
        ScreenUtil.white();
        this.minecraft.getTextureManager().bindTexture(DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        this.blit(ms, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    public ITextComponent getRoleDescription(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return new StringTextComponent(this.getDuelManager().player1 != null ? this.getDuelManager().player1.getScoreboardName() : "");
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return new StringTextComponent(this.getDuelManager().player2 != null ? this.getDuelManager().player2.getScoreboardName() : "");
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            int size = this.getDuelManager().spectators.size();
            
            if(this.getPlayerRole() == PlayerRole.SPECTATOR)
            {
                if(size == 1)
                {
                    return new StringTextComponent(ClientProxy.getPlayer().getScoreboardName());
                }
                else
                {
                    return new StringTextComponent(ClientProxy.getPlayer().getScoreboardName() + " + " + (size - 1));
                }
            }
            else
            {
                return new StringTextComponent("" + size);
            }
        }
        
        return StringTextComponent.EMPTY;
    }
    
    protected void roleButtonClicked(Button button)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SelectRole(this.getHeader(), ((RoleButton)button).role));
    }
    
    protected void ready1ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getPlayerRole() == PlayerRole.PLAYER1)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(this.getHeader(), !this.getDuelManager().player1Ready));
        }
    }
    
    protected void ready2ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getPlayerRole() == PlayerRole.PLAYER2)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(this.getHeader(), !this.getDuelManager().player2Ready));
        }
    }
    
    private static class RoleButton extends Button
    {
        public Supplier<Boolean> available;
        public PlayerRole role;
        
        public RoleButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent text, IPressable onPress, Supplier<Boolean> available, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, text, onPress);
            this.available = available;
            this.role = role;
        }
        
        @Override
        public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            this.active = this.available.get();
            super.render(ms, mouseX, mouseY, partial);
        }
    }
    
    private static class RoleOccupants extends Widget
    {
        public Function<PlayerRole, ITextComponent> nameGetter;
        public PlayerRole role;
        
        public RoleOccupants(int xIn, int yIn, int widthIn, int heightIn, Function<PlayerRole, ITextComponent> nameGetter, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY);
            this.nameGetter = nameGetter;
            this.role = role;
            this.active = false;
        }
        
        @Override
        public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            this.setMessage(this.nameGetter.apply(this.role));
            super.render(ms, mouseX, mouseY, partial);
        }
    }
    
    private static class ReadyCheckbox extends Button
    {
        private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
        
        public Supplier<Boolean> isChecked;
        public Supplier<PlayerRole> activeRole;
        public PlayerRole role;
        
        public ReadyCheckbox(int xIn, int yIn, int widthIn, int heightIn, String msg, IPressable onPress, Supplier<Boolean> isChecked, Supplier<PlayerRole> activeRole, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY, onPress);
            this.isChecked = isChecked;
            this.activeRole = activeRole;
            this.role = role;
        }
        
        public boolean isRoleAssigned()
        {
            return this.role == this.activeRole.get();
        }
        
        @Override
        public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
        {
            this.active = this.isRoleAssigned();
            super.render(ms, mouseX, mouseY, partial);
        }
        
        @Override
        public void renderButton(MatrixStack ms, int mouseX, int mouseY, float p_renderButton_3_)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(ReadyCheckbox.TEXTURE);
            RenderSystem.enableDepthTest();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            AbstractGui.blit(ms, this.x, this.y, 0.0F, this.isChecked.get() ? 20.0F : 0.0F, 20, this.height, 64, 64);
            AbstractGui.drawString(ms, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
