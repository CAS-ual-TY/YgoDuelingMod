package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ReadyCheckboxWidget;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.screen.widget.RoleButtonWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.RoleOccupantsWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelScreenIdle<E extends DuelContainer> extends DuelContainerScreen<E>
{
    protected AbstractButton player1Button;
    protected AbstractButton player2Button;
    protected AbstractButton spectatorButton;
    
    public DuelScreenIdle(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);
        
        int x = width / 2;
        int y = height / 2;
        
        //        this.initChat(width, height, y, margin - 4*2, 3 * 32);
        initDefaultChat(width, height);
        
        addButton(player1Button = new RoleButtonWidget(x - 100, y - 40, 100, 20, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.player_1"), this::roleButtonClicked, () -> getDuelManager().player1 == null && getPlayerRole() != PlayerRole.PLAYER1, PlayerRole.PLAYER1));
        addButton(player2Button = new RoleButtonWidget(x - 100, y - 10, 100, 20, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.player_2"), this::roleButtonClicked, () -> getDuelManager().player2 == null && getPlayerRole() != PlayerRole.PLAYER2, PlayerRole.PLAYER2));
        addButton(spectatorButton = new RoleButtonWidget(x - 100, y + 20, 100, 20, new TranslationTextComponent("container." + YDM.MOD_ID + ".duel.spectators"), this::roleButtonClicked, () -> getPlayerRole() != PlayerRole.SPECTATOR, PlayerRole.SPECTATOR));
        addButton(new RoleOccupantsWidget(x, y - 40, 80, 20, this::getRoleDescription, PlayerRole.PLAYER1));
        addButton(new RoleOccupantsWidget(x, y - 10, 80, 20, this::getRoleDescription, PlayerRole.PLAYER2));
        addButton(new RoleOccupantsWidget(x, y + 20, 100, 20, this::getRoleDescription, PlayerRole.SPECTATOR));
        addButton(new ReadyCheckboxWidget(x + 80, y - 40, 20, 20, "Ready 1", (button) -> ready1ButtonClicked(), () -> getDuelManager().player1Ready, () -> getPlayerRole() == PlayerRole.PLAYER1 && getDuelManager().player2 != null));
        addButton(new ReadyCheckboxWidget(x + 80, y - 10, 20, 20, "Ready 2", (button) -> ready2ButtonClicked(), () -> getDuelManager().player2Ready, () -> getPlayerRole() == PlayerRole.PLAYER2 && getDuelManager().player1 != null));
    }
    
    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY)
    {
        font.draw(ms, "Waiting for players...", 8.0F, 6.0F, 0x404040);
    }
    
    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(ms, partialTicks, mouseX, mouseY);
        
        ScreenUtil.white();
        minecraft.getTextureManager().bind(DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE);
        blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    public ITextComponent getRoleDescription(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return new StringTextComponent(getDuelManager().player1 != null ? getDuelManager().player1.getScoreboardName() : "");
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return new StringTextComponent(getDuelManager().player2 != null ? getDuelManager().player2.getScoreboardName() : "");
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            int size = getDuelManager().spectators.size();
            
            if(getPlayerRole() == PlayerRole.SPECTATOR)
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
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SelectRole(getHeader(), ((RoleButtonWidget) button).role));
    }
    
    protected void ready1ButtonClicked()
    {
        if(player1Button != null && player2Button != null && getPlayerRole() == PlayerRole.PLAYER1)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(getHeader(), !getDuelManager().player1Ready));
        }
    }
    
    protected void ready2ButtonClicked()
    {
        if(player1Button != null && player2Button != null && getPlayerRole() == PlayerRole.PLAYER2)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(getHeader(), !getDuelManager().player2Ready));
        }
    }
}
