package com.masonsrussell.humanityhospice_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutHumanityHospiceActivity extends AppCompatActivity
{
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_humanity_hospice);
		webView = findViewById(R.id.webView);
		webView.loadUrl("http://www.humanityhospice.com");
	}

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
		startActivity(intent);
		finish();
	}
}
