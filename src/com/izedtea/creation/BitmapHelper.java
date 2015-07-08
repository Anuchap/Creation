package com.izedtea.creation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapHelper {
	
	public static byte[] toByteArray(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bitmap.compress(CompressFormat.JPEG, 75, bos);
		} catch (Exception e) {
			Log.e("BitmapHelper", e.getMessage());
		}
		return bos.toByteArray();
	}

	public static Bitmap decodeFile(String fileName, int inSampleSize) {
	    Bitmap bitmap = null;
	    try {
	    	BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inSampleSize = inSampleSize;
	        FileInputStream fis = new FileInputStream(new File(fileName));
	        bitmap = BitmapFactory.decodeStream(fis, null, o);
	        fis.close();
	    } catch (Exception e) {
	    	Log.e("BitmapHelper", e.getMessage());
	    }
	    return bitmap;
	}
	
	public static void bitmapToFile(Bitmap bitmap, String fileName, int quality) {
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
			out.close();
		} catch (Exception e) {
			Log.e("BitmapHelper", e.getMessage());
		}
	}
	
	public static Bitmap toThumbnail(Bitmap bitmap) {
        try     
        {
            int dstWidth = Math.abs((bitmap.getWidth() / 10));
            int dstHeight = Math.abs((bitmap.getHeight() / 10));
            bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
        }
        catch(Exception e) {
        	Log.e("BitmapHelper", e.getMessage());
        }
		return bitmap;
	}
	
	public static Bitmap rotate90Bitmap(Bitmap bitmap) {
		try
		{
			Matrix m = new Matrix();						
			m.preRotate(90);	
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
		} catch (Exception e) {
			Log.e("BitmapHelper", e.getMessage());
		}
		return bitmap;
	}
}
