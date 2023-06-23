package de.cas_ual_ty.ydm.duel.dueldisk;

import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class DuelEntity extends Entity implements MenuProvider
{
    public static final int MAX_TIMEOUT = 20 * 8;
    
    public DuelManager duelManager;
    
    public UUID player1UUID;
    public UUID player2UUID;
    
    public DuelEntity(EntityType<?> pType, Level level)
    {
        super(pType, level);
        duelManager = new DuelManager(level.isClientSide, this::createHeader);
    }
    
    public DuelMessageHeader createHeader()
    {
        return new DuelMessageHeader.EntityHeader(DuelMessageHeaders.ENTITY.get(), getId());
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
                discard();
            }
            else if(duelManager.player1 == null && duelManager.player2 == null)
            {
                if(hasEverStarted)
                {
                    // both players left after the duel was started
                    duelManager.kickAllPlayers();
                    discard();
                }
                else
                {
                    // players have not agreed to duel yet (only request sent out)
                    timeout++;
                    if(timeout >= MAX_TIMEOUT)
                    {
                        duelManager.kickAllPlayers();
                        discard();
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
    protected void readAdditionalSaveData(CompoundTag pCompound)
    {
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound)
    {
    }
    
    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player)
    {
        return new DuelEntityContainer(YdmContainerTypes.DUEL_ENTITY_CONTAINER.get(), id, playerInv, getId(), false);
    }
}
