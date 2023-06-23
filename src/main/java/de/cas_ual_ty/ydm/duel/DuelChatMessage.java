package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


import net.minecraft.ChatFormatting;

public class DuelChatMessage
{
    public final Component message;
    public final Component playerName;
    public final PlayerRole sourceRole;
    public final boolean isAnnouncement;
    
    public DuelChatMessage(Component message, Component playerName, PlayerRole playerRole, boolean isAnnouncement)
    {
        this.message = message;
        this.playerName = playerName;
        sourceRole = playerRole;
        this.isAnnouncement = isAnnouncement;
    }
    
    public DuelChatMessage(Component message, MutableComponent playerName, PlayerRole playerRole)
    {
        this(message, playerName, playerRole, false);
    }
    
    // view role has nothing to do with the view itself
    // flipping does not affect it
    public Component generateStyledMessage(PlayerRole viewerRole, ChatFormatting friendlyColor, ChatFormatting opponentColor, ChatFormatting neutralColor)
    {
        MutableComponent playerName = this.playerName.copy();
        
        if(ZoneOwner.fromPlayerRole(viewerRole).isPlayer())
        {
            // viewer is a player
            
            if(sourceRole == viewerRole)
            {
                // viewer is a player
                // source = viewer
                // so viewer is the player who sent the message
                // blue
                
                playerName.withStyle((style) -> style.applyFormat(friendlyColor));
            }
            else if(ZoneOwner.fromPlayerRole(sourceRole) == ZoneOwner.NONE)
            {
                // viewer is a player
                // message is not from a player
                // white
                
                playerName.withStyle((style) -> style.applyFormat(neutralColor));
            }
            else
            {
                // now it can only be opponent
                // red
                
                playerName.withStyle((style) -> style.applyFormat(opponentColor));
            }
        }
        else
        {
            // viewer is a spectator
            
            if(sourceRole == PlayerRole.PLAYER1)
            {
                playerName.withStyle((style) -> style.applyFormat(friendlyColor));
            }
            else if(sourceRole == PlayerRole.PLAYER2)
            {
                playerName.withStyle((style) -> style.applyFormat(opponentColor));
            }
            else //if(this.playerRole == PlayerRole.SPECTATOR)
            {
                playerName.withStyle((style) -> style.applyFormat(neutralColor));
            }
        }
        
        MutableComponent m = message.copy();
        
        if(isAnnouncement)
        {
            m.withStyle((style) -> style.applyFormat(ChatFormatting.ITALIC));
        }
        
        MutableComponent t = Component.literal("<").append(playerName).append("> ").append(m);
        
        //        if(this.isAnnouncement)
        //        {
        //            t.modifyStyle((style) -> style.applyFormatting(ChatFormatting.BOLD));
        //        }
        
        return t;
    }
}
