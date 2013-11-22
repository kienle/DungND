package com.greenwich.sherlock.util;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationUtil {
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
	private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second
	private Context mContext;
	private Location mLocation;
	private LocationManager locationMgr;

	public LocationUtil(Context context) {
		this.mContext = context;
		this.locationMgr = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public Location getLocation(LocationListener mListener) {
		try {

			if (locationMgr == null) {
				locationMgr = (LocationManager) mContext
						.getSystemService(Context.LOCATION_SERVICE);
			}

			// getting GPS status
			boolean isGPSEnabled = locationMgr
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = locationMgr
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				// DialogUtil.showSettingsAlert(context,
				// R.string.gps_setting_title, R.string.gps_setting_message,
				// Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				return null;
			} else {
				if (isNetworkEnabled) {
					locationMgr.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, mListener);
					if (locationMgr != null) {
						mLocation = locationMgr
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (mLocation == null) {
						locationMgr.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, mListener);
						if (locationMgr != null) {
							mLocation = locationMgr
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mLocation;
	}

	public void stopLocationListener(LocationListener mListener) {
		if (locationMgr == null) {
			return;
		}

		locationMgr.removeUpdates(mListener);
		locationMgr = null;
	}

}
