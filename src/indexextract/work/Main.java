package indexextract.work;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import indexextract.objects.AssetObject;
import indexextract.objects.Version;

/**
 * Minecraft version manifest:
 * https://launchermeta.mojang.com/mc/game/version_manifest.json Resources URL
 * http://resources.download.minecraft.net/HASHLEFT2/FULLHASH
 * 
 * @author User
 *
 */
public class Main {
	public static Logger lg = Logger.getLogger("indexextract");
	public static void main(String[] args) throws Exception {
		lg.info("Initializing Indexextract-Java");
		lg.info("An optimized Minecraft resource downloader");
		lg.info("Initializing...");
		new Main();
	}

	public static final String credit = "Indexextract ©2018 User(dhkim0800). All rights reserved. Uses Java Runtime Enviornment 1.8® ©2014 Oracle.\n"
			+ "All rights reserved. Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners.\n"
			+ "Powered by Gson ©2008 Google Inc. Licensed under the Apache License, Version 2.0. You may obtain a copy of the License at \'http://www.apache.org/licenses/LICENSE-2.0\'.\n"
			+ "Powered by Apache Commons-text ©2014-2018 The Apache Software Foundation. All Rights Reserved.\n"
			+ "Powered by Apache Commons-lang3 ©2014-2018 The Apache Software Foundation. All Rights Reserved.";

	public Main() throws Exception {
		// TODO Auto-generated constructor stub
		clearConsole();
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		Scanner f = new Scanner(System.in);
		lg.info("Connecting...");
		lg.info("Downloading version_manifest...");
		JsonElement je = Downloader.downloadJson("https://launchermeta.mojang.com/mc/game/version_manifest.json");
		clearConsole();
		int rt = 0;
		int drt = 0;
		System.out.println("=======================Credits=========================");
		System.out.println(credit);
		System.out.println("=======================================================");
		System.out.println("----- What to do? -------------------------------------");
		System.out.println("0: Download indexes");
		System.out.println("1: Extract indexes from local file");
		System.out.println("2: I already downloaded indexes and i want some work!");
		drt = f.nextInt();

		clearConsole();

		System.out.println("----- Select type by input ----------------------------");
		System.out.println("0: release");
		System.out.println("1: snapshot");
		System.out.println("2: old_beta");
		System.out.println("3: old_alpha");
		rt = f.nextInt();
		f.nextLine();

		JsonArray ja = je.getAsJsonObject().get("versions").getAsJsonArray();
		Iterator<JsonElement> vsi = ja.iterator();
		HashMap<String, Version> ids = new HashMap<>();
		while (vsi.hasNext()) {
			JsonElement str = vsi.next();
			Version v = g.fromJson(str, Version.class);
			// System.out.println(v.type);
			boolean b = v.type.index() == rt;
			if (b) {
				System.out.println(v.id);
				ids.put(v.id, v);
			}
		}
		System.out.println("----- Select version by input ----------------------------");
		String ver = f.next();
		f.nextLine();
		if (!ids.containsKey(ver)) {
			System.out.println("Version not found. Closing program.");
			System.exit(0);
		}

		Version slt = ids.get(ver);
		lg.info("Version found. Downloading json informations.");
		lg.info("Download from: " + slt.url);
		JsonElement vln = Downloader.downloadJson(slt.url);
		lg.info("Extracting fields");
		String idxurl = vln.getAsJsonObject().get("assetIndex").getAsJsonObject().get("url").getAsString();
		lg.info("Indexes file URL: " + idxurl);
		JsonElement idx = Downloader.downloadJson(idxurl);
		System.out.println(g.toJson(idx));
		JsonObject objs = idx.getAsJsonObject().get("objects").getAsJsonObject();
		Iterator<String> sts = objs.keySet().iterator();
		lg.info("Generating dirs");
		while (sts.hasNext()) {
			String st = sts.next();
			File fi = new File("./indexextract/" + ver + "/" + st);
			fi.getParentFile().mkdirs();
		}
		if (drt != 2) {
			Iterator<String> sts2 = objs.keySet().iterator();
			rm = objs.size();
			int lst = 0;
			while (sts2.hasNext()) {
				if (trds > mx) {
					lst = trds;
					while (trds > 4) {
						if (lst != trds) {
							lst = trds;
						}
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println();
				}
				trds++;
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String st = sts2.next();
						AssetObject ao = g.fromJson(objs.get(st), AssetObject.class);

						lg.info("Downloading " + st + ", " + rm-- + " left.");
						String u = "https://resources.download.minecraft.net/" + ao.hash.substring(0, 2) + "/"
								+ ao.hash;
						File fi = new File("./indexextract/" + ver + "/" + st);
						Downloader.downloadFile(u, ao.size, fi);

						trds--;
					}
				});
//				if (rm <= mx * 3) {
//					t.run();
//				} else {
//					t.start();
//				}
				t.run();
			}
		}

		while (trds != 0) {
			Thread.sleep(10);
		}

		while (true) {
			System.out.println("----- What to do? ----------------------------------------");
			System.out.println("0: Exit");
			System.out.println("1: Open folder");
			System.out.println("2: Decrypt unicode languages(You don't need to encrypt again)");
			System.out.println("3: Get minecraft.jar assets(You have to play that version once)");
			System.out.println("----------------------------------------------------------");

			int n = f.nextInt();
			if (n == 0) {
				break;
			} else if (n == 1) {
				Desktop.getDesktop().open(new File("./indexextract/" + ver));
			} else if (n == 2) {
				JsonParser parser = new JsonParser();
				InputStreamReader isr = new InputStreamReader(new FileInputStream("indexextract/" + ver + "/pack.mcmeta"), "UTF8");
				JsonObject job = parser.parse(isr)
						.getAsJsonObject();
				if (job.get("pack").getAsJsonObject().get("pack_format").getAsInt() < 4) {
					System.out.println("This version doesn't requre this work. Lang files aren't encrypted.");
					System.out.println("");
					continue;
				}

				File d = new File("./indexextract/" + ver + "/minecraft/lang/");
				File[] fs = d.listFiles();
				for (File lng : fs) {
					System.out.println(lng.getCanonicalPath());
					InputStreamReader reader = new InputStreamReader(new FileInputStream(lng), "UTF8");
					JsonElement element = parser.parse(reader);
					OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(lng, false), "UTF8");
					writer.write(g.toJson(element));
					writer.flush();
					writer.close();
					reader.close();
				}
				isr.close();
			} else if (n == 3) {
				System.out.println("[Info] Preparing work...");
				if (!System.getProperty("os.name").toLowerCase().contains("win")) {
					System.out.println("Only Windows is supported to do this!");
					System.out.println("Tested version: Windows 10");
					continue;
				}
				String apdp = System.getenv("AppData");
				String vs = apdp + "/.minecraft/versions/" + ver + "/";
				File vf = new File(vs);
				if (!vf.exists()) {
					System.out.println("Couldn't find selected version folder!");
					System.out.println("(" + vs + ")");
					continue;
				}
				File jar = new File(vs + ver + ".jar");
				System.out.println(jar);
				if (!jar.exists()) {
					System.out.println("Jar file damaged!");
					System.out.println("(" + jar.getAbsolutePath() + ")");
				}
				JarFile jf = new JarFile(jar);
				String dest = "indexextract/" + ver + "/";
				Enumeration<JarEntry> jes = jf.entries();
				Path tmp = Files.createTempDirectory("indexextract");
				System.out.println(tmp.toString());
				FileUtils.forceDeleteOnExit(tmp.toFile());
				System.out.println("[Info] Extracting jar...");
				while (jes.hasMoreElements()) {
					JarEntry entry = jes.nextElement();

					String stt = tmp.toString() + File.separator + entry.getName();
					File fie = new File(stt);
					fie.getParentFile().mkdirs();

					if (!entry.getName().endsWith("/") && !entry.getName().endsWith(".class")) {
						InputStream is = jf.getInputStream(entry);
						FileOutputStream fout = new FileOutputStream(fie);
						int rd = 1;
						byte[] bf = new byte[100000];
						while ((rd = is.read(bf)) > 0) {
							fout.write(bf, 0, rd);
						}
						fout.flush();
						fout.close();
						is.close();
					}

				}
				System.out.print("[Info] Copying to workdir..");
				jf.close();
				FileUtils.deleteQuietly(new File(dest + "/pack_jar.mcmeta"));
				File ttt = new File(tmp.toFile().getAbsolutePath() + "/pack.mcmeta");
				if (ttt.exists())
					FileUtils.moveFile(ttt, new File(dest + "/pack_jar.mcmeta"));
				System.out.print("...");
				FileUtils.copyDirectory(new File(tmp.toFile().getAbsolutePath() + "/assets/minecraft/"),
						new File(dest + "/minecraft/"));
				System.out.print(".....\n");
//				FileUtils.copyDirectory(new File(tmp.toFile().getAbsolutePath() + "/data/minecraft/"), new File(dest + "/"));
//				System.out.print("....\n");

				System.out.println("Work finished.");
			} else {
				System.out.println("Unknown task.");
			}
		}

		f.close();
	}

	static int trds = 0;
	static int rm;
	public static int mx = 10;

	public static String clears = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";

	public static void clearConsole() {
		System.out.println(clears);
	}
}
