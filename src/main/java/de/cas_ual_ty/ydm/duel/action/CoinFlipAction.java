package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CoinFlipAction extends RandomAction
{
    public boolean heads;
    
    public CoinFlipAction(ActionType actionType, boolean heads)
    {
        super(actionType);
        this.heads = heads;
    }
    
    public CoinFlipAction(ActionType actionType)
    {
        this(actionType, false);
    }
    
    public CoinFlipAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readBoolean());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeBoolean(this.heads);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        this.heads = playField.getDuelManager().getRandom().nextBoolean();
    }
    
    @Override
    public IFormattableTextComponent getAnnouncement(ITextComponent playerName)
    {
        return new TranslationTextComponent(this.getAnnouncementLocalKey()).appendString(": ")
            .append(new TranslationTextComponent(this.getAnnouncementLocalKey() + "." + (this.heads ? "heads" : "tails")));
    }
}
