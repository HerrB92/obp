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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The class provides a FIFO queue of limited size, where the oldest element is
 * removed, if a new element is added and the queue is full.
 * 
 * Basic idea by DwB (2011). DwB (2011) Answer to 'Size-limited queue that holds
 * last N elements in Java' stockoverflow.com, 12.04.2011 [Online] Available at:
 * http://stackoverflow.com/a/5637726 (Accessed: 23.11.2013)
 * 
 * @author Björn Behrens
 * @version 1.0
 * @since 1.0
 */
public class CircularQueue<E> implements Queue<E> {
	// Maximum queue size
	private int maxSize;
	// Queue store
	private LinkedList<E> store = new LinkedList<E>();
	
	/**
	 * @param maxSize
	 */
	public CircularQueue(final int maxSize) {
		setMaxSize(maxSize);
	} // Constructor

	/**
	 * @param maxSize
	 */
	private void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	} // setMaxSize

	/**
	 * @return
	 */
	public int getMaxSize() {
		return maxSize;
	} // getMaxSize

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> elements) {
		for (E element: elements) {
			offer(element);
		}
		return true;
	} // addAll

	/**
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		store.clear();
	} // clear

	/**
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object element) {
		return store.contains(element);
	} // contains

	/**
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> elements) {
		return store.containsAll(elements);
	} // containsAll

	/**
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return store.isEmpty();
	} // isEmpty

	/**
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return store.iterator();
	} // iterator

	/**
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object element) {
		return store.remove(element);
	} // remove

	/**
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> elements) {
		return store.removeAll(elements);
	} // removeAll

	/**
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> elements) {
		return store.retainAll(elements);
	} // retainAll

	/**
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return store.size();
	} // size

	/**
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return store.toArray();
	} // toArray

	/**
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] elements) {
		return store.toArray(elements);
	} // toArray

	/** 
	 * The add method of the CircularQueue class acts the
	 * same way as the offer method. As the last element of 
	 * the queue is removed, if the queue is full, adding an
	 * element always works and returns true.
	 * 
	 * @see java.util.Queue#add(java.lang.Object)
	 */
	@Override
	public boolean add(E element) {
		return offer(element);
	} // add

	/**
	 * @see java.util.Queue#element()
	 */
	@Override
	public E element() {
		return store.element();
	} // element

	/**
	 * @see java.util.Queue#offer(java.lang.Object)
	 */
	@Override
	public boolean offer(E element) {
		if (store.size() == maxSize) {
			// Store is full, remove last element
			store.removeLast();
		}
		
	    store.addFirst(element);
	    return true;
	} // offer

	/**
	 * @see java.util.Queue#peek()
	 */
	@Override
	public E peek() {
		return store.peek();
	} // peek

	/**
	 * @see java.util.Queue#poll()
	 */
	@Override
	public E poll() {
		return store.poll();
	} // poll

	/**
	 * @see java.util.Queue#remove()
	 */
	@Override
	public E remove() {
		return store.remove();
	} // remove
}
