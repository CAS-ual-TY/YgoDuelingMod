package de.cas_ual_ty.ydm.duelmanager;

public interface DuelRenderingProvider
{
    public void renderCard(int x, int y, int width, int height, DuelCard card);
    
    public void renderCardReversed(int x, int y, int width, int height, DuelCard card);
    
    public void renderCardManually(int x, int y, int width, int height, DuelCard card, float widthModifier, float heightModifier, float rotation);
    
    public void renderHoverRect(int x, int y, int width, int height);
}
