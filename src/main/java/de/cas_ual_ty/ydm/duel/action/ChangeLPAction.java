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
        buf.writeInt(changeAmount);
        DuelMessageUtility.encodeZoneOwner(owner, buf);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        this.playField = playField;
        prevLP = this.playField.getLifePoints(owner);
    }
    
    @Override
    public void doAction()
    {
        newLP = playField.changeLifePoints(changeAmount, owner);
        trueChange = newLP - prevLP;
    }
    
    @Override
    public void undoAction()
    {
        playField.setLifePoints(prevLP, owner);
    }
    
    @Override
    public void redoAction()
    {
        playField.setLifePoints(newLP, owner);
    }
    
    @Override
    public String getAnnouncementLocalKey()
    {
        return actionType.getLocalKey();
    }
    
    @Override
    public IFormattableTextComponent getAnnouncement(ITextComponent playerName)
    {
        IFormattableTextComponent t = new StringTextComponent(String.valueOf(trueChange));
        
        if(trueChange > 0)
        {
            t = new StringTextComponent("+").append(t);
        }
        
        return new TranslationTextComponent(getAnnouncementLocalKey()).append(": ").append(t);
    }
}
