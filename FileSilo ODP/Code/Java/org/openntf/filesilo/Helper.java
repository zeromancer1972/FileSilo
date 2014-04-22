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

}
