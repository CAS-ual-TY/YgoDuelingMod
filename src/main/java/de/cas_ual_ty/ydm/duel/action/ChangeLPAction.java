package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ChangeLPAction extends Action implements IAnnouncedAction
{
    public int changeAmount;
    public ZoneOwner owner;
    
    public PlayField playField;
    public int prevLP;
    public int trueChange;
    public int newLP;
    
    public ChangeLPAction(ActionType actionType, int changeAmount, ZoneOwner owner)
    {
        super(actionType);
        this.changeAmount = changeAmount;
        this.owner = owner;
    }
    
    public ChangeLPAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readInt(), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeInt(this.changeAmount);
        DuelMessageUtility.encodeZoneOwner(this.owner, buf);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        this.playField = playField;
        this.prevLP = this.playField.getLifePoints(this.owner);
    }
    
    @Override
    public void doAction()
    {
        this.newLP = this.playField.changeLifePoints(this.changeAmount, this.owner);
        this.trueChange = this.newLP - this.prevLP;
    }
    
    @Override
    public void undoAction()
    {
        this.playField.setLifePoints(this.prevLP, this.owner);
    }
    
    @Override
    public void redoAction()
    {
        this.playField.setLifePoints(this.newLP, this.owner);
    }
    
    @Override
    public String getAnnouncementLocalKey()
    {
        return this.actionType.getLocalKey();
    }
    
    @Override
    public IFormattableTextComponent getAnnouncement(ITextComponent playerName)
    {
        IFormattableTextComponent t = new StringTextComponent(String.valueOf(this.trueChange));
        
        if(this.trueChange > 0)
        {
            t = new StringTextComponent("+").appendSibling(t);
        }
        
        return new TranslationTextComponent(this.getAnnouncementLocalKey()).appendString(": ").appendSibling(t);
    }
}
