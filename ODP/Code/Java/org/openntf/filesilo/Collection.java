package org.openntf.filesilo;

import java.io.Serializable;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.Session;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Collection implements Serializable {

	private static final long serialVersionUID = -1626951109590800196L;
	private final String noFiles = "There are no files in this collection";

	@SuppressWarnings("unchecked")
	public String getAttachmentList(final String docid) {

		String list = "";
		try {
			Document doc = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(docid);
			Session session = ExtLibUtil.getCurrentSession();
			Vector v = session.evaluate("@AttachmentNames", doc);

			list = "<ul>";
			for (int x = 0; x < v.size(); x++) {
				list += "<li>";
				if(v.elementAt(x).equals(""))
					return this.noFiles;
				list += v.elementAt(x);
				list += "</li>";
			}
			list += "</ul>";

		} catch (Exception e) {
			return this.noFiles;
		}

		return list;

	}

}
