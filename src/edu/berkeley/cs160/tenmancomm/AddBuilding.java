package edu.berkeley.cs160.tenmancomm;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddBuilding extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbuilding);

        c = this;
        id = getIntent().getExtras().getString("managerid");
        buildinglist = (LinearLayout)findViewById(R.id.buildinglist);
        
        setContent();
        setListeners();
    }
    
    private void setContent()
    {
    	Uri uri = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "managerinfo")
        .appendQueryParameter("managerid", id)
        .build();
    	
	    JSONObject object = TenManComm.JSONFromUri(uri);
	    try {
	    	JSONObject data = object.getJSONObject("data");
			JSONArray ja = data.getJSONArray("buildings");
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				
				TextView address = new TextView(c);
				TextView address2 = new TextView(c);
				TextView code = new TextView(c);
				
				address.setText(jo.getString("address"));
				address.setTextColor(0xFF000000);
				address2.setText(jo.getString("address2"));
				address2.setTextColor(0xFF000000);
				code.setText("Building code: " + jo.getString("code"));
				code.setTextColor(0xFF000000);
				if (i != ja.length()-1) {
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					lp.setMargins(0,0,0,20);
					code.setLayoutParams(lp);
				}
				
				buildinglist.addView(address);
				buildinglist.addView(address2);
				buildinglist.addView(code);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
    }
    
    private void setListeners()
    {
    	((Button)findViewById(R.id.addbutton)).setOnClickListener(new OnClickListener() {
    		public void onClick(View v)
    		{
    			HttpClient httpclient = new DefaultHttpClient();
    	    	String address = ((EditText)findViewById(R.id.address)).getText().toString();
    	    	String address2 = ((EditText)findViewById(R.id.address2)).getText().toString();

    			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    			nameValuePairs.add(new BasicNameValuePair("managerid", id));
    	        nameValuePairs.add(new BasicNameValuePair("address", address));
    	        nameValuePairs.add(new BasicNameValuePair("address2", address2));
    	        
    	        HttpPost httppost = new HttpPost("http://designbyjens.se/tmc/?action=addbuilding");
    	    	
    	    	HttpResponse response = null;
    	    	try {
    				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    				response = httpclient.execute(httppost);
    				
    				//Log.v("DEBUG", EntityUtils.toString(response.getEntity()));
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			Toast.makeText(c, "Building added!", Toast.LENGTH_SHORT).show();
    			finish();
    		}
    	});
    }
    
    Activity c;
    private String id = null;
    private LinearLayout buildinglist;
}
