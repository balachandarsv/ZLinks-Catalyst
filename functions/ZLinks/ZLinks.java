import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.catalyst.advanced.CatalystAdvancedIOHandler;
import com.zc.component.cache.ZCCache;
import com.zc.component.cache.ZCCacheObject;
import com.zc.component.cache.ZCSegment;
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import com.zc.component.object.ZCTable;
import com.zc.component.search.ZCSearch;
import com.zc.component.search.ZCSearchDetails;

public class ZLinks implements CatalystAdvancedIOHandler {
	private static final Logger LOGGER = Logger.getLogger(ZLinks.class.getName());

	@Override
	public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String name = (String) request.getParameter("name");
			String clear = (String) request.getParameter("clear");

			ZCCache cacheobj = ZCCache.getInstance();
			ZCSegment segment = cacheobj.getSegmentInstance(2627000000414073L);
			if (name != null) {
				if (clear != null) {
					segment.deleteCacheObject(segment.getCacheObject(name));
					response.setStatus(200);
					return;
				}
				String cacheValue = segment.getCacheValue(name);
				ZCObject object = ZCObject.getInstance();
				ZCTable table = object.getTable(2627000000407024L);
				if (cacheValue != null) {
					try {
						JSONObject json = new JSONObject(cacheValue);
						Long id = json.getLong("id");
						ZCRowObject row = table.getRow(id);
						row.set("visits", json.getLong("visits") + 1);
						Runnable th = new Runnable() {
							public void run() {
								List<ZCRowObject> urows = new ArrayList<ZCRowObject>();
								urows.add(row);
								try {
									table.updateRows(urows);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						};
						th.run();
						json.put("visits", json.getLong("visits") + 1);
						ZCCacheObject cache = segment.putCacheValue(name, json.toString(), 1L);
						json.remove("id");
						response.getWriter().write(json.toString());
					} catch (Exception e) {
						e.printStackTrace();
						response.getWriter().write("{}");
					}
				} else {

					ZCObject obj = ZCObject.getInstance();
					ZCTable tab = obj.getTable(2627000000407024L);
					ZCRowObject drow = tab.getRow(Long.parseLong(name));

					drow.set("visits", Long.parseLong((String) (drow.get("visits"))) + 1);
					Runnable th = new Runnable() {
						public void run() {
							List<ZCRowObject> urows = new ArrayList<ZCRowObject>();
							urows.add(drow);
							try {
								table.updateRows(urows);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};

					th.run();

					JSONObject data = new JSONObject();
					JSONObject social = new JSONObject();
					org.json.simple.JSONObject drowJSON = drow.getRowObject();
					social.put("whatsapp", (drowJSON.get("whatsapp")) != null ? drowJSON.get("whatsapp") : "");
					social.put("youtube", (drowJSON.get("youtube")) != null ? drowJSON.get("youtube") : "");
					social.put("showin", drowJSON.get("showin"));
					social.put("linkedin", (drowJSON.get("linkedin")) != null ? drowJSON.get("linkedin") : "");
					social.put("instagram", (drowJSON.get("instagram")) != null ? drowJSON.get("instagram") : "");
					social.put("showlin", drowJSON.get("showlin"));
					social.put("showfb", drowJSON.get("showfb"));
					social.put("twitter", (drowJSON.get("twitter")) != null ? drowJSON.get("twitter") : "");
					social.put("github", (drowJSON.get("github")) != null ? drowJSON.get("github") : "");
					social.put("facebook", (drowJSON.get("facebook")) != null ? drowJSON.get("facebook") : "");
					social.put("showds", drowJSON.get("showds"));
					social.put("showtw", drowJSON.get("showtw"));
					social.put("showwa", drowJSON.get("showwa"));
					social.put("discord", (drowJSON.get("discord")) != null ? drowJSON.get("discord") : "");
					social.put("showyt", drowJSON.get("showyt"));
					social.put("showgh", drowJSON.get("showgh"));

					data.put("social", social);
					String zuid = (String) drowJSON.get("zuid");
					JSONObject background = new JSONObject();
					background.put("gradient", true);
					background.put("code", drowJSON.get("code"));
					data.put("description", drowJSON.get("description"));
					data.put("title", drowJSON.get("title"));
					background.put("type", drowJSON.get("type"));
					data.put("logo", drowJSON.get("logo"));
					background.put("text", drowJSON.get("text"));
					data.put("id", drowJSON.get("ROWID"));
					data.put("search", "");
					data.put("visits", drowJSON.get("visits"));
					data.put("category", "");
					data.put("views", 0);
					data.put("showmd", false);

					JSONArray links = new JSONArray();

					ZCTable linkstable = obj.getTable(2627000000407766L);
					List<ZCRowObject> rows = linkstable.getAllRows();

					for (ZCRowObject xrow : rows) {
						org.json.simple.JSONObject linkjson = xrow.getRowObject();

						if (zuid.equalsIgnoreCase((String) linkjson.get("zuid"))) {

							Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
									.parse((String) linkjson.get("CREATEDTIME"));

							JSONObject link = new JSONObject();
							link.put("image", linkjson.get("image"));
							link.put("description", linkjson.get("description"));
							link.put("title", linkjson.get("title"));
							link.put("url", linkjson.get("url"));
							link.put("id", linkjson.get("ROWID"));
							link.put("time", date.getTime() / 1000);
							link.put("highlight", linkjson.get("highlight"));
							link.put("linkid", linkjson.get("linkid"));
							link.put("category", linkjson.get("category"));
							links.put(link);

						}
					}
					data.put("links", links);

					ZCCacheObject cache = segment.putCacheValue(name, data.toString(), 1L);
					data.remove("id");
					response.getWriter().write(data.toString());

				}
			}
			response.setStatus(200);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in ZLinks", e);
			response.setStatus(500);
		}

	}

}