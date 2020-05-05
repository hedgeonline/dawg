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
 * @author hedge
 */
public class DictState {
    protected boolean terminal;
    protected char[] keyArray;
    protected int[] transitionArray;
    
    public DictState(boolean terminal) {
        this.terminal = terminal;
        this.keyArray = new char[0];
        this.transitionArray = new int[0];
    }
    
    public DictState(boolean terminal, char[] keyArray, int[] transitionArray) {
        this.terminal = terminal;
        this.keyArray = keyArray;
        this.transitionArray = transitionArray;
    }
    
    public boolean isTerminal() {
        return terminal;
    }
    
    public int transition(char key) {
        int index = Arrays.binarySearch(keyArray, key);
        
        if (index < 0) {
            return -1;
        } else {
            return transitionArray[index];
        }
    }
    
    public char key(int index) {
        return keyArray[index];
    }
    
    public int transitionCount() {
        return keyArray.length;
    }
    
}
