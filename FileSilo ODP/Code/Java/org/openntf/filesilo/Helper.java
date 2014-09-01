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

	
	public static String byteName(final long bytes, final boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

	}

	public static String byteName(final long size[], final boolean si) {
		long bytes = 0;
		for (int x = 0; x < size.length; x++) {
			bytes += size[x];
		}
		return byteName(bytes, si);
	}

}
