package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Map;

public class EncouragementListAdapter extends RecyclerView.Adapter<EncouragementListAdapter.MainViewHolder> {
    private LayoutInflater inflater;
    private List<Map<String,Object>> postsList;
    private Context context;

    public EncouragementListAdapter(Context context, List<Map<String,Object>> postsList) {
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
        holder.setIsRecyclable(false);
        if (AccountInformation.profilePictures!=null && AccountInformation.profilePictures.containsKey(postsList.get(position).get(FirebaseCalls.PosterUID)))
        {
           loadProfilePicture(holder, position);
        }
        String date = AccountInformation.getDateFromEpochTime(postsList.get(position).get(FirebaseCalls.Timestamp).toString());
        holder.postBody.setText(postsList.get(position).get(FirebaseCalls.Message).toString());
        holder.timestamp.setText(date);
        holder.poster.setText(postsList.get(position).get(FirebaseCalls.PosterName).toString());

    }

    private void loadProfilePicture(MainViewHolder holder, int position)
    {
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
        TextView postBody, timestamp, poster;
        ImageView profilePictureImageView;

        public MainViewHolder(View itemView) {
            super(itemView);
            postBody = itemView.findViewById(R.id.postBodyTextView);
            timestamp = itemView.findViewById(R.id.posterTextView);
            poster = itemView.findViewById(R.id.usernameTextView);
            profilePictureImageView = itemView.findViewById(R.id.profilePicImageView);
        }

    }
}
