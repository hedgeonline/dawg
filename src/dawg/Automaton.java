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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Андрей
 */
public class Automaton extends Dictionary<AutoState> {
    private final Register register;
    
    public Automaton() {
        super(new ArrayList<AutoState>());
        
        register = new Register();
        states.add(new AutoState('\0', false));
    }
    
    protected Automaton(List<AutoState> states, Register register) {
        super(states);
        this.register = register;
    }
    
    public void add(String value) {
        char[] word = value.toCharArray();
        int[] stateList = new int[word.length + 1];
        int common = getCommonPrefix(word, stateList);
        cloneIfConfluence(word, stateList, common);
        addSuffix(word, stateList, common);
        replaceOrRegister(word, stateList);
    }
    
    protected void cloneIfConfluence(char[] word, int[] stateList, int common) {
        int confIdx = findConfluence(stateList);
        
        if (confIdx > -1) {            
            register.remove(stateList[confIdx - 1]);
        
            for (int i = confIdx; i < common; i++) {
                int cloned = cloneState(stateList[i]);
                stateList[i] = cloned;
                setTransition(stateList[i - 1], word[i - 1], cloned);
            }
        }
    }
    
    protected int cloneState(int state) {
        int clone = newState(states.get(state).value());
        
        AutoState initialState = states.get(state);
        AutoState clonedState = states.get(clone);
        
        for (int i = 0; i < initialState.transitionCount(); i++) {
            char key = initialState.key(i);
            clonedState.setTransition(key, initialState.transition(key));
            
            if (clonedState.transition(key) != -1) {
                states.get(clonedState.transition(key)).incrementInbound();
            }
        }

        states.get(clone).setTerminal(states.get(state).isTerminal());
        
        return clone;
    }
    
    protected void replaceOrRegister(char[] word, int[] stateList) {        
        for (int i = stateList.length - 1; i > 0; i--) {
            int registered = register.get(stateList[i], states.get(stateList[i]));
            
            if (registered == -1) {
                register.add(stateList[i], states.get(stateList[i]));
            } else if (registered != stateList[i]) {
                register.remove(stateList[i - 1]);
                setTransition(stateList[i - 1], word[i - 1], registered);
                remove(stateList[i]);  
                register.discard(stateList[i]);
                stateList[i] = registered;
            }            
        }
    }
    
    protected void remove(int state) {
        AutoState wrapper = states.get(state);
        
        for (int i = 0; i < wrapper.transitionCount(); i++) {
            if (wrapper.transition(wrapper.key(i)) != -1) {
                states.get(wrapper.transition(wrapper.key(i))).dencrementInbound();
            }
        }
    }
    
    protected void addSuffix(char[] word, int[] stateList, int prefixSize) {
        register.remove(stateList[prefixSize - 1]);
        
        for (int i = prefixSize - 1; i < word.length; i++) {
            stateList[i + 1] = newState(word[i]);
            setTransition(stateList[i], word[i], stateList[i + 1]);
        }
        
        register.remove(stateList[stateList.length - 1]);
        setFinal(stateList[stateList.length - 1]);        
    }
    
    protected int newState(char value) {
        Integer index = register.nextDiscarded();
        
        if (index == null) {
            index = add(new AutoState(value, false));
        } else {
            set(index, new AutoState(value, false));
        }
        
        return index;
    }
    
    protected void setFinal(int state) {
        states.get(state).setTerminal(true);
    }
    
    protected void setTransition(int state, char value, int target) {
        int old = states.get(state).transition(value);
        
        if (old != -1) {
            decrementInbounds(old);
        }
        
        incrementInbounds(target);
        states.get(state).setTransition(value, target);
    }
    
    private void incrementInbounds(int state) {
        states.get(state).incrementInbound();
    }
    
    private void decrementInbounds(int state) {
        states.get(state).dencrementInbound();
    }
    
    public int add(AutoState wrapper) {
        int index = states.size();
        
        states.add(wrapper);
        
        return index;
    }
    
    public void set(int state, AutoState wrapper) {
        states.set(state, wrapper);
    }
    
    public int findConfluence(int[] stateList) {        
        for (int i = 0; i < stateList.length; i++) {
            if (states.get(stateList[i]).inboundCount() > 1) {
                return i;
            }
        }
        
        return -1;
    }
    
    public void save(OutputStream stream, boolean editable) throws IOException {
        DataOutputStream output = new DataOutputStream(stream);
        output.writeBoolean(editable);
        output.writeInt(stateCount());
        
        for (AutoState wrapper : states) {            
            output.writeBoolean(wrapper.isTerminal());
            output.writeShort(wrapper.transitionCount());
            
            if (editable) {
                output.writeShort(wrapper.inboundCount());
                output.writeChar(wrapper.value());
            }

            for (int i = 0; i < wrapper.transitionCount(); i++) {
                output.writeChar(wrapper.key(i));
                output.writeInt(wrapper.transition(wrapper.key(i)));
            }
        }

        output.flush();
    }
    
    public int discardedCount() {
        return register.discardedStatesCount();
    }
    
    public static Automaton load(InputStream stream) throws IOException {        
        DataInputStream input = new DataInputStream(stream);
        List<AutoState> states = new ArrayList<>();
        Register register = new Register();
        
        boolean editable = input.readBoolean();
        
        if (!editable) {
            throw new IOException("Trying to load Automaton from read-only binary format");
        }
        
        int expected = input.readInt();
        
        while (states.size() < expected) {
            int index = states.size();
            boolean isTerminal = input.readBoolean();
            short outCount = input.readShort();
            short inCount = input.readShort();
            char value = input.readChar();            
            
            char[] keyArray = new char[outCount];
            int[] transitionArray = new int[outCount];            
            
            while (outCount > 0) {
                keyArray[keyArray.length - outCount] = input.readChar();               
                transitionArray[transitionArray.length - outCount] = input.readInt();
                outCount--;
            }
            
            AutoState state = new AutoState(value, isTerminal, inCount, keyArray, transitionArray);
            
            states.add(state);
            register.get(index, state);
            
            if (index > 0 && inCount == 0) {
                register.discard(index);
            }
        }
        
        return new Automaton(states, register);
    }
    
}
