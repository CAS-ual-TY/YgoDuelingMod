package de.cas_ual_ty.ydm.util;

import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

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
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
        assert pow >= 0 && pow < YdmUtil.POW_2.length;
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
    
    public static int toPow2ConfigValue(int i, int min)
    {
        return YdmUtil.getPow2(YdmUtil.range(MathHelper.log2(i), min, YdmUtil.POW_2.length - 1));
    }
    
    public static int range(int i, int min, int max)
    {
        return Math.max(min, Math.min(max, i));
    }
    
    public static @Nullable Hand getActiveItem(PlayerEntity player, Item item)
    {
        return YdmUtil.getActiveItem(player, (itemStack) -> itemStack.getItem() == item);
    }
    
    public static @Nullable Hand getActiveItem(PlayerEntity player, Predicate<ItemStack> item)
    {
        if(item.test(player.getHeldItemMainhand()))
        {
            return Hand.MAIN_HAND;
        }
        else if(item.test(player.getHeldItemOffhand()))
        {
            return Hand.OFF_HAND;
        }
        else
        {
            return null;
        }
    }
    
    public static ZoneOwner getViewOwner(ZoneOwner owner, ZoneOwner view, ZoneOwner toMap)
    {
        if(!toMap.isPlayer())
        {
            return ZoneOwner.NONE;
        }
        else if(owner.isPlayer())
        {
            return owner;
        }
        else
        {
            if(view == toMap)
            {
                return view;
            }
            else
            {
                return view.opponent();
            }
        }
    }
}
