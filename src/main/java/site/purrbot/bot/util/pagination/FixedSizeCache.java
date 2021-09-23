/*
 * The below code originates from https://github.com/JDA-Applications/JDA-Utilities
 * with changes from https://github.com/Almighty-Alpaca/JDA-Butler applied and
 * does not fall under the MIT license of this Project, but the original
 * License of its source.
 * 
 * A copy of the original License header is added below for your convenience.
 * 
 * ================================================================================
 *
 * Copyright 2016-2021 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
