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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author hedge
 * @param <T>
 */
public class Dictionary<T extends DictState> implements ISearch {
    protected final List<T> states;
    
    protected Dictionary(List<T> states) {
        this.states = states;
    }
    
    @Override
    public boolean contains(String value) {
        int state = getWordLastState(value);
        
        if (state > -1) {
            return isFinal(state);
        } else {
            return false;
        }
    }
    
    @Override
    public List<String> listSuffixes(String prefix) {
        int state = getWordLastState(prefix);
        
        if (state == -1) {
            return Collections.EMPTY_LIST;
        } else {
            Collector collector = new Collector();
            recursiveTraversal(state, "", collector);
            return collector.values;
        }
    }
    
    @Override
    public void listSuffixes(String prefix, ICollector collector) {
        int state = getWordLastState(prefix);
        
        if (state != 0) {
            recursiveTraversal(state, "", collector);
        }
    }
    
    public int stateCount() {
        return states.size();
    }
    
    protected int getWordLastState(String value) {
        char[] word = value.toCharArray();
        int[] prefix = new int[word.length + 1];
        getCommonPrefix(word, prefix);
        int last = prefix[prefix.length - 1];
        
        if (last == 0 && prefix.length > 1) {
            return -1;
        } else {        
            return last;
        }
    }
    
    protected void recursiveTraversal(int state, String value, ICollector collector) {
        DictState wrapper = states.get(state);
        
        if (wrapper.isTerminal()) {
            collector.collect(value);
        }
        
        for (int i = 0; i < wrapper.transitionCount(); i++) {
            recursiveTraversal(wrapper.transition(wrapper.key(i)), value + wrapper.key(i), collector);
        }
    }
    
    protected int getCommonPrefix(char[] word, int[] stateList) {
        stateList[0] = 0;
        int current = 0;
        
        for (int i = 0; i < word.length; i++) {
            int next = getTransition(current, word[i]);
            
            if (next == -1) {
                return i + 1;
            }
            
            stateList[i + 1] = next;
            current = next;
            
        }
        
        return stateList.length;
    }
    
    protected int getTransition(int state, char value) {
        return states.get(state).transition(value);
    }
    
    protected boolean isFinal(int state) {
        return states.get(state).isTerminal();
    }
    
    protected class Collector implements ICollector {
        List<String> values = new ArrayList<>();
        
        @Override
        public void collect(String value) {
            values.add(value);
        }
    }
    
    public static Dictionary load(InputStream stream) throws IOException {        
        DataInputStream input = new DataInputStream(stream);
        List<DictState> states = new ArrayList<>();
        
        boolean editable = input.readBoolean();
        int expected = input.readInt();
        
        while (states.size() < expected) {            
            boolean isTerminal = input.readBoolean();
            short outCount = input.readShort();
            
            if (editable) {
                input.readShort();
                input.readChar();
            }
            
            char[] keyArray = new char[outCount];
            int[] transitionArray = new int[outCount];
            
            while (outCount > 0) {
                keyArray[keyArray.length - outCount] = input.readChar();               
                transitionArray[transitionArray.length - outCount] = input.readInt();
                outCount--;
            }
            
            states.add(new DictState(isTerminal, keyArray, transitionArray));
        }
        
        return new Dictionary(states);
    }
    
}
