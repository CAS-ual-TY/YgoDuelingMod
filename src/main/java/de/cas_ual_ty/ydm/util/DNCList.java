package de.cas_ual_ty.ydm.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

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
        
        this.sortingComparator = (v1, v2) -> this.keyComparator.compare(this.keyExtractor.getKeyFrom(v1), this.keyExtractor.getKeyFrom(v2));
        this.dncComparator = (k, v) -> this.keyComparator.compare(k, this.keyExtractor.getKeyFrom(v));
        
        this.isSorted = true;
        this.clear();
    }
    
    public int getIndexOfSameKey(V value)
    {
        return this.getIndex(this.keyExtractor.getKeyFrom(value));
    }
    
    public int getIndex(K key)
    {
        return this.getIndex(key, false);
    }
    
    public int getIndex(K key, boolean forceReturn)
    {
        if(this.list.isEmpty() && !forceReturn)
        {
            return -1;
        }
        
        if(!this.isSorted)
        {
            this.sort();
        }
        
        V p;
        int result;
        
        // left included
        int left = 0;
        
        // right excluded
        int right = this.list.size();
        
        // current index
        int index = this.list.size() / 2;
        
        while(left < right)
        {
            p = this.list.get(index);
            result = this.dncComparator.compare(key, p);
            
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
        return this.list.get(index);
    }
    
    public V get(K key)
    {
        int index = this.getIndex(key);
        
        if(index != -1)
        {
            return this.getByIndex(index);
        }
        else
        {
            return null;
        }
    }
    
    public void add(V value)
    {
        if(this.isSorted)
        {
            this.isSorted = false;
        }
        
        this.list.add(value);
    }
    
    public void addKeepSorted(V value)
    {
        K key = this.keyExtractor.getKeyFrom(value);
        int index = this.getIndex(key, true);
        
        if(index >= this.list.size())
        {
            this.list.add(value);
            return;
        }
        
        V current = this.getByIndex(index);
        int comparison = this.dncComparator.compare(key, current);
        
        if(comparison < 1)
        {
            this.list.add(index, value);
        }
        else
        {
            this.list.add(index + 1, value);
        }
    }
    
    public V remove(K key)
    {
        return this.removeIndex(this.getIndex(key));
    }
    
    public V removeIndex(int index)
    {
        return this.list.remove(index);
    }
    
    public boolean contains(K key)
    {
        return this.get(key) != null;
    }
    
    public int size()
    {
        return this.list.size();
    }
    
    public void sort()
    {
        this.list.sort(this.sortingComparator);
        this.isSorted = true;
    }
    
    public boolean isSorted()
    {
        return this.isSorted;
    }
    
    public void ensureExtraCapacity(int size)
    {
        this.list.ensureCapacity(this.list.size() + size);
    }
    
    public List<V> getSubList(int min, int max)
    {
        return this.list.subList(min, max);
    }
    
    public void clear()
    {
        this.list = new ArrayList<>(0);
    }
    
    @Nullable
    public V getFirst(Predicate<V> predicate)
    {
        for(V v : this.list)
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
        return this.list.iterator();
    }
    
    @Override
    public String toString()
    {
        return this.list.toString();
    }
    
    private static interface DNCComparator<K, V>
    {
        int compare(K k, V v);
    }
    
    public static interface KeyExtractor<K, V>
    {
        K getKeyFrom(V v);
    }
}
