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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author hedge
 */
public class InsertArrayTest {
    @Test
    public void testingArrayInsertion() {
        int[] source0 = new int[] { };
        int[] source1 = new int[] { 1, 2, 3, 5, 6, 7 };
        int[] target0 = new int[] { 0 };
        int[] target1 = new int[] { 0, 1, 2, 3, 5, 6, 7 };
        int[] target2 = new int[] { 1, 2, 3, 5, 6, 7, 8 };
        int[] target3 = new int[] { 1, 2, 3, 4, 5, 6, 7 };
        
        Assert.assertArrayEquals(target0, AutoState.insert(source0, 0, 0));
        Assert.assertArrayEquals(target1, AutoState.insert(source1, 0, 0));
        Assert.assertArrayEquals(target2, AutoState.insert(source1, 6, 8));
        Assert.assertArrayEquals(target3, AutoState.insert(source1, 3, 4));
    }
}
