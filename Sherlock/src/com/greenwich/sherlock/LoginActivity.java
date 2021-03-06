package com.greenwich.sherlock;

import com.greenwich.sherlock.util.Config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText mEtUsername;
	private EditText mEtPassword;
	private Button mBtLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mEtUsername = (EditText) findViewById(R.id.etUsername);
		mEtPassword = (EditText) findViewById(R.id.etPassword);

		mEtUsername.setText(Config.USERNAME);
		mEtPassword.setText(Config.PASSWORD);

		mBtLogin = (Button) findViewById(R.id.btLogin);
		mBtLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == mBtLogin) {
			String username = mEtUsername.getText().toString().trim();
			String password = mEtPassword.getText().toString().trim();
			if (username.equalsIgnoreCase(Config.USERNAME)
					&& password.equalsIgnoreCase(Config.PASSWORD)) {
				Intent intent = new Intent(LoginActivity.this,
						SearchListActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(LoginActivity.this,
						"Username or Password is invalid!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
