import java.util.ArrayList;
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
			ZCCache cacheobj = ZCCache.getInstance();
			ZCSegment segment = cacheobj.getSegmentInstance(2627000000414073L);
			String cacheValue = segment.getCacheValue(name);
			if (cacheValue != null) {
				try {
					JSONObject json = new JSONObject(cacheValue);
					response.getWriter().write(json.toString());
				} catch (Exception e) {
					response.getWriter().write("{}");
				}
			} else {

				ZCSearchDetails search = ZCSearchDetails.getInstance();
				search.setSearch(name);
				HashMap<String, List<String>> map = new HashMap<String, List<String>>();
				List<String> searchList1 = new ArrayList<String>();
				searchList1.add("site");
				map.put("zlinks", searchList1);
				search.setSearchTableColumns(map);
				ArrayList<ZCRowObject> rowList = ZCSearch.getInstance().executeSearchQuery(search);
				if (rowList.size() > 0) {
					ZCRowObject row = rowList.get(0);

					String rowID = (String) row.get("ROWID");

					ZCObject obj = ZCObject.getInstance();
					ZCTable tab = obj.getTable(2627000000407024L);
					ZCRowObject drow = tab.getRow(Long.parseLong(rowID));
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

					data.put("search", "");
					data.put("category", "");
					data.put("views", 0);
					data.put("showmd", false);

					JSONArray links = new JSONArray();

					ZCTable linkstable = obj.getTable(2627000000407766L);
					List<ZCRowObject> rows = linkstable.getAllRows();

					for (ZCRowObject xrow : rows) {
						org.json.simple.JSONObject linkjson = xrow.getRowObject();
						JSONObject link = new JSONObject();
						link.put("image", linkjson.get("image"));
						link.put("description", linkjson.get("description"));
						link.put("title", linkjson.get("title"));
						link.put("url", linkjson.get("url"));
						link.put("zuid", linkjson.get("zuid"));
						link.put("highlight", linkjson.get("highlight"));
						link.put("linkid", linkjson.get("linkid"));
						link.put("category", linkjson.get("category"));
						links.put(link);
					}
					data.put("links", links);

					response.getWriter().write(data.toString());

					LOGGER.log(Level.INFO, drow.getRowObject().toString());
					ZCCacheObject cache = segment.putCacheValue(name, data.toString());
				}else {
					response.getWriter().write("{}");
				}
			}
			response.setStatus(200);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in ZLinks", e);
			response.setStatus(500);
		}

	}

}