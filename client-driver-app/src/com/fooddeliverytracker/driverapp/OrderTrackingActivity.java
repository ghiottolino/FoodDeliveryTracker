package com.fooddeliverytracker.driverapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class OrderTrackingActivity extends Activity {

	protected static final String TAG = "MainActivity";
	private EditText order1Text;
	private EditText order2Text;
	private EditText order3Text;
	private EditText address1Text;

	private static boolean tracking = false;

	private Button startTrackingButton;
	private Button stopTrackingButton;
	private Dialog errorDialog;
	// Location Request variables
	private LocationClient mLocationClient;
	private LocationCallback mLocationCallback = new LocationCallback();
	private Location mLastLocation;
	private final int LOCATION_UPDATES_INTERVAL = 20000;
	public static boolean isAppForeground = false;
	private static final int ERROR_DIALOG_ON_CREATE_REQUEST_CODE = 4055;
	private static final int ERROR_DIALOG_ON_RESUME_REQUEST_CODE = 4056;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_tracking);

		order1Text = (EditText) findViewById(R.id.orderNumber1);
		address1Text = (EditText) findViewById(R.id.orderAddress1);
		// order2Text = (EditText) findViewById(R.id.orderNumber2);
		// order3Text = (EditText) findViewById(R.id.orderNumber3);

		startTrackingButton = (Button) findViewById(R.id.start);
		stopTrackingButton = (Button) findViewById(R.id.stop);

		if (tracking) {
			startTrackingButton.setEnabled(false);
			stopTrackingButton.setEnabled(true);
		} else {
			startTrackingButton.setEnabled(true);
			stopTrackingButton.setEnabled(false);
		}

		// start button
		startTrackingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Start tracking position for orders");
				stopTrackingButton.setEnabled(true);
				startTrackingButton.setEnabled(false);
				tracking = true;
			}
		});
		// stop button

		stopTrackingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Stop tracking position for orders");
				stopTrackingButton.setEnabled(false);
				startTrackingButton.setEnabled(true);
				tracking = false;
			}
		});

		// not really important
		order1Text.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					// Do something
					handled = true;
				}
				return handled;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
		return true;

	};

	private void init() {

		if (mLocationClient == null) {
			mLocationClient = new LocationClient(this, mLocationCallback,
					mLocationCallback);
			Log.v(TAG, "Location Client connect");
			if (!(mLocationClient.isConnected() || mLocationClient
					.isConnecting())) {
				mLocationClient.connect();
			}
		}
	}

	private class LocationCallback implements ConnectionCallbacks,
			OnConnectionFailedListener, LocationListener {

		@Override
		public void onConnected(Bundle connectionHint) {
			Log.v(TAG, "Location Client connected");

			// Display last location
			Location location = mLocationClient.getLastLocation();
			if (location != null) {
				handleLocation(location);
			}

			// Request for location updates
			LocationRequest request = LocationRequest.create();
			request.setInterval(LOCATION_UPDATES_INTERVAL);
			request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationClient.requestLocationUpdates(request, mLocationCallback);

			// Setup map to allow adding Geo Fences
		}

		@Override
		public void onDisconnected() {
			Log.v(TAG, "Location Client disconnected by the system");
		}

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			Log.v(TAG, "Location Client connection failed");
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location == null) {
				Log.v(TAG, "onLocationChanged: location == null");
				return;
			}
			// Add a marker iff location has changed.
			if (mLastLocation != null
					&& mLastLocation.getLatitude() == location.getLatitude()
					&& mLastLocation.getLongitude() == location.getLongitude()) {
				return;
			}

			handleLocation(location);
		}

		private void handleLocation(Location location) {
			// Update the mLocationStatus with the lat/lng of the location
			Log.v(TAG, "LocationChanged == @" + location.getLatitude() + ","
					+ location.getLongitude());
			if (tracking) {
				String order1Id = order1Text.getText().toString();
				String address1 = address1Text.getText().toString();
				// String order2Id = order2Text.getText().toString();
				// String order3Id = order3Text.getText().toString();

				// TODO : one request instead of 3
				LocationTrackerService.getInstance().updateLocation(order1Id,
						address1, location);
				// LocationTrackerService.getInstance().updateLocation(order2Id,
				// location);
				// LocationTrackerService.getInstance().updateLocation(order3Id,
				// location);
			}

			mLastLocation = location;
		}

	};

	@Override
	public void onPause() {
		super.onPause();

		// Indicate the application is in background
		isAppForeground = false;

		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(mLocationCallback);
			mLocationClient.disconnect();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Indicate the application is in foreground
		isAppForeground = true;

		checkGooglePlayServiceAvailability(ERROR_DIALOG_ON_RESUME_REQUEST_CODE);

		restartLocationClient();
	}

	private void restartLocationClient() {
		if (!(mLocationClient.isConnected() || mLocationClient.isConnecting())) {
			mLocationClient.connect(); // Somehow it becomes connected here
			return;
		}

		if (mLocationClient.isConnecting()) {
			return;
		}

		LocationRequest request = LocationRequest.create();
		request.setInterval(LOCATION_UPDATES_INTERVAL);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationClient.requestLocationUpdates(request, mLocationCallback);
	}

	private void checkGooglePlayServiceAvailability(int requestCode) {
		// Query for the status of Google Play services on the device
		int statusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (statusCode == ConnectionResult.SUCCESS) {
			init();
		} else {
			if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
				errorDialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
						this, requestCode);
				errorDialog.show();
			} else {
				// Handle unrecoverable error
			}
		}
	}

}
