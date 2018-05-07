package com.masonsrussell.humanityhospice_android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class PhotoAlbumAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<Object> photos;

	// Constructor
	/*public ImageAdapter(Context c, ArrayList<Object> photos) {
		mContext = c;
		this.photos = photos;
	}*/

	public int getCount() {
		return photos.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		}
		else
		{
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource((Integer) photos.get(position));
		return imageView;
	}
}
