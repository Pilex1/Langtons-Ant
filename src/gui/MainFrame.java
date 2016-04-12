package gui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public MainFrame() {
		MainPanel panel = new MainPanel();
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//setUndecorated(true);
		pack();
		setVisible(true);
	}
	

	public static void main(String[] args) {
		new MainFrame();
	}

}
