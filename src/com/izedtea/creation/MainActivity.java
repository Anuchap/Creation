package com.izedtea.creation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.izedtea.creation.MyLocation.LocationResult;

import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	public static final String BESEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/creation/";
	
	private JobModel jobModel;
	private PhotoModel photoModel;
	
	private Button newJobButton;
	private Button settingButton;
	private ListView jobListView;
	
	private ProgressDialog progressDlg;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        jobModel = new JobModel(this);
        photoModel = new PhotoModel(this);

        newJobButton = (Button)findViewById(R.id.newJobBotton);
        settingButton = (Button)findViewById(R.id.settingButton);
        jobListView = (ListView)findViewById(R.id.jobListView);
        
        newJobButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
	        	progressDlg = ProgressDialog.show(MainActivity.this, null, "finding location...");
	        	
	            LocationResult locationResult = new LocationResult(){
	                @Override
	                public void gotLocation(Location location){
	                	onLocation(location);
	                }
	            };
	            
	            MyLocation myLocation = new MyLocation();

	            myLocation.getLocation(MainActivity.this, locationResult);
			}
		});
        
        settingButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SettingActivity.class));	
			}
		});
        
        updateListView();
    }
	
	private void onLocation(Location location) {
		progressDlg.dismiss();
        
    	if(location == null) {
    		progressDlg.dismiss();
        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);  
            alertDialog.setTitle("Message")
            .setMessage("can not find localtion.")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
            	public void onClick(DialogInterface dialog, int which) {  
            		return;  
            	} 
            }).show();
            
            alertDialog = null;
            
            return;
    	}
    	
		Intent intent = new Intent(MainActivity.this, NewJobActivity.class);
		intent.putExtra("lat", String.valueOf(location.getLatitude()));
		intent.putExtra("lng", String.valueOf(location.getLongitude()));
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        final String timeNow = df.format(new Date(location.getTime()));
		intent.putExtra("time", timeNow);
		startActivity(intent);
	}
	
	private void updateListView() {
       ArrayList<JobEntity> jobs = jobModel.retrieve();
       
       JobAdapter adapter = new JobAdapter(MainActivity.this, jobs, new JobAdapter.OnDeleteListener() {
			public void onDelete(final int jobId, String so) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);  
	            alertDialog.setTitle("Message")  
	            .setMessage("Are you sure delete " + so  + " ?")
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
	            	public void onClick(DialogInterface dialog, int which) {  
	            		jobModel.delete(jobId);
	            		
	            		ArrayList<PhotoEntity> photos = photoModel.getByJobId(jobId);
	            		
	            		for (PhotoEntity p : photos) {
	            			new File(MainActivity.BESEPATH + jobId + "/" + p.getPhoto()).delete();
	            			new File(MainActivity.BESEPATH + jobId + "/thumbs/" + p.getPhoto()).delete();
						}
	            		
	    				updateListView();
	            	} 
	            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();
	            
	            alertDialog = null;
			}
	   });
       
       jobListView.setAdapter(adapter);
       
       jobListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				JobEntity job = (JobEntity)jobListView.getItemAtPosition(position);
				Intent intent = new Intent(MainActivity.this, NewJobActivity.class);
				intent.putExtra("job", job);
				startActivity(intent);
			}
	   });
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
