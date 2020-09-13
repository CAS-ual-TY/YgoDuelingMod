package de.cas_ual_ty.ydm.duelmanager;

import java.util.List;

public interface DuelRenderingProvider
{
    public void renderCard(int x, int y, int width, int height, DuelCard card);
    
    public default void renderCardCentered(int x, int y, int width, int height, DuelCard card)
    {
        // is width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;
        
        this.renderCard(x, y, width, height, card);
    }
    
    public void renderCardReversed(int x, int y, int width, int height, DuelCard card);
    
    public default void renderCardReversedCentered(int x, int y, int width, int height, DuelCard card)
    {
        // is width and height are more of a rectangle, this centers the texture horizontally
        x -= (height - width) / 2;
        width = height;
        
        this.renderCardReversed(x, y, width, height, card);
    }
    
    public void renderCardProportionally(int x, int y, int width, int height, DuelCard card, float widthModifier, float heightModifier, float rotation);
    
    /*
     * row = index / 4
     * column = index % 4
     * 
     * index
     * 0 = To Top of Deck, FD
     * 1 = DEF -> ATK
     * 2 = Normal Smn
     * 3 = Banish, FA
     * 4 = To Bottom of Deck, FD
     * 5 = ATK -> DEF
     * 6 = Set Spell/Trap (FD)
     * 7 = Banish, FD
     * 8 = To Top of (Extra) Deck, FA
     * 9 = ATK/DEF -> SET
     * 10 = Monster Set
     * 11 = ?
     * 12 = Overlay, to Top
     * 13 = SpSmn ATK
     * 14 = SpSmn DEF
     * 15 = ?
     * 16 = Overlay, to Bottom
     * 17 = Add to Hand
     * 18 = Shuffle
     * 19 = ?
     * 20 = ?
     * 21 = Shuffle Hand
     * 22 = View Deck
     * 23 = ?
     * 24 = ?
     * 25 = ?
     * 26 = Special Summon Set
     * 27 = ()
     * 28 = ?
     * 29 = ?
     */
    public void renderAction(int x, int y, int width, int height, int index);
    
    /*
     * index
     * 0 = Show Hand
     * 1 = Show Deck
     * 2 = Show Card
     * 3 = Move From Monster Zone to Monster Zone
     * 4 = GY
     * 5 = Attack
     */
    public void renderLargeAction(int x, int y, int width, int height, int index);
    
    public void renderHoverRect(int x, int y, int width, int height);
    
    public PlayerRole getPlayerRole();
    
    public void renderLinesCentered(int x, int y, List<String> lines);
}
