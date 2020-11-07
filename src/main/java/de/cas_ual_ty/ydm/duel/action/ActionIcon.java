package de.cas_ual_ty.ydm.duel.action;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ActionIcon extends ForgeRegistryEntry<ActionIcon>
{
    public final ResourceLocation sourceFile;
    public final int fileSize;
    public final int iconX;
    public final int iconY;
    public final int iconWidth;
    public final int iconHeight;
    
    public ActionIcon(ResourceLocation sourceFile, int fileSize, int iconX, int iconY, int iconWidth, int iconHeight)
    {
        this.sourceFile = sourceFile;
        this.fileSize = fileSize;
        this.iconX = iconX;
        this.iconY = iconY;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
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
