package com.greenwich.sherlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.greenwich.sherlock.database.UserDataSource;
import com.greenwich.sherlock.entity.User;
import com.greenwich.sherlock.util.Config;

public class UserFormActivity extends Activity implements OnClickListener {
	
	private EditText mEtName;
	private EditText mEtGender;
	private EditText mEtHeight;
	private EditText mEtAgeFrom	;
	private EditText mEtAgeTo;
	private EditText mEtHairColor;
	private EditText mEtComment;
	
	private User mUser;
	private ImageView mImageView;
	private Button mBtSave;
	private UserDataSource mUserDataSource;
	private Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_form);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mUserDataSource = new UserDataSource(this);
		mUserDataSource.open();
		
		mImageView = (ImageView) findViewById(R.id.ivPhoto);
		mEtName = (EditText) findViewById(R.id.etName);
		mEtGender = (EditText) findViewById(R.id.etGender);
		mEtHeight = (EditText) findViewById(R.id.etHeight);
		mEtAgeFrom = (EditText) findViewById(R.id.etAgeFrom);
		mEtAgeTo = (EditText) findViewById(R.id.etAgeTo);
		mEtHairColor = (EditText) findViewById(R.id.etHairColor);
		mEtComment = (EditText) findViewById(R.id.etComment);
		mBtSave = (Button) findViewById(R.id.btSave);
		mBtSave.setOnClickListener(this);
		
		Intent intent = getIntent();
		
		if (intent == null) {
			return;
		}
		
		mUser = (User) intent.getSerializableExtra(Config.USER_OBJECT);
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
		if (v == mBtSave) {
			if (checkRequireField()) {
				User user = new User();
				user.setUsername(mEtName.getText().toString().trim());
				user.setGender(mEtGender.getText().toString().trim());
				user.setHeight(Integer.parseInt(mEtHeight.getText().toString().trim()));
				user.setAgeFrom(Integer.parseInt(mEtAgeFrom.getText().toString().trim()));
				user.setAgeTo(Integer.parseInt(mEtAgeTo.getText().toString().trim()));
				user.setHairColor(mEtHairColor.getText().toString().trim());
				user.setComment(mEtComment.getText().toString().trim());
				
				long result = mUserDataSource.insertUser(user);
				if (result != -1) {
					Intent intent = new Intent(UserFormActivity.this, ViewUserInfoActivity.class);
					intent.putExtra(Config.USER_OBJECT, user);
					startActivity(intent);
				}
			}
		}
	}
	
	private boolean checkRequireField() {
		
		if (mEtName.getText().toString().trim().equals("")
				|| mEtGender.getText().toString().trim().equals("")
				|| mEtHeight.getText().toString().trim().equals("")
				|| mEtAgeFrom.getText().toString().trim().equals("")) {
			mToast.setText("Required fields have to be filled");
			mToast.show();
			
			return false;
		}
		
		return true;
	}
}
