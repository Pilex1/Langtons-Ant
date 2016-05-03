package automata;

import java.awt.Color;
import java.util.HashMap;

public class CustomHashMap extends HashMap<Position, Color> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1500L;
	private int minx = 0, maxx = 0;
	private int miny = 0, maxy = 0;

	@Override
	public Color put(Position key, Color value) {
		if (key.getX() < minx)
			minx = key.getX();
		if (key.getX() > maxx)
			maxx = key.getX();
		if (key.getY() < miny)
			miny = key.getY();
		if (key.getY() > maxy)
			maxy = key.getY();
		return super.put(key, value);
	}
	
	public Color get(int x, int y) {
		return super.get(new Position(x, y));
	}
	
	public int getMinx() {
		return minx;
	}

	public int getMaxx() {
		return maxx;
	}

	public int getMiny() {
		return miny;
	}

	public int getMaxy() {
		return maxy;
	}

	public int getTotalx() {
		return maxx - minx + 1;
	}

	public int getTotaly() {
		return maxy - miny + 1;
	}
	
	public int getDistOriginx() {
		return Math.max(Math.abs(minx), Math.abs(maxx));
	}
	
	public int getDistOriginy() {
		return Math.max(Math.abs(miny), Math.abs(maxy));
	}

}
