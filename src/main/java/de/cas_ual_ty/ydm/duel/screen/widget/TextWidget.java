package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TextWidget extends Widget
{
    public Supplier<ITextComponent> nameGetter;
    
    public TextWidget(int xIn, int yIn, int widthIn, int heightIn, Supplier<ITextComponent> nameGetter)
    {
        super(xIn, yIn, widthIn, heightIn, StringTextComponent.EMPTY);
        this.nameGetter = nameGetter;
        this.active = false;
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partial)
    {
        this.setMessage(this.nameGetter.get());
        super.render(ms, mouseX, mouseY, partial);
    }
    
    @Override
    public int getFGColor()
    {
        return 16777215; //From super
    }
}