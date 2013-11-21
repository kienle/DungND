package com.greenwich.sherlock;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greenwich.sherlock.database.UserDataSource;
import com.greenwich.sherlock.entity.User;
import com.greenwich.sherlock.util.Config;

public class ViewUserInfoActivity extends Activity implements OnClickListener {
	
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int SELECT_PICTURE = 2;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private Bitmap mImageBitmap;

	private String mCurrentPhotoPath;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	private ImageView mImageView;
	private TextView mTvName;
	private TextView mTvGender;
	private TextView mTvHeight;
	private TextView mTvAgeFrom;
	private TextView mTvAgeTo;
	private TextView mTvHairColor;
	private TextView mTvComment;
	private Button mBtAddLocation;
	private User mUser;
	
	private Toast mToast;
	private UserDataSource mUserDataSource;
	
	Button.OnClickListener mTakePicOnClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			AlertDialog.Builder getImageFrom = new AlertDialog.Builder(ViewUserInfoActivity.this);
            getImageFrom.setTitle("Select");
            final CharSequence[] opsChars = {"From Camera", "From Gallery"};
            getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

                @Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
					} else if (which == 1) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, ""), SELECT_PICTURE);
					}
					dialog.dismiss();
				}
            });
            
            getImageFrom.show();
            
//			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_view_info);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mUserDataSource = new UserDataSource(this);
		mUserDataSource.open();
		
		mImageView = (ImageView) findViewById(R.id.ivPhoto);
		
		mTvName = (TextView) findViewById(R.id.tvName);
		mTvGender = (TextView) findViewById(R.id.tvGender);
		mTvHeight = (TextView) findViewById(R.id.tvHeight);
		mTvAgeFrom = (TextView) findViewById(R.id.tvAgeFrom);
		mTvAgeTo = (TextView) findViewById(R.id.tvAgeTo);
		mTvHairColor = (TextView) findViewById(R.id.tvHairColor);
		mTvComment = (TextView) findViewById(R.id.tvComment);
		
		mBtAddLocation = (Button) findViewById(R.id.btAddLocation);
		mBtAddLocation.setOnClickListener(this);
		
		mImageBitmap = null;

		setBtnListenerOrDisable( 
				mImageView, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		Intent intent = getIntent();
		
		if (intent == null) {
			return;
		}
		
		mUser = (User) intent.getSerializableExtra(Config.USER_OBJECT);
		if (mUser != null) {
			String photoPath = mUser.getPhotoPath();
			File file = new File(photoPath);
			if (file.exists()) {
				Bitmap bmp = BitmapFactory.decodeFile(photoPath);
				mImageView.setImageBitmap(bmp);
			} else {
				mImageView.setImageResource(R.drawable.ic_launcher);
			}
			
			mTvName.setText(mUser.getUsername());
			mTvGender.setText(mUser.getGender());
			mTvHeight.setText(String.valueOf(mUser.getHeight()));
			mTvAgeFrom.setText(String.valueOf(mUser.getAgeFrom()));
			mTvAgeTo.setText(String.valueOf(mUser.getAgeTo()));
			mTvHairColor.setText(mUser.getHairColor());
			mTvComment.setText(mUser.getComment());
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBtAddLocation) {
			Intent intent = new Intent(ViewUserInfoActivity.this, AddLocationActivity.class);
			intent.putExtra(Config.USER_ID, mUser.getId());
			intent.putExtra(Config.IS_VIEW, false);
			startActivity(intent);
		}
	}
	
	/* Photo album for this application */
	private String getAlbumName() {
		return "Sherlock";
	}

	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
		
		// save photo path to db
		mUser.setPhotoPath(mCurrentPhotoPath);
		mUserDataSource.updateUser(mUser);
	}

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}

	private void handleCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B:
			if (resultCode == RESULT_OK) {
				handleCameraPhoto();

//				Cursor cursor = getContentResolver().query(
//						Media.EXTERNAL_CONTENT_URI,
//						new String[] { Media.DATA, Media.DATE_ADDED,
//								MediaStore.Images.ImageColumns.ORIENTATION },
//						Media.DATE_ADDED, null, "date_added ASC");
//				if (cursor != null && cursor.moveToFirst()) {
//					do {
//						Uri uri = Uri.parse(cursor.getString(cursor
//								.getColumnIndex(Media.DATA)));
//						mCurrentPhotoPath = uri.toString();
//					} while (cursor.moveToNext());
//					cursor.close();
//				}
				
//				Uri selectedImage = data.getData();
//		        mCurrentPhotoPath = getPath(selectedImage);
			}
			break;
			
		case SELECT_PICTURE:
			if(resultCode == RESULT_OK){  
		        Uri selectedImage = data.getData();
		        mCurrentPhotoPath = getPath(selectedImage);
		        mImageView.setImageURI(selectedImage);
		        
		        // save photo path to db
		        mUser.setPhotoPath(mCurrentPhotoPath);
				mUserDataSource.updateUser(mUser);
		    }
			break;
		} // switch
		
		
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable(ImageView btn, Button.OnClickListener onClickListener, String intentName) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
//			btn.setText(getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ViewUserInfoActivity.this, SearchListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUserDataSource.close();
	}

}
