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

import java.util.Arrays;

/**
 *
 * @author Андрей
 */
public class AutoState extends DictState {
    private final char value;
    private short inboundCount;
    
    public AutoState(char value, boolean terminal) {
        super(terminal);
        this.value = value;
        this.inboundCount = 0;        
    }
    
    public AutoState(char value, boolean terminal, short inboundCount, char[] keyArray, int[] transitionArray) {
        super(terminal, keyArray, transitionArray);
        this.value = value;
        this.inboundCount = inboundCount;        
    }
    
    public char value() {
        return value;
    }
    
    public void setTerminal(boolean value) {
        terminal = value;
    }
    
    public void incrementInbound() {
        inboundCount++;
    }
    
    public void dencrementInbound() {
        inboundCount--;
    }

    public short inboundCount() {
        return inboundCount;
    }
    
    public void setTransition(char key, int target) {
        if (target > -1) {
            int index = Arrays.binarySearch(keyArray, key);

            if (index < 0) {
                index = -index - 1;
                keyArray = insert(keyArray, index, key);
                transitionArray = insert(transitionArray, index, target);
            } else {
                transitionArray[index] = target;
            }
        }
    }
    
    public static int[] insert(int[] source, int index, int value) {
        int[] target = new int[source.length + 1];
        fillArray(source, target, source.length, index); 
        target[index] = value;        
        return target;
    }
    
    public static char[] insert(char[] source, int index, char value) {
        char[] target = new char[source.length + 1];          
        fillArray(source, target, source.length, index);        
        target[index] = value;
        return target;
    }
    
    private static void fillArray(Object source, Object target, int length, int index) {
        if (index == 0) {
            System.arraycopy(source, 0, target, 1, length);
        } else if (index == length) {
            System.arraycopy(source, 0, target, 0, length);          
        } else {
            System.arraycopy(source, 0, target, 0, index);
            System.arraycopy(source, index, target, index + 1, length - index);
        }
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(keyArray) ^ Arrays.hashCode(transitionArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AutoState other = (AutoState) obj;
        if (this.value != other.value) {
            return false;
        }
        if (this.terminal != other.terminal) {
            return false;
        }

        return Arrays.equals(this.keyArray, other.keyArray) && Arrays.equals(this.transitionArray, other.transitionArray);
    }
    
}
