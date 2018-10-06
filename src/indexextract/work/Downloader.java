package indexextract.work;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Downloader {
	public static String downloadString(String url) {
		System.out.println("[Internal] Downloading String: " + url);
		try {
			System.out.print("[Internal] Connecting to server");
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedInputStream in = new BufferedInputStream(c.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String str;
			String txt = "";
			System.out.print("...\n[Internal] Downloading");
			int a = 0;
			while((str = br.readLine()) !=null) {
				if(a++%3==0) {
					System.out.print(".");
				}
				txt+=str + "\n";
			}
			System.out.println("\n[Internal] Download complete");
			return txt;
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
	private static JsonParser parser;
	public static JsonElement downloadJson(String url) {
		System.out.println("[Internal] Downloading JSON element: " + url);
		try {
			String plain = "";
			System.out.println("[Internal] Downloading STR Response => downloadString(String)");
			plain = downloadString(url);
			if(parser == null) {
				parser = new JsonParser();
			}
			System.out.println("[Internal] Download complete");
			return parser.parse(plain);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
	public static void downloadFile(String url, int size, File path) {
		try {
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path, false));
			//System.out.print("[Internal] Connecting to server");
			URL u = new URL(url);
//			System.out.println(System.currentTimeMillis());
			URLConnection c = u.openConnection();
			c.setReadTimeout(0);
			c.setConnectTimeout(0);
//			System.out.println(System.currentTimeMillis());
			BufferedInputStream din = new BufferedInputStream(c.getInputStream());
			//System.out.println("...\n[Internal] Downloading " + url);
			
			int tfs = size;
			if(tfs > 1048576) {
				Main.mx ++;
			}
			// socket.setSoTimeout(Math.max(pingdelay, 50) * 3);
			
			byte[] buf = new byte[10000000];
			int read = 0;
			int trd = tfs;
			while ((read = din.read(buf, 0, Math.min(trd, buf.length))) > 0) {
				//System.out.println(System.currentTimeMillis() + " : " + Math.min(trd, buf.length));
				//System.out.println(trd);
				
				trd -= read;
				bout.write(buf, 0, read);
				// Thread.sleep(3);
			}
			bout.flush();
			bout.close();
//			System.out.println(System.currentTimeMillis());
			if(tfs > 1048576) {
				Main.mx --;
			}
			return;
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
}
