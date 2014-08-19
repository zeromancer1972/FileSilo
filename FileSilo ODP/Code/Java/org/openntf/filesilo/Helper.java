package org.openntf.filesilo;

import java.io.Serializable;

import org.openntf.domino.utils.XSPUtil;

public class Helper implements Serializable {

	private static final long serialVersionUID = -7772345041173618151L;

	public Helper() {

	}

	public void removeLogs() {
		try {
			XSPUtil.getCurrentDatabase().getView("logs").getAllEntries().removeAll(false);
		} catch (Exception e) {

		}
	}

	/**
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-
	 * human-readable-format-in-java
	 * 
	 * @param bytes
	 * @param si
	 * @return
	 */
	public static String humanReadableByteCount(final long bytes, final boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

	}

	public static String humanReadableByteCount(final long size[], final boolean si) {
		long bytes = 0;
		for (int x = 0; x < size.length; x++) {
			bytes += size[x];
		}
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
