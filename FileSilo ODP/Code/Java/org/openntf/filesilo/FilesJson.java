package org.openntf.filesilo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

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

/**
 * Class to produce JSON output of all collections in the database depending of
 * your role and maybe the key in the URL
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

		Session session = XSPUtil.getCurrentSession();
		boolean isAdmin = session.getCurrentDatabase().queryAccessRoles(
				session.getEffectiveUserName()).contains("[Admin]");
		boolean show = false;

		ViewEntryCollection col = session.getCurrentDatabase().getView("files").getAllEntries();

		for (ViewEntry ent : col) {
			Document doc = ent.getDocument();

			// put only visible collections in the output (admin usage or
			// the key is equal)
			// reader fields are natively covered by the allentries
			// entrycollection
			show = isAdmin;
			if(!key.equals("")){
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
				count++;
			}
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

	private String getCommonName(String name) {
		return XSPUtil.getCurrentSession().createName(name).getCommon();
	}

}
