package com.izedtea.creation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

	public DbOpenHelper(Context context) {
		super(context, "creation.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table job(id integer primary key autoincrement, side int, so text, type int, cusname text, lat text, lng text, time text, status integer, distance text);");
		db.execSQL("create table photo(id integer primary key autoincrement, jobid integer, photo text, note text, lat text, lng text, time text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists job;");
		db.execSQL("drop table if exists photo;");
        onCreate(db);
	}
}
