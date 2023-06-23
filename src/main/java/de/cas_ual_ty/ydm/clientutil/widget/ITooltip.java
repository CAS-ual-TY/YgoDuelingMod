package de.cas_ual_ty.ydm.clientutil.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;

public interface ITooltip
{
    void onTooltip(Widget widget, PoseStack ms, int mouseX, int mouseY);
}