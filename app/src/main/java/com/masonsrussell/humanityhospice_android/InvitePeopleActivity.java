package com.masonsrussell.humanityhospice_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;

public class InvitePeopleActivity extends AppCompatActivity
{
	TextView accessCodeView, navHeaderName, navHeaderEmail;
	Button shareInviteCodeButton, callNurseButton;
	private FirebaseAuth mAuth;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
	private FirebaseDatabase mDatabase;
	private DrawerLayout mDrawerLayout;
	@SuppressWarnings("FieldCanBeLocal")
	private static String TAG = "InvitePeopleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_people);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		mAuth = FirebaseAuth.getInstance();
		accessCodeView = findViewById(R.id.accessCodeView);
		mDatabase = FirebaseDatabase.getInstance();
		shareInviteCodeButton = findViewById(R.id.shareMyCodeButton);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionbar = getSupportActionBar();
		try
		{
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
		}
		catch (NullPointerException ex)
		{
			Log.d(TAG, ex.getMessage());
		}

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
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
								Intent journalIntent = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(journalIntent);
								finish();
								break;
							case "Encouragement Board":
								Intent intent = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
								startActivity(intent);
								finish();
								break;
							case "My Photo Album":
								Intent intent1 = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
								startActivity(intent1);
								finish();
								break;
							case "Create Family Account":
								Intent intent2 = new Intent(getApplicationContext(), CreateFamilyAccountActivity.class);
								startActivity(intent2);
								finish();
								break;
							case "Invite People":
								mDrawerLayout.closeDrawers();
								break;
							case "Current Readers":
								Intent intent5 = new Intent(getApplicationContext(), ViewReadersActivity.class);
								startActivity(intent5);
								finish();
								break;
							case "Sign Out":
								mAuth.signOut();
								Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
								startActivity(homeIntent);
								finish();
								break;
							case "About Humanity Hospice":
								Intent intent4 = new Intent(getApplicationContext(), AboutHumanityHospiceActivity.class);
								startActivity(intent4);
								finish();
								break;
						}
						mDrawerLayout.closeDrawers();
						return true;
					}
				});

		mDrawerLayout.addDrawerListener(
				new DrawerLayout.DrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
					}

					@Override
					public void onDrawerOpened(View drawerView) {

					}

					@Override
					public void onDrawerClosed(View drawerView) {
					}

					@Override
					public void onDrawerStateChanged(int newState) {
					}
				}
		);
		getAccessCode();
		shareInviteCodeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = accessCodeView.getText().toString();
				String iosLink = "https://apple.co/2StPCRu";
				String androidLink = "https://goo.gl/DGWdQf";
				String webLink = "Connect.HumanityHospice.com";
				String sharingMessage =  AccountInformation.patientName + " has invited you to follow their profile on Humanity Connect!\n" +
						"\n" +
						"When creating an account, use access code " + shareBody + " to view their profile.\n" +
						"\n" +
						"View their profile online at " + webLink + " or, Download the App!\n" +
						"\n" +
						"For iPhone: " + iosLink + "\n" +
						"For Android: " + androidLink;
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharingMessage);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));
			}
		});
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
				ImageView profilePictureView = findViewById(R.id.userProfilePicImageView);
				if (AccountInformation.profilePictureURL != null)
				{
					GlideApp.with(this)
							.load(AccountInformation.profilePictureURL)
							.apply(RequestOptions.circleCropTransform())
							.into(profilePictureView);
				}
				LinearLayout profileInfo = findViewById(R.id.profileInfo);
				callNurseButton = findViewById(R.id.call_nurse_button);
				callNurseButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.talk");
						if (launchIntent != null) {
							startActivity(launchIntent);//null pointer check in case package name was not found
						}
						else{
							Toast.makeText(getApplicationContext(), "Please install Google Hangouts", Toast.LENGTH_LONG).show();
						}
					}
				});
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

	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			startActivity(new Intent(getApplicationContext(), JournalActivity.class));
			finish();
		}
	}

	public void getAccessCode()
	{
		DatabaseReference userRef = mDatabase.getReference().child(FirebaseCalls.Patients).child(AccountInformation.patientID).child(FirebaseCalls.InviteCode);
		userRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				accessCodeView.setText(dataSnapshot.getValue().toString());
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	public void profileImagePicker()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_menu_camera)
				.setTitle("Update Profile Picture")
				.setMessage("Either select an image from the gallery or take a new photo")
				.setPositiveButton("Choose Photo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						chooseImage();
					}
				})
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
