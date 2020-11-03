package de.cas_ual_ty.ydm.duelmanager;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class DuelChatMessage
{
    public final ITextComponent message;
    public final ITextComponent playerName;
    public final PlayerRole playerRole;
    
    public DuelChatMessage(ITextComponent message, IFormattableTextComponent playerName, PlayerRole playerRole)
    {
        this.message = message;
        this.playerName = playerName;
        this.playerRole = playerRole;
    }
    
    public ITextComponent generateStyledMessage(PlayerRole viewRole, TextFormatting friendlyColor, TextFormatting opponentColor, TextFormatting neutralColor)
    {
        IFormattableTextComponent playerName = this.playerName.deepCopy();
        
        if(viewRole == PlayerRole.PLAYER1 || viewRole == PlayerRole.PLAYER2)
        {
            if(this.playerRole == viewRole)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(friendlyColor));
                //                playerName.getStyle().applyFormatting(friendlyColor);
            }
            else
            {
                playerName.modifyStyle((style) -> style.applyFormatting(opponentColor));
                //                playerName.getStyle().applyFormatting(opponentColor);
            }
        }
        else
        {
            if(this.playerRole == PlayerRole.PLAYER1)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(friendlyColor));
                //                playerName.getStyle().applyFormatting(friendlyColor);
            }
            else if(this.playerRole == PlayerRole.PLAYER2)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(opponentColor));
                //                playerName.getStyle().applyFormatting(opponentColor);
            }
            else //if(this.playerRole == PlayerRole.PLAYER2)
            {
                playerName.modifyStyle((style) -> style.applyFormatting(neutralColor));
                //                playerName.getStyle().applyFormatting(neutralColor);
            }
        }
        
        return new StringTextComponent("<").append(playerName).appendString("> ").append(this.message);
    }
}
