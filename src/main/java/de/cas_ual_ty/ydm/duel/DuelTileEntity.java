package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.IDuelTicker;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageHeaders;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DuelTileEntity extends TileEntity implements IDuelTicker
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
        this.duelManager = this.createDuelManager();
    }
    
    public DuelManager createDuelManager()
    {
        return new DuelManager(this.world.isRemote, this::createHeader, this);
    }
    
    public DuelMessageHeader createHeader()
    {
        return new DuelMessageHeader.TileEntityHeader(DuelMessageHeaders.TILE_ENTITY, this.getPos());
    }
}
