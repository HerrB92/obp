/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package obt.index;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for circular queue class
 * 
 * @author Björn Behrens
 */
public class TestCircularQueue {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#CircularQueue(int)}.
	 */
	@Test
	public final void testCircularQueue() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		assertNotNull(queue);
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#getMaxSize()}.
	 */
	@Test
	public final void testGetMaxSize() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		assertEquals(3, queue.getMaxSize());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#addAll(java.util.Collection)}.
	 */
	@Test
	public final void testAddAll() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		
		ArrayList<String> elements = new ArrayList<String>();
		elements.add("Test1");
		elements.add("Test2");
		elements.add("Test3");
		elements.add("Test4");
		
		queue.addAll(elements);
		
		assertEquals(3, queue.size());
		assertEquals("Test4", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#clear()}.
	 */
	@Test
	public final void testClear() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test");
		assertEquals(1, queue.size());
		
		queue.clear();
		
		assertEquals(0, queue.size());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#contains(java.lang.Object)}.
	 */
	@Test
	public final void testContains() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test");
		
		assertTrue(queue.contains("Test"));
		assertFalse(queue.contains("Test1"));
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#containsAll(java.util.Collection)}.
	 */
	@Test
	public final void testContainsAll() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		queue.add("Test3");
		
		ArrayList<String> elements = new ArrayList<String>();
		elements.add("Test1");
		elements.add("Test2");
		elements.add("Test3");
		
		assertTrue(queue.containsAll(elements));
		
		elements = new ArrayList<String>();
		elements.add("Test1");
		elements.add("Test2");
		elements.add("Test4");
		
		assertFalse(queue.containsAll(elements));
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#isEmpty()}.
	 */
	@Test
	public final void testIsEmpty() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		assertTrue(queue.isEmpty());
		
		queue.add("Test");
		
		assertFalse(queue.isEmpty());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#iterator()}.
	 */
	@Test
	public final void testIterator() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		queue.add("Test3");
		
		Iterator<String> iterator = queue.iterator();
		
		assertNotNull(iterator);
		
		String element;
		for (int i = 2; i >= 0; i--) {
			element = iterator.next();
			assertEquals("Test" + (i + 1), element);
		}
		
		assertFalse(iterator.hasNext());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#remove(java.lang.Object)}.
	 */
	@Test
	public final void testRemoveObject() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		queue.add("Test3");
		
		assertEquals(3, queue.size());
		assertEquals("Test3", queue.peek());
		
		queue.remove("Test3");
		
		assertEquals(2, queue.size());
		assertEquals("Test2", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#removeAll(java.util.Collection)}.
	 */
	@Test
	public final void testRemoveAll() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		queue.add("Test3");
		
		assertEquals(3, queue.size());
		assertEquals("Test3", queue.peek());
		
		ArrayList<String> elements = new ArrayList<String>();
		elements.add("Test2");
		elements.add("Test3");
		
		queue.removeAll(elements);
		
		assertEquals(1, queue.size());
		assertEquals("Test1", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#retainAll(java.util.Collection)}.
	 */
	@Test
	public final void testRetainAll() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		queue.add("Test3");
		
		assertEquals(3, queue.size());
		assertEquals("Test3", queue.peek());
		
		ArrayList<String> elements = new ArrayList<String>();
		elements.add("Test2");
		elements.add("Test3");
		
		queue.retainAll(elements);
		
		assertEquals(2, queue.size());
		assertEquals("Test3", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#size()}.
	 */
	@Test
	public final void testSize() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		assertEquals(0, queue.size());
		
		queue.add("Test1");
		assertEquals(1, queue.size());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#toArray()}.
	 */
	@Test
	public final void testToArray() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		
		Object[] elements = queue.toArray();
		
		assertEquals(2, elements.length);
		
		Object element;
		for (int i = 0; i < 2; i++) {
			element = elements[i];
			
			assertTrue(element instanceof String);
			
			switch (i) {
			case 0:
				assertEquals("Test2", element.toString());
				break;
			case 1:
				assertEquals("Test1", element.toString());
				break;
			}
		}
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#toArray(T[])}.
	 */
	@Test
	public final void testToArrayTArray() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		
		String[] elements = new String[1];
		
		elements = queue.toArray(elements);
		
		assertEquals(2, elements.length);
		
		Object element;
		for (int i = 0; i < 2; i++) {
			element = elements[i];
			
			assertTrue(element instanceof String);
			
			switch (i) {
			case 0:
				assertEquals("Test2", element.toString());
				break;
			case 1:
				assertEquals("Test1", element.toString());
				break;
			}
		}
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#add(java.lang.Object)}.
	 */
	@Test
	public final void testAdd() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		assertEquals(0, queue.size());
		
		queue.add("Test1");
		assertEquals(1, queue.size());
		assertEquals("Test1", queue.peek());
		
		queue.add("Test2");
		assertEquals(2, queue.size());
		assertEquals("Test2", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#element()}.
	 */
	@Test
	public final void testElement() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		
		queue.add("Test1");
		assertEquals(1, queue.size());
		assertEquals("Test1", queue.element());
		
		queue.add("Test2");
		assertEquals(2, queue.size());
		assertEquals("Test2", queue.element());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#offer(java.lang.Object)}.
	 */
	@Test
	public final void testOffer() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		
		queue.offer("Test1");
		queue.offer("Test2");
		queue.offer("Test3");
		queue.offer("Test4");
		queue.offer("Test5");
		
		assertEquals(3, queue.size());
		assertEquals("Test5", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#peek()}.
	 */
	@Test
	public final void testPeek() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		
		queue.add("Test1");
		assertEquals(1, queue.size());
		assertEquals("Test1", queue.peek());
		
		queue.add("Test2");
		assertEquals(2, queue.size());
		assertEquals("Test2", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#poll()}.
	 */
	@Test
	public final void testPoll() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		
		assertEquals(2, queue.size());
		assertEquals("Test2", queue.poll());
		assertEquals(1, queue.size());
		assertEquals("Test1", queue.peek());
	}

	/**
	 * Test method for {@link obt.index.CircularQueue#remove()}.
	 */
	@Test
	public final void testRemove() {
		CircularQueue<String> queue = new CircularQueue<String>(3);
		queue.add("Test1");
		queue.add("Test2");
		
		assertEquals(2, queue.size());
		assertEquals("Test2", queue.remove());
		assertEquals(1, queue.size());
		assertEquals("Test1", queue.peek());
	}

}
