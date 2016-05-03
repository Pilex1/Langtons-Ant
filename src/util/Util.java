package util;

import java.util.Random;

public final class Util {

	/**
	 * generates a random integer between min and max inclusive
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	/**
	 * generates a random double between min and max inclusive
	 * @param min
	 * @param max
	 * @return
	 */
	public static double randDouble(double min, double max) {
		return new Random().nextDouble() * (max - min) + min;
	}

	/**
	 * generates a random float between min and max inclusive
	 * @param min
	 * @param max
	 * @return
	 */
	public static float randFloat(float min, float max) {
		return new Random().nextFloat() * (max - min) + min;
	}

}
