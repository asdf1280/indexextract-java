package indexextract.gui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;

import javax.swing.JButton;

public class RoundButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5423139848620325281L;

	public RoundButton() {
		setBorder(null);
		setFocusable(false);
		setFocusPainted(false);
		setContentAreaFilled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private static final Color dark = new Color(0, 0, 0, 64);
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(dark);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(getModel().isPressed()) {
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		if(getModel().isRollover()) {
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		super.paintComponent(g);
	}
}
