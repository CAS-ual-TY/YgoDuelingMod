package de.cas_ual_ty.ydm.duel.screen.widget;

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

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ZoneWidget extends Button
{
    public final Zone zone;
    public final IDuelScreenContext context;
    public boolean isFlipped;
    public DuelCard hoverCard;
    
    public ZoneWidget(Zone zone, IDuelScreenContext context, int width, int height, ITextComponent title, Consumer<ZoneWidget> onPress, ITooltip onTooltip)
    {
        super(0, 0, width, height, title, (w) -> onPress.accept((ZoneWidget) w), onTooltip);
        this.zone = zone;
        this.context = context;
        shift();
        hoverCard = null;
    }
    
    protected void shift()
    {
        x -= width / 2;
        y -= height / 2;
    }
    
    protected void unshift()
    {
        x += width / 2;
        y += height / 2;
    }
    
    public ZoneWidget flip(int guiWidth, int guiHeight)
    {
        guiWidth /= 2;
        guiHeight /= 2;
        
        unshift();
        
        x -= guiWidth;
        y -= guiHeight;
        
        x = -x;
        y = -y;
        
        x += guiWidth;
        y += guiHeight;
        
        shift();
        
        isFlipped = !isFlipped;
        
        return this;
    }
    
    public ZoneWidget setPositionRelative(int x, int y, int guiWidth, int guiHeight)
    {
        this.x = x + guiWidth / 2;
        this.y = y + guiHeight / 2;
        
        shift();
        
        isFlipped = false;
        
        return this;
    }
    
    public ZoneWidget setPositionRelativeFlipped(int x, int y, int guiWidth, int guiHeight)
    {
        this.x = guiWidth / 2 - x;
        this.y = guiHeight / 2 - y;
        
        shift();
        
        isFlipped = true;
        
        return this;
    }
    
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        
        renderZoneSelectRect(ms, zone, x, y, width, height);
        
        hoverCard = renderCards(ms, mouseX, mouseY);
        
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        
        if(zone.type.getCanHaveCounters() && zone.getCounters() > 0)
        {
            // see font renderer, top static Vector3f
            // white is translated in front by that
            ms.pushPose();
            ms.translate(0, 0, 0.03F);
            AbstractGui.drawCenteredString(ms, fontrenderer, new StringTextComponent("(" + zone.getCounters() + ")"),
                    x + width / 2, y + height / 2 - fontrenderer.lineHeight / 2,
                    16777215 | MathHelper.ceil(alpha * 255.0F) << 24);
            ms.popPose();
        }
        
        if(active)
        {
            if(isHovered())
            {
                if(zone.getCardsAmount() == 0)
                {
                    ScreenUtil.renderHoverRect(ms, x, y, width, height);
                }
                
                renderToolTip(ms, mouseX, mouseY);
            }
        }
        else
        {
            ScreenUtil.renderDisabledRect(ms, x, y, width, height);
        }
    }
    
    public void renderZoneSelectRect(MatrixStack ms, Zone zone, float x, float y, float width, float height)
    {
        if(context.getClickedZone() == zone && context.getClickedCard() == null)
        {
            if(context.getOpponentClickedZone() == zone && context.getOpponentClickedCard() == null)
            {
                DuelScreenDueling.renderBothSelectedRect(ms, x, y, width, height);
            }
            else
            {
                DuelScreenDueling.renderSelectedRect(ms, x, y, width, height);
            }
        }
        else
        {
            if(context.getOpponentClickedZone() == zone && context.getOpponentClickedCard() == null)
            {
                DuelScreenDueling.renderEnemySelectedRect(ms, x, y, width, height);
            }
            else
            {
                //
            }
        }
    }
    
    public void renderCardSelectRect(MatrixStack ms, DuelCard card, float x, float y, float width, float height)
    {
        if(context.getClickedCard() == card)
        {
            if(context.getOpponentClickedCard() == card)
            {
                DuelScreenDueling.renderBothSelectedRect(ms, x, y, width, height);
            }
            else
            {
                DuelScreenDueling.renderSelectedRect(ms, x, y, width, height);
            }
        }
        else
        {
            if(context.getOpponentClickedCard() == card)
            {
                DuelScreenDueling.renderEnemySelectedRect(ms, x, y, width, height);
            }
            else
            {
                //
            }
        }
    }
    
    @Nullable
    public DuelCard renderCards(MatrixStack ms, int mouseX, int mouseY)
    {
        if(zone.getCardsAmount() <= 0)
        {
            return null;
        }
        
        boolean isOwner = zone.getOwner() == context.getZoneOwner();
        DuelCard c = zone.getTopCard();
        
        if(c != null)
        {
            if(drawCard(ms, c, x, y, width, height, mouseX, mouseY, x, y, width, height))
            {
                if(c.getCardPosition().isFaceUp || (isOwner && !zone.getType().getIsSecret()))
                {
                    context.renderCardInfo(ms, c);
                }
                
                if(active)
                {
                    ScreenUtil.renderHoverRect(ms, x, y, width, height);
                    return c;
                }
            }
        }
        
        if(context.getClickedZone() == zone)
        {
            DuelScreenDueling.renderSelectedRect(ms, x, y, width, height);
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
        
        return drawCard(ms, duelCard, renderX, renderY, renderWidth, renderHeight, mouseX, mouseY, hoverX, hoverY, hoverWidth, hoverHeight);
    }
    
    protected boolean drawCard(MatrixStack ms, DuelCard duelCard, float renderX, float renderY, float renderWidth, float renderHeight, int mouseX, int mouseY, float hoverX, float hoverY, float hoverWidth, float hoverHeight)
    {
        boolean isOwner = zone.getOwner() == context.getZoneOwner();
        boolean faceUp = zone.getType().getShowFaceDownCardsToOwner() && isOwner;
        boolean isOpponentView = zone.getOwner() != context.getView();
        
        renderCardSelectRect(ms, duelCard, hoverX, hoverY, hoverWidth, hoverHeight);
        
        if(!isOpponentView)
        {
            CardRenderUtil.renderDuelCardCentered(ms, zone.getSleeves(), mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, faceUp);
        }
        else
        {
            CardRenderUtil.renderDuelCardReversedCentered(ms, zone.getSleeves(), mouseX, mouseY, renderX, renderY, renderWidth, renderHeight, duelCard, faceUp);
        }
        
        if(isHovered() && mouseX >= hoverX && mouseX < hoverX + hoverWidth && mouseY >= hoverY && mouseY < hoverY + hoverHeight)
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
            interactions = m.getActionsFor(player, interactor, interactorCard, zone);
        }
        else
        {
            interactions = m.getAdvancedActionsFor(player, interactor, interactorCard, zone);
        }
        
        if(interactions.size() == 0)
        {
            return;
        }
        
        if(interactions.size() == 1)
        {
            list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height, onPress, onTooltip));
        }
        else if(interactions.size() == 2)
        {
            if(width <= height)
            {
                // Split them horizontally (1 action on top, 1 on bottom)
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x, y + height / 2, width, height / 2, onPress, onTooltip));
            }
            else
            {
                // Split them vertically (1 left, 1 right)
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width / 2, height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x + width / 2, y, width / 2, height, onPress, onTooltip));
            }
        }
        else if(interactions.size() == 3)
        {
            if(width == height)
            {
                // 1 on top half, 1 bottom left, 1 bottom right
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x, y + height / 2, width / 2, height / 2, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), context, x + width / 2, y + height / 2, width / 2, height / 2, onPress, onTooltip));
            }
            else if(width < height)
            {
                // Horizontally split
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width, height / 3, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x, y + height / 3, width, height / 3, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), context, x, y + height * 2 / 3, width, height / 3, onPress, onTooltip));
            }
            else //if(this.width > this.height)
            {
                // Vertically split
                list.add(new InteractionWidget(interactions.get(0), context, x, y, width / 3, height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(1), context, x + width / 3, y, width / 3, height, onPress, onTooltip));
                list.add(new InteractionWidget(interactions.get(2), context, x + width * 2 / 3, y, width / 3, height, onPress, onTooltip));
            }
        }
        else if(interactions.size() == 4 && width == height)
        {
            // 1 on top left, 1 top right, 1 bottom left, 1 bottom right
            list.add(new InteractionWidget(interactions.get(0), context, x, y, width / 2, height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(1), context, x + width / 2, y, width / 2, height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(2), context, x, y + height / 2, width / 2, height / 2, onPress, onTooltip));
            list.add(new InteractionWidget(interactions.get(3), context, x + width / 2, y + height / 2, width / 2, height / 2, onPress, onTooltip));
        }
        else
        {
            if(width < height)
            {
                // Horizontally split
                for(int i = 0; i < interactions.size(); ++i)
                {
                    list.add(new InteractionWidget(interactions.get(i), context, x, y + height * i / interactions.size(), width, height / interactions.size(), onPress, onTooltip));
                }
            }
            else //if(this.width > this.height)
            {
                // Vertically split
                for(int i = 0; i < interactions.size(); ++i)
                {
                    list.add(new InteractionWidget(interactions.get(i), context, x + width * i / interactions.size(), y, width / interactions.size(), height, onPress, onTooltip));
                }
            }
        }
    }
    
    public int getAnimationSourceX()
    {
        return x + width / 2;
    }
    
    public int getAnimationSourceY()
    {
        return y + height / 2;
    }
    
    public int getAnimationDestX()
    {
        return x + width / 2;
    }
    
    public int getAnimationDestY()
    {
        return y + height / 2;
    }
    
    public ITextComponent getTranslation()
    {
        return new TranslationTextComponent(zone.getType().getRegistryName().getNamespace() + ".zone." + zone.getType().getRegistryName().getPath());
    }
    
    public boolean openAdvancedZoneView()
    {
        return false;
    }
}