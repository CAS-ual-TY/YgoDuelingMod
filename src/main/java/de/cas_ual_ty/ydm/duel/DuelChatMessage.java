package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class DuelChatMessage
{
    public final ITextComponent message;
    public final ITextComponent playerName;
    public final PlayerRole sourceRole;
    public final boolean isAnnouncement;
    
    public DuelChatMessage(ITextComponent message, ITextComponent playerName, PlayerRole playerRole, boolean isAnnouncement)
    {
        this.message = message;
        this.playerName = playerName;
        this.sourceRole = playerRole;
        this.isAnnouncement = isAnnouncement;
    }
    
    public DuelChatMessage(ITextComponent message, IFormattableTextComponent playerName, PlayerRole playerRole)
    {
        this(message, playerName, playerRole, false);
    }
    
    // view role has nothing to do with the view itself
    // flipping does not affect it
    public ITextComponent generateStyledMessage(PlayerRole viewerRole, TextFormatting friendlyColor, TextFormatting opponentColor, TextFormatting neutralColor)
    {
        IFormattableTextComponent playerName = this.playerName.deepCopy();
        
        
        
        if(ZoneOwner.fromPlayerRole(viewerRole).isPlayer())
        {
            // viewer is a player
            
            if(this.sourceRole == viewerRole)
            {
                // viewer is a player
                // source = viewer
                // so viewer is the player who sent the message
                // blue
                
                playerName.modifyStyle((style) -> style.applyFormatting(friendlyColor));
            }
            else if(ZoneOwner.fromPlayerRole(this.sourceRole) == ZoneOwner.NONE)
            {
                // viewer is a player
                // message is not from a player
                // white
                
                playerName.modifyStyle((style) -> style.applyFormatting(neutralColor));
            }
            else
            {
                // now it can only be opponent
                // red
                
                playerName.modifyStyle((style) -> style.applyFormatting(opponentColor));
            }
        }
        else
        {
            if(this.sourceRole == PlayerRole.PLAYER1)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(friendlyColor));
            }
            else if(this.sourceRole == PlayerRole.PLAYER2)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(opponentColor));
            }
            else //if(this.playerRole == PlayerRole.SPECTATOR)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(neutralColor));
            }
        }
        
        IFormattableTextComponent m = this.message.deepCopy();
        
        if(this.isAnnouncement)
        {
            m.modifyStyle((style) -> style.applyFormatting(TextFormatting.ITALIC));
        }
        
        IFormattableTextComponent t = new StringTextComponent("<").append(playerName).appendString("> ").append(m);
        
        //        if(this.isAnnouncement)
        //        {
        //            t.modifyStyle((style) -> style.applyFormatting(TextFormatting.BOLD));
        //        }
        
        return t;
    }
}
