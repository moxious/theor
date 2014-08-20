package org.rcl.theor;

import java.util.Iterator;

/**
 * A ring is just an array of items, that can be iterated continuously as a ring.  For example, if you have the items
 * [1, 2, 3] you can iterate indefinitely; once you reach 3, the next item will be 1.   Rings are by definition never 
 * exhausted.
 * @author moxious
 *
 * @param <T> the type of the ring
 */
public class Ring<T> implements Iterator<T> {
	protected T[] ring;
	protected int cur=-1;
	
	public Ring(T[]ring) { 
		this.ring = ring;
		cur=-1;
	}
	
	public boolean hasNext() {
		return ring != null && ring.length > 0;
	}

	public T next() {
		if(!hasNext()) return null;
		cur++; 		
		if(cur >= ring.length) cur = 0;	
		//System.out.println("Returning ringpos " + cur + " => " + ring[cur]); 
		return ring[cur];
	}

	/** Always throws UnsupportedException */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void reset() { cur = -1; } 
	public int size() { return ring.length; } 
	public T get(int idx) { return ring[idx]; } 	
}
