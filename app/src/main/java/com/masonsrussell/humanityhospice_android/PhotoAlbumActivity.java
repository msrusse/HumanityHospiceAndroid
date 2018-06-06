package com.masonsrussell.humanityhospice_android;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PhotoAlbumActivity extends AppCompatActivity
{

	private DrawerLayout mDrawerLayout;
	TextView navHeaderName, navHeaderEmail;
	private FirebaseDatabase mDatabase;
	private FirebaseAuth mAuth;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
	private GridView photoGridView;
	private List<Map<String, Object>> imageURLs = new ArrayList<>();
	int screenWidth;
	int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (AccountInformation.accountType.equals("Reader"))
		{
			setContentView(R.layout.activity_reader_photo_album);
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();
			screenWidth = metrics.widthPixels;
			screenHeight = metrics.heightPixels;
			photoGridView = findViewById(R.id.photo_gridview);
			mDatabase = FirebaseDatabase.getInstance();
			mAuth = FirebaseAuth.getInstance();
			mDrawerLayout = findViewById(R.id.drawer_layout);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			setReaderNavMenu();
			getPhotoAlbumImages();
		}
		else
		{
			setContentView(R.layout.activity_photo_album);
			Button addPhotoButton = findViewById(R.id.addPhotoButton);
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();
			screenWidth = metrics.widthPixels;
			screenHeight = metrics.heightPixels;
			photoGridView = findViewById(R.id.photo_gridview);
			mDatabase = FirebaseDatabase.getInstance();
			mAuth = FirebaseAuth.getInstance();
			mDrawerLayout = findViewById(R.id.drawer_layout);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			setFamilyPatientNavMenu();
			getPhotoAlbumImages();

			addPhotoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(getApplicationContext(), AddPhotoActivity.class);
					startActivity(intent);
					finish();
				}
			});
		}
	}

	public class ImageAdapterGridView extends BaseAdapter
	{
		private Context mContext;

		public ImageAdapterGridView(Context c) {
			mContext = c;
		}

		public int getCount() {
			return imageURLs.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView mImageView;

			if (convertView == null) {
				mImageView = new ImageView(mContext);
				mImageView.setLayoutParams(new GridView.LayoutParams(screenWidth/3, screenWidth/3));
				mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				mImageView.setPadding(16, 16, 16, 16);
			} else {
				mImageView = (ImageView) convertView;
			}
			Glide.with(getApplicationContext()).load(imageURLs.get(position).get("url")).into(mImageView);
			return mImageView;
		}
	}

	private void setFamilyPatientNavMenu()
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{

						// set item as selected to persist highlight
						switch(menuItem.toString())
						{
							case "My Journal":
								Intent intent0 = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(intent0);
								finish();
								break;
							case "Encouragement Board":
								Intent intent = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
								startActivity(intent);
								finish();
								break;
							case "My Photo Album":
								mDrawerLayout.closeDrawers();
								break;
							case "Create Family Account":
								Intent intent2 = new Intent(getApplicationContext(), CreateFamilyAccountActivity.class);
								startActivity(intent2);
								finish();
								break;
							case "Invite People":
								Intent intent3 = new Intent(getApplicationContext(), InvitePeopleActivity.class);
								startActivity(intent3);
								finish();
								break;
							case "Sign Out":
								mAuth.signOut();
								finish();
								break;
							case "About Humanity Hospice":
								Intent intent4 = new Intent(getApplicationContext(), AboutHumanityHospiceActivity.class);
								startActivity(intent4);
								finish();
								break;
						}
						// close drawer when item is tapped
						mDrawerLayout.closeDrawers();

						// Add code here to update the UI based on the item selected
						// For example, swap UI fragments here

						return true;
					}
				});

		mDrawerLayout.addDrawerListener(
				new DrawerLayout.DrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
						// Respond when the drawer's position changes
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						// Respond when the drawer is opened

					}

					@Override
					public void onDrawerClosed(View drawerView) {
						// Respond when the drawer is closed
					}

					@Override
					public void onDrawerStateChanged(int newState) {
						// Respond when the drawer motion state changes
					}
				}
		);
	}

	private void setReaderNavMenu()
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
						// set item as selected to persist highlight
						switch(menuItem.toString())
						{
							case "Journal":
								Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(intent);
								finish();
								break;
							case "Encouragement Board":
								Intent intent1 = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
								startActivity(intent1);
								finish();
								break;
							case "Photo Album":
								mDrawerLayout.closeDrawers();
								break;
							case "Sign Out":
								mAuth.signOut();
								finish();
								break;
							case "About Humanity Hospice":
								Intent intent2 = new Intent(getApplicationContext(), AboutHumanityHospiceActivity.class);
								startActivity(intent2);
								finish();
								break;
							case "Add Patient":
								Intent intent3 = new Intent(getApplicationContext(), AddPatientActivity.class);
								startActivity(intent3);
								finish();
								break;
							case "Change Patient":
								Intent intent4 = new Intent(getApplicationContext(), ChangePatientActivity.class);
								startActivity(intent4);
								finish();
								break;
						}
						// close drawer when item is tapped
						mDrawerLayout.closeDrawers();

						// Add code here to update the UI based on the item selected
						// For example, swap UI fragments here

						return true;
					}
				});

		mDrawerLayout.addDrawerListener(
				new DrawerLayout.DrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
						// Respond when the drawer's position changes
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						// Respond when the drawer is opened

					}

					@Override
					public void onDrawerClosed(View drawerView) {
						// Respond when the drawer is closed
					}

					@Override
					public void onDrawerStateChanged(int newState) {
						// Respond when the drawer motion state changes
					}
				}
		);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				navHeaderName = findViewById(R.id.navHeaderName);
				navHeaderEmail = findViewById(R.id.navHeaderEmail);
				navHeaderEmail.setText(AccountInformation.email);
				navHeaderName.setText(AccountInformation.username);
				ImageView profilePictureView = findViewById(R.id.profilePicImageView);
				if (AccountInformation.profilePictureURL != null)
				{
					Glide.with(this).load(AccountInformation.profilePictureURL).into(profilePictureView);
				}
				LinearLayout profileInfo = findViewById(R.id.profileInfo);
				profileInfo.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						profileImagePicker();
					}
				});
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getPhotoAlbumImages()
	{
		final DatabaseReference photoRef = mDatabase.getReference("PhotoAlbum");
		photoRef.child(AccountInformation.patientID).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				try
				{
					imageURLs.clear();
					dataSnapshot.getValue();
					Map<Object, Map> postsMap = (HashMap) dataSnapshot.getValue();
					for (Object post : postsMap.keySet())
					{
						Map<String, Object> addImage = new HashMap<>();
						if (postsMap.get(post).containsKey("caption"))
						{
							addImage.put("caption", postsMap.get(post).get("caption").toString());
						}
						else
						{
							addImage.put("caption", "");
						}
						addImage.put("timestamp", postsMap.get(post).get("timestamp"));
						addImage.put("url", postsMap.get(post).get("url").toString());
						imageURLs.add(addImage);
					}
					setAdapter();
				}
				catch (Exception ex)
				{
					Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{

			}
		});
	}

	private void setAdapter()
	{
		Collections.sort(imageURLs, new MapComparator());
		Collections.reverse(imageURLs);
		photoGridView.setAdapter(new ImageAdapterGridView(getApplication()));

		photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
			                        View v, int position, long id) {
				displayDialog(position);
			}
		});
	}

	class MapComparator implements Comparator<Map<String, Object>>
	{
		private final String key;

		private MapComparator()
		{
			this.key = "timestamp";
		}

		public int compare(Map<String, Object> first,
		                   Map<String, Object> second)
		{
			Double firstDoub = (double) first.get(key);
			Double secondDoub = (double) second.get(key);
			Long firstValue = Double.valueOf(firstDoub).longValue();
			Long secondValue = Double.valueOf(secondDoub).longValue();
			return firstValue.compareTo(secondValue);
		}
	}

	private void displayDialog(int index)
	{
		final Dialog dialog = new Dialog(PhotoAlbumActivity.this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_display_photo_with_caption);

		ImageView photo = dialog.findViewById(R.id.photoView);
		photo.getLayoutParams().height = (int)(screenHeight / 1.5);
		photo.getLayoutParams().width = (int)(screenWidth/1.5);
		TextView captionView = dialog.findViewById(R.id.photoTextView);
		captionView.setText(imageURLs.get(index).get("caption").toString());
		Glide.with(this).load(imageURLs.get(index).get("url")).into(photo);
		dialog.show();
	}

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}

	public void profileImagePicker()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				//set icon
				.setIcon(android.R.drawable.ic_menu_camera)
				//set title
				.setTitle("Update Profile Picture")
				//set message
				.setMessage("Either select an image from the gallery or take a new photo")
				//set positive button
				.setPositiveButton("Choose Photo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						chooseImage();
					}
				})
				//set negative button
				.setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						takePicture();
					}
				})
				.show();
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
					FirebaseCalls.addProfilePictureFromCamera(data);
				}

				break;
			case 1:
				if(resultCode == RESULT_OK){
					selectedImage = imageReturnedIntent.getData();
					FirebaseCalls.addProfilePictureFromGallery(selectedImage);
				}
				break;
		}
	}
}
