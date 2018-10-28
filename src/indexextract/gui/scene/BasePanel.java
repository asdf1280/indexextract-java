package indexextract.gui.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import indexextract.gui.GuiUtils;
import indexextract.gui.LangSaver;
import indexextract.gui.MainGui;
import indexextract.gui.component.RoundButton;

public class BasePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage back = null;

	public BasePanel() {
		setPreferredSize(new Dimension(500, 270));
		setOpaque(true);
		setLayout(null);
		_waitPaintImage();
	}
	
	public RoundButton extract_button = null;
	public RoundButton download_button = null;
	public RoundButton exit_button = null;
	
	public void initComponent() {
		removeAll();
		extract_button = new RoundButton();
		extract_button.setText(LangSaver.MAINMENU_INDEX_EXTRACTOR);
		extract_button.setLocation(10, 50);
		extract_button.setSize(getWidth() / 2 - 15, getHeight() - 60);
		extract_button.setBorder(BorderFactory.createLineBorder(new Color(0x7cdb53), 5, true));
		extract_button.setForeground(Color.white);
		extract_button.setFont(MainGui.guifont.deriveFont(0, 20f));
		extract_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(BasePanel.this, LangSaver.DIALOG_WIP_MESSAGE, LangSaver.DIALOG_WIP_TITLE, JOptionPane.CANCEL_OPTION);
			}
		});
		add(extract_button);
		
		download_button = new RoundButton();
		download_button.setText(LangSaver.MAINMENU_INDEX_DOWNLOADER);
		download_button.setLocation(getWidth() / 2 + 5, 50);
		download_button.setSize(getWidth() / 2 - 15, getHeight() - 60);
		download_button.setBorder(BorderFactory.createLineBorder(new Color(0x00e0b3), 5, true));
		download_button.setForeground(Color.white);
		download_button.setFont(MainGui.guifont.deriveFont(0, 20f));
		download_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(BasePanel.this, LangSaver.DIALOG_WIP_MESSAGE, LangSaver.DIALOG_WIP_TITLE, JOptionPane.CANCEL_OPTION);
			}
		});
		add(download_button);
		
		exit_button = new RoundButton();
		exit_button.setText(LangSaver.MAINMENU_EXIT);
		exit_button.setLocation(getWidth() - 90, 10);
		exit_button.setSize(80, 30);
		exit_button.setBorder(BorderFactory.createLineBorder(new Color(0xff0000), 5, true));
		exit_button.setForeground(Color.white);
		exit_button.setFont(MainGui.guifont.deriveFont(0, 20f));
		exit_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		add(exit_button);
		
		repaint();
	}

	private void _waitPaintImage() {
		new Thread(() -> {
			while (back == null) {
				GuiUtils.sleepQuietly(0);
			}
			repaint();
		}).start();
	}
	
	public BufferedImage resizeBack(int width, int height) {
		try {
			BufferedImage tback = ImageIO.read(getClass().getResourceAsStream(
					"/indexextract/gui/resource/image/" + (int)(Math.random() * 15 + 1) + ".png"));

			double iw = tback.getWidth();
			double ih = tback.getHeight();
			double ir = iw / ih;
			double sw = width;
			double sh = height;
			double sr = sw / sh;
			double tw = 0;
			double th = 0;
			
			if(ir == sr) {
				tw = sw;
				th = sh;
			}else {
				if (ir > sr) { // 사진이 화면보다 옆으로 김, 높이에 맞춤
					if (ir > 1) { // 사진이 옆으로 김, 높이에 맞춤
						th = sh;
						tw = th * ir;
					} else { // 사진이 위로 김, 너비에 맞춤
						tw = sw;
						th = tw / ir;
					}
				} else if (ir < sr) {// 사진이 화면보다 높음
					if (ir < 1) { // 사진이 옆으로 김, 높이에 맞춤
						th = sh;
						tw = th * ir;
					} else { // 사진이 위로 김, 너비에 맞춤
						tw = sw;
						th = tw / ir;
					}
				}
			}

			BufferedImage r = new BufferedImage((int)sw, (int)sh, BufferedImage.TYPE_INT_ARGB);
			r.createGraphics().drawImage(tback, -(int)(tw - sw) / 2, -(int)(th - sh) / 2, (int)tw, (int)th, null);
			repaint();
			
			return r;
		} catch (Exception e) {
			System.out.println("Error: An error occured while loading background image");
			e.printStackTrace();
			return null;
		}
	}

	public void loadImage() {
		try {
			BufferedImage tback = resizeBack(getWidth(), getHeight());

			back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			back.createGraphics().drawImage(tback,0, 0, getWidth(), getHeight(), null);
			repaint();
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
		if (back == null) {
			g2.setColor(Color.white);
			g2.setFont(getFont().deriveFont(0, 30));
			g2.drawString(LangSaver.MAINMENU_RESOURCE_INIT, 50, 100);
		}
		g2.drawImage(back, 0, 0, getWidth(), getHeight(), null);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (back != null) {
			g2.setColor(Color.white);
			g2.setFont(getFont().deriveFont(0, 30));
			FontMetrics fm = g2.getFontMetrics();
			String txt = LangSaver.MAINMENU_TITLE;
			g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, 0 + fm.getAscent());
		}
	}
}
