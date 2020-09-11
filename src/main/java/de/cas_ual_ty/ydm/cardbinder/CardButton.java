package de.cas_ual_ty.ydm.cardbinder;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.gui.widget.button.AbstractButton;

public class CardButton extends AbstractButton
{
    public final int index;
    private Function<Integer, CardHolder> cardHolder;
    private BiConsumer<CardButton, Integer> onPress;
    
    public CardButton(int posX, int posY, int width, int height, int index, BiConsumer<CardButton, Integer> onPress, Function<Integer, CardHolder> cardHolder)
    {
        super(posX, posY, width, height, "");
        this.index = index;
        this.cardHolder = cardHolder;
        this.onPress = onPress;
    }
    
    @Override
    public void renderButton(int mouseX, int mouseY, float partialTick)
    {
        CardHolder card = this.getCard();
        if(card != null)
        {
            ClientProxy.bindMainResourceLocation(card);
            YdmBlitUtil.fullBlit(this.x + 1, this.y + 1, 16, 16);
            
            if(this.isHovered())
            {
                this.drawHover();
            }
        }
    }
    
    protected void drawHover()
    {
        RenderSystem.disableDepthTest();
        int x = this.x + 1;
        int y = this.y + 1;
        RenderSystem.colorMask(true, true, true, false);
        int slotColor = -2130706433; // From ContainerScreen::slotColor
        this.fillGradient(x, y, x + 16, y + 16, slotColor, slotColor);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
    
    @Override
    public void onPress()
    {
        this.onPress.accept(this, this.index);
    }
    
    public CardHolder getCard()
    {
        return this.cardHolder.apply(this.index);
    }
}
