package indexextract.gui.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import indexextract.gui.LangSaver;
import indexextract.gui.MainGui;
import indexextract.gui.component.JColorFill;

public class ExtractPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JFrame frame = null;

	public ExtractPanel(JFrame frm) {
		frame = frm;
		setPreferredSize(new Dimension(500, 270));
		setOpaque(true);
		setLayout(null);
		setBackground(new Color(0x7cdb53));
	}

	public void initComponent() {
		//Init work
		JColorFill f = new JColorFill();
		f.setBackground(getBackground().darker());
		f.setLocation(0, 0);
		
		JLabel t1 = new JLabel();
		t1.setFont(MainGui.guifont.deriveFont(0, 40f));
		t1.setLocation(5, 5);
		t1.setForeground(Color.white);
		t1.setText(LangSaver.EXTRACTPANEL_LABEL_TITLE);
		t1.setSize(t1.getPreferredSize());

		JButton rb = new JButton();
		rb.setCursor(MainGui.handCursor);
		rb.setText(LangSaver.COMMON_BUTTON_BACK);
		rb.setBackground(Color.black);
		rb.setFont(MainGui.guifont.deriveFont(0, 25f));
		rb.setSize(rb.getPreferredSize());
		rb.setLocation(getWidth() - rb.getWidth() - 5, t1.getY());
		rb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MainGui.addBasePanel(frame);
				System.gc();
			}
		});
		
		JLabel wip = new JLabel(LangSaver.DIALOG_WIP_MESSAGE);
		wip.setFont(MainGui.osfont.deriveFont(0, 25f));
		wip.setLocation(0, 100);
		wip.setSize(wip.getPreferredSize());
		
		//Final work
		f.setSize(getWidth(), Math.max(t1.getHeight(), rb.getHeight()));
		
		//Add work
		//First is top
		add(t1);
		add(rb);
		add(wip);
		add(f);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
