package de.cas_ual_ty.ydm.binder;

import java.util.function.Function;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;

public class CardButton extends Button
{
    public final int index;
    public Function<Integer, CardHolder> cardHolder;
    
    public CardButton(int posX, int posY, int width, int height, int index, IPressable onPress, Function<Integer, CardHolder> cardHolder)
    {
        super(posX, posY, width, height, "", onPress);
        this.index = index;
        this.cardHolder = cardHolder;
    }
    
    @Override
    public void renderButton(int mouseX, int mouseY, float partialTick)
    {
        CardHolder card = this.cardHolder.apply(this.index);
        if(card != null)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(card.getMainImageResourceLocation());
            //blit(int x, int y, int desiredWidth, int desiredHeight, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight);
            AbstractGui.blit(this.x + 1, this.y + 1, 16, 16, 0, 0, YDM.activeMainImageSize, YDM.activeMainImageSize, YDM.activeMainImageSize, YDM.activeMainImageSize);
            this.renderBg(minecraft, mouseX, mouseY);
        }
    }
    
    @Override
    protected void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_)
    {
        super.renderBg(p_renderBg_1_, p_renderBg_2_, p_renderBg_3_);
    }
    
}
