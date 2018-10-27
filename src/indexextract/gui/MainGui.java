package indexextract.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Locale;

import javax.swing.JFrame;
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
	public static Dimension preferredSize = null;

	public MainGui() {
		JFrame frm = new JFrame("Indexextract Java");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(500, 300);
		frm.setPreferredSize(frm.getSize());
		preferredSize = frm.getSize();
		frm.addComponentListener(new UnresizableCenterComponentListener(frm));
		frm.setVisible(true);

		BasePanel basePanel = new BasePanel();
		basePanel.setBackground(Color.black);
		basePanel.setFont(guifont);
		frm.setContentPane(basePanel);

		frm.validate();
		basePanel.loadImage();
	}
	
	public static final Gson gi;
	public static final JsonParser jp;
	static {
		GsonBuilder tmp = new GsonBuilder();
		tmp.setPrettyPrinting().serializeNulls();
		gi = tmp.create();
		
		jp = new JsonParser();
	}
	
	public static String[] arguments;

	public static void main(String[] args) {
		arguments = args;
		OptionParser op = new OptionParser();
		OptionSpec<String> localeOptionSpec = op.accepts("locale").withOptionalArg();
		OptionSet os = op.parse(args);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error: An error occured while changing look and feel");
		}
		{ //Load language
			String forceLang = os.valueOf(localeOptionSpec);
			String lfn = "en_us";
			if(Locale.getDefault().equals(Locale.KOREA)) lfn = "ko_kr";
			if(forceLang != null && resourceExists("/indexextract/gui/resource/lang/" + forceLang + ".json")) {
				lfn = forceLang;
			}
			loadLanguage(lfn);
		}
		new MainGui();
	}

	public static void loadLanguage(String langname) {
		String lpath = "/indexextract/gui/resource/lang/listlang.json";
		String path = "/indexextract/gui/resource/lang/" + langname + ".json";
		try {
			JsonElement list = jp.parse(IOUtils.toString(MainGui.class.getResourceAsStream(lpath), "UTF8"));
			JsonElement file = jp.parse(IOUtils.toString(MainGui.class.getResourceAsStream(path), "UTF8"));
			JsonArray keys = list.getAsJsonObject().get("keys").getAsJsonArray();
			JsonObject values = file.getAsJsonObject();
			for(JsonElement keyElem : keys) {
				String key = keyElem.getAsString();
				try {
					//String str = (String) LangSaver.class.getField(key).get(null);
					//System.out.println(key + ": OLD:" + str + ", NEW:" + values.get(key).getAsString());
					LangSaver.class.getField(key).set(null, values.get(key).getAsString());
				} catch (NullPointerException e) {
					System.out.println("Warning: Cannot find language value "+key+" from " + langname+".json");
				} catch (NoSuchFieldException e) {
					System.out.println("Error: Language file " + langname + ".json muse be incompatible");
				}
			}
			
			try {
				guifont = Font.createFont(Font.TRUETYPE_FONT,
						MainGui.class.getResourceAsStream("/indexextract/gui/resource/font/" + list.getAsJsonObject().get("langfonts").getAsJsonObject().get(langname).getAsString()));
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
