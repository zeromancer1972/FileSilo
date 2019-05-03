package org.openntf.filesilo.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

/**
 * Class to produce JSON output of all collections in the database depending of your role and maybe the key in the URL
 * 
 * @author Oliver Busse (obusse@gmail.com)
 * 
 */
public class ActivitiesJson implements Serializable {

	private static final long serialVersionUID = 3664069373171425206L;
	private String json;

	@SuppressWarnings("unchecked")
	public ActivitiesJson() {
		// collections
		ArrayList<Object> collections = new ArrayList<Object>();

		// JSON output
		JsonJavaObject jsobj = new JsonJavaObject();
		int count = 0;
		try {
			Session session = ExtLibUtil.getCurrentSession();
			boolean isAdmin = session.getCurrentDatabase().queryAccessRoles(session.getEffectiveUserName()).contains("[Admin]");
			boolean show = false;

			ViewEntryCollection col = session.getCurrentDatabase().getView("logs").getAllEntries();

			ViewEntry ent = col.getFirstEntry();
			while (ent != null) {
				Document doc = ent.getDocument();

				// put only visible collections in the output (admin usage or
				// the key is equal)
				// reader fields are natively covered by the allentries
				// entrycollection
				show = isAdmin;

				if (show) {
					HashMap<String, Object> collection = new HashMap<String, Object>();
					collection.put("unid", doc.getUniversalID());
					collection.put("what", doc.getItemValueString("logSubject"));
					collection.put("details", doc.getItemValueString("logMessage"));

					// date time stuff is a hassle...
					List<DateTime> dtArray = doc.getItemValue("logTimestamp");
					try {
						collection.put("when", dtArray.get(0).toString());
					} catch (Exception e) {
						collection.put("when", null);
					}

					collections.add(collection);
					count++;
				}
			}
			// store count of all files
		} catch (Exception e) {

		}
		jsobj.put("count", count);
		jsobj.put("activities", collections);
		this.json = "{}";
		try {
			this.json = JsonGenerator.toJson(JsonJavaFactory.instanceEx, jsobj);
		} catch (JsonException e) {
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
