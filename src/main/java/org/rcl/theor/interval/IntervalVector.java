package org.rcl.theor.interval;

import java.util.List;

import org.rcl.theor.note.PitchClassSet;

/**
 * An interval vector is an array that expresses the intervallic content of a pitch-class set. 
 * Some texts call these APIC vector (or absolute pitch-class interval vector).
 * 
 * @see http://en.wikipedia.org/wiki/Interval_vector
 * @author moxious
 */
public class IntervalVector {
	/** Number of times interval class 1 occurs (interval difference of 1 or 11) */
	public int ic1 = 0;
	/** Number of times interval class 2 occurs (interval difference of 2 or 10) */
	public int ic2 = 0;
	/** Number of times interval class 3 occurs (interval difference of 3 or 9) */
	public int ic3 = 0;
	/** Number of times interval class 4 occurs (interval difference of 4 or 8) */
	public int ic4 = 0;
	/** Number of times interval class 5 occurs (interval difference of 5 or 7) */
	public int ic5 = 0;
	/** Number of times interval class 6 occurs (interval difference of 6) */
	public int ic6 = 0;

	public IntervalVector(int ic1, int ic2, int ic3, int ic4, int ic5, int ic6) {
		this.ic1 = ic1;
		this.ic2 = ic2;
		this.ic3 = ic3;
		this.ic4 = ic4;
		this.ic5 = ic5;
		this.ic6 = ic6;
	}

	/**
	 * @return the number of interval sets that are empty, i.e. == 0 (between 0 and 6)
	 */
	public int countEmptyIntervalSets() {
		int x = 0;

		if (ic1 == 0)
			x++;
		if (ic2 == 0)
			x++;
		if (ic3 == 0)
			x++;
		if (ic4 == 0)
			x++;
		if (ic5 == 0)
			x++;
		if (ic6 == 0)
			x++;

		return x;
	}

	/**
	 * @return the number of interval sets that are not empty, i.e. > 0 (between 0 and 6)
	 */
	public int countNonEmptyIntervalSets() {
		return 6 - countEmptyIntervalSets();
	}

	/**
	 * @return the total number of all intervals in the vector, the sum of ic1 .. ic6.
	 */
	public int countIntervals() {
		return ic1 + ic2 + ic3 + ic4 + ic5 + ic6;
	}

	public static IntervalVector fromPitchClassSet(PitchClassSet pcs) {
		return fromPitchClassSet(pcs.getNaturalOrder());
	}

	public static IntervalVector fromPitchClassSet(List<Integer> pcs) {
		int[] ics = new int[] { 0, 0, 0, 0, 0, 0 };

		// Count the all-pairs interval content.
		for (int x = 0; x < pcs.size(); x++) {
			for (int y = (x + 1); y < pcs.size(); y++) {
				// Calculate the interval between two items
				int i = pcs.get(x) - pcs.get(y);
				int intervalClass = new Interval(i).getIntervalClass();

				// System.out.println("Between " + pcs.get(x) + " and " + pcs.get(y) + " is " + i + " => " + intervalClass); 

				// Seen this class one more time....
				// NOTE!  Interval class 1 is at index *0* in this array. So subtract one.
				ics[intervalClass - 1]++;
			}
		}

		return new IntervalVector(ics[0], ics[1], ics[2], ics[3], ics[4], ics[5]);
	}

	public boolean equals(IntervalVector other) {
		return other != null && ic1 == other.ic1 && ic2 == other.ic2 && ic3 == other.ic3 && ic4 == other.ic4
				&& ic5 == other.ic5 && ic6 == other.ic6;
	}

	public String toString() {
		return new String("<" + ic1 + " " + ic2 + " " + ic3 + " " + ic4 + " " + ic5 + " " + ic6 + ">");
	}
} // End IntervalVector
