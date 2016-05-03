package automata;

public class Position {

	private int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			Position pos = (Position) obj;
			if (pos.x == x && pos.y == y) {
				return true;
			}
			return false;
		}
		return false;
	}

}
