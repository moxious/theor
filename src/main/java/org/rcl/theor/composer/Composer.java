package org.rcl.theor.composer;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.rcl.theor.TheorException;

public abstract class Composer {
	public static Random r = null;

	public static void init(DataFeed df) throws TheorException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] shaHash = digest.digest(df.data);
			long seed = ByteBuffer.wrap(shaHash).getLong();
			r = new Random(seed);
		} catch (NoSuchAlgorithmException e) {
			throw new TheorException("Failed to init", e);
		}
	}

	public abstract Ditty compose(DataFeed df) throws TheorException;
}
