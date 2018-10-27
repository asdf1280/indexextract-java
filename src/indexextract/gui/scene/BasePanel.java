package indexextract.gui.scene;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import indexextract.gui.GuiUtils;
import indexextract.gui.LangSaver;

public class BasePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage back = null;
	public BasePanel() {
		setOpaque(true);
		setLayout(null);
		_waitPaintImage();
	}
	private void _waitPaintImage() {
		new Thread(() -> {
			while(back == null) {
				GuiUtils.sleepQuietly(0);
			}
			repaint();
		}).start();
	}
	public void loadImage() {
		try {
			BufferedImage tback = ImageIO.read(getClass().getResourceAsStream("/indexextract/gui/resource/image/" + (int)(Math.random() * 6 + 1) + ".png"));
			back = Scalr.resize(tback, Method.ULTRA_QUALITY, Mode.AUTOMATIC, getWidth(), getHeight());
		} catch (Exception e) {
			System.out.println("Error: An error occured while loading background image");
			e.printStackTrace();
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(back == null) {
			g2.setColor(Color.white);
			g2.setFont(getFont().deriveFont(0, 30));
			g2.drawString(LangSaver.RSRC_INIT, 50, 100);
		}
		g2.drawImage(back, 0, 0, getWidth(), getHeight(), null);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(back != null) {
			g2.setColor(Color.white);
			g2.setFont(getFont().deriveFont(0, 30));
			FontMetrics fm = g2.getFontMetrics();
			String txt = LangSaver.WELCOME;
			g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, 0 + fm.getAscent());
		}
	}
}
