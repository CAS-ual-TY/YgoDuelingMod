package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.SwitchableContainerScreen;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageHeader;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public abstract class DuelContainerScreen<E extends DuelContainer> extends SwitchableContainerScreen<E>
{
    public DuelContainerScreen(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    public void reInit()
    {
        this.init(this.minecraft, this.width, this.height);
    }
    
    @Override
    protected void onGuiClose()
    {
        super.onGuiClose();
        this.getDuelManager().reset();
    }
    
    public DuelManager getDuelManager()
    {
        return this.container.getDuelManager();
    }
    
    public DuelMessageHeader getHeader()
    {
        return this.getDuelManager().headerFactory.get();
    }
    
    public DuelState getState()
    {
        return this.getDuelManager().getDuelState();
    }
    
    public PlayerRole getPlayerRole()
    {
        return this.getDuelManager().getRoleFor(ClientProxy.getPlayer());
    }
}
