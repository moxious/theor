package org.rcl.theor;

import java.util.Iterator;

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

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void reset() { cur = -1; } 
	public int size() { return ring.length; } 
	public T get(int idx) { return ring[idx]; } 	
}
