package com.greenwich.sherlock.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityHelper {
	private DefaultHttpClient httpClient;
	private List<NameValuePair> nameValuePairs;
	private Context mContext;
	
	public ConnectivityHelper(Context context){
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_0);
		
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		
		httpClient = new DefaultHttpClient(params);
		
		nameValuePairs = new ArrayList<NameValuePair>();
		mContext = context;
	}
	
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null);
	}
	
	public void setParameters(String key, String value){
		nameValuePairs.add(new BasicNameValuePair(key, value));
	}
	
	public ResultRequest doGet(String pageUrl) throws Exception  {
		try {					
			pageUrl += '?' + URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
			HttpResponse response = httpClient.execute(new HttpGet(pageUrl)); 
			HttpEntity entity = response.getEntity();
			
			return new ResultRequest(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, HTTP.UTF_8));
		} catch (Exception e) {
			Log.e("Connect_Web", "Error in http connection " + e.toString());
			throw new Exception();
		}
	}	
	
	public ResultRequest getResultRequest(String pageUrl, boolean isDoPost) {
		HttpResponse response;
		try {	
			if (isDoPost) {
				HttpPost httpPost = new HttpPost(pageUrl); 
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
				response = httpClient.execute(httpPost);
			} else {
				pageUrl += '?' + URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
				response = httpClient.execute(new HttpGet(pageUrl)); 
			}
			
			HttpEntity entity = response.getEntity();
			return new ResultRequest(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, HTTP.UTF_8));
		} catch (Exception e) {
			Log.e("OEE", "[getResultRequest] Error in http connection " + e.toString());
			try {
				throw new Exception();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return null;
	}
	
	public void clearParameters(){
		nameValuePairs.clear();
	}
}
