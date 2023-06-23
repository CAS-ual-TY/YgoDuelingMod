package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


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
    
    public CoinFlipAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readBoolean());
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        buf.writeBoolean(heads);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        heads = playField.getDuelManager().getRandom().nextBoolean();
    }
    
    @Override
    public MutableComponent getAnnouncement(Component playerName)
    {
        return Component.translatable(getAnnouncementLocalKey()).append(": ")
                .append(Component.translatable(getAnnouncementLocalKey() + "." + (heads ? "heads" : "tails")));
    }
}
