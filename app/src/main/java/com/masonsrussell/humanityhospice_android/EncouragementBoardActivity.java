package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncouragementBoardActivity extends AppCompatActivity
{
	TextView navHeaderName, navHeaderEmail;
	Button writePostButton;
	ListView encouragementBoardListView;
	private FirebaseAuth mAuth;
	private DrawerLayout mDrawerLayout;
	private FirebaseDatabase mDatabase;
	Map<String, Map> posts = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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

		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), WriteEncouragementActivity.class);
				startActivity(intent);
			}
		});

		NavigationView navigationView = findViewById(R.id.nav_view);
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
								Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
								startActivity(intent);
								finish();
								break;
							case "Encouragement Board":
								mDrawerLayout.closeDrawers();
								break;
							case "My Photo Album":

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
		getEncouragementPosts();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				navHeaderName = findViewById(R.id.navHeaderName);
				navHeaderEmail = findViewById(R.id.navHeaderEmail);
				navHeaderEmail.setText(mAuth.getCurrentUser().getEmail());
				navHeaderName.setText(mAuth.getCurrentUser().getDisplayName());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getEncouragementPosts()
	{
		DatabaseReference journalPostsRef = mDatabase.getReference("EncouragementBoard");
		journalPostsRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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
						addPost.put("Post", postsMap.get(post).get("post").toString());
						addPost.put("Poster", postsMap.get(post).get("poster").toString());
						addPost.put("Time", postsMap.get(post).get("timestamp"));
						posts.put(post.toString(), addPost);
					}
					setListView();
				}
				catch (Exception ex)
				{
					Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
			String[] postsStringArray = new String[posts.size()];
			String[] posterStringArray = new String[posts.size()];
			long[] timeStampLongArray = new long[posts.size()];
			for (Object key : posts.keySet())
			{
				int postLocation = key.toString().charAt(4) - '0';
				postsStringArray[posts.size()-1-postLocation] = posts.get(key).get("Post").toString();
				posterStringArray[posts.size()-1-postLocation] = posts.get(key).get("Poster").toString();
				timeStampLongArray[posts.size()-1-postLocation] = (long) posts.get(key).get("Time");

			}
			ArrayList<String> postsArrayList = new ArrayList<>();
			ArrayList<String> posterArrayList = new ArrayList<>();
			ArrayList<Long> timestampArrayList = new ArrayList<>();
			for (int i=0;i<posts.size();i++)
			{
				postsArrayList.add(postsStringArray[i]);
				posterArrayList.add(posterStringArray[i]);
				timestampArrayList.add(timeStampLongArray[i]);
			}
			ListAdapter listAdapter = new EncouragementBoardActivity.CustomListAdapter(EncouragementBoardActivity.this, R.layout.journal_listview_adapter, postsArrayList, posterArrayList);
			encouragementBoardListView.setAdapter(listAdapter);
		}
		catch (Exception ex)
		{
			Log.d("EncouragementBoard", ex.getMessage());
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

			if (items.get(position) != null)
			{
				postBody.setText(items.get(position));
				poster.setText(posters.get(position));
				posted.setText(R.string.posted_in_board);
			}

			return mView;
		}
	}

	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			mAuth.signOut();
			finish();
		}
	}
}
