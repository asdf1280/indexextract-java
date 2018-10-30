package indexextract.gui.scene;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import indexextract.gui.GuiUtils;
import indexextract.gui.LangSaver;
import indexextract.gui.MainGui;
import indexextract.gui.component.JColorFill;

public class DownloadPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JFrame frame = null;

	public DownloadPanel(JFrame frm) {
		frame = frm;
		setPreferredSize(new Dimension(500, 270));
		setOpaque(true);
		setLayout(null);
		setBackground(new Color(0x00e0b3));
	}
	
	private JColorFill jcf;
	private JLabel titleLabel;
	private JComboBox<String> cb;
	private JLabel resourcever;

	public void initComponent() {
		//Init work
		jcf = new JColorFill();
		jcf.setBackground(getBackground().darker());
		jcf.setLocation(0, 0);
		
		titleLabel = new JLabel();
		titleLabel.setFont(MainGui.guifont.deriveFont(0, 40f));
		titleLabel.setLocation(5, 5);
		titleLabel.setForeground(Color.white);
		titleLabel.setText(LangSaver.DOWNLOADPANEL_LABEL_TITLE);
		titleLabel.setSize(titleLabel.getPreferredSize());
		titleLabel.setName(titleLabel.getText());

		JButton rb = new JButton();
		rb.setCursor(MainGui.handCursor);
		rb.setText(LangSaver.COMMON_BUTTON_BACK);
		rb.setBackground(Color.black);
		rb.setFont(MainGui.guifont.deriveFont(0, 25f));
		rb.setSize(rb.getPreferredSize());
		rb.setLocation(getWidth() - rb.getWidth() - 5, titleLabel.getY());
		rb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MainGui.addBasePanel(frame);
				System.gc();
			}
		});
		
		//Final work
		jcf.setSize(getWidth(), Math.max(titleLabel.getHeight(), rb.getHeight()));
		
		//Add work
		//First is top
		add(titleLabel);
		add(rb);
		add(jcf);
		
		initAsync();
		
	}
	private void initAsync() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int lax = 5;
				int hgt = jcf.getY() + jcf.getHeight() + 5;
				ButtonGroup mg = new ButtonGroup();
				JRadioButton rel = new JRadioButton(LangSaver.DOWNLOADPANEL_RADIOBUTTON_RELEASE);
				init(rel, lax, hgt);
				rel.setSelected(true);
				lax += rel.getWidth();
				
				JRadioButton snap = new JRadioButton(LangSaver.DOWNLOADPANEL_RADIOBUTTON_SNAPSHOT);
				init(snap, lax, hgt);
				lax += snap.getWidth();
				
				JRadioButton ob = new JRadioButton(LangSaver.DOWNLOADPANEL_RADIOBUTTON_OLDBETA);
				init(ob, lax, hgt);
				lax += ob.getWidth();
				
				JRadioButton oa = new JRadioButton(LangSaver.DOWNLOADPANEL_RADIOBUTTON_OLDALPHA);
				init(oa, lax, hgt);
				lax += oa.getWidth();
				
				mg.add(rel);
				mg.add(snap);
				mg.add(ob);
				mg.add(oa);
				
				JLabel la = new JLabel(LangSaver.DOWNLOADPANEL_LABEL_SELECTVERSION);
				la.setFont(getOsFont(la));
				la.setSize(la.getPreferredSize());
				la.setLocation(5, hgt += rel.getHeight() + 5);
				
				cb = new JComboBox<>(new String[] {LangSaver.DOWNLOADPANEL_COMBOBOX_LOADING});
				cb.setFont(getOsFont(cb));
				cb.setSize(getWidth() - la.getX() - la.getWidth() - 5,cb.getPreferredSize().height);
				cb.setLocation(la.getX() + la.getWidth() + 5, la.getY() + la.getHeight() - cb.getHeight() + cb.getHeight() / 5);
				cb.setMaximumRowCount(20);
				cb.addItemListener(e -> {
					if(e.getStateChange() != ItemEvent.SELECTED) return;
					new Thread(() -> {
						resourcever.setText("Resource version: Loading...");
						String item = (String) e.getItem();
						
						try {
							for(JsonElement elem : obj.get("versions").getAsJsonArray()) {
								String str = elem.getAsJsonObject().get("id").getAsString();
								if(str.equals(item)) {
									JsonObject obj = elem.getAsJsonObject();
									String ur = obj.get("url").getAsString();
									JsonParser jp = new JsonParser();
									JsonElement je = jp.parse(IOUtils.toString(new URL(ur).openStream(), "UTF8"));
									currentObj = je.getAsJsonObject(); 
									resourcever.setText("Resource version: " + currentObj.get("assets").getAsString());
									return;
								}
							}
							resourcever.setText("Resource version: UNKNOWN");
						} catch (Exception e2) {
							e2.printStackTrace();
							String msgstr = LangSaver.DOWNLOADPANEL_ERROR_DIALOG;
							msgstr = msgstr.replaceAll(Pattern.quote("{0}"), e2.getClass().getName());
							msgstr = msgstr.replaceAll(Pattern.quote("{1}"), e2.getLocalizedMessage());
							JOptionPane.showConfirmDialog(null, msgstr, LangSaver.DOWNLOADPANEL_ERROR_DIALOG_TITLE, JOptionPane.OK_OPTION);
							System.exit(0);
						}
					}).start();
				});
				
				resourcever = new JLabel("Resource version: UNKNOWN");
				resourcever.setFont(getOsFont(resourcever));
				resourcever.setSize(resourcever.getPreferredSize());
				resourcever.setLocation(cb.getX(), cb.getY() + cb.getHeight());
				
				add(rel);
				add(snap);
				add(ob);
				add(oa);
				add(la);
				add(cb);
				add(resourcever);
				repaint();
			}
		}).start();
	}
	private static String loadVersionsUrl = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
	private JsonObject obj = null;
	private JsonObject currentObj = null;
	private void init(JRadioButton rb, int x, int y) {
		rb.setFont(getOsFont(rb));
		rb.setOpaque(false);
		rb.setFocusPainted(true);
		rb.setLocation(x, y);
		rb.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				new Thread(() -> {
					while(cb == null) {
						GuiUtils.sleepQuietly(100);
					}
					if(e.getStateChange() == ItemEvent.SELECTED) {
						if(cb != null) {
							cb.removeAllItems();
						}
						try {
							JsonParser jp = new JsonParser();
							JsonElement je = jp.parse(IOUtils.toString(new URL(loadVersionsUrl).openStream(), "UTF8"));
							obj = je.getAsJsonObject();
							String filter = "release";
							if(e.getSource() instanceof JRadioButton) {
								JRadioButton rb1 = (JRadioButton) e.getSource();
								String str = rb1.getText();
								if(str.equalsIgnoreCase(LangSaver.DOWNLOADPANEL_RADIOBUTTON_RELEASE)) {
									filter = "release";
								}else if(str.equalsIgnoreCase(LangSaver.DOWNLOADPANEL_RADIOBUTTON_SNAPSHOT)) {
									filter = "snapshot";
								}else if(str.equalsIgnoreCase(LangSaver.DOWNLOADPANEL_RADIOBUTTON_OLDBETA)) {
									filter = "old_beta";
								}else if(str.equalsIgnoreCase(LangSaver.DOWNLOADPANEL_RADIOBUTTON_OLDALPHA)) {
									filter = "old_alpha";
								}
							}else {
								throw new Exception(LangSaver.DOWNLOADPANEL_ERROR_JSON);
							}
							;{
								JsonElement versions = obj.get("versions");
								JsonArray ja = versions.getAsJsonArray();
								for(JsonElement jelem : ja) {
									JsonObject ver = jelem.getAsJsonObject();
									if(ver.get("type").getAsString().equals(filter)) {
										cb.addItem(ver.get("id").getAsString());
									}
								}
							}
						} catch (Exception e2) {
							e2.printStackTrace();
							String msgstr = LangSaver.DOWNLOADPANEL_ERROR_DIALOG;
							msgstr = msgstr.replaceAll(Pattern.quote("{0}"), e2.getClass().getName());
							msgstr = msgstr.replaceAll(Pattern.quote("{1}"), e2.getLocalizedMessage());
							JOptionPane.showConfirmDialog(rb, msgstr, LangSaver.DOWNLOADPANEL_ERROR_DIALOG_TITLE, JOptionPane.OK_OPTION);
							System.exit(0);
						}
					}
					repaint();
				}).start();
			}
		});
		rb.setSize(rb.getPreferredSize());
	}
	
	private static Font getOsFont(Component comp) {
		return MainGui.osfont.deriveFont(comp.getFont().getStyle(), comp.getFont().getSize());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
