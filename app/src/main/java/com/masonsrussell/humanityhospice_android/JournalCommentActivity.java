package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalCommentActivity extends AppCompatActivity
{

	TextView usernameView, timestampView, captionView;
	Button addCommentButton;
	RecyclerView commentsRecyclerView;
	ImageView postImageView, profilePicImageView;
	EditText enterCommentText;
	CommentListAdapter mAdapter;
	String postID;
	public int screenWidth, screenHeight;
	private FirebaseDatabase mDatabase;
	List<Map<String, Object>> comments = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journal_comment);
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		mDatabase = FirebaseDatabase.getInstance();
		profilePicImageView = findViewById(R.id.posterProfilePicImageView);
		commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
		enterCommentText = findViewById(R.id.enterCommentText);
		addCommentButton = findViewById(R.id.addCommentButton);
		usernameView = findViewById(R.id.usernameTextView);
		timestampView = findViewById(R.id.posterTextView);
		captionView = findViewById(R.id.postBodyTextView);
		postImageView = findViewById(R.id.postImageView);
		postID = getIntent().getStringExtra("postID");
		usernameView.setText(getIntent().getStringExtra(FirebaseCalls.PosterName));
		captionView.setText(getIntent().getStringExtra(FirebaseCalls.Post));
		timestampView.setText(AccountInformation.getDateFromEpochTime(getIntent().getStringExtra(FirebaseCalls.Timestamp)));
		if (getIntent().getStringExtra(FirebaseCalls.PostImageURL) != null)
		{
			loadPostImage(getIntent().getStringExtra(FirebaseCalls.PostImageURL));
		}
		String posterUID = getIntent().getStringExtra(FirebaseCalls.PosterUID);
		Object hasProfilePic = AccountInformation.profilePictures.get(posterUID);
		if(hasProfilePic != null)
		{
			loadProfilePicture(AccountInformation.profilePictures.get(posterUID).toString());
		}
		getComments();

		addCommentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(enterCommentText.getText()))
				{
					FirebaseCalls.addCommentToJournalPost(enterCommentText.getText().toString(), postID);
					enterCommentText.getText().clear();
					View view = getCurrentFocus();
					if (view != null) {
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please enter a comment before submitting", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void loadProfilePicture(String url)
	{
		postImageView.getLayoutParams().width = 180;
		postImageView.getLayoutParams().height = 180;
		GlideApp.with(this)
				.load(url)
				.apply(RequestOptions.circleCropTransform())
				.into(profilePicImageView);
	}

	private void loadPostImage(String url)
	{
		GlideApp.with(this)
				.load(url)
				.into(postImageView);
	}

	private void getComments()
	{
		DatabaseReference journalPostsRef = mDatabase.getReference(FirebaseCalls.Journals);
		journalPostsRef.child(AccountInformation.patientID).child(postID).child(FirebaseCalls.Comments).addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				try
				{
					comments.clear();
					dataSnapshot.getValue();
					Map<Object, Map> postsMap = (HashMap) dataSnapshot.getValue();
					for (Object post : postsMap.keySet())
					{
						Map<String, Object> addPost = new HashMap<>();
						addPost.put(FirebaseCalls.Post, postsMap.get(post).get(FirebaseCalls.Comment).toString());
						addPost.put(FirebaseCalls.PosterName, postsMap.get(post).get(FirebaseCalls.PosterName).toString());
						addPost.put(FirebaseCalls.Timestamp, postsMap.get(post).get(FirebaseCalls.Timestamp));
						addPost.put(FirebaseCalls.PosterUID, postsMap.get(post).get(FirebaseCalls.PosterUID));
						comments.add(addPost);
					}
					setListView();
				} catch (Exception ex)
				{
					Log.d("JournalComment", ex.getMessage());
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
			Collections.sort(comments, new MapComparator());
			mAdapter = new CommentListAdapter(this, comments);
			commentsRecyclerView.setAdapter(mAdapter);
			commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
			commentsRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
			commentsRecyclerView.setNestedScrollingEnabled(true);
			commentsRecyclerView.smoothScrollBy(1, 1);
		} catch (Exception ex)
		{
			Log.d("JournalActivity", ex.getMessage());
		}
	}

	class MapComparator implements Comparator<Map<String, Object>>
	{
		private final String key;

		private MapComparator()
		{
			this.key = FirebaseCalls.Timestamp;
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

	public void onBackPressed()
	{
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}
}
