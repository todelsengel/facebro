package com.facebro.model;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class Game {

	private static final String TAG = "SelectionFragment";
	private String txtPost;
	String postagens;
	
	
	public String requestPostsList(){
		
			String fqlQuery = "SELECT actor_id,description, post_id, message FROM stream WHERE source_id IN (SELECT uid2 FROM friend WHERE uid1 = me()) LIMIT 5000";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		Request request = new Request(session, "/fql", params,
				HttpMethod.GET, new Request.Callback() {
					public void onCompleted(Response response) {
						//req = new WebMessage();
						postagens = response.toString();//parseUserFromFQLResponse(response);
						/*ReqServlet re = new ReqServlet();
						re.execute();*/
						Log.i(TAG, "Result: " + response.toString());
					}
				});
		Request.executeBatchAsync(request);
		return postagens;
	}
	
	public static final String parseUserFromFQLResponse(Response response) {
		StringBuilder userInfo = new StringBuilder("");
		try {

			GraphObject go = response.getGraphObject();
			JSONObject jso = go.getInnerJSONObject();
			JSONArray arr = jso.getJSONArray("data");

			for (int i = 0; i < (arr.length()); i++) {
				JSONObject json_obj = arr.getJSONObject(i);
				/*userInfo.append(String.format("[Post_id: %s\n\n",
						json_obj.getString("page_id")));*/
				userInfo.append(String.format("Texto: %s\n\n",
						json_obj.getString("message")));
				/*userInfo.append(String.format("Likes: %s\n\n",
						json_obj.getString("fan_count")));*/
				userInfo.append(String.format("Usuário: %s\n\n",
						json_obj.getString("actor_id")));
				userInfo.append(String.format("Tipo: %s ]\n\n",
						json_obj.getString("type")));

			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return userInfo.toString();
	}
	
	
}
