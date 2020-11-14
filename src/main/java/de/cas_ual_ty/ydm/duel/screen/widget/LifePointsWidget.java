package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class LifePointsWidget extends Widget
{
    public static final ResourceLocation DUEL_WIDGETS = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_widgets.png");
    
    public Supplier<Integer> lpGetter;
    public int maxLP;
    
    public LifePointsWidget(int x, int y, int width, int height, Supplier<Integer> lpGetter, int maxLP)
    {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.lpGetter = lpGetter;
        this.maxLP = maxLP;
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        int lp = this.lpGetter.get();
        float relativeLP = lp / (float)this.maxLP;
        
        final int margin = 1;
        int x = this.x;
        int y = this.y;
        int w = this.width;
        int h = this.height;
        
        RenderSystem.color4f(1F, 1F, 1F, this.alpha);
        minecraft.getTextureManager().bindTexture(LifePointsWidget.DUEL_WIDGETS);
        this.blit(ms, x, y, 0, 1 * 8, this.width, this.height);
        this.blit(ms, x, y, 0, 0, MathHelper.ceil(this.width * relativeLP), this.height);
        this.blit(ms, x, y, 0, 2 * 8, this.width, this.height);
        this.renderBg(ms, minecraft, mouseX, mouseY);
        
        x = this.x + this.width / 2;
        y = this.y + this.height / 2;
        
        ms.push();
        
        ms.scale(0.5F, 0.5F, 1F);
        
        int j = this.getFGColor();
        AbstractGui.drawCenteredString(ms, fontrenderer, new StringTextComponent(String.valueOf(lp)), x * 2, y * 2 - fontrenderer.FONT_HEIGHT / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        
        ms.pop();
    }
}
