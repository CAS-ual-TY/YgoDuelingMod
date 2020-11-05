package de.cas_ual_ty.ydm.duel.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duel.DuelContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DuelScreenBase<E extends DuelContainer> extends DuelContainerScreen<E>
{
    public DuelScreenBase(E screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int x, int y)
    {
        this.font.drawString(ms, "Waiting for server...", 8.0F, 6.0F, 0x404040);
    }
}
