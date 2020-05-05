/*
 * The MIT License
 *
 * Copyright 2020 hedgeonline.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dawg;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author Андрей
 */
public class Register {
    private final Map<AutoState, Integer> stateRegister;
    private final Map<Integer, AutoState> stateIndex;
    private final TreeSet<Integer> discarded;
    
    public Register() {
        this.stateRegister = new HashMap<>();
        this.stateIndex = new HashMap<>();
        this.discarded = new TreeSet<>();
    }
    
    public void add(int state, AutoState wrapper) {
        stateRegister.put(wrapper, state);
        stateIndex.put(state, wrapper);
    }
    
    public void remove(int state) {
        AutoState wrapper = stateIndex.remove(state);
        stateIndex.remove(state);
        stateRegister.remove(wrapper);        
    }
    
    public void discard(int state) {
        discarded.add(state);
    }
    
    public Integer nextDiscarded() {
        return discarded.pollFirst();
    }
        
    public int discardedStatesCount() {
        return discarded.size();
    }
    
    public int get(int state, AutoState wrapper) {
        AutoState old = stateIndex.get(state);
        
        if (old != null) {
            return state;
        } else {        
            Integer other = stateRegister.get(wrapper);
            
            if (other != null) {
                return other;
            } else {
                return -1;
            }
        }
    }
    
}
