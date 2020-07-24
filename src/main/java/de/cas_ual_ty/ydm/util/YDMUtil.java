package de.cas_ual_ty.ydm.util;

import java.util.UUID;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.properties.DefMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.LevelMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.LinkMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.MonsterProperties;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.card.properties.SpellProperties;
import de.cas_ual_ty.ydm.card.properties.TrapProperties;
import de.cas_ual_ty.ydm.card.properties.XyzMonsterProperties;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.NonNullSupplier;

public class YdmUtil
{
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
            
            if(p1.getHasDef())
            {
                p1 = new DefMonsterProperties(p1, j);
                
                if(p1.getHasLevel())
                {
                    return new LevelMonsterProperties(p1, j);
                }
                else if(p1.getIsXyz())
                {
                    return new XyzMonsterProperties(p1, j);
                }
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
        return YdmUtil.POW_2[pow];
    }
    
    public static UUID createRandomUUID()
    {
        return MathHelper.getRandomUUID();
    }
    
    public static NonNullSupplier<IllegalArgumentException> throwNullCapabilityException()
    {
        return () -> new IllegalArgumentException("[" + YDM.MOD_ID + "] Capability can not be null!");
    }
    
    public static int toPow2(int i)
    {
        return MathHelper.log2(i);
    }
}
