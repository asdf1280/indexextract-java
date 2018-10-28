package indexextract.gui;

public class GuiUtils {
	public static void sleepQuietly(int length) {
		long a = System.currentTimeMillis() + length;
		try {
			Thread.sleep(length);
		} catch (InterruptedException e) {
			sleepQuietly(Math.max((int) (a - System.currentTimeMillis()), 0));
		}
	}
}
