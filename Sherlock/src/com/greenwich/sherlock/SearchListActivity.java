package com.greenwich.sherlock;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greenwich.sherlock.adapter.SearchResultAdapter;
import com.greenwich.sherlock.database.UserDataSource;
import com.greenwich.sherlock.database.UserLocationDataSource;
import com.greenwich.sherlock.entity.User;
import com.greenwich.sherlock.util.Config;
import com.greenwich.sherlock.util.ConnectivityHelper;
import com.greenwich.sherlock.util.DialogUtil;
import com.greenwich.sherlock.util.ResultRequest;

public class SearchListActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	public static final String COMPLETE = "complete";
	public static final String ERROR = "error";

	private ImageButton mBtNew;

	private EditText mEtSearch;
	private ImageButton mBtSearch;

	private ListView mLvResult;
	private SearchResultAdapter mAdapter;
	private List<User> mUsers;
	private UserDataSource mUserDataSource;
	private UserLocationDataSource mUserLocationDataSource;
	private User mUser;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search_list);

		mProgressDialog = new ProgressDialog(SearchListActivity.this);
		mProgressDialog.setMessage("Posting...");

		mUserDataSource = new UserDataSource(this);
		mUserDataSource.open();

		mUserLocationDataSource = new UserLocationDataSource(this);
		mUserLocationDataSource.open();

		mBtNew = (ImageButton) findViewById(R.id.btNew);
		mBtNew.setOnClickListener(this);

		ImageButton btPost = (ImageButton) findViewById(R.id.btPost);
		btPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mUsers == null || mUsers.size() == 0) {
					Toast.makeText(SearchListActivity.this,
							"No any user to post!", Toast.LENGTH_SHORT).show();
					return;
				}
				new PostTask().execute();
			}
		});

		mEtSearch = (EditText) findViewById(R.id.etSearch);
		mBtSearch = (ImageButton) findViewById(R.id.btSearch);
		mBtSearch.setOnClickListener(this);

		mLvResult = (ListView) findViewById(R.id.lvSearchResult);

		// mUsers = mUserDataSource.getAllUser();
		// mAdapter = new SearchResultAdapter(this, mUsers);
		// mLvResult.setAdapter(mAdapter);
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
		Intent intent = new Intent(SearchListActivity.this,
				ViewUserInfoActivity.class);
		intent.putExtra(Config.USER_OBJECT, mUsers.get(position));
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long id) {
		mUser = mUsers.get(position);

		AlertDialog.Builder selectOption = new AlertDialog.Builder(
				SearchListActivity.this);
		final CharSequence[] opsChars = { "Edit", "Delete" };
		selectOption.setItems(opsChars,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent(SearchListActivity.this,
									UserFormActivity.class);
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
		DialogUtil.createConfirmExistDialog(this, confirmDeleteListenner,
				R.string.delete_confirm);
	}

	DialogInterface.OnClickListener confirmDeleteListenner = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			long result = mUserDataSource.deleteUser(mUser.getId());
			long deleteLocation = mUserLocationDataSource
					.deleteUserLocation(mUser.getId());
			Log.d("Issues", "deleteLocation = " + deleteLocation);
			if (result != -1) {
				mUsers.remove(mUser);
				mAdapter.setmUsers(mUsers);
				Toast.makeText(SearchListActivity.this, "Delete complete!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v == mBtNew) {
			Intent intent = new Intent(SearchListActivity.this,
					UserFormActivity.class);
			intent.putExtra(Config.IS_NEW, true);
			startActivity(intent);
		} else if (v == mBtSearch) {
			String keySearch = mEtSearch.getText().toString().trim();
			List<User> searchResults = new ArrayList<User>();
			for (User user : mUsers) {
				if (user.getUsername().toLowerCase()
						.contains(keySearch.toLowerCase())) {
					searchResults.add(user);
				}
			}
			mUsers = searchResults;
			mAdapter.setmUsers(searchResults);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUserDataSource.close();
		mUserLocationDataSource.close();
	}

	private class PostTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			String url1 = "http://stark-journey-7979.herokuapp.com/api/users/auth_token";
			String result = pushListUsers(SearchListActivity.this, url1, mUsers);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result.equals(COMPLETE)) {
				new AlertDialog.Builder(SearchListActivity.this)
						.setMessage("Post user json to server successful!")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}
	}

	private String pushListUsers(Context context, String url, List<User> users) {
		ConnectivityHelper con = new ConnectivityHelper(context);
		con.setParameters("param", getUserJsonFromObject(context, users));
		ResultRequest result = null;
		try {
			result = con.doGet(url);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (result == null) {
			return ERROR;
		}

		String jsonResult = result.getStringResult();

		try {
			JSONObject jsonData = new JSONObject(jsonResult);
			String code = jsonData.getString("code");

			if (!code.equals("")) {
				return COMPLETE;
			} else {
				return ERROR;
			}

		} catch (JSONException e) {
			Log.d("Issues", "[ConnectionUtil][pushListEvents] JSONException: "
					+ e.toString());
			return ERROR;
		}
	}

	private static String getUserJsonFromObject(Context context,
			List<User> events) {
		Gson gson = new Gson();
		String json = gson.toJson(events);
		Log.d("Issues", "getUserJsonFromObject = " + json);
		return json;
	}

}
