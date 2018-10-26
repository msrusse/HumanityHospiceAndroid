package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncouragementBoardActivity extends AppCompatActivity
{
	TextView navHeaderName, navHeaderEmail;
	Button writePostButton, callNurseButton;
	RecyclerView encouragementBoardListView;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
	EncouragementListAdapter mAdapter;
	private FirebaseAuth mAuth;
	private DrawerLayout mDrawerLayout;
	private FirebaseDatabase mDatabase;
	List<Map<String, Object>> posts = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (AccountInformation.accountType.equals(FirebaseCalls.Patient))
		{
			setContentView(R.layout.activity_encouragement_board);
			mDrawerLayout = findViewById(R.id.drawer_layout);
			writePostButton = findViewById(R.id.writePostButton);
			mAuth = FirebaseAuth.getInstance();
			mDatabase = FirebaseDatabase.getInstance();
			encouragementBoardListView = findViewById(R.id.postsListView);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			writePostButton.setVisibility(View.INVISIBLE);
			setFamilyPatientNavMenu();
			getPatientEncouragement();
		}
		else if (AccountInformation.accountType.equals("Reader"))
		{
			setContentView(R.layout.activity_reader_encouragement_board);
			mDrawerLayout = findViewById(R.id.drawer_layout);
			writePostButton = findViewById(R.id.writePostButton);
			mAuth = FirebaseAuth.getInstance();
			mDatabase = FirebaseDatabase.getInstance();
			encouragementBoardListView = findViewById(R.id.postsListView);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			setReaderNavMenu();
			writePostButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(getApplicationContext(), WriteEncouragementActivity.class);
					startActivity(intent);
				}
			});
			if (AccountInformation.patientID != null) getReaderEncouragement();
			else findViewById(R.id.noPatientTextView).setVisibility(View.VISIBLE);
		}
		else
		{
			setContentView(R.layout.activity_encouragement_board);
			mDrawerLayout = findViewById(R.id.drawer_layout);
			writePostButton = findViewById(R.id.writePostButton);
			mAuth = FirebaseAuth.getInstance();
			mDatabase = FirebaseDatabase.getInstance();
			encouragementBoardListView = findViewById(R.id.postsListView);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			setFamilyPatientNavMenu();
			writePostButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(getApplicationContext(), WriteEncouragementActivity.class);
					startActivity(intent);
				}
			});
			getPatientEncouragement();
		}
		getProfilePictures();
	}

	private void getProfilePictures()
	{
		DatabaseReference profilePictures = mDatabase.getReference(FirebaseCalls.ProfilePictures);
		profilePictures.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				HashMap<String, Object> profilePictures = (HashMap) dataSnapshot.getValue();
				AccountInformation.profilePictures = profilePictures;
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void setReaderNavMenu()
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
						switch(menuItem.toString())
						{
							case "Journal":
								Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(intent);
								finish();
								break;
							case "Encouragement Board":
								mDrawerLayout.closeDrawers();
								break;
							case "Photo Album":
								Intent intent1 = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
								startActivity(intent1);
								finish();
								break;
							case "Sign Out":
								mAuth.signOut();
								Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
								startActivity(homeIntent);
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
	}

	private void setFamilyPatientNavMenu()
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener()
				{
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
						switch(menuItem.toString())
						{
							case "My Journal":
                                Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
                                startActivity(intent);
                                finish();
								break;
							case "Encouragement Board":
                                mDrawerLayout.closeDrawers();
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
								Intent intent3 = new Intent(getApplicationContext(), InvitePeopleActivity.class);
								startActivity(intent3);
								finish();
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
							.load(mAuth.getCurrentUser().getPhotoUrl().toString())
							.apply(RequestOptions.circleCropTransform())
							.into(profilePictureView);
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
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getPatientEncouragement()
	{
		DatabaseReference journalPostsRef = mDatabase.getReference(FirebaseCalls.EncouragementBoards);
		journalPostsRef.child(AccountInformation.patientID).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				try
				{
					dataSnapshot.getValue();
					Map<Object, Map> postsMap = (HashMap) dataSnapshot.getValue();
					for (Object post : postsMap.keySet())
					{
						Map<String, Object> addPost = new HashMap<>();
						addPost.put(FirebaseCalls.Message, postsMap.get(post).get(FirebaseCalls.Message).toString());
						addPost.put(FirebaseCalls.PosterName, postsMap.get(post).get(FirebaseCalls.PosterName).toString());
						addPost.put(FirebaseCalls.Timestamp, postsMap.get(post).get(FirebaseCalls.Timestamp));
						posts.add(addPost);
					}
					setListView();
				}
				catch (Exception ex)
				{
					Log.d("Encouragement", ex.getMessage());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private void getReaderEncouragement()
	{
		DatabaseReference journalPostsRef = mDatabase.getReference(FirebaseCalls.EncouragementBoards);
		journalPostsRef.child(AccountInformation.patientID).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				try
				{
					dataSnapshot.getValue();
					Map<Object, Map> postsMap = (HashMap) dataSnapshot.getValue();
					for (Object post : postsMap.keySet())
					{
						Map<String, Object> addPost = new HashMap<>();
						if (postsMap.get(post).get(FirebaseCalls.PosterUID).toString().equals(mAuth.getCurrentUser().getUid()))
						{
							addPost.put(FirebaseCalls.Message, postsMap.get(post).get(FirebaseCalls.Message).toString());
							addPost.put(FirebaseCalls.PosterName, postsMap.get(post).get(FirebaseCalls.PosterName).toString());
							addPost.put(FirebaseCalls.Timestamp, postsMap.get(post).get(FirebaseCalls.Timestamp));
							addPost.put(FirebaseCalls.PosterUID, postsMap.get(post).get(FirebaseCalls.PosterUID));
							posts.add(addPost);
						}
					}
					setListView();
				}
				catch (Exception ex)
				{
					Log.d("EncouragementBoard", ex.getMessage());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
			}
		});
	}

	private void setListView()
	{
		try
		{
			Collections.sort(posts, new MapComparator(FirebaseCalls.Timestamp));
			Collections.reverse(posts);
			mAdapter = new EncouragementListAdapter(this, posts);
			encouragementBoardListView.setAdapter(mAdapter);
			encouragementBoardListView.setLayoutManager(new LinearLayoutManager(this));
			encouragementBoardListView.getRecycledViewPool().setMaxRecycledViews(0, 0);
			encouragementBoardListView.setNestedScrollingEnabled(true);
			encouragementBoardListView.smoothScrollBy(1, 1);
		}
		catch (Exception ex)
		{
			Log.d("EncouragementBoard", ex.getMessage());
		}
	}

	class MapComparator implements Comparator<Map<String, Object>>
	{
		private final String key;

		public MapComparator(String key)
		{
			this.key = key;
		}

		public int compare(Map<String, Object> first,
		                   Map<String, Object> second)
		{
			long firstValue, secondValue;
			try { firstValue = (long) first.get(key); }
			catch (Exception e) { firstValue = Math.round((double) first.get(key)); }
			try { secondValue = (long) second.get(key); }
			catch (Exception ex) { secondValue = Math.round((double) second.get(key)); }
			return Long.compare(firstValue, secondValue);
		}
	}

	public void onBackPressed() {
		finish();
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
