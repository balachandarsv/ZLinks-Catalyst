import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.catalyst.advanced.CatalystAdvancedIOHandler;
import com.zc.component.ZCUserDetail;
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import com.zc.component.object.ZCTable;
import com.zc.component.search.ZCSearch;
import com.zc.component.search.ZCSearchDetails;
import com.zc.component.users.ZCUser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShortenURL implements CatalystAdvancedIOHandler {
	private static final Logger LOGGER = Logger.getLogger(ShortenURL.class.getName());
	private static final String AUTHTOKEN = "sk_rqs5w7lFBMvReCWK";
	@Override
	public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String name = (String) request.getParameter("name");
			ZCUserDetail details = ZCUser.getInstance().getCurrentUser();
			OkHttpClient client = new OkHttpClient();			
			if(name!=null) {
				ZCObject obj = ZCObject.getInstance();
				ZCTable tab = obj.getTable(2627000000407024L);
				ZCRowObject drow = tab.getRow(Long.parseLong(name));
				String linkid = (String)drow.get("linkid");
				String sitename = (String)drow.get("site");
				MediaType mediaType = MediaType.parse("application/json");
				JSONObject json = new JSONObject();
				json.put("allowDuplicates", false);
				json.put("originalURL",
						"https://zlinks-687312579.development.catalystserverless.com/view?name=" + name);
				json.put("domain", "shrt.host");
				json.put("path", sitename);
				json.put("title", sitename);
				
				RequestBody body = RequestBody.create(mediaType, json.toString());
				Request req = new Request.Builder().url("https://api.short.io/links").post(body)
						.addHeader("Accept", "application/json").addHeader("Content-Type", "application/json")
						.addHeader("Authorization", AUTHTOKEN).build();

				Response resp = client.newCall(req).execute();	
				JSONObject respJSON = new JSONObject(resp.body().string());				
				drow.set("linkid", respJSON.get("id"));
				
				req = new Request.Builder()
				  .url("https://api.short.io/links/"+linkid)
				  .delete(null)
				  .addHeader("Authorization", AUTHTOKEN)
				  .build();

				client.newCall(req).execute();
				Runnable th = new Runnable() {
					public void run() {
						List<ZCRowObject> urows = new ArrayList<ZCRowObject>();
						urows.add(drow);
						try {
							tab.updateRows(urows);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				th.run();
				
				
				
					
				System.out.println();
			}
			LOGGER.log(Level.INFO, "Hello " + name);
			response.setStatus(200);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in ShortenURL", e);
			response.setStatus(500);
		}
		
	}

}