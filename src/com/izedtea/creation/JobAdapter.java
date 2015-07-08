package com.izedtea.creation;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JobAdapter extends BaseAdapter {

	public interface OnDeleteListener {
	    public void onDelete(int jobId, String so);
	}
	
	private OnDeleteListener listener;
	
	private LayoutInflater inflater;
	private ArrayList<JobEntity> jobs;
	
	private String[] jobType;
	
	public JobAdapter(Context context, ArrayList<JobEntity> jobs, OnDeleteListener listener) {
		this.listener = listener;
		inflater = LayoutInflater.from(context);
		this.jobs = jobs;
		this.jobType = context.getResources().getStringArray(R.array.job_type);
	}

	public int getCount() {
		return jobs.size();
	}

	public Object getItem(int position) {
		return jobs.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.list_job, null);
			holder = new ViewHolder();
			holder.statusImageView = (ImageView)convertView.findViewById(R.id.statusImageView);
			holder.soTextView = (TextView)convertView.findViewById(R.id.soTextView);
			holder.typeTextView = (TextView)convertView.findViewById(R.id.typeTextView);
			holder.delImageView = (ImageView)convertView.findViewById(R.id.delImageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if(jobs.get(position).getStatus() == 1)
			holder.statusImageView.setImageResource(R.drawable.check);
		
		holder.soTextView.setText("S/O : " + jobs.get(position).getSo());
		holder.typeTextView.setText("Type : " + jobType[jobs.get(position).getType()] + " [" + (jobs.get(position).getDistance() == "0.0" ? "-" : jobs.get(position).getDistance()) + " km.]");
		holder.delImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				listener.onDelete(jobs.get(position).getId(), jobs.get(position).getSo());
			}
		});
		
		return convertView;
	}

	static class ViewHolder
	{
		ImageView statusImageView;
		TextView soTextView;
		TextView typeTextView;
		ImageView delImageView;
	}
}
