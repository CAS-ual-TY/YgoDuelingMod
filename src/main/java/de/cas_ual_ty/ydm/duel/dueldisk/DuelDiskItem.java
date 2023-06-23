package de.cas_ual_ty.ydm.duel.dueldisk;

import de.cas_ual_ty.ydm.YdmEntityTypes;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class DuelDiskItem extends Item
{
    public DuelDiskItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if(hand == InteractionHand.OFF_HAND && !level.isClientSide && player instanceof ServerPlayer)
        {
            ServerLevel slevel = (ServerLevel) level;
            ServerPlayer splayer = (ServerPlayer) player;
            
            if(openActiveDM(slevel, splayer, true))
            {
                return InteractionResultHolder.success(player.getOffhandItem());
            }
        }
        
        return super.use(level, player, hand);
    }
    
    public boolean openActiveDM(ServerLevel slevel, ServerPlayer splayer, boolean open)
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
                        NetworkHooks.openScreen(splayer, dm, buf ->
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
    public InteractionResult interactLivingEntity(ItemStack pStack, Player player1u, LivingEntity target, InteractionHand hand)
    {
        if(hand == InteractionHand.OFF_HAND && !player1u.level.isClientSide && player1u instanceof ServerPlayer && target instanceof ServerPlayer)
        {
            ServerLevel level = (ServerLevel) player1u.level;
            
            ServerPlayer player1 = (ServerPlayer) player1u;
            
            if(openActiveDM(level, player1, false))
            {
                return InteractionResult.SUCCESS;
            }
            
            ServerPlayer player2 = (ServerPlayer) target;
            
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
                            NetworkHooks.openScreen(player1, dm, buf ->
                            {
                                buf.writeInt(dm.getId());
                                buf.writeBoolean(true);
                            });
                            
                            return InteractionResult.SUCCESS;
                        }
                        
                        if(player1.getUUID().equals(lastP2Request))
                        {
                            // player2 already asked player1
                            setPlayer2UUID(pStack, player2.getUUID());
                            setDMUUID(pStack, dmUUID);
                            
                            //open it for both players
                            
                            NetworkHooks.openScreen(player1, dm, buf ->
                            {
                                buf.writeInt(dm.getId());
                                buf.writeBoolean(false);
                            });
                            NetworkHooks.openScreen(player2, dm, buf ->
                            {
                                buf.writeInt(dm.getId());
                                buf.writeBoolean(false);
                            });
                            
                            dm.duelManager.playerSelectRole(player1, PlayerRole.PLAYER1);
                            dm.duelManager.playerSelectRole(player2, PlayerRole.PLAYER2);
                            dm.duelManager.requestReady(player1, true);
                            dm.duelManager.requestReady(player2, true);
                            
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                
                //player1 asks player2
                setPlayer2UUID(pStack, player2.getUUID());
                DuelEntity dm = new DuelEntity(YdmEntityTypes.DUEL.get(), level);
                dm.setPos(player1.position().x(), player1.position().y(), player1.position().z());
                level.addFreshEntity(dm);
                setDMUUID(pStack, dm.getUUID());
                
                player2.displayClientMessage(Component.literal("\"" + player1.getGameProfile().getName() + "\" requested a DUEL!"), false);
                player1.displayClientMessage(Component.literal("DUEL request sent to \"" + player2.getGameProfile().getName() + "\"!"), false);
            }
        }
        
        return super.interactLivingEntity(pStack, player1u, target, hand);
    }
    
    public void requestDuel(ServerPlayer requester, ServerPlayer requestee)
    {
        requestee.displayClientMessage(Component.literal(requester.getName() + " requested a DUEL!"), false);
    }
    
    public CompoundTag getTag(ItemStack stack)
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
