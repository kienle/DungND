package com.greenwich.sherlock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.greenwich.sherlock.database.UserDataSource;
import com.greenwich.sherlock.entity.User;
import com.greenwich.sherlock.util.Config;

public class UserFormActivity extends Activity implements OnClickListener {
	
	private EditText mEtName;
//	private EditText mEtGender;
	private Spinner mPnGender;
	private EditText mEtHeight;
	private EditText mEtAgeFrom	;
	private EditText mEtAgeTo;
	private EditText mEtHairColor;
	private Spinner mPnBodyType;
	private EditText mEtComment;
	
	private User mUser;
	private boolean mIsAddNewUser;
	private Button mBtSave;
	private UserDataSource mUserDataSource;
	private Toast mToast;
	private String[] mBodyTypes;
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_form);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mUserDataSource = new UserDataSource(this);
		mUserDataSource.open();
		
		mEtName = (EditText) findViewById(R.id.etName);
		mEtHeight = (EditText) findViewById(R.id.etHeight);
		mEtAgeFrom = (EditText) findViewById(R.id.etAgeFrom);
		mEtAgeTo = (EditText) findViewById(R.id.etAgeTo);
		mEtHairColor = (EditText) findViewById(R.id.etHairColor);
		mEtComment = (EditText) findViewById(R.id.etComment);
		mBtSave = (Button) findViewById(R.id.btSave);
		mBtSave.setOnClickListener(this);
		
		mBodyTypes = getResources().getStringArray(R.array.bodyType);
		
		mPnGender = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPnGender.setAdapter(adapter);
        
        mPnBodyType = (Spinner) findViewById(R.id.bodyTypes);
        ArrayAdapter<CharSequence> adpBodyTypes = ArrayAdapter.createFromResource(
                this, R.array.bodyType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPnBodyType.setAdapter(adpBodyTypes);
        
		Intent intent = getIntent();
		
		if (intent == null) {
			return;
		}
		
		mIsAddNewUser = intent.getBooleanExtra(Config.IS_NEW, true);

		mUser = (User) intent.getSerializableExtra(Config.USER_OBJECT);
		if (mUser != null) {
			mEtName.setText(mUser.getUsername());
			int genderSelection = mUser.getGender().equals("Male") ? 0 : 1;
			mPnGender.setSelection(genderSelection);
			mEtHeight.setText(String.valueOf(mUser.getHeight()));
			mEtAgeFrom.setText(String.valueOf(mUser.getAgeFrom()));
			mEtAgeTo.setText(String.valueOf(mUser.getAgeTo()));
			mEtHairColor.setText(mUser.getHairColor());
			
			int index = 0;
			for (String bodyType : mBodyTypes) {
				if (bodyType.equals(mUser.getBodyType())) {
					mPnBodyType.setSelection(index);
				}
				index += 1;
			}
			
			mEtComment.setText(mUser.getComment());
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v == mBtSave) {
			if (checkRequireField()) {
				User user = new User();
				user.setPhotoPath("");
				user.setUsername(mEtName.getText().toString().trim());
				user.setGender(mPnGender.getSelectedItem().toString());
				user.setHeight(Integer.parseInt(mEtHeight.getText().toString().trim()));
				user.setAgeFrom(Integer.parseInt(mEtAgeFrom.getText().toString().trim()));
				user.setAgeTo(Integer.parseInt(mEtAgeTo.getText().toString().trim()));
				user.setHairColor(mEtHairColor.getText().toString().trim());
				user.setBodyType(mPnBodyType.getSelectedItem().toString());
				user.setComment(mEtComment.getText().toString().trim());
				
				long result = -1;
				Intent intent = new Intent(UserFormActivity.this, ViewUserInfoActivity.class);
				if (mIsAddNewUser) {
					result = mUserDataSource.insertUser(user);
					
					if (result != -1) {
						Log.d("Logs", "insert result = " + result);
						user.setId((int) result);
						intent.putExtra(Config.USER_OBJECT, user);
					}
					
				} else {
					mUser.setUsername(mEtName.getText().toString().trim());
					mUser.setGender(mPnGender.getSelectedItem().toString());
					mUser.setHeight(Integer.parseInt(mEtHeight.getText().toString().trim()));
					mUser.setAgeFrom(Integer.parseInt(mEtAgeFrom.getText().toString().trim()));
					mUser.setAgeTo(Integer.parseInt(mEtAgeTo.getText().toString().trim()));
					mUser.setHairColor(mEtHairColor.getText().toString().trim());
					mUser.setBodyType(mPnBodyType.getSelectedItem().toString());
					mUser.setComment(mEtComment.getText().toString().trim());
					result = mUserDataSource.updateUser(mUser);
					if (result != -1) {
						intent.putExtra(Config.USER_OBJECT, mUser);
					}
				}
				startActivity(intent);
				
			}
		}
	}
	
	private boolean checkRequireField() {
		String ageFromTxt = mEtAgeFrom.getText().toString().trim();
		String ageToTxt = mEtAgeTo.getText().toString().trim();
		if (ageFromTxt.equals("") || ageToTxt.equals("")) {
			mToast.setText("Required fields have to be filled");
			mToast.show();
			return false;
		}
		int ageFrom = Integer.parseInt(ageFromTxt);
		int ageTo = Integer.parseInt(ageToTxt);
		if (mEtName.getText().toString().trim().equals("")
				|| mPnGender.getSelectedItem().toString().trim().equals("")
				|| mEtHeight.getText().toString().trim().equals("")
				|| mEtAgeFrom.getText().toString().trim().equals("")
				|| mEtAgeTo.getText().toString().trim().equals("")) {
			mToast.setText("Required fields have to be filled");
			mToast.show();
			
			return false;
		} else if (ageTo <= ageFrom) {
			mToast.setText("Age to must be greater than age from");
			mToast.show();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUserDataSource.close();
	}
}
