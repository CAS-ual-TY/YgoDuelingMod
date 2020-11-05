package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RoleOccupantsWidget extends Widget
{
    public Function<PlayerRole, ITextComponent> nameGetter;
    public PlayerRole role;
    
    public RoleOccupantsWidget(int xIn, int yIn, int widthIn, int heightIn, Function<PlayerRole, ITextComponent> nameGetter, PlayerRole role)
    {
        super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY);
        this.nameGetter = nameGetter;
        this.role = role;
        this.active = false;
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
    {
        this.setMessage(this.nameGetter.apply(this.role));
        super.render(ms, mouseX, mouseY, partial);
    }
}