package com.olb;

import java.io.Serializable;
import java.util.Date;

import lotus.domino.Document;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Log implements Serializable {

	private static final long serialVersionUID = -3332856685880091459L;

	public Log() {

	}

	public void add(String subject, String message) {
		try {
			Document doc = ExtLibUtil.getCurrentDatabase().createDocument();
			doc.replaceItemValue("Form", "log");
			doc.replaceItemValue("$PublicAccess", "1");
			doc.replaceItemValue("logTimestamp", ExtLibUtil.getCurrentSession().createDateTime(new Date()));
			doc.replaceItemValue("logSubject", subject);
			doc.replaceItemValue("logMessage", message);
			doc.save(true, false);
			doc.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
