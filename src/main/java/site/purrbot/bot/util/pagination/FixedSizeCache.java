/*
 *  Copyright 2018 - 2021 Andre601
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util.pagination;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class FixedSizeCache<K, V>{
    private final Map<K, V> map = new HashMap<>();
    private final K[] keys;
    private int currentIndex = 0;
    
    @SuppressWarnings("unchecked")
    public FixedSizeCache(int size){
        if(size < 1)
            throw new IllegalArgumentException("Cache size must be at least 1!");
        
        this.keys = (K[])new Object[size];
    }
    
    public V find(Predicate<K> test){
        return map.entrySet()
            .stream()
            .filter(it -> test.test(it.getKey()))
            .map(Map.Entry::getValue)
            .findFirst().orElse(null);
    }
    
    public void add(K key, V value){
        if(contains(key))
            return;
        
        if(keys[currentIndex] != null){
            map.remove(keys[currentIndex]);
        }
        map.put(key, value);
        keys[currentIndex] = key;
        currentIndex = (currentIndex + 1) % keys.length;
    }
    
    public boolean contains(K key){
        return map.containsKey(key);
    }
    
    public V get(K key){
        return map.get(key);
    }
}
