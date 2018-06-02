package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class JournalListAdapter extends RecyclerView.Adapter<JournalListAdapter.MainViewHolder> {
	LayoutInflater inflater;
	List<Map<String,Object>> postsList;
	Context context;

	public JournalListAdapter(Context context, List<Map<String,Object>> postsList) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.postsList = postsList;
	}

	@Override
	public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.journal_listview_adapter, parent, false);
		return new MainViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MainViewHolder holder, int position) {
		//holder.bindData();
		holder.setIsRecyclable(false);
		String date = AccountInformation.getDateFromEpochTime(postsList.get(position).get("timestamp").toString());
		if (postsList.get(position).containsKey("postImageURL") && postsList.get(position).containsKey("comments"))
		{
			if (AccountInformation.profilePictureURL != null) Glide.with(context).load(AccountInformation.profilePictureURL).into(holder.profilePictureImageView);
			holder.postBody.setText(postsList.get(position).get("Post").toString());
			holder.poster.setText(postsList.get(position).get("Poster").toString());
			Map<String, Object> commentsMap = ((HashMap) postsList.get(position).get("comments"));
			String commentsTotal = "Comments (" + commentsMap.size() + ") v";
			holder.commentsView.setText(commentsTotal);
			holder.postImageView.setVisibility(View.VISIBLE);
			holder.timestamp.setText(date);
			Glide.with(context).load(postsList.get(position).get("postImageURL")).into(holder.postImageView);
		}
		else if (postsList.get(position).containsKey("postImageURL"))
		{
			if (AccountInformation.profilePictureURL != null) Glide.with(context).load(AccountInformation.profilePictureURL).into(holder.profilePictureImageView);
			holder.postBody.setText(postsList.get(position).get("Post").toString());
			holder.poster.setText(postsList.get(position).get("Poster").toString());
			String commentsTotal = "Comments (0) v";
			holder.commentsView.setText(commentsTotal);
			holder.timestamp.setText(date);
			holder.postImageView.setVisibility(View.VISIBLE);
			Glide.with(context).load(postsList.get(position).get("postImageURL")).into(holder.postImageView);
		}
		else if (postsList.get(position).containsKey("comments"))
		{
			if (AccountInformation.profilePictureURL != null) Glide.with(context).load(AccountInformation.profilePictureURL).into(holder.profilePictureImageView);
			holder.postBody.setText(postsList.get(position).get("Post").toString());
			holder.poster.setText(postsList.get(position).get("Poster").toString());
			holder.timestamp.setText(date);
			Map<String, Object> commentsMap = ((HashMap) postsList.get(position).get("comments"));
			String commentsTotal = "Comments (" + commentsMap.size() + ") v";
			holder.commentsView.setText(commentsTotal);
		}
		else
		{
			if (AccountInformation.profilePictureURL != null) Glide.with(context).load(AccountInformation.profilePictureURL).into(holder.profilePictureImageView);
			holder.postBody.setText(postsList.get(position).get("Post").toString());
			holder.poster.setText(postsList.get(position).get("Poster").toString());
			holder.timestamp.setText(date);
			String commentsTotal = "Comments (0) v";
			holder.commentsView.setText(commentsTotal);
		}
	}

	@Override
	public int getItemCount() {
		return postsList.size();
	}

	class MainViewHolder extends RecyclerView.ViewHolder {

		TextView postBody, timestamp, poster, commentsView;
		ImageView postImageView, profilePictureImageView;

		public MainViewHolder(View itemView) {
			super(itemView);
			postBody = itemView.findViewById(R.id.postBodyTextView);
			timestamp = itemView.findViewById(R.id.posterTextView);
			poster = itemView.findViewById(R.id.usernameTextView);
			postImageView = itemView.findViewById(R.id.postImageView);
			commentsView = itemView.findViewById(R.id.commentsView);
			profilePictureImageView = itemView.findViewById(R.id.profilePicImageView);
		}

	}
}
