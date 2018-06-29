package com.masonsrussell.humanityhospice_android;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class WritePostActivity extends AppCompatActivity
{
	private EditText postBox;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
	private ImageView postImageView;
	int screenWidth;
	int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		Button writePostButton = findViewById(R.id.writePostButton);
		Button closeButton = findViewById(R.id.closeButton);
		Button cancelButton = findViewById(R.id.cancelButton);
		Button attachPhotoButton = findViewById(R.id.attachPhotoButton);
		postImageView = findViewById(R.id.postImageView);
		postBox = findViewById(R.id.postEditText);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		attachPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				displayDialog();
			}
		});
		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (TextUtils.isEmpty(postBox.getText()))
				{
					Toast.makeText(getApplicationContext(), "A message must be written to publish", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if (data != null)
					{
						FirebaseCalls.createPhotoRefFromCamera(data, postBox.getText().toString(), FirebaseCalls.Journals);
					}
					else if (selectedImage != null)
					{
						FirebaseCalls.addAlbumPictures(selectedImage, postBox.getText().toString(), FirebaseCalls.Journals);
					}
					else if (data == null && selectedImage == null)
					{
						FirebaseCalls.createJournalPostWithoutPhoto(postBox.getText().toString());
					}
                    Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
                    startActivity(intent);
					finish();
				}
			}
		});
	}

	private void displayDialog()
	{
		final Dialog dialog = new Dialog(WritePostActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_choose_take_photo);
		Button takePictureButton = dialog.findViewById(R.id.takePictureButton);
		Button choosePictureButton = dialog.findViewById(R.id.choosePictureButton);
		Button cancelButton = dialog.findViewById(R.id.cancelButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				takePicture();
				dialog.hide();
			}
		});
		choosePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				chooseImage();
				dialog.hide();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dialog.hide();
			}
		});
		dialog.show();
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
					selectedImage = null;
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
					data = null;
					selectedImage = imageReturnedIntent.getData();
					postImageView.setImageURI(selectedImage);
					postImageView.getLayoutParams().width = 200;
					postImageView.getLayoutParams().height = 150;
				}
				break;
		}
	}

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}
}
