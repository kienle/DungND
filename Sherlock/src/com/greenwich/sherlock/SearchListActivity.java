package com.greenwich.sherlock;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SearchListActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search_list);
	}
}
