package com.izedtea.creation;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class JobModel {	
	private SQLiteDatabase db;
	
	public JobModel(Context context) {
		DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
		db = dbOpenHelper.getWritableDatabase();
	}
	
	private static final String tb_name = "job";
	
	public long create(ContentValues values) {
        return db.insert(tb_name, null, values); 
	}
	
	public ArrayList<JobEntity> retrieve() {
		Cursor c = db.query(tb_name, new String[] { "id", "side", "so", "cusname", "type", "lat", "lng", "time", "status", "distance" }, null, null, null, null, null);
		
		ArrayList<JobEntity> jobs = new ArrayList<JobEntity>();
		
		JobEntity job;

    	if(c.moveToFirst()) {
    		do {
    			job = new JobEntity();
    			job.setId(c.getInt(c.getColumnIndex("id")));
    			job.setSide(c.getInt(c.getColumnIndex("side")));
    			job.setSo(c.getString(c.getColumnIndex("so")));
    			job.setCusName(c.getString(c.getColumnIndex("cusname")));
    			job.setType(c.getInt(c.getColumnIndex("type")));
    			job.setLat(c.getString(c.getColumnIndex("lat")));
    			job.setLng(c.getString(c.getColumnIndex("lng")));
    			job.setTime(c.getString(c.getColumnIndex("time")));
    			job.setStatus(c.getInt(c.getColumnIndex("status")));
    			job.setDistance(c.getString(c.getColumnIndex("distance")));
    			jobs.add(job);
    		} while (c.moveToNext());
    	}

		if (c != null && !c.isClosed()) {
	         c.close();
	    }
		
		return jobs;
	}
	
	public int update(int id, ContentValues values) {
		return db.update(tb_name, values, "id=" + id, null);
	}
	
	public int delete(int id) {
		return db.delete(tb_name, "id=" + id, null);
	}
	
	public JobEntity get_by_id(int id) {
		Cursor c = db.query(tb_name, new String[] { "id", "side", "so", "cusname", "type", "lat", "lng", "time", "status", "distance" }, "id=" + id, null, null, null, null);
		
		JobEntity job = null;
		
		if(c.moveToFirst()) {
			job = new JobEntity();
			job.setId(c.getInt(c.getColumnIndex("id")));
			job.setSide(c.getInt(c.getColumnIndex("side")));
			job.setSo(c.getString(c.getColumnIndex("so")));
			job.setCusName(c.getString(c.getColumnIndex("cusname")));
			job.setType(c.getInt(c.getColumnIndex("type")));
			job.setLat(c.getString(c.getColumnIndex("lat")));
			job.setLng(c.getString(c.getColumnIndex("lng")));
			job.setTime(c.getString(c.getColumnIndex("time")));
			job.setStatus(c.getInt(c.getColumnIndex("status")));
			job.setDistance(c.getString(c.getColumnIndex("distance")));
		}
		
		if (c != null && !c.isClosed()) {
	         c.close();
	    }
		
		return job;
	}
	
	public int get_create_id() {
		Cursor c = db.rawQuery("select id from job order by id desc limit 1;", null);
		
		if(c != null && c.moveToFirst()) {
			return c.getInt(0);
		}
		
		return 0;
	}
}
