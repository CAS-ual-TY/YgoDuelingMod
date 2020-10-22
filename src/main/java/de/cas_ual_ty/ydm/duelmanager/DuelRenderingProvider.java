package de.cas_ual_ty.ydm.duelmanager;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.action.ActionIcon;

public interface DuelRenderingProvider
{
    public void renderCard(MatrixStack ms, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp);
    
    public default void renderCardCentered(MatrixStack ms, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp)
    {
        // if width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;
        
        this.renderCard(ms, x, y, width, height, card, forceFaceUp);
    }
    
    public void renderCardReversed(MatrixStack ms, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp);
    
    public default void renderCardReversedCentered(MatrixStack ms, int x, int y, int width, int height, DuelCard card, boolean forceFaceUp)
    {
        // if width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;
        
        this.renderCardReversed(ms, x, y, width, height, card, forceFaceUp);
    }
    
    public void renderAction(MatrixStack ms, int x, int y, int width, int height, ActionIcon icon);
    
    public void renderHoverRect(MatrixStack ms, int x, int y, int width, int height);
    
    public void renderLineRect(MatrixStack ms, int x, int y, int width, int height, int lineWidth, float r, float g, float b, float a);
    
    public PlayerRole getPlayerRole();
    
    public void renderLinesCentered(MatrixStack ms, int x, int y, List<String> lines);
    
    public void sendActionToServer(Action action);
}
