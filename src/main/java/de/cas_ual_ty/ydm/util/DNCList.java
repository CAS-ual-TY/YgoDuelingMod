package de.cas_ual_ty.ydm.util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

// Divide and Conquer list
// V is element type (value)
// K is type by what to sort and by what to extract (key)
public class DNCList<K, V> implements Iterable<V>
{
    private final KeyExtractor<K, V> keyExtractor;
    private final Comparator<K> keyComparator;
    
    private final Comparator<V> sortingComparator;
    private final DNCComparator<K, V> dncComparator;
    
    private boolean isSorted;
    
    private ArrayList<V> list;
    
    public DNCList(KeyExtractor<K, V> keyExtractor, Comparator<K> keyComparator)
    {
        this.keyExtractor = keyExtractor;
        this.keyComparator = keyComparator;
        
        sortingComparator = (v1, v2) -> this.keyComparator.compare(this.keyExtractor.getKeyFrom(v1), this.keyExtractor.getKeyFrom(v2));
        dncComparator = (k, v) -> this.keyComparator.compare(k, this.keyExtractor.getKeyFrom(v));
        
        isSorted = true;
        clear();
    }
    
    public int getIndexOfSameKey(V value)
    {
        return getIndex(keyExtractor.getKeyFrom(value));
    }
    
    public int getIndex(K key)
    {
        return getIndex(key, false);
    }
    
    public int getIndex(K key, boolean forceReturn)
    {
        if(list.isEmpty() && !forceReturn)
        {
            return -1;
        }
        
        if(!isSorted)
        {
            sort();
        }
        
        V p;
        int result;
        
        // left included
        int left = 0;
        
        // right excluded
        int right = list.size();
        
        // current index
        int index = list.size() / 2;
        
        while(left < right)
        {
            p = list.get(index);
            result = dncComparator.compare(key, p);
            
            if(result <= -1) // id comes before currently viewed p
            {
                right = index;
            }
            else if(result >= 1) // id comes after currently viewed p
            {
                left = index + 1;
            }
            else
            {
                return index;
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
         * - left = 3
         * - right = 3
         * - index = 3
         * - loop end (left<right does not hold anymore)
         *
         * And finally, if we would have instead searched for 3.5:
         * ...
         * - result = 1 (3.5~3 -> 1)
         *
         * Iteration 4:
         * - left = 4
         * - right = 4
         * - index = 4
         * - loop end (left<right does not hold anymore)
         */
        
        if(forceReturn)
        {
            return index;
        }
        else
        {
            return -1;
        }
    }
    
    public V getByIndex(int index)
    {
        return list.get(index);
    }
    
    public V get(K key)
    {
        int index = getIndex(key);
        
        if(index != -1)
        {
            return getByIndex(index);
        }
        else
        {
            return null;
        }
    }
    
    public void add(V value)
    {
        if(isSorted)
        {
            isSorted = false;
        }
        
        list.add(value);
    }
    
    public void addAll(Collection<V> collection)
    {
        if(isSorted)
        {
            isSorted = false;
        }
        
        list.addAll(collection);
    }
    
    public void addAll(DNCList<K, V> list)
    {
        if(isSorted)
        {
            isSorted = false;
        }
        
        this.list.addAll(list.list);
    }
    
    public void addKeepSorted(V value)
    {
        K key = keyExtractor.getKeyFrom(value);
        int index = getIndex(key, true);
        
        if(index >= list.size())
        {
            list.add(value);
            return;
        }
        
        V current = getByIndex(index);
        int comparison = dncComparator.compare(key, current);
        
        if(comparison < 1)
        {
            list.add(index, value);
        }
        else
        {
            list.add(index + 1, value);
        }
    }
    
    public V remove(K key)
    {
        return removeIndex(getIndex(key));
    }
    
    public V removeIndex(int index)
    {
        return list.remove(index);
    }
    
    public boolean contains(K key)
    {
        return get(key) != null;
    }
    
    public int size()
    {
        return list.size();
    }
    
    public void sort()
    {
        list.sort(sortingComparator);
        isSorted = true;
    }
    
    public boolean isSorted()
    {
        return isSorted;
    }
    
    public void ensureExtraCapacity(int size)
    {
        list.ensureCapacity(list.size() + size);
    }
    
    public List<V> getList()
    {
        return list;
    }
    
    public List<V> getSubList(int min, int max)
    {
        return list.subList(min, max);
    }
    
    public void clear()
    {
        list = new ArrayList<>(0);
    }
    
    @Nullable
    public V getFirst(Predicate<V> predicate)
    {
        for(V v : list)
        {
            if(predicate.test(v))
            {
                return v;
            }
        }
        
        return null;
    }
    
    @Override
    public Iterator<V> iterator()
    {
        return list.iterator();
    }
    
    @Override
    public String toString()
    {
        return list.toString();
    }
    
    private interface DNCComparator<K, V>
    {
        int compare(K k, V v);
    }
    
    public interface KeyExtractor<K, V>
    {
        K getKeyFrom(V v);
    }
}
