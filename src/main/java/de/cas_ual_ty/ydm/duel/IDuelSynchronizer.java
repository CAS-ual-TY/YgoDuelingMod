package de.cas_ual_ty.ydm.duel;

import java.util.List;

import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;

public interface IDuelSynchronizer
{
    void sendActionTo(PlayerEntity player, Action action);
    
    void sendActionsTo(PlayerEntity player, List<Action> actions);
    
    void sendDuelStateTo(PlayerEntity player, DuelState state);
    
    void sendRoleTo(PlayerEntity player, PlayerRole role);
    
    void sendChatTo(PlayerEntity player, String message);
}
