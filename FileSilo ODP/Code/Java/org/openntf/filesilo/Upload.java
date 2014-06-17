package org.openntf.filesilo;

import java.io.IOException;
import java.io.Serializable;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import org.apache.http.client.ClientProtocolException;
import org.openntf.domino.utils.XSPUtil;

import com.ibm.commons.util.io.json.JsonException;

public class Upload implements Serializable {
	/**
	 * Members
	 */
	private static final long serialVersionUID = 3037095703579052234L;
	private Session session;
	private Document profile = null;
	private Database db;
	private String creator = "";
	private String adr = "";
	private String msg = "A new upload has been posted in <DB>";
	private int adjust = 30;

	// Pushover
	private String userToken;
	private String appToken;
	private String url;

	public Upload() {
		String dbtitle = "";
		String dbpath = "";
		try {
			this.session = XSPUtil.getCurrentSession();
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
			if (!doc.getItemValueString("profileMessage").equals("")) {
				this.msg = doc.getItemValueString("profileMessage");
			}
			this.msg = this.msg.replace("<DB>", this.db.getTitle());
			this.adjust = doc.getItemValueInteger("profileAdjust");

			// Pushover
			this.userToken = doc.getItemValueString("profileUserToken");
			this.appToken = doc.getItemValueString("profileAppToken");
			this.url = doc.getItemValueString("profileURL");

			doc.recycle();
		} catch (NotesException e) {
			System.out.println("FileSilo: please provide a profile settings document in " + dbtitle
					+ ", " + dbpath);
		}
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void sendInfo() {
		if (this.profile == null)
			return;
		sendPushover();
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

	public void setCreator(final String creator) {
		this.creator = creator;
	}

	public void sendPushover() {
		if (this.profile == null)
			return;

		Pushover p = new Pushover();
		p.setUserToken(this.userToken);
		p.setAppToken(this.appToken);
		p.setMessage(this.creator + ": " + this.msg);
		p.setUrl(this.url);
		try {
			p.send();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getAdjust() {
		return adjust;
	}

}
