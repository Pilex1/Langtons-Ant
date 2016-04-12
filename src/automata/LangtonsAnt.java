package automata;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class LangtonsAnt extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * size of each pixel the ant covers
	 */
	private int size;

	public void setPixelSize(int size) {
		clear();
		pausePainting();
		this.size = size;
		pixels = new Color[WIDTH / size][HEIGHT / size];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				pixels[i][j] = clra;
			}
		}
		resumePainting();
	}

	private Color[][] pixels;

	private Color[] colours; // storage of colours for the different states of
								// the ant; is linearly interpolated from clra
								// to clrb
	private Color clra = Color.WHITE, clrb = Color.BLACK;

	private ArrayList<Ant> ants = new ArrayList<>();
	private ArrayList<Ant> bufferAnts = new ArrayList<>();

	private int fps;
	private static final int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]
			.getDisplayMode().getRefreshRate();
	private long cycles;

	public long getCycles() {
		return cycles;
	}

	private AnimationThread animationThread;

	private String rule = "RL";
	private static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 300,
			HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private boolean gap;

	private boolean runAnimation = true;
	private boolean runAnimationCopy = runAnimation;
	private boolean finishedRendering = false;

	private BufferedImage img;

	public LangtonsAnt(int size, int fps, boolean gap) {
		super(true);
		this.size = size;
		this.fps = fps;
		this.gap = gap;
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		pixels = new Color[WIDTH / size][HEIGHT / size];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				pixels[i][j] = clra;
			}
		}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() / LangtonsAnt.this.size, y = e.getY() / LangtonsAnt.this.size;
				bufferAnts.add(new Ant(x, y));
			}
		});
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = img.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
		colours = new Color[rule.length()];
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
	public void paintComponent(Graphics graphics) {
		Graphics g = img.getGraphics();
		if (runAnimation && ants.size() > 0) {
			finishedRendering = false;
			for (int i = 0; i < pixels.length; i++) {
				for (int j = 0; j < pixels[0].length; j++) {
					g.setColor(pixels[i][j]);
					g.fillRect(i * size, j * size, size - (gap ? 1 : 0), size - (gap ? 1 : 0));
				}
			}
			for (Ant ant : ants) {
				g.setColor(Color.RED);
				g.fillRect(ant.x * size, ant.y * size, size - (gap ? 1 : 0), size - (gap ? 1 : 0));
			}
			finishedRendering = true;
		} else {
			finishedRendering = true;
		}
		super.paintComponent(graphics);
		graphics.drawImage(img, 0, 0, this);

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
		return -1;
	}

	public void pauseAnimation() {
		pausePainting();
	}

	public void resumeAnimation() {
		resumePainting();
	}

	/**
	 * returns true once the painting has stopped
	 */
	private boolean pausePainting() {
		runAnimationCopy = runAnimation;
		runAnimation = false;
		while (!finishedRendering) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void resumePainting() {
		runAnimation = runAnimationCopy;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		if (fps >= -1) {
			this.fps = fps;
		} else {
			System.err.println("Invalid fps: " + fps);
		}
	}

	private class Ant {
		private int x, y;
		private int px, py;
		private Direction direction = Direction.NORTH;

		private Ant(int x, int y) {
			this.x = x;
			this.y = y;
		}

		private void rotateLeft() {
			switch (direction) {
			case NORTH:
				direction = Direction.WEST;
				break;
			case EAST:
				direction = Direction.NORTH;
				break;
			case SOUTH:
				direction = Direction.EAST;
				break;
			case WEST:
				direction = Direction.SOUTH;
				break;
			}
		}

		private void rotateRight() {
			switch (direction) {
			case NORTH:
				direction = Direction.EAST;
				break;
			case EAST:
				direction = Direction.SOUTH;
				break;
			case SOUTH:
				direction = Direction.WEST;
				break;
			case WEST:
				direction = Direction.NORTH;
				break;
			}
		}

		private void move() {
			px = x;
			py = y;
			switch (direction) {
			case NORTH:
				y -= 1;
				break;
			case EAST:
				x += 1;
				break;
			case SOUTH:
				y += 1;
				break;
			case WEST:
				x -= 1;
				break;
			}
		}
	}

	public void clear() {
		pausePainting();
		ants.clear();
		cycles = 0;
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				pixels[i][j] = clra;
			}
		}
		Graphics graphics = img.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		colours = new Color[rule.length()];
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
		resumePainting();
	}

	public void setRule(String rule) throws RuleParsingException {
		if (rule.length() <= 1)
			throw new RuleParsingException(rule);
		for (int i = 0; i < rule.length(); i++) {
			if (rule.charAt(i) != 'R' && rule.charAt(i) != 'L') {
				throw new RuleParsingException(rule);
			}
		}
		this.rule = rule;
	}

	private class AnimationThread extends Thread {
		@Override
		public void run() {
			new Thread() {
				@Override
				public void run() {
					while (true) {
						update(getGraphics());
						try {
							Thread.sleep((long) (1000D / REFRESH_RATE));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.start();
			while (true) {
				ants.addAll(bufferAnts);
				bufferAnts.clear();
				if (runAnimation && ants.size() > 0) {
					pausePainting();
					for (Ant ant : ants) {
						if (ant.x >= pixels.length) {
							ant.x = 0;
							continue;
						} else if (ant.x < 0) {
							ant.x = pixels.length - 1;
							continue;
						}
						if (ant.y >= pixels[0].length) {
							ant.y = 0;
							continue;
						} else if (ant.y < 0) {
							ant.y = pixels[0].length - 1;
							continue;
						}

						int index = getIndexOfColour(pixels[ant.x][ant.y]);
						char direction = rule.charAt(index);
						if (direction == 'R') {
							ant.rotateRight();
						} else if (direction == 'L') {
							ant.rotateLeft();
						}

						pixels[ant.x][ant.y] = nextColour(index);
						ant.move();
					}
					resumePainting();
					cycles++;
				}
				try {
					if (fps == -1) {
						Thread.sleep(0);
					} else {
						Thread.sleep(1000 / LangtonsAnt.this.fps);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
