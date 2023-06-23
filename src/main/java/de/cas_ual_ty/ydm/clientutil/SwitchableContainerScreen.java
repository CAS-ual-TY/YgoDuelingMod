package de.cas_ual_ty.ydm.clientutil;


import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

// From
// https://github.com/CAS-ual-TY/UsefulCodeBitsForTheBlocksGame/blob/main/src/main/java/com/example/examplemod/client/screen/SwitchableContainerScreen.java
public abstract class SwitchableContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
    private boolean isClosedByPlayer;
    
    public SwitchableContainerScreen(T screenContainer, Inventory inv, Component titleIn)
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
    
    public void switchScreen(AbstractContainerScreen<T> screen)
    {
        isClosedByPlayer = false;
        minecraft.setScreen(screen);
    }
}
