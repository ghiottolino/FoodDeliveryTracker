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
import android.util.Log;



public class DriverTrackerService {

	private static DriverTrackerService instance;

	private static final String TAG = "DriverTrackerService";

	private HttpClient appEngineCouchHttpClient;
	private HttpPut appEngineHttpPost;
	private boolean requestInProgress = false;

	

	public static DriverTrackerService getInstance(String driverName) {
		if (instance == null)
			instance = new DriverTrackerService(driverName);
		return instance;
	}

	public DriverTrackerService(String driverName) {

		appEngineCouchHttpClient = new DefaultHttpClient();
		appEngineHttpPost = new HttpPut(
				"http://fooddeliverytracker.appspot.com/resources/drivers/"+driverName);

	}
	


	private class PutJsonTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			if (requestInProgress)
				return "OK";

			try {
				// Add your data
				Date now = new Date();
				JSONObject json = new JSONObject();
				JSONObject positionJson = new JSONObject();

				positionJson.put("latitude", Double.parseDouble(params[1]));
				positionJson.put("longitude", Double.parseDouble(params[2]));

				json.put("name", params[0]);
				json.put("position", positionJson);
				// json.put("deliveryTime", new Date());
				json.put("lastUpdated", new Date().getTime());
				
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

					requestInProgress = true;
					HttpResponse response = appEngineCouchHttpClient
						.execute(appEngineHttpPost);
					Log.i(TAG, "HTTP Resp Code:"
							+ response.getStatusLine().toString());

					String temp = EntityUtils.toString(response.getEntity());
					Log.i(TAG, "HTTP Resp Body:" + temp);

				} catch (ClientProtocolException e) {
					Log.e(TAG, e.toString());
				} finally {
					requestInProgress = false;
				}
				// // nothing


			} catch (Exception e) {
				// nothing
				Log.e(TAG, e.toString());
			}
			return "OK";
		}
	}
	
	



	

	public void updateDriverLocation(String driverName, Location location) {
		

		if (driverName != null && !driverName.equals("")) {

			PutJsonTask appEngine = new PutJsonTask();
			appEngine.execute(new String[] { driverName,
					Double.toString(location.getLatitude()),
					Double.toString(location.getLongitude()) });
		}

	}

}
