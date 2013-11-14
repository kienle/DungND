package com.greenwich.sherlock;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.greenwich.sherlock.adapter.SearchResultAdapter;
import com.greenwich.sherlock.database.UserDataSource;
import com.greenwich.sherlock.entity.User;

public class SearchListActivity extends Activity implements OnClickListener, OnItemClickListener {
	public static final String USER = "user";
	
	private Button mBtNew;
	private ListView mLvResult;
	private SearchResultAdapter mAdapter;
	private List<User> mUsers;
	private UserDataSource mUserDataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search_list);
		
		mUserDataSource = new UserDataSource(this);
		mUserDataSource.open();
		
		mBtNew = (Button) findViewById(R.id.btNew);
		mBtNew.setOnClickListener(this);
		
		mLvResult = (ListView) findViewById(R.id.lvSearchResult);
		
		mUsers = mUserDataSource.getAllUser();
		
		for (int i = 0; i < 50; i++) {
			User user = new User();
			user.setId(i);
			user.setUsername("Name " + i);
			
			mUsers.add(user);
		}
		
		mAdapter = new SearchResultAdapter(this, mUsers);
		
		mLvResult.setAdapter(mAdapter);
		mLvResult.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(SearchListActivity.this, UserFormActivity.class);
		intent.putExtra(USER, mUsers.get(position));
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtNew) {
			Intent intent = new Intent(SearchListActivity.this, UserFormActivity.class);
			startActivity(intent);
		}
	}
}
