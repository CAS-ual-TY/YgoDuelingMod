package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DiceRollAction extends RandomAction
{
    public int result;
    
    public DiceRollAction(ActionType actionType, int result)
    {
        super(actionType);
        this.result = result;
    }
    
    public DiceRollAction(ActionType actionType)
    {
        this(actionType, -1);
    }
    
    public DiceRollAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readInt());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeInt(this.result);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        this.result = playField.getDuelManager().getRandom().nextInt(6) + 1;
    }
    
    @Override
    public IFormattableTextComponent getAnnouncement(ITextComponent playerName)
    {
        return new TranslationTextComponent(this.getAnnouncementLocalKey()).appendString(": " + this.result);
    }
}
