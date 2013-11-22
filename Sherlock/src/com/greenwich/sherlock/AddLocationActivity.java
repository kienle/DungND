package com.greenwich.sherlock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.greenwich.sherlock.database.UserLocationDataSource;
import com.greenwich.sherlock.entity.UserLocation;
import com.greenwich.sherlock.util.Config;

@SuppressLint("SimpleDateFormat")
public class AddLocationActivity extends Activity implements OnClickListener,
		LocationListener {

	private ImageButton mIbLocation;
	private EditText mEtTime;
	private EditText mEtAddress;
	private EditText mEtNote;

	private Button mBtHistory;
	private Button mBtSave;
	
	private int mUserId;
	private UserLocation mUserLocation;
	private boolean mIsView;
	
	private UserLocationDataSource mUserLocationDataSource;

	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_location);

		mProgressDialog = new ProgressDialog(AddLocationActivity.this);
		mProgressDialog.setMessage("Loading...");
		
		mUserLocationDataSource = new UserLocationDataSource(this);
		mUserLocationDataSource.open();
		
		mIbLocation = (ImageButton) findViewById(R.id.ibLocation);
		mIbLocation.setOnClickListener(this);

		mEtTime = (EditText) findViewById(R.id.etTime);
		mEtAddress = (EditText) findViewById(R.id.etAddress);
		mEtNote = (EditText) findViewById(R.id.etNote);

		mBtSave = (Button)findViewById(R.id.btSave);
		mBtHistory = (Button) findViewById(R.id.btLocationHis);
		mBtSave.setOnClickListener(this);
		mBtHistory.setOnClickListener(this);
		 
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mIsView = intent.getBooleanExtra(Config.IS_VIEW, false);
		mUserLocation = (UserLocation) intent.getSerializableExtra(Config.USER_LOCATION_OBJECT);
		mUserId = intent.getIntExtra(Config.USER_ID, -1);
		
		if (mUserLocation != null) {
			mEtTime.setText(mUserLocation.getTime());
			mEtAddress.setText(mUserLocation.getAddress());
			mEtNote.setText(mUserLocation.getNote());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		
		GetCurrentAddress task = new GetCurrentAddress(latitude, longitude);
		task.execute();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		if (v == mIbLocation) {
//			getAddress();
			getLocation();
			Calendar cal = Calendar.getInstance();
			java.util.Date date = cal.getTime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = df.format(date);
			mEtTime.setText(currentTime);
		} else if (v == mBtSave) {
			if (mEtTime.getText().toString().trim().equals("") ||
					mEtAddress.getText().toString().trim().equals("")) {
				Toast.makeText(AddLocationActivity.this, "Please get location first!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (mIsView) {
				mUserLocation.setTime(mEtTime.getText().toString().trim());
				mUserLocation.setAddress(mEtAddress.getText().toString().trim());
				mUserLocation.setNote(mEtNote.getText().toString().trim());
				
				long result = mUserLocationDataSource.updateUserLocation(mUserLocation);
				if (result != -1) {
					Toast.makeText(AddLocationActivity.this, "Update location complete!", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(AddLocationActivity.this, "Update location fail!", Toast.LENGTH_SHORT).show();
				}
				
			} else {
				UserLocation location = new UserLocation();
				location.setUserId(mUserId);
				location.setTime(mEtTime.getText().toString().trim());
				location.setAddress(mEtAddress.getText().toString().trim());
				location.setNote(mEtNote.getText().toString().trim());
				
				long result = mUserLocationDataSource.insertUserLocation(location);
				if (result != -1) {
					Toast.makeText(AddLocationActivity.this, "Add location complete!", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(AddLocationActivity.this, "Add location fail!", Toast.LENGTH_SHORT).show();
				}
			}
			
		} else if (v == mBtHistory) {
			Intent intent = new Intent(AddLocationActivity.this, LocationHistoryActivity.class);
			intent.putExtra(Config.USER_ID, mUserId);
			startActivity(intent);
		}
	}
	
	private class GetCurrentAddress extends AsyncTask<Void, Void, String> {
		private double mLat;
		private double mLog;
		
		public GetCurrentAddress(double lat, double log) {
			this.mLat = lat;
			this.mLog = log;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... arg0) {
			StringBuilder result = new StringBuilder();
			Geocoder geocoder;
			List<Address> addresses = new ArrayList<Address>();
			geocoder = new Geocoder(AddLocationActivity.this, Locale.getDefault());
			try {
				addresses = geocoder.getFromLocation(mLat, mLog, 1);
			} catch (IOException e) {
				Log.d("KienLT", "IOException = " + e.getMessage());
			}

			if (addresses.size() > 0) {
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
	
				result.append(address + ", ");
				result.append(city + ", ");
				result.append(country);
				
				return result.toString();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result != null) {
				mEtAddress.setText(result);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mUserLocationDataSource.close();
	}
	
	double longitude = 0;
	double latitude = 0;
	
	private void getLocation() {
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			
			GetCurrentAddress task = new GetCurrentAddress(latitude, longitude);
			task.execute();
		} else {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
		}
		
	}

}
