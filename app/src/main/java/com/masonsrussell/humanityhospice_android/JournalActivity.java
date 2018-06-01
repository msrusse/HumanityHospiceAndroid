package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalActivity extends AppCompatActivity
{
	TextView navHeaderName, navHeaderEmail;
	Button writePostButton;
	RecyclerView postsListView;
	JournalListAdapter mAdapter;
	List<Map<String, Object>> posts = new ArrayList<>();
	private FirebaseAuth mAuth;
	private DrawerLayout mDrawerLayout;
	private FirebaseDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (AccountInformation.accountType.equals("Reader"))
		{
			setContentView(R.layout.activity_reader_journal);
			mDrawerLayout = findViewById(R.id.drawer_layout);
			//Toast.makeText(getApplicationContext(), AccountInformation.patientID, Toast.LENGTH_LONG).show();
			mAuth = FirebaseAuth.getInstance();
			mDatabase = FirebaseDatabase.getInstance();
			postsListView = findViewById(R.id.postsListView);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			setReaderNavMenu();
		} else
		{
			setContentView(R.layout.activity_journal);
			mDrawerLayout = findViewById(R.id.drawer_layout);
			//Toast.makeText(getApplicationContext(), AccountInformation.patientID, Toast.LENGTH_LONG).show();
			writePostButton = findViewById(R.id.writePostButton);
			mAuth = FirebaseAuth.getInstance();
			mDatabase = FirebaseDatabase.getInstance();
			postsListView = findViewById(R.id.postsListView);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionbar = getSupportActionBar();
			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			setFamilyPatientNavMenu();
			writePostButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(getApplicationContext(), WritePostActivity.class);
					startActivity(intent);
				}
			});
		}
		getJournalPosts();
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
						switch (menuItem.toString())
						{
							case "My Journal":
								mDrawerLayout.closeDrawers();
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
				new DrawerLayout.DrawerListener()
				{
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset)
					{
						// Respond when the drawer's position changes
					}

					@Override
					public void onDrawerOpened(View drawerView)
					{
						// Respond when the drawer is opened

					}

					@Override
					public void onDrawerClosed(View drawerView)
					{
						// Respond when the drawer is closed
					}

					@Override
					public void onDrawerStateChanged(int newState)
					{
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
						switch (menuItem.toString())
						{
							case "Journal":
								mDrawerLayout.closeDrawers();
								break;
							case "Encouragement Board":
								Intent intent = new Intent(getApplicationContext(), EncouragementBoardActivity.class);
								startActivity(intent);
								finish();
								break;
							case "Photo Album":
								Intent intent1 = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
								startActivity(intent1);
								finish();
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
				new DrawerLayout.DrawerListener()
				{
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset)
					{
						// Respond when the drawer's position changes

					}

					@Override
					public void onDrawerOpened(View drawerView)
					{
						// Respond when the drawer is opened

					}

					@Override
					public void onDrawerClosed(View drawerView)
					{
						// Respond when the drawer is closed
					}

					@Override
					public void onDrawerStateChanged(int newState)
					{
						// Respond when the drawer motion state changes
					}
				}
		);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				navHeaderName = findViewById(R.id.navHeaderName);
				navHeaderEmail = findViewById(R.id.navHeaderEmail);
				navHeaderEmail.setText(AccountInformation.email);
				navHeaderName.setText(AccountInformation.username);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getJournalPosts()
	{
		DatabaseReference journalPostsRef = mDatabase.getReference("Journals");
		journalPostsRef.child(AccountInformation.patientID).addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				try
				{
					posts.clear();
					dataSnapshot.getValue();
					Map<Object, Map> postsMap = (HashMap) dataSnapshot.getValue();
					for (Object post : postsMap.keySet())
					{
						Map<String, Object> addPost = new HashMap<>();
						if (postsMap.get(post).containsKey("url"))
						{
							addPost.put("url", postsMap.get(post).get("url").toString());
						}
						addPost.put("Post", postsMap.get(post).get("post").toString());
						addPost.put("Poster", postsMap.get(post).get("poster").toString());
						addPost.put("timestamp", postsMap.get(post).get("timestamp"));
						posts.add(addPost);
					}
					setListView();
				} catch (Exception ex)
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
			Collections.sort(posts, new MapComparator());
			Collections.reverse(posts);
			mAdapter = new JournalListAdapter(this, posts);
			postsListView.setAdapter(mAdapter);
			postsListView.setLayoutManager(new LinearLayoutManager(this));
			postsListView.getRecycledViewPool().setMaxRecycledViews(0, 0);
			postsListView.setNestedScrollingEnabled(true);
			postsListView.smoothScrollBy(1, 1);
			postsListView.addOnItemTouchListener(
					new RecyclerItemClickListener(JournalActivity.this, new RecyclerItemClickListener.OnItemClickListener()
					{
						@Override
						public void onItemClick(View v, int position)
						{
							Intent intent = new Intent(getApplicationContext(), JournalCommentActivity.class);
							intent.putExtra("username", posts.get(position).get("Poster").toString());
							intent.putExtra("post", posts.get(position).get("Post").toString());
							intent.putExtra("timestamp", posts.get(position).get("timestamp").toString());
							startActivity(intent);
						}
					})
			);
		} catch (Exception ex)
		{
			Log.d("JournalActivity", ex.getMessage());
		}
	}

	public void onBackPressed()
	{
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
		{
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else
		{
			mAuth.signOut();
			finish();
		}
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
			// TODO: Null checking, both for maps and values
			Long firstValue = (Long) first.get(key);
			Long secondValue = (Long) second.get(key);
			return firstValue.compareTo(secondValue);
		}
	}
}
