package com.greenwich.sherlock;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.greenwich.sherlock.adapter.SearchResultAdapter;
import com.greenwich.sherlock.database.UserDataSource;
import com.greenwich.sherlock.entity.User;
import com.greenwich.sherlock.util.Config;
import com.greenwich.sherlock.util.DialogUtil;

public class SearchListActivity extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	
	private Button mBtNew;
	
	private EditText mEtSearch;
	private Button mBtSearch;
	
	private ListView mLvResult;
	private SearchResultAdapter mAdapter;
	private List<User> mUsers;
	private UserDataSource mUserDataSource;
	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search_list);
		
		mUserDataSource = new UserDataSource(this);
		mUserDataSource.open();
		
		mBtNew = (Button) findViewById(R.id.btNew);
		mBtNew.setOnClickListener(this);
		
		mEtSearch = (EditText) findViewById(R.id.etSearch);
		mBtSearch = (Button) findViewById(R.id.btSearch);
		mBtSearch.setOnClickListener(this);
		
		mLvResult = (ListView) findViewById(R.id.lvSearchResult);
		
//		mUsers = mUserDataSource.getAllUser();
//		mAdapter = new SearchResultAdapter(this, mUsers);
//		mLvResult.setAdapter(mAdapter);
		mLvResult.setOnItemClickListener(this);
		mLvResult.setOnItemLongClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		mUsers = mUserDataSource.getAllUser();
		mAdapter = new SearchResultAdapter(this, mUsers);
		mLvResult.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(SearchListActivity.this, ViewUserInfoActivity.class);
		intent.putExtra(Config.USER_OBJECT, mUsers.get(position));
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position,
			long id) {
		mUser = mUsers.get(position);
		
		AlertDialog.Builder selectOption = new AlertDialog.Builder(SearchListActivity.this);
        final CharSequence[] opsChars = {"Edit", "Delete"};
        selectOption.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

            @Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(SearchListActivity.this, UserFormActivity.class);
					intent.putExtra(Config.USER_OBJECT, mUser);
					intent.putExtra(Config.IS_NEW, false);
					startActivity(intent);
				} else if (which == 1) {
					showDialogConfirmDelete();
				}
				dialog.dismiss();
			}
        });
        
        selectOption.show();
		return false;
	}
	
	private void showDialogConfirmDelete() {
		DialogUtil.createConfirmExistDialog(this, confirmDeleteListenner, R.string.delete_confirm);
	}
	
	DialogInterface.OnClickListener confirmDeleteListenner = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			long result = mUserDataSource.deleteUser(mUser.getId());
			if (result != -1) {
				mUsers.remove(mUser);
				mAdapter.setmUsers(mUsers);
				Toast.makeText(SearchListActivity.this, "Delete complete!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		if (v == mBtNew) {
			Intent intent = new Intent(SearchListActivity.this, UserFormActivity.class);
			intent.putExtra(Config.IS_NEW, true);
			startActivity(intent);
		} else if (v == mBtSearch) {
			String keySearch = mEtSearch.getText().toString().trim();
			List<User> searchResults = new ArrayList<User>();
			for (User user : mUsers) {
				if (user.getUsername().toLowerCase().contains(keySearch.toLowerCase())) {
					searchResults.add(user);
				}
			}
			mAdapter.setmUsers(searchResults);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUserDataSource.close();
	}

}
