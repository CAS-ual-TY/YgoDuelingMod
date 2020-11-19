package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Function;

import de.cas_ual_ty.ydm.clientutil.widget.TextWidget;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import net.minecraft.util.text.ITextComponent;

public class RoleOccupantsWidget extends TextWidget
{
    public RoleOccupantsWidget(int xIn, int yIn, int widthIn, int heightIn, Function<PlayerRole, ITextComponent> nameGetter, PlayerRole role)
    {
        super(xIn, yIn, widthIn, heightIn, () -> nameGetter.apply(role));
    }
}