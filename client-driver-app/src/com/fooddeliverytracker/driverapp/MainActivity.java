package com.fooddeliverytracker.driverapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class MainActivity extends Activity {

	protected static final String TAG = "MainActivity";

	private static boolean tracking = false;

	private Button startTrackingButton;
	private Button stopTrackingButton;
	private Dialog errorDialog;

	private static final int ERROR_DIALOG_ON_CREATE_REQUEST_CODE = 4055;
	private static final int ERROR_DIALOG_ON_RESUME_REQUEST_CODE = 4056;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
				
				Intent intent = new Intent(getApplicationContext(), DriverBackgroundLocationService.class);
			    startService(intent);
				
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
				
				Intent intent = new Intent(getApplicationContext(), DriverBackgroundLocationService.class);
			    stopService(intent);
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



	

	@Override
	public void onPause() {
		super.onPause();

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		Intent intent = new Intent(getApplicationContext(), DriverBackgroundLocationService.class);
	    stopService(intent);
		
	}

	@Override
	public void onResume() {
		super.onResume();

		checkGooglePlayServiceAvailability(ERROR_DIALOG_ON_RESUME_REQUEST_CODE);

	}


	private void checkGooglePlayServiceAvailability(int requestCode) {
		// Query for the status of Google Play services on the device
		int statusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (statusCode == ConnectionResult.SUCCESS) {
			//init();
			//everything is good, do nothing
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
