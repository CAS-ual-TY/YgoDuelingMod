package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ActionType
{
    public final ActionType.Factory factory;
    
    private String localKey;
    
    public ActionType(ActionType.Factory factory)
    {
        this.factory = factory;
        localKey = null;
    }
    
    public ActionType.Factory getFactory()
    {
        return factory;
    }
    
    public String getLocalKey()
    {
        if(localKey == null)
        {
            ResourceLocation rl = YDM.actionTypeRegistry.get().getKey(this);
            localKey = "action." + rl.getNamespace() + "." + rl.getPath();
        }
        
        return localKey;
    }
    
    public Component getLocal()
    {
        return Component.translatable(getLocalKey());
    }
    
    public interface Factory
    {
        Action create(ActionType type, FriendlyByteBuf buf);
    }
}
