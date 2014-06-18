package org.openntf.filesilo.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openntf.domino.DateTime;
import org.openntf.domino.Document;
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

		Session session = XSPUtil.getCurrentSession();
		boolean isAdmin = session.getCurrentDatabase().queryAccessRoles(
				session.getEffectiveUserName()).contains("[Admin]");
		boolean show = false;

		ViewEntryCollection col = session.getCurrentDatabase().getView("logs").getAllEntries();

		for (ViewEntry ent : col) {
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
		jsobj.put("count", count);
		jsobj.put("activities", collections);
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
}
