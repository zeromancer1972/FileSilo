package org.openntf.filesilo.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

/**
 * Class to produce JSON output of all collections in the database depending of your role and maybe the key in the URL
 * 
 * @author Oliver Busse (obusse@gmail.com)
 * 
 */
public class FilesJson implements Serializable {

	private static final long serialVersionUID = 3664069373171425206L;
	private String json;

	@SuppressWarnings("unchecked")
	public FilesJson() {
		// collections
		ArrayList<Object> collections = new ArrayList<Object>();

		// JSON output
		JsonJavaObject jsobj = new JsonJavaObject();

		// key parameter?
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		// key provided?
		String key = params.get("key");
		jsobj.put("key", key);
		key = (key == null) ? "" : key;

		int count = 0;

		Session session = ExtLibUtil.getCurrentSession();
		try {
			boolean isAdmin = session.getCurrentDatabase().queryAccessRoles(session.getEffectiveUserName()).contains("[Admin]");
			boolean show = false;

			ViewEntryCollection col = session.getCurrentDatabase().getView("files").getAllEntries();
			ViewEntry tmp = null;
			ViewEntry ent = col.getFirstEntry();
			while (ent != null) {
				tmp = col.getNextEntry(ent);
				Document doc = ent.getDocument();

				// put only visible collections in the output (admin usage or
				// the key is equal)
				// reader fields are natively covered by the allentries
				// entrycollection
				show = isAdmin;
				if (!key.equals("")) {
					show = key.equalsIgnoreCase(doc.getItemValueString("fileKey"));
				}

				if (params.get("upload") != null) {
					show = doc.hasItem("fileUpload");
				}

				if (show) {
					HashMap<String, Object> collection = new HashMap<String, Object>();
					collection.put("unid", doc.getUniversalID());
					collection.put("id", doc.getItemValueString("fileId"));
					collection.put("key", doc.getItemValueString("fileKey"));
					collection.put("desc", doc.getItemValueString("fileMessage"));
					collection.put("readers", doc.getItemValue("fileReaders"));
					collection.put("authors", doc.getItemValue("fileAuthors"));
					collection.put("upload", doc.hasItem("fileUpload"));
					collection.put("creator", getCommonName(doc.getItemValueString("fileCreator")));

					Vector dt = doc.getItemValue("fileExpires");
					try {
						DateTime dta = (DateTime) dt.elementAt(0);
						collection.put("expires", dta.toString());
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
					count++;
				}
				ent = tmp;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// store count of all files
		jsobj.put("count", count);
		jsobj.put("collections", collections);
		this.json = "{}";
		try {
			this.json = JsonGenerator.toJson(JsonJavaFactory.instanceEx, jsobj);
		} catch (JsonException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getJson() {
		return json;
	}

	private String getCommonName(String name) throws NotesException {
		return ExtLibUtil.getCurrentSession().createName(name).getCommon();
	}

}
