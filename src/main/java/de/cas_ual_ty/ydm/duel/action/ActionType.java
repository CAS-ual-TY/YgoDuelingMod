package de.cas_ual_ty.ydm.duel.action;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ActionType extends ForgeRegistryEntry<ActionType>
{
    public final ActionType.Factory factory;
    
    public ActionType(ActionType.Factory factory)
    {
        this.factory = factory;
    }
    
    public ActionType.Factory getFactory()
    {
        return this.factory;
    }
    
    public String getLocalKey()
    {
        return "action." + this.getRegistryName().getNamespace() + "." + this.getRegistryName().getPath();
    }
    
    public ITextComponent getLocal()
    {
        return new TranslationTextComponent(this.getLocalKey());
    }
    
    public static interface Factory
    {
        Action create(ActionType type, PacketBuffer buf);
    }
}
