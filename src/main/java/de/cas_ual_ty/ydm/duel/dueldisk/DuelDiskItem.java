package de.cas_ual_ty.ydm.duel.dueldisk;

import de.cas_ual_ty.ydm.YdmEntityTypes;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class DuelDiskItem extends Item
{
    public DuelDiskItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
    {
        if(hand == Hand.OFF_HAND && !level.isClientSide && player instanceof ServerPlayerEntity)
        {
            ServerWorld slevel = (ServerWorld) level;
            ServerPlayerEntity splayer = (ServerPlayerEntity) player;
            
            if(openActiveDM(slevel, splayer, true))
            {
                return ActionResult.success(player.getOffhandItem());
            }
        }
        
        return super.use(level, player, hand);
    }
    
    public boolean openActiveDM(ServerWorld slevel, ServerPlayerEntity splayer, boolean open)
    {
        UUID dmUUID = getDMUUID(splayer.getOffhandItem());
        
        if(dmUUID != null)
        {
            Entity e = slevel.getEntity(dmUUID);
            
            if(e instanceof DuelEntity)
            {
                DuelEntity dm = (DuelEntity) e;
                
                if(dm.duelManager.hasStarted())
                {
                    if(open)
                    {
                        NetworkHooks.openGui(splayer, dm, buf ->
                        {
                            buf.writeInt(dm.getId());
                            buf.writeBoolean(true);
                        });
                    }
                    return true;
                }
                
            }
        }
        
        return false;
    }
    
    @Override
    public ActionResultType interactLivingEntity(ItemStack pStack, PlayerEntity player1u, LivingEntity target, Hand hand)
    {
        if(hand == Hand.OFF_HAND && !player1u.level.isClientSide && player1u instanceof ServerPlayerEntity && target instanceof ServerPlayerEntity)
        {
            ServerWorld level = (ServerWorld) player1u.level;
            
            ServerPlayerEntity player1 = (ServerPlayerEntity) player1u;
            
            if(openActiveDM(level, player1, false))
            {
                return ActionResultType.SUCCESS;
            }
            
            ServerPlayerEntity player2 = (ServerPlayerEntity) target;
            
            if(player2.getOffhandItem().getItem() instanceof DuelDiskItem)
            {
                DuelDiskItem disk2 = (DuelDiskItem) player2.getOffhandItem().getItem();
                UUID lastP2Request = disk2.getPlayer2UUID(player2.getOffhandItem());
                UUID dmUUID = disk2.getDMUUID(player2.getOffhandItem());
                
                if(dmUUID != null)
                {
                    Entity e = level.getEntity(dmUUID);
                    
                    if(e instanceof DuelEntity)
                    {
                        //dm exists -> player2 already playing or player2s request was done recently
                        DuelEntity dm = (DuelEntity) e;
                        
                        if(dm.duelManager.hasStarted())
                        {
                            // player2 is already playing -> spectate
                            NetworkHooks.openGui(player1, dm, buf ->
                            {
                                buf.writeInt(dm.getId());
                                buf.writeBoolean(true);
                            });
                            
                            return ActionResultType.SUCCESS;
                        }
                        
                        if(player1.getUUID().equals(lastP2Request))
                        {
                            // player2 already asked player1
                            setPlayer2UUID(pStack, player2.getUUID());
                            setDMUUID(pStack, dmUUID);
                            
                            //open it for both players
                            
                            NetworkHooks.openGui(player1, dm, buf ->
                            {
                                buf.writeInt(dm.getId());
                                buf.writeBoolean(false);
                            });
                            NetworkHooks.openGui(player2, dm, buf ->
                            {
                                buf.writeInt(dm.getId());
                                buf.writeBoolean(false);
                            });
                            
                            dm.duelManager.playerSelectRole(player1, PlayerRole.PLAYER1);
                            dm.duelManager.playerSelectRole(player2, PlayerRole.PLAYER2);
                            dm.duelManager.requestReady(player1, true);
                            dm.duelManager.requestReady(player2, true);
                            
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
                
                //player1 asks player2
                setPlayer2UUID(pStack, player2.getUUID());
                DuelEntity dm = new DuelEntity(YdmEntityTypes.DUEL, level);
                dm.setPos(player1.position().x(), player1.position().y(), player1.position().z());
                level.addFreshEntity(dm);
                setDMUUID(pStack, dm.getUUID());
                
                player2.displayClientMessage(new StringTextComponent("\"" + player1.getGameProfile().getName() + "\" requested a DUEL!"), false);
                player1.displayClientMessage(new StringTextComponent("DUEL request sent to \"" + player2.getGameProfile().getName() + "\"!"), false);
            }
        }
        
        return super.interactLivingEntity(pStack, player1u, target, hand);
    }
    
    public void requestDuel(ServerPlayerEntity requester, ServerPlayerEntity requestee)
    {
        requestee.displayClientMessage(new StringTextComponent(requester.getName() + " requested a DUEL!"), false);
    }
    
    public CompoundNBT getTag(ItemStack stack)
    {
        return stack.getOrCreateTag();
    }
    
    public void setPlayer2UUID(ItemStack stack, UUID uuid)
    {
        getTag(stack).putUUID("duel_player2", uuid);
    }
    
    public UUID getPlayer2UUID(ItemStack stack)
    {
        return getTag(stack).hasUUID("duel_player2") ? getTag(stack).getUUID("duel_player2") : null;
    }
    
    public void setDMUUID(ItemStack stack, UUID uuid)
    {
        getTag(stack).putUUID("duelmanager", uuid);
    }
    
    public UUID getDMUUID(ItemStack stack)
    {
        return getTag(stack).hasUUID("duelmanager") ? getTag(stack).getUUID("duelmanager") : null;
    }
}
