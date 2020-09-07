package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmTileEntityTypes;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.IDuelTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class DuelTileEntity extends TileEntity implements INamedContainerProvider, IDuelTicker
{
    public DuelManager duelManager;
    
    public DuelTileEntity(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
    }
    
    @Override
    public void setWorldAndPos(World world, BlockPos pos)
    {
        super.setWorldAndPos(world, pos);
        
        // world is still null at constructor, so we gotta do this here
        this.duelManager = new DuelManager(this.world.isRemote, this);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new DuelContainer(YdmContainerTypes.DUEL, id, playerInventory, this.getPos());
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + "." + YdmTileEntityTypes.DUEL.getRegistryName().getPath());
    }
}
