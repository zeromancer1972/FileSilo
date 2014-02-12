package org.openntf.filesilo;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Upload {
	private Session session;
	private Document profile = null;
	private Database db;
	private String adr = "";

	public Upload() {
		String dbtitle = "";
		String dbpath = "";
		try {
			this.session = ExtLibUtil.getCurrentSession();
			this.db = session.getCurrentDatabase();
			dbtitle = db.getTitle();
			dbpath = db.getFilePath();
			View v = db.getView("profiles");
			Document doc = v.getDocumentByKey("profile", true);
			if (doc == null) {
				return;
			}
			this.profile = doc;
			adr = doc.getItemValueString("profileMail");
			doc.recycle();
		} catch (NotesException e) {
			System.out.println("FileSilo: please provide a profile settings document in " + dbtitle + ", " + dbpath);
		}
	}

	public void sendInfo() {
		if (this.profile == null)
			return;
		try {
			Document mail = this.db.createDocument();
			if (this.adr.equals(""))
				return;
			mail.replaceItemValue("Form", "Memo");
			mail.replaceItemValue("SendTo", adr);
			mail.replaceItemValue("Recipients", adr);
			mail.replaceItemValue("Subject", "A new upload has been posted in " + this.db.getTitle());
			mail.send();
			mail.recycle();
		} catch (NotesException e) {

		}

	}
}
