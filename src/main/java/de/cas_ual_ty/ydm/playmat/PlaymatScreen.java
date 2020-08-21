package de.cas_ual_ty.ydm.playmat;

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelMessages;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
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
import net.minecraftforge.fml.network.PacketDistributor;

public class PlaymatScreen extends ContainerScreen<PlaymatContainer>
{
    private static final ResourceLocation PLAYMAT_FOREGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/playmat_foreground.png");
    private static final ResourceLocation PLAYMAT_BACKGROUND_GUI_TEXTURE = new ResourceLocation(YDM.MOD_ID, "textures/gui/playmat_background.png");
    
    protected Button player1Button;
    protected Button player2Button;
    protected Button spectatorButton;
    
    public PlaymatScreen(PlaymatContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
        
        int x = width / 2;
        int y = height / 2;
        
        this.addButton(this.player1Button = new RoleButton(x - 100, y - 40, 100, 20, "Player 1", this::roleButtonClicked, () -> this.getDuelManager().player1 == null && this.getRole() != PlayerRole.PLAYER1, PlayerRole.PLAYER1));
        this.addButton(this.player2Button = new RoleButton(x - 100, y - 10, 100, 20, "Player 2", this::roleButtonClicked, () -> this.getDuelManager().player2 == null && this.getRole() != PlayerRole.PLAYER2, PlayerRole.PLAYER2));
        this.addButton(this.spectatorButton = new RoleButton(x - 100, y + 20, 100, 20, "Spectators", this::roleButtonClicked, () -> this.getRole() != PlayerRole.SPECTATOR, PlayerRole.SPECTATOR));
        this.addButton(new RoleDescriptions(x, y - 40, 80, 20, this::getRoleDescription, PlayerRole.PLAYER1));
        this.addButton(new RoleDescriptions(x, y - 10, 80, 20, this::getRoleDescription, PlayerRole.PLAYER2));
        this.addButton(new RoleDescriptions(x, y + 20, 100, 20, this::getRoleDescription, PlayerRole.SPECTATOR));
        this.addButton(new ReadyCheckbox(x + 80, y - 40, 20, 20, "Ready 1", (button) -> this.ready1ButtonClicked(), () -> this.getDuelManager().player1Ready, this::getRole, PlayerRole.PLAYER1));
        this.addButton(new ReadyCheckbox(x + 80, y - 10, 20, 20, "Ready 2", (button) -> this.ready2ButtonClicked(), () -> this.getDuelManager().player2Ready, this::getRole, PlayerRole.PLAYER2));
    }
    
    @Override
    protected void init()
    {
        this.xSize = 234;
        this.ySize = 250;
        super.init();
    }
    
    protected void drawIdle(int mouseX, int mouseY)
    {
        
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.minecraft.getTextureManager().bindTexture(PlaymatScreen.PLAYMAT_BACKGROUND_GUI_TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.getState() == DuelState.DUELING)
        {
            this.minecraft.getTextureManager().bindTexture(PlaymatScreen.PLAYMAT_FOREGROUND_GUI_TEXTURE);
            this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        }
        
        String text = this.getContainer().getDuelManager().duelState.name() + " " + this.getRole();
        int width = this.font.getStringWidth(text);
        int height = this.font.FONT_HEIGHT;
        this.font.drawString(text, this.guiLeft + (this.xSize - width) / 2F, -60 + (this.ySize - height) / 2F, 0xFF4040);
    }
    
    protected void drawFullRectBackground(float r, float g, float b, float a)
    {
        ClientProxy.drawRect(this.guiLeft, this.guiTop, this.xSize, this.ySize, r, g, b, a);
    }
    
    public DuelManager getDuelManager()
    {
        return this.getContainer().getDuelManager();
    }
    
    public DuelState getState()
    {
        return this.getDuelManager().getDuelState();
    }
    
    public PlayerRole getRole()
    {
        return this.getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }
    
    public String getRoleDescription(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return this.getDuelManager().player1 != null ? this.getDuelManager().player1.getScoreboardName() : "";
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return this.getDuelManager().player2 != null ? this.getDuelManager().player2.getScoreboardName() : "";
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            int size = this.getDuelManager().spectators.size();
            
            if(this.getRole() == PlayerRole.SPECTATOR)
            {
                if(size == 1)
                {
                    return "You";
                }
                else
                {
                    return "You + " + (size - 1);
                }
            }
            else
            {
                return "" + size;
            }
        }
        
        return "";
    }
    
    public void roleButtonClicked(Button button)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SelectRole(((RoleButton)button).role));
    }
    
    public void ready1ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getRole() == PlayerRole.PLAYER1)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(!this.getDuelManager().player1Ready));
        }
    }
    
    public void ready2ButtonClicked()
    {
        if(this.player1Button != null && this.player2Button != null && this.getRole() == PlayerRole.PLAYER2)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(!this.getDuelManager().player2Ready));
        }
    }
    
    private static class RoleButton extends Button
    {
        public Supplier<Boolean> available;
        public PlayerRole role;
        
        public RoleButton(int xIn, int yIn, int widthIn, int heightIn, String text, IPressable onPress, Supplier<Boolean> available, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, text, onPress);
            this.available = available;
            this.role = role;
        }
        
        @Override
        public void render(int mouseX, int mouseY, float partial)
        {
            this.active = this.available.get();
            super.render(mouseX, mouseY, partial);
        }
        
        @Override
        public void queueNarration(int p_queueNarration_1_)
        {
        }
    }
    
    private static class RoleDescriptions extends Widget
    {
        public Function<PlayerRole, String> nameGetter;
        public PlayerRole role;
        
        public RoleDescriptions(int xIn, int yIn, int widthIn, int heightIn, Function<PlayerRole, String> nameGetter, PlayerRole role)
        {
            super(xIn, yIn, widthIn, heightIn, "");
            this.nameGetter = nameGetter;
            this.role = role;
            this.active = false;
        }
        
        @Override
        public void render(int mouseX, int mouseY, float partial)
        {
            this.setMessage(this.nameGetter.apply(this.role));
            super.render(mouseX, mouseY, partial);
        }
        
        @Override
        public void queueNarration(int p_queueNarration_1_)
        {
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
            super(xIn, yIn, widthIn, heightIn, "", onPress);
            this.isChecked = isChecked;
            this.activeRole = activeRole;
            this.role = role;
        }
        
        public boolean isRoleAssigned()
        {
            return this.role == this.activeRole.get();
        }
        
        @Override
        public void render(int mouseX, int mouseY, float partial)
        {
            this.active = this.isRoleAssigned();
            super.render(mouseX, mouseY, partial);
        }
        
        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(ReadyCheckbox.TEXTURE);
            RenderSystem.enableDepthTest();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            AbstractGui.blit(this.x, this.y, 0.0F, this.isChecked.get() ? 20.0F : 0.0F, 20, this.height, 32, 64);
            this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
            this.drawString(fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
