package de.cas_ual_ty.ydm.cardbinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.YdmBlitUtil;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CardButton extends AbstractButton
{
    public final int index;
    private Function<Integer, CardHolder> cardHolder;
    private BiConsumer<CardButton, Integer> onPress;
    
    public CardButton(int posX, int posY, int width, int height, int index, BiConsumer<CardButton, Integer> onPress, Function<Integer, CardHolder> cardHolder)
    {
        super(posX, posY, width, height, Component.empty());
        this.index = index;
        this.cardHolder = cardHolder;
        this.onPress = onPress;
    }
    
    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTick)
    {
        CardHolder card = getCard();
        if(card != null)
        {
            ScreenUtil.white();
            CardRenderUtil.bindMainResourceLocation(card);
            YdmBlitUtil.fullBlit(ms, x + 1, y + 1, 16, 16);
            
            if(isHoveredOrFocused())
            {
                drawHover(ms);
            }
        }
    }
    
    protected void drawHover(PoseStack ms)
    {
        RenderSystem.disableDepthTest();
        int x = this.x + 1;
        int y = this.y + 1;
        RenderSystem.colorMask(true, true, true, false);
        int slotColor = -2130706433; // From ContainerScreen::slotColor
        fillGradient(ms, x, y, x + 16, y + 16, slotColor, slotColor);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
    
    @Override
    public void onPress()
    {
        onPress.accept(this, index);
    }
    
    public CardHolder getCard()
    {
        return cardHolder.apply(index);
    }
    
    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {
    
    }
}
