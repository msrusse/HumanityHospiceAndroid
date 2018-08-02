package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalListAdapter extends RecyclerView.Adapter<JournalListAdapter.MainViewHolder> {
	private LayoutInflater inflater;
	private List<Map<String,Object>> postsList;
	private Context context;

	public JournalListAdapter(Context context, List<Map<String,Object>> postsList) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.postsList = postsList;
	}

	@Override
	public @NonNull MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.journal_listview_adapter, parent, false);
		return new MainViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
		holder.setIsRecyclable(false);
		String date = AccountInformation.getDateFromEpochTime(postsList.get(position).get(FirebaseCalls.Timestamp).toString());
		if (AccountInformation.profilePictures.containsKey(postsList.get(position).get(FirebaseCalls.PosterUID)))
		{
			loadProfilePicture(holder, position);
		}
		else
		{
			holder.profilePictureImageView.setImageResource(R.mipmap.logo);
		}
		if (postsList.get(position).containsKey(FirebaseCalls.PostImageURL) && postsList.get(position).containsKey(FirebaseCalls.Comments))
		{
			holder.postBody.setText(postsList.get(position).get(FirebaseCalls.Post).toString());
			if(postsList.get(position).get(FirebaseCalls.Post).equals("")) holder.postBody.setVisibility(View.INVISIBLE);
			holder.poster.setText(postsList.get(position).get(FirebaseCalls.PosterName).toString());
			Map<String, Object> commentsMap = ((HashMap) postsList.get(position).get(FirebaseCalls.Comments));
			String commentsTotal = "Comments (" + commentsMap.size() + ") v";
			holder.commentsView.setText(commentsTotal);
			holder.timestamp.setText(date);
			GlideApp.with(context).load(postsList.get(position).get(FirebaseCalls.PostImageURL)).into(holder.postImageView);
		}
		else if (postsList.get(position).containsKey(FirebaseCalls.PostImageURL))
		{
			holder.postBody.setText(postsList.get(position).get(FirebaseCalls.Post).toString());
			if(postsList.get(position).get(FirebaseCalls.Post).equals("")) holder.postBody.setVisibility(View.INVISIBLE);
			holder.poster.setText(postsList.get(position).get(FirebaseCalls.PosterName).toString());
			String commentsTotal = "Comments (0) v";
			holder.commentsView.setText(commentsTotal);
			holder.timestamp.setText(date);
			GlideApp.with(context).load(postsList.get(position).get(FirebaseCalls.PostImageURL)).into(holder.postImageView);
		}
		else if (postsList.get(position).containsKey(FirebaseCalls.Comments))
		{
			holder.postBody.setText(postsList.get(position).get(FirebaseCalls.Post).toString());
			if(postsList.get(position).get(FirebaseCalls.Post).equals("")) holder.postBody.setVisibility(View.INVISIBLE);
			holder.poster.setText(postsList.get(position).get(FirebaseCalls.PosterName).toString());
			holder.timestamp.setText(date);
			Map<String, Object> commentsMap = ((HashMap) postsList.get(position).get(FirebaseCalls.Comments));
			String commentsTotal = "Comments (" + commentsMap.size() + ") v";
			holder.commentsView.setText(commentsTotal);
		}
		else
		{
			holder.postBody.setText(postsList.get(position).get(FirebaseCalls.Post).toString());
			if(postsList.get(position).get(FirebaseCalls.Post).equals("")) holder.postBody.setVisibility(View.INVISIBLE);
			holder.poster.setText(postsList.get(position).get(FirebaseCalls.PosterName).toString());
			holder.timestamp.setText(date);
			String commentsTotal = "Comments (0) v";
			holder.commentsView.setText(commentsTotal);
		}
	}

	private void loadProfilePicture(MainViewHolder holder, int position)
	{
		holder.profilePictureImageView.getLayoutParams().width = 120;
		holder.profilePictureImageView.getLayoutParams().height = 120;
		GlideApp.with(context)
				.load(AccountInformation.profilePictures.get(postsList.get(position).get(FirebaseCalls.PosterUID)))
				.apply(RequestOptions.circleCropTransform())
				.into(holder.profilePictureImageView);
	}

	@Override
	public int getItemCount() {
		return postsList.size();
	}

	class MainViewHolder extends RecyclerView.ViewHolder {
		TextView postBody, timestamp, poster, commentsView;
		ImageView postImageView, profilePictureImageView;

		private MainViewHolder(View itemView) {
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
