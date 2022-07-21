package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.Widget;

public interface ITooltip
{
    void onTooltip(Widget widget, MatrixStack ms, int mouseX, int mouseY);
}