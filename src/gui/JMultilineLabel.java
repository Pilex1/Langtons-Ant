package gui;

import java.awt.Font;

import javax.swing.JTextArea;

public class JMultilineLabel extends JTextArea {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1400L;

	public JMultilineLabel(Font font, String text) {
		setBorder(null);
		setFocusable(false);
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setFont(font);
		setText(text);
	}

}
