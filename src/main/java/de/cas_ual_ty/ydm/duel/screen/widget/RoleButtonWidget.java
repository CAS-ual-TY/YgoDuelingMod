package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class RoleButtonWidget extends Button
{
    public Supplier<Boolean> available;
    public PlayerRole role;
    
    public RoleButtonWidget(int xIn, int yIn, int widthIn, int heightIn, ITextComponent text, IPressable onPress, Supplier<Boolean> available, PlayerRole role)
    {
        super(xIn, yIn, widthIn, heightIn, text, onPress);
        this.available = available;
        this.role = role;
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
    {
        this.active = this.available.get();
        super.render(ms, mouseX, mouseY, partial);
    }
}