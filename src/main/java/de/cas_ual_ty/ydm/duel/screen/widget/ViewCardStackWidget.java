package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class ViewCardStackWidget extends Button
{
    public final IDuelScreenContext context;
    public DuelCard hoverCard;
    protected int cardsTextureSize;
    protected int rows;
    protected int columns;
    protected int currentRow;
    protected List<DuelCard> cards;
    protected boolean forceFaceUp;
    
    public ViewCardStackWidget(IDuelScreenContext context, int x, int y, int width, int height, ITextComponent title, Consumer<ViewCardStackWidget> onPress, ITooltip onTooltip)
    {
        super(x, y, width, height, title, (button) -> onPress.accept((ViewCardStackWidget)button), onTooltip);
        this.context = context;
        this.hoverCard = null;
        this.rows = 0;
        this.columns = 0;
        this.currentRow = 0;
        this.deactivate();
    }
    
    public ViewCardStackWidget setRowsAndColumns(int cardsTextureSize, int rows, int columns)
    {
        this.cardsTextureSize = cardsTextureSize;
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        return this;
    }
    
    public void activate(List<DuelCard> cards, boolean forceFaceUp)
    {
        this.active = true;
        this.visible = true;
        this.currentRow = 0;
        this.cards = cards;
        this.forceFaceUp = forceFaceUp;
    }
    
    public void forceFaceUp()
    {
        this.forceFaceUp = true;
    }
    
    public void deactivate()
    {
        this.cards = null;
        this.visible = false;
        this.active = false;
    }
    
    public int getCurrentRow()
    {
        return this.currentRow;
    }
    
    public int getMaxRows()
    {
        if(this.cards != null && this.columns > 0)
        {
            return Math.max(0, MathHelper.ceil(this.cards.size() / (float)this.columns) - this.rows);
        }
        else
        {
            return 0;
        }
    }
    
    public void decreaseCurrentRow()
    {
        this.currentRow = Math.max(0, this.currentRow - 1);
    }
    
    public void increaseCurrentRow()
    {
        this.currentRow = Math.min(this.getMaxRows(), this.currentRow + 1);
    }
    
    public boolean getForceFaceUp()
    {
        return this.forceFaceUp;
    }
    
    public List<DuelCard> getCards()
    {
        return this.cards;
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1F, 1F, 1F, this.alpha);
        
        if(!this.cards.isEmpty())
        {
            this.hoverCard = this.renderCards(ms, mouseX, mouseY);
        }
        else
        {
            this.hoverCard = null;
        }
        
        int j = this.getFGColor();
        AbstractGui.drawCenteredString(ms, fontrenderer, this.getMessage(), this.x, this.y, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
    
    @Nullable
    public DuelCard renderCards(MatrixStack ms, int mouseX, int mouseY)
    {
        DuelCard hoveredCard = null;
        int hoverX = 0, hoverY = 0;
        
        int index = this.currentRow * this.columns;
        int x, y;
        DuelCard c;
        
        for(int i = 0; i < this.rows; ++i)
        {
            for(int j = 0; j < this.columns && index < this.cards.size(); ++j)
            {
                x = this.x + j * this.cardsTextureSize;
                y = this.y + i * this.cardsTextureSize;
                
                c = this.cards.get(index++);
                
                if(this.drawCard(ms, c, x, y, this.cardsTextureSize, this.cardsTextureSize, mouseX, mouseY))
                {
                    hoverX = x;
                    hoverY = y;
                    hoveredCard = c;
                }
            }
        }
        
        if(hoveredCard != null)
        {
            if(hoveredCard.getCardPosition().isFaceUp || this.forceFaceUp || (this.context.getClickedZone() != null && this.context.getZoneOwner() == this.context.getClickedZone().getOwner() && !this.context.getClickedZone().getType().getIsSecret()))
            {
                this.context.renderCardInfo(ms, hoveredCard);
            }
            
            ScreenUtil.renderHoverRect(ms, hoverX, hoverY, this.cardsTextureSize, this.cardsTextureSize);
        }
        
        if(!this.active)
        {
            return null;
        }
        else
        {
            return hoveredCard;
        }
    }
    
    protected boolean drawCard(MatrixStack ms, DuelCard duelCard, int renderX, int renderY, int renderWidth, int renderHeight, int mouseX, int mouseY)
    {
        if(duelCard == this.context.getClickedDuelCard())
        {
            DuelScreenDueling.renderSelectedRect(ms, renderX, renderY, renderWidth, renderHeight);
        }
        
        CardRenderUtil.renderDuelCardCentered(ms, renderX, renderY, renderWidth, renderHeight, duelCard, this.forceFaceUp);
        
        if(this.isHovered() && mouseX >= renderX && mouseX < renderX + renderWidth && mouseY >= renderY && mouseY < renderY + renderHeight)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}