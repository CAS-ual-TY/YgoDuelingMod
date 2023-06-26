package de.cas_ual_ty.ydm.clientutil;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class CardRenderUtil
{
    private static LimitedTextureBinder infoTextureBinder;
    private static LimitedTextureBinder mainTextureBinder;
    
    // called from ClientProxy
    public static void init(int maxInfoImages, int maxMainImages)
    {
        CardRenderUtil.infoTextureBinder = new LimitedTextureBinder(ClientProxy.getMinecraft(), maxInfoImages);
        CardRenderUtil.mainTextureBinder = new LimitedTextureBinder(ClientProxy.getMinecraft(), maxMainImages);
    }
    
    public static void renderCardInfo(PoseStack ms, CardHolder card, AbstractContainerScreen<?> screen)
    {
        CardRenderUtil.renderCardInfo(ms, card, screen.getGuiLeft());
    }
    
    public static void renderCardInfo(PoseStack ms, CardHolder card)
    {
        CardRenderUtil.renderCardInfo(ms, card, 100);
    }
    
    public static void renderCardInfo(PoseStack ms, CardHolder card, int width)
    {
        CardRenderUtil.renderCardInfo(ms, card, false, width);
    }
    
    public static void renderCardInfo(PoseStack ms, CardHolder card, boolean token, int width)
    {
        if(card == null || card.getCard() == null)
        {
            return;
        }
        
        final float f = 0.5f;
        final int imageSize = 64;
        int margin = 2;
        
        int maxWidth = width - margin * 2;
        
        ms.pushPose();
        ScreenUtil.white();
        
        int x = margin;
        
        if(maxWidth < imageSize)
        {
            // draw it centered if the space we got is limited
            // to make sure the image is NOT rendered more to the right of the center
            x = (maxWidth - imageSize) / 2 + margin;
        }
        
        // card texture
        
        CardRenderUtil.bindInfoResourceLocation(card);
        YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        
        if(token)
        {
            RenderSystem.setShaderTexture(0, CardRenderUtil.getInfoTokenOverlay());
            YdmBlitUtil.fullBlit(ms, x, margin, imageSize, imageSize);
        }
        
        // need to multiply x2 because we are scaling the text to x0.5
        maxWidth *= 2;
        margin *= 2;
        ms.scale(f, f, f);
        
        // card description text
        
        Font fontRenderer = ClientProxy.getMinecraft().font;
        
        List<Component> list = new LinkedList<>();
        card.getCard().addInformation(list);
        
        ScreenUtil.drawSplitString(ms, fontRenderer, list, margin, imageSize * 2 + margin * 2, maxWidth, 0xFFFFFF);
        
        ms.popPose();
    }
    
    public static void bindInfoResourceLocation(CardHolder c)
    {
        CardRenderUtil.infoTextureBinder.bind(c.getInfoImageResourceLocation());
    }
    
    public static void bindMainResourceLocation(CardHolder c)
    {
        CardRenderUtil.mainTextureBinder.bind(c.getMainImageResourceLocation());
    }
    
    public static void bindInfoResourceLocation(Properties p, byte imageIndex)
    {
        CardRenderUtil.infoTextureBinder.bind(p.getInfoImageResourceLocation(imageIndex));
    }
    
    public static void bindMainResourceLocation(Properties p, byte imageIndex)
    {
        CardRenderUtil.mainTextureBinder.bind(p.getMainImageResourceLocation(imageIndex));
    }
    
    public static void bindInfoResourceLocation(ResourceLocation r)
    {
        CardRenderUtil.infoTextureBinder.bind(r);
    }
    
    public static void bindMainResourceLocation(ResourceLocation r)
    {
        CardRenderUtil.mainTextureBinder.bind(r);
    }
    
    public static void bindSleeves(CardSleevesType s)
    {
        RenderSystem.setShaderTexture(0, s.getMainRL(ClientProxy.activeCardMainImageSize));
    }
    
    public static ResourceLocation getInfoCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardInfoImageSize + "/" + YdmItems.CARD_BACK.getId().getPath() + ".png");
    }
    
    public static ResourceLocation getMainCardBack()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardMainImageSize + "/" + YdmItems.CARD_BACK.getId().getPath() + ".png");
    }
    
    public static ResourceLocation getInfoTokenOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardInfoImageSize + "/" + "token_overlay" + ".png");
    }
    
    public static ResourceLocation getMainTokenOverlay()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ClientProxy.activeCardMainImageSize + "/" + "token_overlay" + ".png");
    }
    
    public static void renderDuelCardAdvanced(PoseStack ms, CardSleevesType back, int mouseX, int mouseY, float x, float y, float width, float height, DuelCard card, YdmBlitUtil.FullBlitMethod blitMethod, boolean forceFaceUp)
    {
        CardPosition position = card.getCardPosition();
        
        // bind the texture depending on faceup or facedown
        if(!card.getCardPosition().isFaceUp && forceFaceUp)
        {
            position = position.flip();
        }
        
        CardRenderUtil.renderDuelCardAdvanced(ms, back, mouseX, mouseY, x, y, width, height, card, position, blitMethod);
    }
    
    public static void renderDuelCardAdvanced(PoseStack ms, CardSleevesType back, int mouseX, int mouseY, float x, float y, float width, float height, DuelCard card, CardPosition position, YdmBlitUtil.FullBlitMethod blitMethod)
    {
        Minecraft mc = ClientProxy.getMinecraft();
        
        // bind the texture depending on faceup or facedown
        if(position.isFaceUp)
        {
            CardRenderUtil.bindMainResourceLocation(card.getCardHolder());
        }
        else
        {
            RenderSystem.setShaderTexture(0, back.getMainRL(ClientProxy.activeCardMainImageSize));
        }
        
        blitMethod.fullBlit(ms, x, y, width, height);
        
        if(card.getIsToken())
        {
            RenderSystem.setShaderTexture(0, CardRenderUtil.getMainTokenOverlay());
            blitMethod.fullBlit(ms, x, y, width, height);
        }
    }
    
    public static void renderDuelCard(PoseStack ms, CardSleevesType back, int mouseX, int mouseY, float x, float y, float width, float height, DuelCard card, boolean forceFaceUp)
    {
        CardRenderUtil.renderDuelCardAdvanced(ms, back, mouseX, mouseY, x, y, width, height, card,
                card.getCardPosition().isStraight
                        ? YdmBlitUtil::fullBlit
                        : YdmBlitUtil::fullBlit90Degree, forceFaceUp);
    }
    
    public static void renderDuelCardReversed(PoseStack ms, CardSleevesType back, int mouseX, int mouseY, float x, float y, float width, float height, DuelCard card, boolean forceFaceUp)
    {
        CardRenderUtil.renderDuelCardAdvanced(ms, back, mouseX, mouseY, x, y, width, height, card,
                card.getCardPosition().isStraight
                        ? YdmBlitUtil::fullBlit180Degree
                        : YdmBlitUtil::fullBlit270Degree, forceFaceUp);
    }
    
    public static void renderDuelCardCentered(PoseStack ms, CardSleevesType back, int mouseX, int mouseY, float x, float y, float width, float height, DuelCard card, boolean forceFaceUp)
    {
        // if width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;
        
        CardRenderUtil.renderDuelCard(ms, back, mouseX, mouseY, x, y, width, height, card, forceFaceUp);
    }
    
    public static void renderDuelCardReversedCentered(PoseStack ms, CardSleevesType back, int mouseX, int mouseY, float x, float y, float width, float height, DuelCard card, boolean forceFaceUp)
    {
        // if width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;
        
        CardRenderUtil.renderDuelCardReversed(ms, back, mouseX, mouseY, x, y, width, height, card, forceFaceUp);
    }
}
