package de.cas_ual_ty.ydm.duelmanager.playfield;

public enum ZoneInteractionIcon
{
    TO_TOP_OF_DECK_FD(0), SWITCH_FROM_DEF_TO_ATK(1), NORMAL_SUMMON(2), BANISH_FU(3), TO_BOTTOM_OF_DECK_FD(4), SWITCH_FROM_ATK_TO_DEF(5), SET_SPELL_TRAP(6), BANISH_FD(7),
    ;
    
    private ZoneInteractionIcon(int index)
    {
        
    }
    
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
}
