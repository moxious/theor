package org.rcl.theor.composer.chooser;

import org.rcl.theor.composer.DataFeed;

public abstract class Chooser {
	public abstract Object generateConstraint(DataFeed f);
}
