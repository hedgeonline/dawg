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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author hedge
 */
public class IntegrationTest {
    
    Automaton dict = new Automaton();
    
    // For this string array total state count should equal 23 with 3 discarded state
    protected static String[] testArray = {        
        "abcxyz",
        "abc0xyz",        
        "abcxyz0",
        "0abcxyz",
        "0abcxyz0",
        "0abc0xyz0",
        "abcxyz"
    };
    
    protected static Set<String> testSet = new HashSet<>(Arrays.asList(testArray));
    
    @Before
    public void initDictionary() {
        for (String value : testArray) {
            dict.add(value);
        }
    }
    
    @Test
    public void testOverallStateCount() {
        Assert.assertEquals(23, dict.stateCount());
        Assert.assertEquals(3, dict.discardedCount());
    }
    
    @Test
    public void testDictContent() {
        List<String> result = dict.listSuffixes("");
        
        // Testing that iterator does not return duplicate entries
        Assert.assertEquals(testSet.size(), result.size());
        
        // Testing that unique set of test array equals the unique set of dictionary entries
        Assert.assertEquals(testSet, new HashSet<>(result));
    }
    
    @Test
    public void testInputOutput() throws IOException {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        
        // Saving full format with information for future editing
        dict.save(baos1, true);
        
        // Saving read-only format which requires less space
        dict.save(baos2, false);
        
        ByteArrayInputStream bais1 = new ByteArrayInputStream(baos1.toByteArray());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(baos2.toByteArray());
        
        Dictionary dict1 = Dictionary.load(bais1);
        Dictionary dict2 = Dictionary.load(bais2);
        
        bais1.reset();
        Automaton auto = Automaton.load(bais1);
        
        List<String> result1 = dict1.listSuffixes("");
        List<String> result2 = dict2.listSuffixes("");
        List<String> result3 = auto.listSuffixes("");
        
        Assert.assertEquals(testSet, new HashSet<>(result1));
        Assert.assertEquals(testSet, new HashSet<>(result2));   
        Assert.assertEquals(testSet, new HashSet<>(result3)); 
        Assert.assertEquals(23, dict1.stateCount());
        Assert.assertEquals(23, dict2.stateCount());
        Assert.assertEquals(23, auto.stateCount());
        Assert.assertEquals(3, auto.discardedCount());
    }
}
