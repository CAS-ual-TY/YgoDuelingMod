package de.cas_ual_ty.ydm.duel.screen.widget;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.cas_ual_ty.ydm.clientutil.CardRenderUtil;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneInteraction;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duel.screen.DuelScreenDueling;
import de.cas_ual_ty.ydm.duel.screen.IDuelScreenContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ZoneWidget extends Button
{
    public final Zone zone;
    public final IDuelScreenContext context;
    public boolean isFlipped;
    public DuelCard hoverCard;
    
    public ZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(0, 0, width, height, title, (w) -> onPress.accept((ZoneWidget)w), onTooltip);
        this.zone = zone;
        this.context = context;
        this.shift();
        this.hoverCard = null;
    }
    
    protected void shift()
    {
        this.x -= this.width / 2;
        this.y -= this.height / 2;
    }
    
    protected void unshift()
    {
        this.x += this.width / 2;
        this.y += this.height / 2;
    }
    
    public ZoneWidget flip(int guiWidth, int guiHeight)
    {
        guiWidth /= 2;
        guiHeight /= 2;
        
        this.unshift();
        
        this.x -= guiWidth;
        this.y -= guiHeight;
        
        this.x = -this.x;
        this.y = -this.y;
        
        this.x += guiWidth;
        this.y += guiHeight;
        
        this.shift();
        
        this.isFlipped = !this.isFlipped;
        
        return this;
    }
    
    public ZoneWidget setPositionRelative(int x, int y, int guiWidth, int guiHeight)
    {
        this.x = x + guiWidth / 2;
        this.y = y + guiHeight / 2;
        
        this.shift();
        
        this.isFlipped = false;
        
        return this;
    }
    
    public ZoneWidget setPositionRelativeFlipped(int x, int y, int guiWidth, int guiHeight)
    {
        this.x = guiWidth / 2 - x;
        this.y = guiHeight / 2 - y;
        
        this.shift();
        
        this.isFlipped = true;
        
        return this;
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
        
        if(this.context.getClickedZone() == this.zone && this.context.getClickedDuelCard() == null)
        {
            DuelScreenDueling.renderSelectedRect(ms, this.x, this.y, this.width, this.height);
        }
        
        this.hoverCard = this.renderCards(ms, mouseX, mouseY);
        
        RenderSystem.color4f(1F, 1F, 1F, this.alpha);
        
        if(this.zone.type.getCanHaveCounters() && this.zone.getCounters() > 0)
        {
            // see font renderer, top static Vector3f
            // white is translated in front by that
            ms.push();
            ms.translate(0, 0, 0.03F);
            AbstractGui.drawCenteredString(ms, fontrenderer, new StringTextComponent("(" + this.zone.getCounters() + ")"),
                this.x + this.width / 2, this.y + this.height / 2 - fontrenderer.FONT_HEIGHT / 2,
                16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
            ms.pop();
        }
        
        if(this.active)
        {
            if(this.isHovered())
            {
                if(this.zone.getCardsAmount() == 0)
                {
                    ScreenUtil.renderHoverRect(ms, this.x, this.y, this.width, this.height);
                }
                
                this.renderToolTip(ms, mouseX, mouseY);
            }
        }
        else
        {
            ScreenUtil.renderDisabledRect(ms, this.x, this.y, this.width, this.height);
        }
    }
    
    @Nullable
    public DuelCard renderCards(MatrixStack ms, int mouseX, int mouseY)
    {
        if(this.zone.getCardsAmount() <= 0)
        {
            return null;
        }
        
        boolean isOwner = this.zone.getOwner() == this.context.getZoneOwner();
        DuelCard c = this.zone.getTopCard();
        
        if(c != null)
        {
            if(this.drawCard(ms, c, this.x, this.y, this.width, this.height, mouseX, mouseY, this.x, this.y, this.width, this.height))
            {
                if(c.getCardPosition().isFaceUp || (isOwner && !this.zone.getType().getIsSecret()))
                {
                    this.context.renderCardInfo(ms, c);
                }
                
                if(this.active)
                {
                    ScreenUtil.renderHoverRect(ms, this.x, this.y, this.width, this.height);
                    return c;
                }
            }
        }
        
        if(this.context.getClickedZone() == this.zone)
        {
            DuelScreenDueling.renderSelectedRect(ms, this.x, this.y, this.width, this.height);
        }
        
        return null;
    }
    
    protected boolean drawCard(MatrixStack ms, DuelCard duelCard, int renderX, int renderY, int renderWidth, int renderHeight, int mouseX, int mouseY, int cardsWidth, int cardsHeight)
    {
        int offset = cardsHeight - cardsWidth;
        
        int hoverX = renderX;
        int hoverY = renderY;
        int hoverWidth;
        int hoverHeight;
        
        if(duelCard.getCardPosition().isStraight)
        {
            hoverX += offset;
            hoverWidth = cardsWidth;
            hoverHeight = cardsHeight;
        }
        else
        {
            hoverY += offset;
            hoverWidth = cardsHeight;
            hoverHeight = cardsWidth;
        }
        
        return this.drawCard(ms, duelCard, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, hoverX, hoverY, hoverWidth, hoverHeight);
    }
    
    protected boolean drawCard(MatrixStack ms, DuelCard duelCard, float renderX, float renderY, float renderWidth, float renderHeight, int mouseX, int mouseY, float hoverX, float hoverY, float hoverWidth, float hoverHeight)
    {
        boolean isOwner = this.zone.getOwner() == this.context.getZoneOwner();
        boolean faceUp = this.zone.getType().getShowFaceDownCardsToOwner() && isOwner;
        boolean isOpponentView = this.zone.getOwner() != this.context.getView();
        
        if(duelCard == this.context.getClickedDuelCard())
        {
            DuelScreenDueling.renderSelectedRect(ms, hoverX, hoverY, hoverWidth, hoverHeight);
        }
        
        if(!isOpponentView)
        {
            CardRenderUtil.renderDuelCardCentered(ms, this.zone.getSleeves(), mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, faceUp);
        }
        else
        {
            CardRenderUtil.renderDuelCardReversedCentered(ms, this.zone.getSleeves(), mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, faceUp);
        }
        
        if(this.isHovered() && mouseX >= hoverX && mouseX < hoverX + hoverWidth && mouseY >= hoverY && mouseY < hoverY + hoverHeight)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void addInteractionWidgets(ZoneOwner player, Zone interactor, DuelCard interactorCard, DuelManager m, List<InteractionWidget> list, Consumer<InteractionWidget> onPress, ITooltip onTooltip, boolean isAdvanced)
    {
        List<ZoneInteraction> interactions;
        
        if(!isAdvanced)
        {
            interactions = m.getActionsFor(player, interactor, interactorCard, this.zone);
        }
        else
        {
            interactions = m.getAdvancedActionsFor(player, interactor, interactorCard, this.zone);
        }
        
        if(interactions.size() == 0)
        {
            return;
        }
        
        if(interactions.size() == 1)
        {
            list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width, this.height, onPress, onTooltip));
        }
        else if(interactions.size() == 2)
        {
            if(this.width <= this.height)
            {
                // Split them horizontally (1 action on top, 1 on bottom)
                list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width, this.height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), this.context, this.x, this.y + this.height / 2, this.width, this.height / 2, onPress, onTooltip));
            }
            else
            {
                // Split them vertically (1 left, 1 right)
                list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width / 2, this.height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), this.context, this.x + this.width / 2, this.y, this.width / 2, this.height, onPress, onTooltip));
            }
        }
        else if(interactions.size() == 3)
        {
            if(this.width == this.height)
            {
                // 1 on top half, 1 bottom left, 1 bottom right
                list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width, this.height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), this.context, this.x, this.y + this.height / 2, this.width / 2, this.height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), this.context, this.x + this.width / 2, this.y + this.height / 2, this.width / 2, this.height / 2, onPress, onTooltip));
            }
            else if(this.width < this.height)
            {
                // Horizontally split
                list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width, this.height / 3, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), this.context, this.x, this.y + this.height / 3, this.width, this.height / 3, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), this.context, this.x, this.y + this.height * 2 / 3, this.width, this.height / 3, onPress, onTooltip));
            }
            else //if(this.width > this.height)
            {
                // Vertically split
                list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width / 3, this.height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), this.context, this.x + this.width / 3, this.y, this.width / 3, this.height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), this.context, this.x + this.width * 2 / 3, this.y, this.width / 3, this.height, onPress, onTooltip));
            }
        }
        else if(interactions.size() == 4 && this.width == this.height)
        {
            // 1 on top left, 1 top right, 1 bottom left, 1 bottom right
            list.add(new InteractionWidget(interactions.get(0), this.context, this.x, this.y, this.width / 2, this.height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(1), this.context, this.x + this.width / 2, this.y, this.width / 2, this.height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(2), this.context, this.x, this.y + this.height / 2, this.width / 2, this.height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(3), this.context, this.x + this.width / 2, this.y + this.height / 2, this.width / 2, this.height / 2, onPress, onTooltip));
        }
        else
        {
            if(this.width < this.height)
            {
                // Horizontally split
                for(int i = 0; i < interactions.size(); ++i)
                {
                    list.add(new InteractionWidget(interactions.get(i), this.context, this.x, this.y + this.height * i / interactions.size(), this.width, this.height / interactions.size(), onPress, onTooltip));
                }
            }
            else //if(this.width > this.height)
            {
                // Vertically split
                for(int i = 0; i < interactions.size(); ++i)
                {
                    list.add(new InteractionWidget(interactions.get(i), this.context, this.x + this.width * i / interactions.size(), this.y, this.width / interactions.size(), this.height, onPress, onTooltip));
                }
            }
        }
    }
    
    public int getAnimationSourceX()
    {
        return this.x + this.width / 2;
    }
    
    public int getAnimationSourceY()
    {
        return this.y + this.height / 2;
    }
    
    public int getAnimationDestX()
    {
        return this.x + this.width / 2;
    }
    
    public int getAnimationDestY()
    {
        return this.y + this.height / 2;
    }
    
    public ITextComponent getTranslation()
    {
        return new TranslationTextComponent(this.zone.getType().getRegistryName().getNamespace() + ".zone." + this.zone.getType().getRegistryName().getPath());
    }
    
    public boolean openAdvancedZoneView()
    {
        return false;
    }
}