package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JournalCommentActivity extends AppCompatActivity
{

	TextView usernameView, timestampView, captionView;
	Button addCommentButton;
	RecyclerView commentsRecyclerView;
	EditText enterCommentText;
	private FirebaseAuth mAuth;
	private FirebaseDatabase mDatabase;
	ArrayList<Object> comments = new ArrayList<>();


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journal_comment);
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance();
		commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
		enterCommentText = findViewById(R.id.enterCommentText);
		addCommentButton = findViewById(R.id.addCommentButton);
		usernameView = findViewById(R.id.usernameTextView);
		timestampView = findViewById(R.id.posterTextView);
		captionView = findViewById(R.id.postBodyTextView);
		usernameView.setText(getIntent().getStringExtra("username"));
		captionView.setText(getIntent().getStringExtra("post"));

		addCommentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(enterCommentText.getText()))
				{
					FirebaseCalls.addCommentToJournalPost(enterCommentText.getText().toString(), getIntent().getStringExtra("postID"));
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
}
