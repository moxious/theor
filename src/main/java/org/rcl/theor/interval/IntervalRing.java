package org.rcl.theor.interval;

import org.rcl.theor.Ring;

/**
 * A ring of interval objects.   Interval rings are good for continuosly applying the same set of intervals to a sequence of
 * notes indefinitely far.  For example, consider a whole tone scale as the Interval Ring of [Interval.WHOLE_STEP] continuously applied
 * to notes.
 * @author moxious
 *
 */
public class IntervalRing extends Ring<Interval> {
	public IntervalRing(Interval[] ivs) {
		super(ivs);
	}

	/** Shift the ring; that is, move everything over one position */
	public IntervalRing shift() {
		Interval[] ivs = new Interval[ring.length];

		for (int x = 0; x < (ring.length - 1); x++) {
			ivs[x] = ring[x + 1];
		}
		ivs[ring.length - 1] = ring[0];

		return new IntervalRing(ivs);
	}

	public Interval[] getRing() {
		return ring;
	}

	public String toString() {
		StringBuffer b = new StringBuffer("<IntervalRing: ");

		for (Interval i : ring) {
			b.append(i);
		}

		b.append(">");
		return b.toString();
	}

	public boolean isCyclical() {
		int total = 0;

		for (Interval i : ring) {
			total += i.intValue();
		}

		return total % 12 == 0;
	}

	public static void main(String[] args) throws Exception {
		IntervalRing ir = new IntervalRing(Interval.parse("w w h w w w h"));
		System.out.println(ir);
		System.out.println(ir.shift());
		System.out.println(ir.shift().shift());
	}
}
