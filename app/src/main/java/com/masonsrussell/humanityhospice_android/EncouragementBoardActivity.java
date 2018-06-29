package com.masonsrussell.humanityhospice_android;

import android.content.Context;
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
	Button writePostButton;
	ListView encouragementBoardListView;
	Bitmap bitmap = null;
	byte[] data = null;
	Uri selectedImage = null;
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
			getReaderEncouragement();
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

	private void getPatientEncouragement()
	{
		DatabaseReference journalPostsRef = mDatabase.getReference("EncouragementBoard");
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
						addPost.put(FirebaseCalls.Post, postsMap.get(post).get(FirebaseCalls.Post).toString());
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
		DatabaseReference journalPostsRef = mDatabase.getReference("EncouragementBoard");
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
						if (postsMap.get(post).get(FirebaseCalls.PosterName).toString().equals(mAuth.getCurrentUser().getUid()))
						{
							addPost.put(FirebaseCalls.Post, postsMap.get(post).get(FirebaseCalls.Post).toString());
							addPost.put(FirebaseCalls.PosterName, postsMap.get(post).get(FirebaseCalls.PosterName).toString());
							addPost.put(FirebaseCalls.Timestamp, postsMap.get(post).get(FirebaseCalls.Timestamp));
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
			ArrayList<String> postsArrayList = new ArrayList<>();
			ArrayList<String> posterArrayList = new ArrayList<>();
			ArrayList<Long> timestampArrayList = new ArrayList<>();
			Collections.reverse(posts);
			for (Map post : posts)
			{
				postsArrayList.add(post.get(FirebaseCalls.Post).toString());
				posterArrayList.add(post.get(FirebaseCalls.PosterName).toString());
				timestampArrayList.add((Long) post.get(FirebaseCalls.Timestamp));
			}
			ListAdapter listAdapter = new EncouragementBoardActivity.CustomListAdapter(EncouragementBoardActivity.this, R.layout.journal_listview_adapter, postsArrayList, posterArrayList);
			encouragementBoardListView.setAdapter(listAdapter);
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
			try {
				firstValue = (long) first.get(key);
				secondValue = (long) second.get(key);
			}
			catch (Exception ex)
			{
				double firstDoub = (double) first.get(key);
				double secondDoub = (double) second.get(key);
				firstValue = (long) firstDoub;
				secondValue = (long) secondDoub;
			}
			return Long.compare(firstValue, secondValue);
		}
	}

	private class CustomListAdapter extends ArrayAdapter<String>
	{

		private Context mContext;
		private int id;
		private List<String> items;
		private List<String> posters;

		public CustomListAdapter(Context context, int textViewResourceId, List<String> postList, List<String> posterList)
		{
			super(context, textViewResourceId, postList);
			mContext = context;
			id = textViewResourceId;
			items = postList;
			posters = posterList;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent)
		{
			View mView = v;
			if (mView == null)
			{
				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				mView = vi.inflate(id, null);
			}

			TextView postBody = mView.findViewById(R.id.postBodyTextView);
			TextView poster = mView.findViewById(R.id.usernameTextView);
			TextView posted = mView.findViewById(R.id.posterTextView);
			ImageView profilePictureImageView = mView.findViewById(R.id.profilePicImageView);

			if (AccountInformation.accountType.equals("Reader"))
			{
				if (AccountInformation.profilePictureURL != null)
				{
					Glide.with(getApplicationContext()).load(AccountInformation.profilePictureURL).into(profilePictureImageView);
					profilePictureImageView.getLayoutParams().width = 250;
					profilePictureImageView.getLayoutParams().height = 250;
				}
				if (items.get(position) != null)
				{
					String you = "You";
					String posted1 = "posted";
					postBody.setText(items.get(position));
					poster.setText(you);
					posted.setText(posted1);
				}
			}
			else
			{
				if (AccountInformation.profilePictureURL != null)
				{
					Glide.with(getApplicationContext()).load(AccountInformation.profilePictureURL).into(profilePictureImageView);
					profilePictureImageView.getLayoutParams().width = 250;
					profilePictureImageView.getLayoutParams().height = 250;
				}
				if (items.get(position) != null)
				{
					postBody.setText(items.get(position));
					poster.setText(posters.get(position));
					posted.setText(R.string.posted_in_board);
				}
			}

			return mView;
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
