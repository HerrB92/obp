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
 * 
 */
public class CircularQueue<E> implements Queue<E> {
	private int maxSize;
	private LinkedList<E> store = new LinkedList<E>();

	public CircularQueue(final int maxSize) {
		setMaxSize(maxSize);
	} // Constructor

	private void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	} // setMaxSize

	public int getMaxSize() {
		return maxSize;
	} // getMaxSize

	@Override
	public boolean addAll(Collection<? extends E> elements) {
		for (E element: elements) {
			offer(element);
		}
		return true;
	} // addAll

	@Override
	public void clear() {
		store.clear();
	} // clear

	@Override
	public boolean contains(Object element) {
		return store.contains(element);
	} // contains

	@Override
	public boolean containsAll(Collection<?> elements) {
		return store.containsAll(elements);
	} // containsAll

	@Override
	public boolean isEmpty() {
		return store.isEmpty();
	} // isEmpty

	@Override
	public Iterator<E> iterator() {
		return store.iterator();
	} // iterator

	@Override
	public boolean remove(Object element) {
		return store.remove(element);
	} // remove

	@Override
	public boolean removeAll(Collection<?> elements) {
		return store.removeAll(elements);
	} // removeAll

	@Override
	public boolean retainAll(Collection<?> elements) {
		return store.retainAll(elements);
	} // retainAll

	@Override
	public int size() {
		return store.size();
	} // size

	@Override
	public Object[] toArray() {
		return store.toArray();
	} // toArray

	@Override
	public <T> T[] toArray(T[] elements) {
		return store.toArray(elements);
	} // toArray

	/* 
	 * The add method of the CircularQueue class acts the
	 * same way as the offer method. As the last element of 
	 * the queue is removed, if the queue is full, adding an
	 * element always works and returns true.
	 * 
	 * (non-Javadoc)
	 * @see java.util.Queue#add(java.lang.Object)
	 */
	@Override
	public boolean add(E element) {
		return offer(element);
	} // add

	@Override
	public E element() {
		return store.element();
	} // element

	@Override
	public boolean offer(E element) {
		if (store.size() == maxSize) {
			// Store is full, remove last element
			store.removeLast();
		}
		
	    store.addFirst(element);
	    return true;
	} // offer

	@Override
	public E peek() {
		return store.peek();
	} // peek

	@Override
	public E poll() {
		return store.poll();
	} // poll

	@Override
	public E remove() {
		return store.remove();
	} // remove
}
