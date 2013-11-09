package com.fooddeliverytracker.driverapp;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;



public class LocationTrackerService {

	private static LocationTrackerService instance;

	private static final String TAG = "LocationTrackerService";

	private HttpClient appEngineCouchHttpClient;
	private HttpPut appEngineHttpPost;


	public static LocationTrackerService getInstance() {
		if (instance == null)
			instance = new LocationTrackerService();
		return instance;
	}

	public LocationTrackerService() {

		appEngineCouchHttpClient = new DefaultHttpClient();
		appEngineHttpPost = new HttpPut(
				"http://1.fooddeliverytracker.appspot.com/resources/orders/1");

	}
	


	private class PutJsonTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Looper.prepare();
			try {
				// Add your data
				Date now = new Date();
				JSONObject json = new JSONObject();
				JSONObject positionJson = new JSONObject();

				positionJson.put("latitude", Double.parseDouble(params[1]));
				positionJson.put("longitude", Double.parseDouble(params[2]));

				json.put("orderId", params[0]);

				json.put("position", positionJson);
				json.put("deliveryAddress", "Seilerstr. 7 Konstanz");
				// json.put("deliveryTime", new Date());
				json.put("status", "On its way");
				
				Log.i(TAG, json.toString());
				StringEntity se = new StringEntity(json.toString());

				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				appEngineHttpPost.setEntity(se);
				appEngineHttpPost.setHeader("Content-Type",
						"application/json");

				Log.i(TAG,
						"Http Req Headers" + appEngineHttpPost.getAllHeaders());
				Log.i(TAG, "Http Req Body" + json.toString());

				// Execute HTTP Post Request
				try {


				HttpResponse response = appEngineCouchHttpClient
						.execute(appEngineHttpPost);
					Log.i(TAG, "HTTP Resp Code:"
							+ response.getStatusLine().toString());
					String temp = EntityUtils.toString(response.getEntity());
					Log.i(TAG, "HTTP Resp Body:" + temp);

				} catch (ClientProtocolException e) {
					Log.e(TAG, e.toString());
				}
				// // nothing


			} catch (Exception e) {
				// nothing
				Log.e(TAG, e.toString());
			}
			Looper.loop();
			return "OK";
		}
	}
	
	


	public void updateLocation(String orderId, Location location) {
		
		// CouchDB
		// if (!activity.equalsIgnoreCase(currentActivity)) {

		if (orderId != null && !orderId.equals("") && orderId.length() == 6) {

			PutJsonTask appEngine = new PutJsonTask();
			appEngine.execute(new String[] { orderId,
					Double.toString(location.getLatitude()),
					Double.toString(location.getLongitude()) });
		}

		// }
		
		
	}

}
