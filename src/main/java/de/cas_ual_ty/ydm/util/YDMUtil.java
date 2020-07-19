package de.cas_ual_ty.ydm.util;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.properties.LevelMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.LinkMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.MonsterProperties;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.card.properties.SpellProperties;
import de.cas_ual_ty.ydm.card.properties.TrapProperties;
import de.cas_ual_ty.ydm.card.properties.XyzMonsterProperties;

public class YDMUtil
{
    public static final FileFilterSuffix JSON_FILTER = YDMUtil.createFileFilter(".json");
    public static final FileFilterSuffix PNG_FILTER = YDMUtil.createFileFilter(".png");
    
    private static final int[] POW_2 = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024 };
    
    public static Properties buildProperties(JsonObject j)
    {
        Properties p0 = new Properties(j);
        
        if(p0.getIsSpell())
        {
            return new SpellProperties(p0, j);
        }
        else if(p0.getIsTrap())
        {
            return new TrapProperties(p0, j);
        }
        else if(p0.getIsMonster())
        {
            MonsterProperties p1 = new MonsterProperties(p0, j);
            
            if(p1.getHasLevel())
            {
                return new LevelMonsterProperties(p1, j);
            }
            else if(p1.getIsXyz())
            {
                return new XyzMonsterProperties(p1, j);
            }
            else if(p1.getIsLink())
            {
                return new LinkMonsterProperties(p1, j);
            }
        }
        
        return p0;
    }
    
    public static String toSimpleString(String s)
    {
        return s.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }
    
    public static int getPow2(int pow)
    {
        assert pow >= 0 && pow < 10;
        return YDMUtil.POW_2[pow];
    }
    
    public static FileFilterSuffix createFileFilter(String requiredSuffix)
    {
        return () -> requiredSuffix;
    }
}
