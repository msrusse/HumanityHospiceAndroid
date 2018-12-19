package com.masonsrussell.humanityhospice_android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
	Button addCommentButton, deletePostButton, editPostButton;
	RecyclerView commentsRecyclerView;
	ImageView postImageView, profilePicImageView;
	LinearLayout patientButtons;
	EditText enterCommentText;
	CommentListAdapter mAdapter;
	String postID;
	public int screenWidth, screenHeight;
	private FirebaseDatabase mDatabase;
	private FirebaseAuth mAuth;
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
		deletePostButton = findViewById(R.id.deletePostButton);
		editPostButton = findViewById(R.id.editPostButton);
		patientButtons = findViewById(R.id.patientButtonView);
		postID = getIntent().getStringExtra("postID");
		mAuth = FirebaseAuth.getInstance();
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

		if (AccountInformation.accountType.equals("Reader"))
		{
			patientButtons.setVisibility(View.INVISIBLE);
		}
		else
		{
			editPostButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					displayEditPostDialog(null, "post");
				}
			});

			deletePostButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mDatabase.getReference(FirebaseCalls.Journals).child(AccountInformation.patientID).child(getIntent().getStringExtra("postID")).setValue(null);
					Intent homeIntent = new Intent(getApplicationContext(), JournalActivity.class);
					startActivity(homeIntent);
					finish();
				}
			});
		}

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
		GlideApp.with(this)
				.load(url)
				.apply(RequestOptions.circleCropTransform())
				.into(profilePicImageView);
	}

	private void loadPostImage(final String url)
	{
//		postImageView.getLayoutParams().height = 800;
		postImageView.getLayoutParams().width = captionView.getLayoutParams().width;
		GlideApp.with(this)
				.load(url)
				.into(postImageView);
		postImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(JournalCommentActivity.this);

				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_display_photo_with_caption);
				Button deleteButton = dialog.findViewById(R.id.button);
				LinearLayout readerPatientView = dialog.findViewById(R.id.readerPatientView);

				if (AccountInformation.accountType.equals("Reader"))
				{
					readerPatientView.setVisibility(View.INVISIBLE);
				}

				ImageView photo = dialog.findViewById(R.id.photoView);
				GlideApp.with(v.getContext())
						.load(url)
						.apply(new RequestOptions().override(screenWidth, (int)(screenHeight*.8)))
						.into(photo);
				dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				deleteButton.setVisibility(View.GONE);
				dialog.show();

			}
		});
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
						addPost.put("postID", post);
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
			commentsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(JournalCommentActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
						@Override
						public void onItemClick(View view, int position) {
                            if(comments.get(position).get(FirebaseCalls.PosterUID).equals(mAuth.getUid()))
                            {
                                displayEditDeleteDialog(position);
                            }
						}
					})

			);
		} catch (Exception ex)
		{
			Log.e("JournalActivity", ex.getMessage());
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
			try { firstValue = (long) first.get(key); }
			catch (Exception e) { firstValue = Math.round((double) first.get(key)); }
			try { secondValue = (long) second.get(key); }
			catch (Exception ex) { secondValue = Math.round((double) second.get(key)); }

			return Long.compare(firstValue, secondValue);
		}
	}

	public void onBackPressed()
	{
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}

    private void displayEditDeleteDialog(final Integer selectedPost){
        final Dialog dialog = new Dialog(JournalCommentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_delete);
        Button deleteButton = dialog.findViewById(R.id.deletePostButton);
        Button editButton = dialog.findViewById(R.id.editPostButton);
        Button dialogCancelButton = dialog.findViewById(R.id.editDeleteCancelButton);
        dialog.show();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference(FirebaseCalls.Journals).child(AccountInformation.patientID).child(postID).child(FirebaseCalls.Comments).child(comments.get(selectedPost).get("postID").toString()).setValue(null);
                dialog.hide();
                getComments();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                displayEditPostDialog(selectedPost, "comment");
            }
        });

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    private void displayEditPostDialog(final Integer selectedPost, final String postOrComment) {
        final Dialog dialog = new Dialog(JournalCommentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_post);
        final EditText editPost = dialog.findViewById(R.id.postText);
        Button editDialogCancelButton = dialog.findViewById(R.id.cancelButton);
        Button enterButton = dialog.findViewById(R.id.enterButton);
        dialog.show();

        if (postOrComment.equals("comment")) {
			editPost.setText(comments.get(selectedPost).get(FirebaseCalls.Post).toString());
		}
		else {
        	editPost.setText(getIntent().getStringExtra(FirebaseCalls.Post));
		}
		editDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                displayEditDeleteDialog(selectedPost);
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if (postOrComment.equals("comment")) {
					String currentPostId = comments.get(selectedPost).get("postID").toString();
					comments.get(selectedPost).put(FirebaseCalls.Comment, editPost.getText().toString());
					comments.get(selectedPost).remove(FirebaseCalls.Post);
					comments.get(selectedPost).remove("postID");
					mDatabase.getReference(FirebaseCalls.Journals).child(AccountInformation.patientID).child(postID).child(FirebaseCalls.Comments).child(currentPostId).updateChildren(comments.get(selectedPost));
				}
				else {
            		mDatabase.getReference(FirebaseCalls.Journals).child(AccountInformation.patientID).child(postID).child(FirebaseCalls.Post).setValue(editPost.getText().toString());
				}
                dialog.hide();
            }
        });
    }
}
