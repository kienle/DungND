package com.greenwich.sherlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.greenwich.sherlock.entity.User;

public class UserFormActivity extends Activity implements OnClickListener {
	
	private EditText mEtName;
	private EditText mEtGender;
	private EditText mEtHeight;
	private EditText mEtAgeFrom	;
	private EditText mEtAgeTo;
	private EditText mEtHairColor;
	private EditText mEtComment;
	private Button mBtAddLocation;
	
	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_form);
		
		mEtName = (EditText) findViewById(R.id.etName);
		mEtGender = (EditText) findViewById(R.id.etGender);
		mEtHeight = (EditText) findViewById(R.id.etHeight);
		mEtAgeFrom = (EditText) findViewById(R.id.etAgeFrom);
		mEtAgeTo = (EditText) findViewById(R.id.etAgeTo);
		mEtHairColor = (EditText) findViewById(R.id.etHairColor);
		mEtComment = (EditText) findViewById(R.id.etComment);
		
		mBtAddLocation = (Button) findViewById(R.id.btAddLocation);
		mBtAddLocation.setOnClickListener(this);
		
		Intent intent = getIntent();
		
		if (intent == null) {
			return;
		}
		
		mUser = (User) intent.getSerializableExtra(SearchListActivity.USER);
		if (mUser != null) {
			mEtName.setText(mUser.getUsername());
			mEtGender.setText(mUser.getGender());
			mEtHeight.setText(String.valueOf(mUser.getHeight()));
			mEtAgeFrom.setText(String.valueOf(mUser.getAgeFrom()));
			mEtAgeTo.setText(String.valueOf(mUser.getAgeTo()));
			mEtHairColor.setText(mUser.getHairColor());
			mEtComment.setText(mUser.getComment());
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v == mBtAddLocation) {
			Intent intent = new Intent(UserFormActivity.this, AddLocationActivity.class);
			startActivity(intent);
		}
	}
}
