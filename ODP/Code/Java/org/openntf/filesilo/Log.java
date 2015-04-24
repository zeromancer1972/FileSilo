package org.openntf.filesilo;

import java.io.Serializable;
import java.util.Date;

import lotus.domino.Document;
import lotus.domino.NotesException;

import org.openntf.domino.utils.XSPUtil;

public class Log implements Serializable {

	private static final long serialVersionUID = -3332856685880091459L;

	public Log() {

	}

	public void add(String subject, String message) {
		try {
			Document doc = XSPUtil.getCurrentDatabase().createDocument();
			doc.replaceItemValue("Form", "log");
			doc.replaceItemValue("$PublicAccess", "1");
			doc.replaceItemValue("logTimestamp", XSPUtil.getCurrentSession().createDateTime(new Date()));
			doc.replaceItemValue("logSubject", subject);
			doc.replaceItemValue("logMessage", message);
			doc.save(true, false);
			doc.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void add(String subject, String message, String unid) {
		try {
			Document doc = XSPUtil.getCurrentDatabase().createDocument();
			doc.replaceItemValue("Form", "log");
			doc.replaceItemValue("$PublicAccess", "1");
			doc.replaceItemValue("logTimestamp", XSPUtil.getCurrentSession().createDateTime(new Date()));
			doc.replaceItemValue("logSubject", subject);
			doc.replaceItemValue("logMessage", message);
			doc.replaceItemValue("logUnid", unid);
			doc.save(true, false);
			doc.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getUserName() throws NotesException{
		return XSPUtil.getCurrentSession().createName(XSPUtil.getCurrentSession().getEffectiveUserName()).getAbbreviated();
	}

}
