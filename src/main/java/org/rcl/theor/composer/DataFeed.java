package org.rcl.theor.composer;


public class DataFeed {
	byte[]data;
	int ptr;
	int max;
	
	public DataFeed(byte [] data) { 
		this.data = data;
		ptr=0;
		max=data.length;
	}
	
	protected void retreat() { 
		ptr--;		
	}
	
	protected void advance() { 
		if(ptr < max) ptr++;
	}
	
	public void scanTo(int idx) { 
		if(idx < 0) idx = -1;
		if(idx >= max) idx = max;
		ptr = idx;
	}
	
	public byte get() { 
		if(ptr >= 0 && ptr < max) return data[ptr];
		//System.out.println("byte over/underrun");
		return (byte)0;
	}
	
	public byte[] nextN(int n) { 
		byte[]results = new byte[n];
		for(int x=0; x<n; x++) results[x] = next();
		return results;
	}

	public byte next() { 
		advance();
		return get();
	}
	
	public byte previous() { 
		retreat();
		return get();
	}
}
