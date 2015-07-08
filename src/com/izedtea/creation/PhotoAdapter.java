package com.izedtea.creation;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends BaseAdapter {

	public interface OnDeleteListener {
	    public void onDelete(int photoId, String photoname);
	}
	
	private OnDeleteListener listener;
	
	private LayoutInflater inflater;
	private ArrayList<PhotoEntity> photos;
	
	public PhotoAdapter(Context context, ArrayList<PhotoEntity> photos, OnDeleteListener listener) {
		this.listener = listener;
		inflater = LayoutInflater.from(context);
		this.photos = photos;
	}

	public int getCount() {
		return photos.size();
	}

	public Object getItem(int position) {
		return photos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.list_photo, null);
			holder = new ViewHolder();
			holder.thumbImageView = (ImageView)convertView.findViewById(R.id.thumbImageView);
			holder.noteTextView = (TextView)convertView.findViewById(R.id.noteTextView);
			holder.delImageView = (ImageView)convertView.findViewById(R.id.delImageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.thumbImageView.setImageBitmap(BitmapFactory.decodeFile(MainActivity.BESEPATH + photos.get(position).getJobId() + "/thumbs/" + photos.get(position).getPhoto()));
		holder.noteTextView.setText(photos.get(position).getNote());
		holder.delImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				listener.onDelete(photos.get(position).getId(), photos.get(position).getPhoto());
			}
		});
		
		return convertView;
	}

	static class ViewHolder
	{
		ImageView thumbImageView;
		TextView noteTextView;
		ImageView delImageView;
	}
}
