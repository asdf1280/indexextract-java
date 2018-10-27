package indexextract.gui.listener;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;

import javax.swing.JFrame;

public class UnresizableCenterComponentListener extends ComponentAdapter {
	private JFrame frm = null;
	private static Robot r = null;
	public UnresizableCenterComponentListener(JFrame jFrame) {
		if(jFrame == null) {
			throw new NullPointerException("jFrame cannot be null");
		}
		frm = jFrame;
		try {
			r = new Robot();
		} catch (AWTException e) {
		}
	}
	@Override
	public void componentResized(ComponentEvent e) {
		frm.setSize(frm.getPreferredSize());
		if(r != null) {
			r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		}
		centerFrame(frm);
	}
	public static void centerFrame(Component comp) {
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		int sw = ss.width;
		int sh = ss.height;
		int cw = comp.getWidth();
		int ch = comp.getHeight();
		
		comp.setLocation((sw - cw) / 2, (sh - ch) / 2);
	}
}
