package org.openntf.filesilo;

import java.io.Serializable;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.Session;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class RightsManagement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8157811105680678241L;

	public RightsManagement() {

	}

	/**
	 * Calculated all name fields to canonical form
	 * 
	 * @param doc
	 */
	@SuppressWarnings("unchecked")
	public void setNames(Document doc) {
		Session session = ExtLibUtil.getCurrentSession();
		Vector names = null;
		String items[] = { "fileAdmin", "fileReaders", "fileAuthors" };
		try {
			for (String item : items) {
				names = session.evaluate("@Name([Canonicalize]; " + item + ")", doc);
				doc.replaceItemValue(item, names);
			}
			doc.save();

		} catch (Exception e) {

		}
	}

}
