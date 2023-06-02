package de.cas_ual_ty.ydm.duel.dueldisk;

import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class DuelEntity extends Entity implements INamedContainerProvider
{
    public static final int MAX_TIMEOUT = 20 * 8;
    
    public DuelManager duelManager;
    
    public UUID player1UUID;
    public UUID player2UUID;
    
    public DuelEntity(EntityType<?> pType, World level)
    {
        super(pType, level);
        duelManager = new DuelManager(level.isClientSide, this::createHeader);
    }
    
    public DuelMessageHeader createHeader()
    {
        return new DuelMessageHeader.EntityHeader(DuelMessageHeaders.ENTITY, getId());
    }
    
    private int timeout = 0;
    private boolean hasEverStarted = false;
    
    @Override
    public void tick()
    {
        super.tick();
        
        if(!level.isClientSide)
        {
            if(!hasEverStarted)
            {
                hasEverStarted = duelManager.duelState == DuelState.DUELING;
            }
            
            if(duelManager.duelState == DuelState.IDLE && (duelManager.player1 != null || duelManager.player2 != null))
            {
                // 1 player left during deck selection
                duelManager.kickAllPlayers();
                remove();
            }
            else if(duelManager.player1 == null && duelManager.player2 == null)
            {
                if(hasEverStarted)
                {
                    // both players left after the duel was started
                    duelManager.kickAllPlayers();
                    remove();
                }
                else
                {
                    // players have not agreed to duel yet (only request sent out)
                    timeout++;
                    if(timeout >= MAX_TIMEOUT)
                    {
                        duelManager.kickAllPlayers();
                        remove();
                    }
                }
            }
            else if(duelManager.player1 != null && duelManager.player2 != null)
            {
                timeout = 0;
            }
        }
    }
    
    @Override
    protected void defineSynchedData()
    {
    
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound)
    {
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound)
    {
    }
    
    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player)
    {
        return new DuelEntityContainer(YdmContainerTypes.DUEL_ENTITY_CONTAINER, id, playerInv, getId(), false);
    }
}
