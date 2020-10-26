package de.cas_ual_ty.ydm.clientutil;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessages;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelManagerScreen extends SizedScreen
{
    protected final DuelManager duelManager;
    
    protected DuelManagerScreen(DuelManager duelManager, ITextComponent titleIn)
    {
        super(titleIn);
        this.duelManager = duelManager;
    }
    
    public DuelManager getDuelManager()
    {
        return this.duelManager;
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
    
    public void requestFullUpdate()
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestFullUpdate(this.getHeader()));
    }
}
