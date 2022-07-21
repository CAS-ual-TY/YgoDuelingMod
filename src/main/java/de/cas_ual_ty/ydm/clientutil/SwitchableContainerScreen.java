package de.cas_ual_ty.ydm.clientutil;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

// From
// https://github.com/CAS-ual-TY/UsefulCodeBitsForTheBlocksGame/blob/main/src/main/java/com/example/examplemod/client/screen/SwitchableContainerScreen.java
public abstract class SwitchableContainerScreen<T extends Container> extends ContainerScreen<T>
{
    private boolean isClosedByPlayer;
    
    public SwitchableContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        isClosedByPlayer = true;
    }
    
    @Override
    public void removed()
    {
        if(isClosedByPlayer)
        {
            onGuiClose();
        }
    }
    
    protected void onGuiClose()
    {
        super.removed();
    }
    
    public void switchScreen(ContainerScreen<T> screen)
    {
        isClosedByPlayer = false;
        minecraft.setScreen(screen);
    }
}
