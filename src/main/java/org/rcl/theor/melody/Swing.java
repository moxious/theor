package org.rcl.theor.melody;

import org.rcl.theor.composer.ProgressionComposer;

public class Swing {
	int swing = -1;
	double howOftenModify;

	public Swing(double howOftenModify) {
		this.howOftenModify = howOftenModify;
		modifySwing();
	}

	protected int modifySwing() {
		if (swing == -1)
			swing = (Math.abs(ProgressionComposer.r.nextInt()) % 3) + 2;
		else
			swing = ProgressionComposer.randomBetween(1, 3);

		return swing;
	} // End modifySwing

	public void modify() {
		if (ProgressionComposer.r.nextDouble() <= (1 - howOftenModify))
			modifySwing();
	}

	public int getSwing() {
		return swing;
	}

	public String toString() {
		return "" + swing;
	}
}
