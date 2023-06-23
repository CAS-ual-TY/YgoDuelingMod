package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public class ActionIcon
{
    public final ResourceLocation sourceFile;
    public final int fileSize;
    public final int iconX;
    public final int iconY;
    public final int iconWidth;
    public final int iconHeight;
    
    private String localKey;
    
    public ActionIcon(ResourceLocation sourceFile, int fileSize, int iconX, int iconY, int iconWidth, int iconHeight)
    {
        this.sourceFile = sourceFile;
        this.fileSize = fileSize;
        this.iconX = iconX;
        this.iconY = iconY;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }
    
    public String getLocalKey()
    {
        if(localKey == null)
        {
            ResourceLocation rl = YDM.actionIconRegistry.get().getKey(this);
            localKey = "actionIcon." + rl.getNamespace() + "." + rl.getPath();
        }
        
        return localKey;
    }
    
    public Component getLocal()
    {
        return Component.translatable(getLocalKey());
    }
    
    public ActionIcon(ResourceLocation sourceFile, int size)
    {
        this(sourceFile, size, 0, 0, size, size);
    }
    
    public ActionIcon(ResourceLocation sourceFile, int fileSize, int iconWidth, int iconHeight, byte iconIndex)
    {
        this(sourceFile, fileSize, iconWidth * (iconIndex % (fileSize / iconWidth)), iconHeight * (iconIndex / (fileSize / iconWidth)), iconWidth, iconHeight);
    }
}
