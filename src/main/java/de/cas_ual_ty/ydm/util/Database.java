package de.cas_ual_ty.ydm.util;

import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.properties.Properties;

public class Database
{
    public static DNCList<Long, Properties> PROPERTIES_LIST = new DNCList<>((p) -> p.getId(), Long::compare);
    public static DNCList<String, Card> CARDS_LIST = new DNCList<>((c) -> c.getSetId(), (s1, s2) -> s1.compareTo(s2));
}
