package org.openntf.filesilo;

import java.io.Serializable;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Upload implements Serializable {
	/**
	 * Members
	 */
	private static final long serialVersionUID = 3037095703579052234L;
	private Session session;
	private Document profile = null;
	private Database db;
	private String adr = "";
	private String msg = "A new upload has been posted in <DB>";
	private int adjust = 30;

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
			this.adr = doc.getItemValueString("profileMail");
			this.msg = doc.getItemValueString("profileMessage");
			this.adjust = doc.getItemValueInteger("profileAdjust");
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
			mail.replaceItemValue("Subject", this.msg.replace("<DB>", this.db.getTitle()));
			mail.send();
			mail.recycle();
		} catch (NotesException e) {

		}

	}

	public int getAdjust() {
		return adjust;
	}

}
