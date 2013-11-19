package com.greenwich.sherlock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.greenwich.sherlock.util.LocationUtil;

public class AddLocationActivity extends Activity implements OnClickListener,
		LocationListener {

	private ImageButton mIbLocation;
	private EditText mEtTime;
	private EditText mEtAddress;
	private EditText mEtNote;

	LocationManager Locationm;
	public static Location lastbestlocation_trck = null;
	public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	public static final long MIN_TIME_BW_UPDATES = 1000; // 1 minute

	private LocationUtil mLocationUtil;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_location);

		mLocationUtil = new LocationUtil(this);

		mIbLocation = (ImageButton) findViewById(R.id.ibLocation);
		mIbLocation.setOnClickListener(this);

		mEtTime = (EditText) findViewById(R.id.etTime);
		mEtAddress = (EditText) findViewById(R.id.etAddress);
		mEtNote = (EditText) findViewById(R.id.etNote);

		if (lastbestlocation_trck == null) {
			Locationm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// ——–Gps provider—
			Locationm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

			// ——–Network provider—
			Locationm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(getApplicationContext(), "gahdfkls", Toast.LENGTH_SHORT)
				.show();
		try {
			double latitude = 0, longitude = 0;
			if (isBetterLocation(location, lastbestlocation_trck)) {
				lastbestlocation_trck = location;
				latitude = location.getLatitude();
				longitude = location.getLongitude();

				Toast.makeText(getApplicationContext(), latitude + "",
						Toast.LENGTH_SHORT).show();

//				Geocoder geocoder;
//				List<Address> addresses = new ArrayList<Address>();
//				geocoder = new Geocoder(this, Locale.getDefault());
//				try {
//					addresses = geocoder
//							.getFromLocation(latitude, longitude, 1);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				String address = addresses.get(0).getAddressLine(0);
//				String city = addresses.get(0).getAddressLine(1);
//				String country = addresses.get(0).getAddressLine(2);
//
//				Time time = new Time();
//				time.setToNow();
//
//				mEtAddress.setText(address + " : " + city + " : " + country);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
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

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			if (location.getProvider().contains("gps")) {
				return true;
			} else {
				return false;
			}
		}

		// Check whether the new location fix is newer or older
		boolean isSignificantlyNewer = location.getTime() > currentBestLocation
				.getTime();

		// If it’s been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (!isSignificantlyNewer) {
			return false;
		}
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isSignificantlyNewer && !isLessAccurate) {
			return true;
		} else if (isSignificantlyNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onClick(View v) {
		if (v == mIbLocation) {
			getLocation();
			Calendar cal = Calendar.getInstance();
			java.util.Date date = cal.getTime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = df.format(date);
			mEtTime.setText(currentTime);

			dialog = ProgressDialog.show(AddLocationActivity.this, "", "Please wait..", true);
			GetCurrentAddress currentadd = new GetCurrentAddress();
			currentadd.execute();
		}
	}

	public String getAddress(Context ctx, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);

				String locality = address.getLocality();
				String city = address.getCountryName();
				String region_code = address.getCountryCode();
				String zipcode = address.getPostalCode();
				double lat = address.getLatitude();
				double lon = address.getLongitude();

				result.append(locality + " ");
				result.append(city + " " + region_code + " ");
				result.append(zipcode);

			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	private class GetCurrentAddress extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			// this lat and log we can get from current location but here we
			// given hard coded
			double latitude = 12.916523125961666;
			double longitude = 77.61959824603072;
//			double latitude = lastbestlocation_trck.getLatitude();
//			double longitude = lastbestlocation_trck.getLongitude();

			String address = getAddress(AddLocationActivity.this, latitude,
					longitude);
			return address;
		}

		@Override
		protected void onPostExecute(String resultString) {
			dialog.dismiss();
			mEtAddress.setText(resultString);
		}
	}
	
	public double getLocation()
    {
     // Get the location manager
     LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
     Criteria criteria = new Criteria();
     String bestProvider = locationManager.getBestProvider(criteria, false);
     Location location = locationManager.getLastKnownLocation(bestProvider);
     double lat, lon;
     try {
       lat = location.getLatitude ();
       lon = location.getLongitude ();
       Log.d("KienLT", "test fasfdsa = " + lat);
       return lat;
     }
     catch (NullPointerException e){
         e.printStackTrace();
       return 0;
     }
    }
}
