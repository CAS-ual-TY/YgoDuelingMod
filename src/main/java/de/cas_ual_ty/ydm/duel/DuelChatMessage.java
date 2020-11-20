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
    public final PlayerRole playerRole;
    public final boolean isAnnouncement;
    
    public DuelChatMessage(ITextComponent message, ITextComponent playerName, PlayerRole playerRole, boolean isAnnouncement)
    {
        this.message = message;
        this.playerName = playerName;
        this.playerRole = playerRole;
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
        
        ZoneOwner owner = ZoneOwner.fromPlayerRole(this.playerRole);
        
        if(owner.isPlayer())
        {
            if(this.playerRole == viewerRole)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(friendlyColor));
            }
            else if(owner == ZoneOwner.NONE)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(neutralColor));
            }
            else
            {
                playerName.modifyStyle((style) -> style.applyFormatting(opponentColor));
            }
        }
        else
        {
            if(this.playerRole == PlayerRole.PLAYER1)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(friendlyColor));
            }
            else if(this.playerRole == PlayerRole.PLAYER2)
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
