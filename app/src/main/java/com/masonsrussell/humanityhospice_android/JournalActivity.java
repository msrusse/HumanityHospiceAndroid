package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class JournalActivity extends AppCompatActivity
{
	TextView signOut;
	private FirebaseAuth mAuth;
	private DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journal);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		signOut = findViewById(R.id.signOutView);
		mAuth = FirebaseAuth.getInstance();
		ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.menu);

		signOut.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mAuth.signOut();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
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
						switch(menuItem.getItemId())
						{
							case 0:

								break;
							case 1:

								break;
							case 2:

								break;
							case 3:

								break;
							case 4:

								break;
							case 5:

								break;
							case 6:

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
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
