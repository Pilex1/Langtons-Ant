package automata;

import static automata.Direction.EAST;
import static automata.Direction.LEFT;
import static automata.Direction.NORTH;
import static automata.Direction.RIGHT;
import static automata.Direction.SOUTH;
import static automata.Direction.WEST;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import util.Util;

public class LangtonsAnt extends JPanel {

	private static final long serialVersionUID = 1100L;

	private CustomHashMap pixelsHm = new CustomHashMap();

	/**
	 * size of each pixel the ant covers
	 */
	private int minPixelSize;

	public void setMinPixelSize(int minPixelSize) {
		clear();
		this.minPixelSize = minPixelSize;
	}

	/**
	 * storage for the colour data of all the grid squares in the world
	 */
	// private Color[][] pixels;
	/**
	 * number of columns in world
	 */
	private int gridx;

	public int getGridx() {
		return gridx;
	}

	/**
	 * number of rows in world
	 */
	private int gridy;

	public int getGridy() {
		return gridy;
	}

	/**
	 * storage of colours for the different states of the ant; is linearly
	 * interpolated from <code>clra</code> to <code>clrb</code>.
	 */
	private Color[] colours;
	private Color clra = Color.WHITE, clrb = Color.BLACK;

	/**
	 * storage of ants being displayed on screen
	 */
	private ArrayList<Ant> ants = new ArrayList<>();
	/**
	 * buffer for ants being spawned by the user, which will then be added to
	 * <code>ants</code> once it is not being used
	 */
	private ArrayList<Ant> bufferAnts = new ArrayList<>();

	/**
	 * speed of the animation; a value of -1 means unlimited
	 */
	private int speed;
	/**
	 * the number of cycles the animation has gone past by
	 */
	private long cycles;

	public synchronized long getCycles() {
		return cycles;
	}

	/**
	 * true to indicate that ants at the edge of the world should be looped back
	 * to the other side; false if they should be destroyed instead
	 */
	private boolean loopEdgeWorld = true;

	public boolean isLoopEdgeWorld() {
		return loopEdgeWorld;
	}

	public void setLoopEdgeWorld(boolean loopEdgeWorld) {
		this.loopEdgeWorld = loopEdgeWorld;
	}

	private static final transient int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getScreenDevices()[0].getDisplayMode().getRefreshRate();

	private AnimationThread animationThread;

	private Move[] rule;
	private boolean gap;

	private boolean runAnimation = true;

	/**
	 * BufferedImage for double buffering
	 */
	private BufferedImage buffImg;

	public LangtonsAnt(int pixelSize, int speed, boolean gap) {
		super(false);
		setBorder(null);
		this.minPixelSize = pixelSize;
		this.speed = speed;
		this.gap = gap;
		setLayout(new MigLayout("", "[]", "[]"));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX() / LangtonsAnt.this.minPixelSize, y = e.getY() / LangtonsAnt.this.minPixelSize;
				addAnt(x, y);
			}
		});

	}

	/**
	 * gets called after the main frame is packed, and all the sizes have been
	 * calculated essentially, at this point, getWidth() and getHeight() can be
	 * properly called, and so the actual grid is initialized here
	 */
	public void initialize() {
		resetRule();
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(getWidth(), getHeight()));

		for (int i = -gridx / 2; i < gridx - gridx / 2; i++) {
			for (int j = -gridy / 2; j < gridy - gridy / 2; j++) {
				pixelsHm.put(new Position(i, j), clra);
			}
		}
		updateGridSize();

		buffImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = buffImg.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, buffImg.getWidth(), buffImg.getHeight());
		colours = new Color[rule.length];
		int r = clrb.getRed() - clra.getRed();
		int g = clrb.getGreen() - clra.getGreen();
		int b = clrb.getBlue() - clra.getBlue();
		for (int i = 0; i < colours.length; i++) {
			double perc = (double) i / (colours.length - 1);
			int nr = (int) (clra.getRed() + perc * r);
			int ng = (int) (clra.getGreen() + perc * g);
			int nb = (int) (clra.getBlue() + perc * b);
			colours[i] = new Color(nr, ng, nb);
		}
		animationThread = new AnimationThread();
		animationThread.start();
	}

	@Override
	public synchronized void paintComponent(Graphics g) {
		// draws pixels to the offscreen BufferedImage buffImg
		if (buffImg != null) {
			Graphics buffGraphics = buffImg.getGraphics();
			// synchronized (pixels) {
			// for (int i = 0; i < pixels.length; i++) {
			// for (int j = 0; j < pixels[0].length; j++) {
			// buffGraphics.setColor(pixels[i][j]);
			// buffGraphics.fillRect(i * pixelSize, j * pixelSize, pixelSize -
			// (gap ? 1 : 0),
			// pixelSize - (gap ? 1 : 0));
			// }
			// }
			// }

			int pxx = (int) Math.ceil(((double) getWidth()) / pixelsHm.getTotalx());
			int pxy = (int) Math.ceil(((double) getHeight()) / pixelsHm.getTotaly());

			synchronized (pixelsHm) {
				for (int i = -pixelsHm.getDistOriginx(); i <= pixelsHm.getDistOriginx(); i++) {
					for (int j = -pixelsHm.getDistOriginy(); j <= pixelsHm.getDistOriginy(); j++) {
						Color colour = pixelsHm.get(i, j);
						buffGraphics.setColor(colour);
						buffGraphics.fillRect(getWidth()/2+getWidth() * (i - pixelsHm.getMinx()) / (pixelsHm.getDistOriginx()*2),
								getHeight()/2+getHeight() * (j - pixelsHm.getMiny()) / (pixelsHm.getDistOriginy()*2), pxx, pxy);
					}
				}
			}

			synchronized (ants) {
				for (Ant ant : ants) {
					buffGraphics.setColor(Color.RED);
					buffGraphics.fillRect(gridx * (ant.pos.getX() - pixelsHm.getMinx()) / pixelsHm.getTotalx(),
							gridy * (ant.pos.getY() - pixelsHm.getMiny()), pxx, pxy);
				}
			}

		}
		// everything has been drawn to buffImg; now it is copied to the display
		super.paintComponent(g);
		g.drawImage(buffImg, 0, 0, this);

	}

	private void updateGridSize() {
		gridx = pixelsHm.getTotalx();
		gridy = pixelsHm.getTotaly();
	}

	/**
	 * adds an ant to the specified grid x and y coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public synchronized void addAnt(int x, int y) {
		bufferAnts.add(new Ant(x, y));
	}

	private Color nextColour(int i) {
		if (i == colours.length - 1) {
			return colours[0];
		}
		return colours[i + 1];
	}

	private int getIndexOfColour(Color colour) {
		for (int i = 0; i < colours.length; i++) {
			if (colours[i].equals(colour)) {
				return i;
			}
		}
		// shouldn't get to this stage if all is working properly
		System.err.println(colour + " is not in array!");
		new Exception().printStackTrace();
		return -1;
	}

	public synchronized void pauseAnimation() {
		runAnimation = false;
	}

	public synchronized void resumeAnimation() {
		runAnimation = true;
	}

	public void setSpeed(int speed) {
		if (speed > 0 || speed == -1) {
			this.speed = speed;
		} else {
			// shouldn't get here unless you change the slider, or you manually
			// invoke the method with an invalid value
			System.err.println("Invalid speed: " + speed);
			new Exception().printStackTrace();
		}
	}

	/**
	 * clears the screen, resetting the ants, the display, and the cycles
	 * counter
	 */
	public void clear() {
		System.out.println("Clearing world");
		synchronized (ants) {
			ants.clear();
		}
		synchronized (bufferAnts) {
			bufferAnts.clear();
		}

		cycles = 0;

		synchronized (pixelsHm) {
			pixelsHm.clear();
		}
		Graphics graphics = buffImg.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, buffImg.getWidth(), buffImg.getHeight());
	}

	/**
	 * generates a random custom rule
	 * 
	 * @return the rule
	 */
	public static String genRandomRule(int minMoves, int maxMoves, int minSteps, int maxSteps) {
		if (minMoves < 2) {
			minMoves = 2;
			System.err.println("Too little moves: " + minMoves + ". Resetting to default value of 2.");
			new Exception().printStackTrace();
		}
		if (maxSteps < minSteps) {
			maxSteps = minSteps;
			System.err.printf(
					"Max steps must be greater than min steps! Max: %s, Min: %s. Setting maxSteps equal to minSteps.%n",
					maxMoves, minMoves);
			new Exception().printStackTrace();
		}
		if (minSteps < 1) {
			minSteps = 1;
			System.err.println("Too little steps: " + minSteps + ". Resetting to default value of 1");
			new Exception().printStackTrace();
		}
		StringBuilder rule = new StringBuilder();
		for (int i = 0; i < Util.randInt(minMoves, maxMoves); i++) {
			int randLetter = Util.randInt(0, 5);
			switch (randLetter) {
			case 0:
				rule.append('L');
				break;
			case 1:
				rule.append('R');
				break;
			case 2:
				rule.append('N');
				break;
			case 3:
				rule.append('E');
				break;
			case 4:
				rule.append('S');
				break;
			case 5:
				rule.append('W');
				break;

			default:
				System.err.println("Incorrect case: " + randLetter);
				new Exception().printStackTrace();
			}
			int steps = Util.randInt(minSteps, maxSteps);
			rule.append(steps == 1 ? "" : steps);
		}
		return rule.toString();
	}

	public void resetRule() {
		try {
			setRule("RL");
		} catch (RuleParsingException e) {
			System.err.println("SEVERE ERROR: DEFAULT RULE NOT VALID");
			e.printStackTrace();
		}
	}

	/**
	 * sets the custom rule for the ant (by default it is LR)
	 * 
	 * @param rule
	 * @throws RuleParsingException
	 *             if the rule contains characters other than L, R, N, E, S, W,
	 *             and the digits 0 - 9
	 */
	public void setRule(String rule) throws RuleParsingException {
		// parsing for rule
		ArrayList<Move> newRuleAl = new ArrayList<>();
		for (int i = 0; i < rule.length(); i++) {
			String moveAmountStr = getMoveAmount(rule.substring(i + 1, rule.length()));
			if (moveAmountStr == null) {
				switch (rule.charAt(i)) {
				case 'R':
					newRuleAl.add(new Move(RIGHT));
					break;
				case 'L':
					newRuleAl.add(new Move(LEFT));
					break;
				case 'N':
					newRuleAl.add(new Move(NORTH));
					break;
				case 'E':
					newRuleAl.add(new Move(EAST));
					break;
				case 'S':
					newRuleAl.add(new Move(SOUTH));
					break;
				case 'W':
					newRuleAl.add(new Move(WEST));
					break;

				default:
					throw new RuleParsingException(rule);
				}
			} else {
				int moveAmount = Integer.parseInt(moveAmountStr);
				switch (rule.charAt(i)) {
				case 'R':
					newRuleAl.add(new Move(RIGHT, moveAmount));
					break;
				case 'L':
					newRuleAl.add(new Move(LEFT, moveAmount));
					break;
				case 'N':
					newRuleAl.add(new Move(NORTH, moveAmount));
					break;
				case 'E':
					newRuleAl.add(new Move(EAST, moveAmount));
					break;
				case 'S':
					newRuleAl.add(new Move(SOUTH, moveAmount));
					break;
				case 'W':
					newRuleAl.add(new Move(WEST, moveAmount));
					break;

				default:
					throw new RuleParsingException(rule);
				}
				i += moveAmountStr.length();
			}
		}
		if (newRuleAl.size() <= 1) {
			throw new RuleParsingException(rule);
		}
		this.rule = newRuleAl.toArray(new Move[0]);

		// recalculating colours
		colours = new Color[this.rule.length];
		int r = clrb.getRed() - clra.getRed();
		int g = clrb.getGreen() - clra.getGreen();
		int b = clrb.getBlue() - clra.getBlue();
		for (int i = 0; i < colours.length; i++) {
			double perc = (double) i / (colours.length - 1);
			int nr = (int) (clra.getRed() + perc * r);
			int ng = (int) (clra.getGreen() + perc * g);
			int nb = (int) (clra.getBlue() + perc * b);
			colours[i] = new Color(nr, ng, nb);
		}
	}

	private String getMoveAmount(String string) {
		StringBuilder number = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				number.append(string.charAt(i));
			} else {
				break;
			}
		}
		if (number.length() == 0) {
			return null;
		}
		return number.toString();
	}

	public void nextCycle() {
		if (ants.isEmpty() && !bufferAnts.isEmpty()) {
			cycles = 0;
		}
		if (!runAnimation) {
			calculateAnts();
		}
	}

	public void removeLastAnt() {
		if (!ants.isEmpty()) {
			synchronized (ants) {
				ants.remove(ants.size() - 1);
			}
		}
	}

	public void removeAllAnts() {
		synchronized (ants) {
			ants.clear();
		}
	}

	private void calculateAnts() {
		if (ants.isEmpty()) {
			return;
		}
		// System.out.println(2);
		for (Iterator<Ant> iterator = ants.iterator(); iterator.hasNext();) {
			Ant ant = iterator.next();
			ant.move();

		}
		cycles++;
	}

	/**
	 * 
	 * Encapsulates an instance of Langton's Ant, including its current position
	 * and movement, as well as methods for moving
	 *
	 */
	private class Ant {

		private Position pos;
		private Move move = new Move(NORTH);

		private Ant(int x, int y) {
			pos = new Position(x, y);
		}

		/**
		 * moves the ant by its current move
		 * 
		 * 
		 */
		private void move() {
			// the current state of the ant determined by the colour of the
			// square he is on
			Color colour = pixelsHm.get(pos);
			int state = getIndexOfColour(colour==null?clra:colour);

			// the ant moves in a different direction depending on the current
			// state of the square he is on and the custom rule
			switch (rule[state].getDirection()) {
			case NORTH:
				move.setDirection(NORTH);
				break;
			case EAST:
				move.setDirection(EAST);
				break;
			case SOUTH:
				move.setDirection(SOUTH);
				break;
			case WEST:
				move.setDirection(WEST);
				break;
			case LEFT:
				move.turnLeft();
				break;
			case RIGHT:
				move.turnRight();
				break;
			}
			move.setAmount(rule[state].getAmount());

			Direction direction = move.getDirection();
			int amount = move.getAmount();
			int x = pos.getX(), y = pos.getY();
			switch (direction) {
			case NORTH:
				for (int i = 0; i < amount; i++) {
					// increases the state of the current square
					pixelsHm.put(pos, nextColour(state));
					y--;
					pos.setY(y);
					
				}

				break;
			case EAST:
				for (int i = 0; i < amount; i++) {
					// increases the state of the current square
					pixelsHm.put(pos, nextColour(state));
					x++;
					pos.setX(x);
				}

				break;
			case SOUTH:
				for (int i = 0; i < amount; i++) {
					// increases the state of the current square
					pixelsHm.put(pos, nextColour(state));
					y++;
				}

				break;
			case WEST:
				for (int i = 0; i < amount; i++) {
					// increases the state of the current square
					pixelsHm.put(pos, nextColour(state));
					x--;
					pos.setX(x);
				}

				break;
			default:
				// should not get here if all is well
				System.err.println("INVALID DIRECTION: " + direction);
				new Exception().printStackTrace();
				break;
			}
		}
	}

	private class AnimationThread extends Thread {

		/**
		 * manages the refreshing of the display
		 */
		@Override
		public void run() {
			new Thread() {
				@Override
				public void run() {
					while (true) {
						repaint();
						try {
							// display is refreshed per monitor refresh
							Thread.sleep((long) (1000D / REFRESH_RATE));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			/**
			 * manages the calculations for the ants
			 */
			while (true) {
				synchronized (ants) {
					if (ants.isEmpty() && !bufferAnts.isEmpty()) {
						cycles = 0;
					}
					ants.addAll(bufferAnts);
					bufferAnts.clear();
					if (runAnimation) {
						calculateAnts();
					}
				}
				try {
					if (cycles % speed == 0 && speed != -1 || ants.size() == 0) {
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
