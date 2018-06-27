package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class AddPhotoActivity extends AppCompatActivity
{
	private EditText postBox;
	private ImageView postImageView;
	private Button takePictureButton, choosePictureButton;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
	int screenWidth;
	int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_photo);
		Button writePostButton = findViewById(R.id.writePostButton);
		Button closeButton = findViewById(R.id.closeButton);
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		postImageView = findViewById(R.id.postImageView);
		postBox = findViewById(R.id.postEditText);
		takePictureButton = findViewById(R.id.takePhotoButton);
		choosePictureButton = findViewById(R.id.choosePhotoButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				takePicture();
			}
		});
		choosePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				chooseImage();
			}
		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent restartPhotoAlbumIntent = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
				startActivity(restartPhotoAlbumIntent);
				finish();
			}
		});

		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent restartPhotoAlbumIntent = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
					if (data != null)
					{
						FirebaseCalls.createPhotoRefFromCamera(data, null, "PhotoAlbum");
						startActivity(restartPhotoAlbumIntent);
						finish();
					}
					else if (selectedImage != null)
					{
						FirebaseCalls.addAlbumPictures(selectedImage, null, "PhotoAlbum");
						startActivity(restartPhotoAlbumIntent);
						finish();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "A photo must be added before posting", Toast.LENGTH_LONG).show();
					}
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		Intent restartPhotoAlbumIntent = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
		startActivity(restartPhotoAlbumIntent);
		finish();
	}

	private void chooseImage() {
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto , 1);
	}

	private void takePicture()
	{
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
			case 0:
				if(resultCode == RESULT_OK && imageReturnedIntent != null){
					bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					data = baos.toByteArray();
					postImageView.setImageBitmap(bitmap);
					postImageView.getLayoutParams().width = (int) (screenWidth *.75);
					postImageView.getLayoutParams().height = (int) (screenHeight*.2);
				}

				break;
			case 1:
				if(resultCode == RESULT_OK){
					selectedImage = imageReturnedIntent.getData();
					postImageView.setImageURI(selectedImage);
					postImageView.getLayoutParams().width = (int) (screenWidth *.75);
					postImageView.getLayoutParams().height = (int) (screenHeight*.5);
				}
				break;
		}
	}
}
