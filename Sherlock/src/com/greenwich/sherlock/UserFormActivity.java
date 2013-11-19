package com.greenwich.sherlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
//		mEtGender = (EditText) findViewById(R.id.etGender);
		mEtHeight = (EditText) findViewById(R.id.etHeight);
		mEtAgeFrom = (EditText) findViewById(R.id.etAgeFrom);
		mEtAgeTo = (EditText) findViewById(R.id.etAgeTo);
		mEtHairColor = (EditText) findViewById(R.id.etHairColor);
		mEtComment = (EditText) findViewById(R.id.etComment);
		mBtSave = (Button) findViewById(R.id.btSave);
		mBtSave.setOnClickListener(this);
		
		
		mPnGender = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPnGender.setAdapter(adapter);
        mPnGender.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        showToast("Spinner1: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
//                        showToast("Spinner1: unselected");
                    }
                });
        
		Intent intent = getIntent();
		
		if (intent == null) {
			return;
		}
		
		mUser = (User) intent.getSerializableExtra(Config.USER_OBJECT);
		if (mUser != null) {
			mEtName.setText(mUser.getUsername());
			int selection = mUser.getGender().equals("Male") ? 0 : 1;
			mPnGender.setSelection(selection);
//			mEtGender.setText(mUser.getGender());
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
				user.setPhotoPath("");
				user.setUsername(mEtName.getText().toString().trim());
				user.setGender(mPnGender.getSelectedItem().toString());
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
		int ageFrom = Integer.parseInt(mEtAgeFrom.getText().toString().trim());
		int ageTo = Integer.parseInt(mEtAgeTo.getText().toString().trim());
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
}
