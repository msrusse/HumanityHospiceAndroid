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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MainViewHolder> {
	LayoutInflater inflater;
	List<Map<String,Object>> postsList;
	Context context;

	public CommentListAdapter(Context context, List<Map<String,Object>> postsList) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.postsList = postsList;
	}

	@Override
	public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.comment_listview_adapter, parent, false);
		return new MainViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MainViewHolder holder, int position) {
		//holder.bindData();
		holder.setIsRecyclable(false);
		holder.postBody.setText(postsList.get(position).get("Post").toString());
		holder.poster.setText(postsList.get(position).get("Poster").toString());

	}

	@Override
	public int getItemCount() {
		return postsList.size();
	}

	class MainViewHolder extends RecyclerView.ViewHolder {

		TextView postBody, timestamp, poster;

		public MainViewHolder(View itemView) {
			super(itemView);
			postBody = itemView.findViewById(R.id.postBodyTextView);
			timestamp = itemView.findViewById(R.id.posterTextView);
			poster = itemView.findViewById(R.id.usernameTextView);
		}

	}
}
