package com.izedtea.creation;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhotoModel {
	private SQLiteDatabase db;
	
	public PhotoModel(Context context) {
		DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
		db = dbOpenHelper.getWritableDatabase();
	}
	
	private static final String tb_name = "photo";
	
	public long create(ContentValues values) {
        return db.insert(tb_name, null, values); 
	}
	
	public ArrayList<PhotoEntity> retrieve() {
		Cursor c = db.query(tb_name, new String[] { "id", "jobid", "photo", "note", "lat", "lng", "time" }, null, null, null, null, null);
		
		ArrayList<PhotoEntity> photos = new ArrayList<PhotoEntity>();
		
		PhotoEntity photo;

    	if(c.moveToFirst()) {
    		do {
    			photo = new PhotoEntity();
    			photo.setId(c.getInt(c.getColumnIndex("id")));
    			photo.setJobId(c.getInt(c.getColumnIndex("jobid")));
    			photo.setPhoto(c.getString(c.getColumnIndex("photo")));
    			photo.setNote(c.getString(c.getColumnIndex("note")));
    			photo.setLat(c.getString(c.getColumnIndex("lat")));
    			photo.setLng(c.getString(c.getColumnIndex("lng")));
    			photo.setTime(c.getString(c.getColumnIndex("time")));
    			photos.add(photo);
    		} while (c.moveToNext());
    	}

		if (c != null && !c.isClosed()) {
	         c.close();
	    }
		
		return photos;
	}
	
	public int update(int id, ContentValues values) {
		return db.update(tb_name, values, "id=" + id, null);
	}
	
	public int delete(int id) {
		return db.delete(tb_name, "id=" + id, null);
	}
	
	public PhotoEntity getById(int id) {
		Cursor c = db.query(tb_name, new String[] { "id", "jobid", "photo", "note", "lat", "lng", "time" }, "id=" + id, null, null, null, null);
		
		PhotoEntity photo = null;
		
		if(c.moveToFirst()) {
			photo = new PhotoEntity();
			photo.setId(c.getInt(c.getColumnIndex("id")));
			photo.setId(c.getInt(c.getColumnIndex("jobid")));
			photo.setPhoto(c.getString(c.getColumnIndex("photo")));
			photo.setNote(c.getString(c.getColumnIndex("note")));
			photo.setLat(c.getString(c.getColumnIndex("lat")));
			photo.setLng(c.getString(c.getColumnIndex("lng")));
			photo.setTime(c.getString(c.getColumnIndex("time")));
		}
		
		if (c != null && !c.isClosed()) {
	         c.close();
	    }
		
		return photo;
	}
	
	public ArrayList<PhotoEntity> getByJobId(int jobId) {
		Cursor c = db.query(tb_name, new String[] { "id", "jobid", "photo", "note", "lat", "lng", "time" }, "jobid=" + jobId, null, null, null, null);
		
		ArrayList<PhotoEntity> photos = new ArrayList<PhotoEntity>();
		
		PhotoEntity photo;

    	if(c.moveToFirst()) {
    		do {
    			photo = new PhotoEntity();
    			photo.setId(c.getInt(c.getColumnIndex("id")));
    			photo.setJobId(c.getInt(c.getColumnIndex("jobid")));
    			photo.setPhoto(c.getString(c.getColumnIndex("photo")));
    			photo.setNote(c.getString(c.getColumnIndex("note")));
    			photo.setLat(c.getString(c.getColumnIndex("lat")));
    			photo.setLng(c.getString(c.getColumnIndex("lng")));
    			photo.setTime(c.getString(c.getColumnIndex("time")));
    			photos.add(photo);
    		} while (c.moveToNext());
    	}

		if (c != null && !c.isClosed()) {
	         c.close();
	    }
		
		return photos;
	}
}
