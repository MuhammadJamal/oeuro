package com.example.goeuro;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.example.goeuro.model.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class SearchActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	private SearchView fromSearchView;
	private SearchView toSearchView;
	private Button srchBtn;
	private LocationClient mLocationClient;
	private SearchView selectedSearchView;

	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private OnQueryTextListener fromOnQueryTextListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			selectedSearchView = fromSearchView;
			refreshSearchButton();
			return false;
		}
	};

	private OnQueryTextListener toOnQueryTextListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			selectedSearchView = toSearchView;
			refreshSearchButton();
			return false;
		}

	};

	private void refreshSearchButton() {
		if (!fromSearchView.getQuery().toString().isEmpty()
				&& !toSearchView.getQuery().toString().isEmpty()) {
			srchBtn.setEnabled(true);
		} else {
			srchBtn.setEnabled(false);
		}
	}

	private OnClickListener srchBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplication(),
					R.string.search_is_not_yet_implemented, Toast.LENGTH_LONG)
					.show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		fromSearchView = (SearchView) findViewById(R.id.formSearchView);
		fromSearchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		toSearchView = (SearchView) findViewById(R.id.toSearchView);
		toSearchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		fromSearchView.setOnQueryTextListener(fromOnQueryTextListener);
		toSearchView.setOnQueryTextListener(toOnQueryTextListener);

		srchBtn = (Button) findViewById(R.id.srch_button);
		srchBtn.setOnClickListener(srchBtnClickListener);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// a suggestion was clicked... do something about it...
			String string = intent.getDataString();
			selectedSearchView.setQuery(string, true);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {
		UserLocation.setLocation(location);
	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
		UserLocation.setLocation(mLocationClient.getLastLocation());
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onDisconnected() {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

}
