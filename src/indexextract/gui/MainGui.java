package indexextract.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import indexextract.gui.listener.UnresizableCenterComponentListener;
import indexextract.gui.scene.BasePanel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class MainGui {
	public static Font guifont = null;
	public static Font osfont = null;
	public static Dimension preferredSize = null;

	public MainGui() {
		JFrame frm = new JFrame("Indexextract Java");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		preferredSize = frm.getSize();
		frm.addComponentListener(new UnresizableCenterComponentListener(frm));
		frm.setVisible(true);
		addBasePanel(frm);
	}
	
	public static void addBasePanel(JFrame frm) {
		BasePanel basePanel = initBasePanel(frm);
		frm.setContentPane(basePanel);

		for (MouseListener ml : frm.getMouseListeners()) {
			frm.removeMouseListener(ml);
		}
		frm.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				basePanel.loadImage();
			}
		});
		frm.validate();
		frm.pack();
		frm.setPreferredSize(frm.getSize());
		basePanel.loadImage();
		basePanel.initComponent();
	}

	public static BasePanel initBasePanel(JFrame frm) {
		BasePanel basePanel = new BasePanel(frm);
		basePanel.setBackground(Color.black);
		basePanel.setFont(osfont);
		return basePanel;
	}

	public static final Gson gi;
	public static final JsonParser jp;
	static {
		GsonBuilder tmp = new GsonBuilder();
		tmp.setPrettyPrinting().serializeNulls();
		gi = tmp.create();

		jp = new JsonParser();
	}
	
	public static final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	public static String[] arguments;
	
	private static String lpath = "/indexextract/gui/resource/lang/listlang.json";
	private static String path = "/indexextract/gui/resource/lang/{0}.json";

	public static void main(String[] args) {
		arguments = args;
		OptionParser op = new OptionParser();
		OptionSpec<String> localeOptionSpec = op.accepts("locale").withOptionalArg();
		OptionSet os = op.parse(args);
		try {
			String cln = UIManager.getSystemLookAndFeelClassName();
			// cln = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			System.out.println(cln);
			UIManager.setLookAndFeel(cln);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: An error occured while changing look and feel");
		}
		osfont = new JOptionPane().getFont();
		{ // Load language
			String forceLang = os.valueOf(localeOptionSpec);
			String lfn = "en_us";
			if (Locale.getDefault().equals(Locale.KOREA))
				lfn = "ko_kr";
			if (Locale.getDefault().equals(Locale.JAPAN))
				lfn = "ja_jp";
			if (Locale.getDefault().equals(Locale.CHINA))
				lfn = "zh_cn";
			if (forceLang != null && resourceExists("/indexextract/gui/resource/lang/" + forceLang + ".json")) {
				lfn = forceLang;
			}
			loadLanguage(lfn);
		}
		new MainGui();
	}

	public static void loadLanguage(String langname) {
		try {
			JsonElement list = jp.parse(IOUtils.toString(MainGui.class.getResourceAsStream(lpath), "UTF8"));
			JsonElement file = jp.parse(IOUtils.toString(MainGui.class.getResourceAsStream(path.replaceAll(Pattern.quote("{0}"), langname)), "UTF8"));
			JsonArray keys = list.getAsJsonObject().get("keys").getAsJsonArray();
			JsonObject values = file.getAsJsonObject();
			for (JsonElement keyElem : keys) {
				String key = keyElem.getAsString();
				try {
					// String str = (String) LangSaver.class.getField(key).get(null);
					// System.out.println(key + ": OLD:" + str + ", NEW:" +
					// values.get(key).getAsString());
					System.out.println("Register localization: " + key + ": " + values.get(key).getAsString());
					LangSaver.class.getField(key).set(null, values.get(key).getAsString());
				} catch (NullPointerException e) {
					System.out.println("Warning: Cannot find language \'VALUE\' " + key + " from " + langname + ".json");
				} catch (NoSuchFieldException e) {
					System.out.println("Warning: Cannot find language \'FIELD\' " + key + " from LangSaver.class (" + langname + ".json)");
				}
			}
			for(String ks : values.keySet()) {
				if(!keys.toString().contains("\"" + ks + "\"")) {
					System.out.println("Warning: Unknown field was found from "+langname+".json: " + ks);
				}
			}
			for(Field fd : LangSaver.class.getFields()) {
				String str = fd.getName();
				if(!values.has(str)) {
					System.out.println("Warning: Cannot find language key(Defined in LangSaver) from "+langname+".json: " + str);
				}
			}
			for(Field fd : LangSaver.class.getFields()) {
				String str = fd.getName();
				if(!keys.toString().contains("\"" + str + "\"")) {
					System.out.println("Warning: Cannot find language key(Defined in LangSaver) from KEY LIST: " + str);
				}
			}

			try {
				String var = list.getAsJsonObject().get("langfonts").getAsJsonObject().get(langname).getAsString();
				if (var.equals("SYSTEM_FONT")) {
					guifont = osfont;
				} else {
					guifont = Font.createFont(Font.TRUETYPE_FONT,
							MainGui.class.getResourceAsStream("/indexextract/gui/resource/font/" + var));
					if (var.endsWith(".otf")) {
						new Thread(() -> JOptionPane.showMessageDialog(null, LangSaver.DIALOG_FONT_MESSAGE,
								LangSaver.DIALOG_COMMON_TITLE, JOptionPane.CANCEL_OPTION)).start();
					}
				}
			} catch (Exception e) {
				System.out.println("Error: An error occured while loading GUI font");
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Error: An error occured while reading language files");
			e.printStackTrace();
		}
	}

	public static boolean resourceExists(String path) {
		return MainGui.class.getResourceAsStream(path) != null;
	}
}
