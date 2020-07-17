package de.cas_ual_ty.ydm.util;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.card.properties.Properties;

public class Database
{
    private static List<Properties> PROPERTIES_LIST;
    
    public static void registerProperties(Properties p)
    {
        if(Database.getPropertiesById(p.getId()) == null)
        {
            Database.PROPERTIES_LIST.add(p);
        }
    }
    
    public static void initPropertiesList(int size)
    {
        Database.PROPERTIES_LIST = new ArrayList<>(size);
    }
    
    public static void sortPropertiesList()
    {
        Database.PROPERTIES_LIST.sort((p1, p2) -> Long.compare(p1.getId(), p2.getId()));
    }
    
    // Divide and conquer algorithm on a sorted list
    public static Properties getPropertiesById(long id)
    {
        Properties p = null;
        int result;
        
        // left included
        int left = 0;
        
        // right excluded
        int right = Database.PROPERTIES_LIST.size();
        
        // current index
        int index = Database.PROPERTIES_LIST.size() / 2;
        
        while(left < right)
        {
            p = Database.PROPERTIES_LIST.get(index);
            result = Long.compare(id, p.getId());
            
            if(result == -1) // id comes before currently viewed p
            {
                right = index;
            }
            else if(result == 1) // id comes after currently viewed p
            {
                left = index + 1;
            }
            else
            {
                return p;
            }
            
            index = (left + right) / 2;
        }
        
        /*
         * Example:
         * - list: [0, 1, 2, 3, 4] (size: 5)
         * - search for: 3
         * - compare method ~: > (greater than)
         * 
         * Iteration 1:
         * - left = 0
         * - right = 5
         * - index = 2
         * - result = 1 (3~2 -> 1)
         * 
         * Iteration 2:
         * - left = 3
         * - right = 5
         * - index = 4
         * - result = -1 (3~4 -> -1)
         * 
         * Iteration 3:
         * - left = 3
         * - right = 4
         * - index = 3
         * - result = 0 (3~3 -> 0)
         * 
         * Otherwise, if this would not have been a match and we would search for 2.5:
         * ...
         * - result = -1 (2.5~3 -> -1)
         * 
         * Iteration 4:
         * - left = 4
         * - right = 4
         * - index = 4
         * - loop end (left<right does not hold anymore)
         * 
         * And finally, if we would have instead searched for 3.5:
         * ...
         * - result = 1 (3.5~3 -> 1)
         * 
         * Iteration 4:
         * - left = 3
         * - right = 3
         * - index = 3
         * - loop end (left<right does not hold anymore)
         */
        
        return p;
    }
}
