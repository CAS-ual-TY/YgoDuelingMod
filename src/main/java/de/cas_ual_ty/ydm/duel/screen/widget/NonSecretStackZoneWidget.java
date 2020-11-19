package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.util.text.ITextComponent;

public class NonSecretStackZoneWidget extends StackZoneWidget
{
    public NonSecretStackZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(zone, context, width, height, title, onPress, onTooltip);
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        super.renderButton(ms, mouseX, mouseY, partialTicks);
        this.hoverCard = null; // dont select top card when clicking on it, ever
    }
}
