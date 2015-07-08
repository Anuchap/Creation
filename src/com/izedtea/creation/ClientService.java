package com.izedtea.creation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;

public class ClientService {
	
    public static boolean isOnline(Context context) {
    	try {
    		return ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}
    }
    
    public static String post(String url, MultipartEntity entity) {
		try {			
			HttpPost httpPost = new HttpPost(url); 
			httpPost.setEntity(entity);		
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			return EntityUtils.toString(httpResponse.getEntity());		
		} catch (Exception e) {
			return e.getMessage();
		}		
	}
}
