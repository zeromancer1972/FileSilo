package org.openntf.filesilo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openntf.domino.DateTime;
import org.openntf.domino.Document;
import org.openntf.domino.EmbeddedObject;
import org.openntf.domino.Session;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ViewEntryCollection;
import org.openntf.domino.utils.XSPUtil;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;

public class FilesJson implements Serializable {

	private static final long serialVersionUID = 3664069373171425206L;
	private String json;

	@SuppressWarnings("unchecked")
	public FilesJson() {
		Session session = XSPUtil.getCurrentSession();

		if (session.getEffectiveUserName().equals("Anonymous")) {
			this.json = "{\"error\":\"you are not allowed to perform that operation\"}";
			return;
		}

		ViewEntryCollection col = session.getCurrentDatabase().getView("files").getAllEntries();
		ArrayList<Object> collections = new ArrayList<Object>();

		JsonJavaObject jsobj = new JsonJavaObject();

		// store count of all files
		jsobj.put("count", col.getCount());

		for (ViewEntry ent : col) {
			Document doc = ent.getDocument();
			HashMap<String, Object> collection = new HashMap<String, Object>();
			collection.put("unid", doc.getUniversalID());
			collection.put("id", doc.getItemValueString("fileId"));
			collection.put("key", doc.getItemValueString("fileKey"));
			collection.put("desc", doc.getItemValueString("fileMessage"));
			collection.put("readers", doc.getItemValue("fileReaders"));
			collection.put("authors", doc.getItemValue("fileAuthors"));
			collection.put("upload", doc.hasItem("fileUpload"));
			// date time stuff is a hassle...
			List<DateTime> dtArray = doc.getItemValue("fileExpires");
			try {
				collection.put("expires", dtArray.get(0).toString());
			} catch (Exception e) {
				collection.put("expires", null);
			}

			// files
			Vector attachments = session.evaluate("@AttachmentNames", doc);
			ArrayList<HashMap<String, Object>> atts = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < attachments.size(); i++) {
				EmbeddedObject eo = doc.getAttachment(attachments.get(i).toString());
				try {
					HashMap<String, Object> file = new HashMap<String, Object>();
					file.put("name", eo.getName());
					file.put("type", eo.getType());
					file.put("size", eo.getFileSize());
					atts.add(file);
				} catch (Exception e) {
					// no files
				}
			}
			collection.put("files", atts);
			collections.add(collection);
		}

		jsobj.put("collections", collections);
		this.json = "{}";
		try {
			this.json = JsonGenerator.toJson(JsonJavaFactory.instanceEx, jsobj);
		} catch (JsonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getJson() {
		return json;
	}

}
