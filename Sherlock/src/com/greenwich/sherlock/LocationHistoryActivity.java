package com.greenwich.sherlock;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.greenwich.sherlock.adapter.LocationHistoryAdapter;
import com.greenwich.sherlock.database.UserLocationDataSource;
import com.greenwich.sherlock.entity.UserLocation;
import com.greenwich.sherlock.util.Config;

public class LocationHistoryActivity extends Activity implements OnItemClickListener {
	
	private ListView mLvHistory;
	private LocationHistoryAdapter mHistoryAdapter;
	
	private List<UserLocation> mUserLocations;
	private UserLocationDataSource mUserLocationDataSource;
	
	private int mUserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_location_history);

		mUserLocationDataSource = new UserLocationDataSource(this);
		mUserLocationDataSource.open();
		
		mLvHistory = (ListView) findViewById(R.id.lvHistory);
		mLvHistory.setOnItemClickListener(this);
		
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mUserId = intent.getIntExtra(Config.USER_ID, -1);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		mUserLocations = mUserLocationDataSource.getAllLocationByUser(mUserId);
		mHistoryAdapter = new LocationHistoryAdapter(this, mUserLocations);
		mLvHistory.setAdapter(mHistoryAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long id) {
		Intent intent = new Intent(LocationHistoryActivity.this, AddLocationActivity.class);
		intent.putExtra(Config.USER_LOCATION_OBJECT, mUserLocations.get(postion));
		intent.putExtra(Config.USER_ID, mUserId);
		intent.putExtra(Config.IS_VIEW, true);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mUserLocationDataSource.close();
	}
}
