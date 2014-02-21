/*
 *  Copyright 2010 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.bindings.core.jdk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BooleanStringBindingTest {

    private static final BooleanStringBinding BINDING = new BooleanStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(Boolean.TRUE.toString(), BINDING.unmarshal("true").toString());
        assertEquals(Boolean.FALSE.toString(), BINDING.unmarshal("false").toString());        
    }
    
    @Test
    public void testMarshal() {

        assertEquals("true", BINDING.marshal(Boolean.TRUE));
        assertEquals("false", BINDING.marshal(Boolean.FALSE));
    }
}
