package de.cas_ual_ty.ydm.playmat;

import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.IDuelSynchronizer;
import de.cas_ual_ty.ydm.duel.IDuelTicker;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PlaymatTileEntity extends TileEntity implements INamedContainerProvider, IDuelSynchronizer, IDuelTicker
{
    public DuelManager duelManager;
    
    public PlaymatTileEntity(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
        this.duelManager = new DuelManager(this, this);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new PlaymatContainer(YdmContainerTypes.PLAYMAT, id, playerInventory, this.getPos());
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".playmat");
    }
    
    @Override
    public void sendActionTo(PlayerEntity player, Action action)
    {
        
    }
    
    @Override
    public void sendActionsTo(PlayerEntity player, List<Action> actions)
    {
        
    }
    
    @Override
    public void sendDuelStateTo(PlayerEntity player, DuelState state)
    {
        
    }
    
    @Override
    public void sendRoleTo(PlayerEntity player, PlayerRole role)
    {
        
    }
    
    @Override
    public void sendChatTo(PlayerEntity player, String message)
    {
        
    }
}
