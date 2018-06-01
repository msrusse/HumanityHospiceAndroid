package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
		usernameView = findViewById(R.id.usernameTextView);
		timestampView = findViewById(R.id.posterTextView);
		captionView = findViewById(R.id.postBodyTextView);
		usernameView.setText(getIntent().getStringExtra("username"));
		captionView.setText(getIntent().getStringExtra("post"));
	}
}
