package automata;

public class Move {

	private Direction direction;
	private int amount;

	public Move(Direction direction, int amount) {
		this.direction = direction;
		this.amount = amount;
	}
	
	public Move(Direction direction) {
		this(direction, 1);
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void turnLeft() {
		direction = direction.turnLeft();
	}
	
	public void turnRight() {
		direction = direction.turnRight();
	}

}
