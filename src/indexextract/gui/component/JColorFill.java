package indexextract.gui.component;

import java.awt.Graphics;

import javax.swing.JComponent;

public class JColorFill extends JComponent {
	private static final long serialVersionUID = 1L;
	public JColorFill() {
		setOpaque(true);
		setFocusable(false);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}
