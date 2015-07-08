package com.izedtea.creation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.izedtea.creation.MyLocation.LocationResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class NewJobActivity extends Activity {
	
	private JobModel jobModel;
	private PhotoModel photoModel;
	
	private Spinner sideSpinner;
	private EditText soEditText;
	private Spinner typeSpinner;
	private EditText cusEditText;
	private Button takeButton;
	private ListView photoListView;
	private Button uploadButton;
	
	private JobEntity job;
	
	private ProgressDialog progressDlg;
	
	private Location loc;
	
	private String[] sidePositions = {
			"13.79451,100.574574",   // รัชดา
			"14.026767,100.61673",   // รังสิต
			"13.786357,100.372843",  // ปิ่นเกล้า
			"13.644866,100.692723",  // สุวรรณภูมิ
			"13.634935,100.36176",   // พระราม 2
			"13.821122,100.633636",  // เกษตรนวมินทร์
			"12.939611,100.90216",   // พัทยา 
			"12.572575,99.957905" }; // หัวหิน

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.menu_takephoto:
					takeButton.performClick();
				break;
			case R.id.menu_upload:
					uploadButton.performClick();
				break;	
			case R.id.menu_setting:
					startActivity(new Intent(NewJobActivity.this, SettingActivity.class));	
				break;
		}
		
		return true;
	}

	@Override
	public void onBackPressed() {
		updateJob(job.getId());
		
		startActivity(new Intent(NewJobActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newjob);
		
		jobModel = new JobModel(this);
		photoModel = new PhotoModel(this);
		
		photoListView = (ListView)findViewById(R.id.photoListView);
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup headerView = (ViewGroup)inflater.inflate(R.layout.list_photo_header, photoListView, false);
		photoListView.addHeaderView(headerView, null, false);
		
		sideSpinner = (Spinner)headerView.findViewById(R.id.sideSpiner);
		soEditText = (EditText)headerView.findViewById(R.id.soEditText);
		typeSpinner = (Spinner)headerView.findViewById(R.id.typeSpiner);
		cusEditText = (EditText)headerView.findViewById(R.id.cusEditText);
		takeButton = (Button)headerView.findViewById(R.id.takeButton);
		uploadButton = (Button)headerView.findViewById(R.id.uploadButton);
		
		createDirectory(MainActivity.BESEPATH);
	
		job = (JobEntity)getIntent().getSerializableExtra("job");
		final String lat = getIntent().getStringExtra("lat");
		final String lng = getIntent().getStringExtra("lng");
		final String time = getIntent().getStringExtra("time");
		
		if(job == null) {
			job = new JobEntity();
			job.setLat(lat);
			job.setLng(lng);
			job.setTime(time);
			job.setId(createJob(job));
			
			new Thread(new Runnable() {
				public void run() {
					ContentValues values = new ContentValues();
					values.put("distance", String.valueOf(getDistance("14.02738", "100.61461", job.getLat(), job.getLng())));
					jobModel.update(job.getId(), values);
				}
			}).start();
			
		} else {
			sideSpinner.setSelection(job.getSide());
			soEditText.setText(job.getSo());
			typeSpinner.setSelection(job.getType());
			cusEditText.setText(job.getCusName());
		}
		
		takeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);     
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MainActivity.BESEPATH + "temp.jpg")));
		        startActivityForResult(intent, 0);
			}
		});
		
		uploadButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if(!ClientService.isOnline(NewJobActivity.this)) {
					
					showMessage("Can not access internet.");
					
		            return;
		            
				} else {
				
					final ProgressDialog progressDlg = ProgressDialog.show(NewJobActivity.this, null, "uploading...");
					
					Runnable runnable = new Runnable() {
						public void run() {
							SharedPreferences sp = getSharedPreferences("global", 0);
							
							String newid = null;
							String side = String.valueOf(sideSpinner.getSelectedItemPosition());
							String so = soEditText.getText().toString();
							String type = String.valueOf(typeSpinner.getSelectedItemPosition());
							String cusName = cusEditText.getText().toString();
							String lat = job.getLat();
							String lng = job.getLng();
							String time = dateTimeFormat(job.getTime());
							String userName = sp.getString("username", null);
							String[] latLng = sidePositions[sideSpinner.getSelectedItemPosition()].split(",");
							String distance = String.valueOf(getDistance(latLng[0], latLng[1], job.getLat(), job.getLng()));

							ContentValues values = new ContentValues();
							values.put("distance", distance);
							jobModel.update(job.getId(), values);
							
							if(userName == null || userName == "")
							{
								progressDlg.dismiss();

								NewJobActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										showMessage("Please setting username.");
									}
								});
								
					            return;
							}
							
							try {
								MultipartEntity jEntity = new MultipartEntity();
								jEntity.addPart("side", new StringBody(side));
								jEntity.addPart("so", new StringBody(so));
								jEntity.addPart("type", new StringBody(type));
								jEntity.addPart("cusname", new StringBody(cusName, Charset.forName("TIS-620")));
								jEntity.addPart("lat", new StringBody(lat));
								jEntity.addPart("lng", new StringBody(lng));
								jEntity.addPart("time", new StringBody(time));
								jEntity.addPart("username", new StringBody(userName, Charset.forName("TIS-620")));
								jEntity.addPart("distance", new StringBody(distance));
								newid = ClientService.post("http://srv8.great-corner.com/crt_mobile/Upload.aspx?method=job", jEntity);
								//newid = ClientService.post("http://192.168.1.119/gc/crt_mobile/Upload.aspx?method=job", jEntity);
							} catch (Exception e) { }	
							
							ArrayList<PhotoEntity> photos = photoModel.getByJobId(job.getId());
							
							for (PhotoEntity p : photos) {
								String photoName = p.getPhoto();
								String note = p.getNote();
								String pLat = p.getLat();
								String pLng = p.getLng();
								String pTime = dateTimeFormat(p.getTime());
								byte[] photo = BitmapHelper.toByteArray(BitmapHelper.decodeFile(MainActivity.BESEPATH + p.getJobId() + "/" + p.getPhoto(), 1));
								
								try {
									MultipartEntity pEntity = new MultipartEntity();
									pEntity.addPart("jobid", new StringBody(newid));
									pEntity.addPart("photoName", new StringBody(photoName));
									pEntity.addPart("note", new StringBody(note, Charset.forName("TIS-620")));
									pEntity.addPart("lat", new StringBody(pLat));
									pEntity.addPart("lng", new StringBody(pLng));
									pEntity.addPart("time", new StringBody(pTime));
									pEntity.addPart("photo", new ByteArrayBody(photo, photoName));
									ClientService.post("http://srv8.great-corner.com/crt_mobile/Upload.aspx?method=photo", pEntity);
									//ClientService.post("http://192.168.1.119/gc/crt_mobile/Upload.aspx?method=photo", pEntity);
								} catch (Exception e) { }
							}
							
							ContentValues status = new ContentValues();
							status.put("status", 1);
							jobModel.update(job.getId(), status);
							
							NewJobActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									takeButton.setEnabled(false);
									uploadButton.setEnabled(false);
								}
							}); 
							
							progressDlg.dismiss();
							
							NewJobActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									showMessage("Upload success.");
								}
							});
						}
					};
					
					Thread thread = new Thread(runnable);
			        thread.start();
				}
			}
		});
		
		if(job.getStatus() == 1) {
			takeButton.setEnabled(false);
			uploadButton.setEnabled(false);
		}
		
		updateListView();
	}
	
	private void showMessage(String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewJobActivity.this);  
        alertDialog.setTitle("Message")  
        .setMessage(message)  
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
        	public void onClick(DialogInterface dialog, int which) {  
        		return;
        	} 
        }).show();
        
        alertDialog = null;
	}
	
	private String dateTimeFormat(String dateTime) {
		String year = dateTime.substring(0, 4);
		String month = dateTime.substring(4, 6);
		String day = dateTime.substring(6, 8);
		String hour = dateTime.substring(8, 10);
		String min = dateTime.substring(10, 12);
		String sec = dateTime.substring(12);
		return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
	}
	
	private void updateListView() {
       ArrayList<PhotoEntity> photos = photoModel.getByJobId(job.getId());
       
       PhotoAdapter adapter = new PhotoAdapter(NewJobActivity.this, photos, new PhotoAdapter.OnDeleteListener() {
			public void onDelete(final int id, final String photoName) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewJobActivity.this);  
	            alertDialog.setTitle("Message") 
	            .setMessage("Are you sure delete ?")  
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
	            	public void onClick(DialogInterface dialog, int which) {  
	            		photoModel.delete(id);
	            		new File(MainActivity.BESEPATH + job.getId() + "/" + photoName).delete();
	            		new File(MainActivity.BESEPATH + job.getId() + "/thumbs/" + photoName).delete();
	    				updateListView();
	            	} 
	            });  
	            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();
	            
	            alertDialog = null;
			}
	   });
       
       photoListView.setAdapter(adapter);
       
       photoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				
				job.setSo(soEditText.getText().toString());
				job.setType(typeSpinner.getSelectedItemPosition());
				job.setCusName(cusEditText.getText().toString());
				
				PhotoEntity photo = (PhotoEntity)photoListView.getItemAtPosition(position);
				Intent intent = new Intent(NewJobActivity.this, ViewPhotoActivity.class);
				intent.putExtra("job", job);
				intent.putExtra("photo", photo);
				startActivity(intent);
			}
		});
	}
	
	private int createJob(JobEntity j) {
		ContentValues values = new ContentValues();
		values.put("side", sideSpinner.getSelectedItemPosition());
		values.put("so", soEditText.getText().toString());
		values.put("type", typeSpinner.getSelectedItemPosition());
		values.put("cusname", cusEditText.getText().toString());
		values.put("lat", j.getLat());
		values.put("lng", j.getLng());
		values.put("time", j.getTime());
		values.put("status", 0);
		jobModel.create(values);
		
		int jobId = jobModel.get_create_id();
		
		createDirectory(MainActivity.BESEPATH + jobId);
		createDirectory(MainActivity.BESEPATH + jobId + "/thumbs");
		
		return jobId;
	}
	
	private void updateJob(int id) {
		ContentValues values = new ContentValues();
		values.put("side", sideSpinner.getSelectedItemPosition());
		values.put("so", soEditText.getText().toString());
		values.put("type", typeSpinner.getSelectedItemPosition());
		values.put("cusname", cusEditText.getText().toString());
		jobModel.update(id, values);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	try {
        	if(requestCode == 0) {
    			if(resultCode == RESULT_OK) {
    				
    	        	progressDlg = ProgressDialog.show(NewJobActivity.this, null, "finding location...");
    	        	
    	            LocationResult locationResult = new LocationResult() {
    	                @Override
    	                public void gotLocation(Location location) {
    	                	onLocation(location);
    	                }
    	            };
    	            
    	            MyLocation myLocation = new MyLocation();

    	            myLocation.getLocation(NewJobActivity.this, locationResult);
    			} else {
    				updateListView();
    			}
        	}
    	} catch (Exception e) { 
    		
    	}
    }
    
    private void onLocation(Location location) {
    	loc = location; // new FakeLocation(); 
    	
        progressDlg.dismiss();
        
    	if(loc == null) {
    		progressDlg.dismiss();

    		showMessage("Can not find location.");
    		
            return;
    	}
        
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);		 
		final View layout = inflater.inflate(R.layout.dialog_note, (ViewGroup)findViewById(R.id.noteLinearLayout));
		final EditText noteEditText = (EditText)layout.findViewById(R.id.noteEditText);
		
		AlertDialog.Builder noteDialog = new AlertDialog.Builder(NewJobActivity.this);  
		noteDialog.setTitle("Note") 
		.setView(layout)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
        	public void onClick(DialogInterface dialog, int which) {	
        		
            	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                final String timeNow = df.format(new Date(loc.getTime()));
                
                ContentValues values = new ContentValues();
                values.put("jobid", job.getId());
                values.put("photo", timeNow + ".jpg");
                values.put("note", noteEditText.getText().toString());
                values.put("lat", String.valueOf(loc.getLatitude()));
                values.put("lng", String.valueOf(loc.getLongitude()));
                values.put("time", timeNow);
                photoModel.create(values);
                
        		Bitmap full = BitmapHelper.rotate90Bitmap(BitmapHelper.decodeFile(MainActivity.BESEPATH + "temp.jpg", 2));  
        		
        		BitmapHelper.bitmapToFile(BitmapHelper.toThumbnail(full), MainActivity.BESEPATH + job.getId() + "/thumbs/" + timeNow + ".jpg", 75);
        		BitmapHelper.bitmapToFile(full, MainActivity.BESEPATH + job.getId() + "/" + timeNow + ".jpg", 75);
        		
        		AlertDialog.Builder takeDialog = new AlertDialog.Builder(NewJobActivity.this);  
        		takeDialog.setTitle("Message") 
        		.setMessage("Are you Take Photo ?")
        		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
                	public void onClick(DialogInterface dialog, int which) {
        				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);     
        				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MainActivity.BESEPATH + "temp.jpg")));
        		        startActivityForResult(intent, 0);
                	}
        		})
        		.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						updateListView();
						return;
					}
				}).show();
        		
        		takeDialog = null;
        	}
        }).show();
		
		noteDialog = null;
    }
    
    private float getDistance(String oLat, String oLng, String dLat, String dLng) {
    	if(!ClientService.isOnline(NewJobActivity.this))
    		return 0.0f;
    	
    	float iDistance = 0.0f;
    	try {
	        StringBuilder urlString = new StringBuilder();
	        urlString.append("http://maps.google.com/maps?f=d&hl=en&saddr=");
	        urlString.append(oLat);
	        urlString.append(",");
	        urlString.append(oLng);
	        urlString.append("&daddr=");
	        urlString.append(dLat);
	        urlString.append(",");
	        urlString.append(dLng);
	        urlString.append("&ie=UTF8&0&om=0&output=dragdir");
	
	        // get the JSON And parse it to get the directions data.
	        HttpURLConnection urlConnection= null;
	        URL url = null;
	
	        url = new URL(urlString.toString());
	        urlConnection=(HttpURLConnection)url.openConnection();
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);
	        urlConnection.setDoInput(true);
	        urlConnection.connect();
	
	        InputStream inStream = urlConnection.getInputStream();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
	
	        String temp, response = "";
	        while((temp = bReader.readLine()) != null){
	            //Parse data
	            response += temp;
	        }
	        //Close the reader, stream & connection
	        bReader.close();
	        inStream.close();
	        urlConnection.disconnect();
	
	        JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
	        
	        iDistance = Float.parseFloat(object.getString("tooltipHtml").replace("(", "").trim().split(" ")[0].replace(",", ""));
        
    	} catch(Exception e) {
    		
    	}
        return iDistance;
    }
    
    private void createDirectory(String path) {
        File dir = new File(path);
        if(!(dir.exists() && dir.isDirectory())) {
            dir.mkdir();
        }
    }
}
