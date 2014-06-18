package edu.berkeley.cs160.tenmancomm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TenManComm extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        c = this;
        setListeners();
    }
    
    private void setListeners()
    {
    	((TextView)findViewById(R.id.regbutton)).setOnClickListener(new OnClickListener() {
    		public void onClick(View v)
    		{
    			Intent intent = new Intent(c, Register.class);
    			startActivity(intent);
    		}
    	});
    	((Button)findViewById(R.id.logbutton)).setOnClickListener(new OnClickListener() {
    		public void onClick(View v)
    		{
    			String username = ((EditText)findViewById(R.id.username)).getText().toString();
    			String password = ((EditText)findViewById(R.id.password)).getText().toString();

    	    	Uri uri = new Uri.Builder()
    	        .scheme("http")
    	        .authority("designbyjens.se")
    	        .path("tmc/")
    	        .appendQueryParameter("action", "login")
    	        .appendQueryParameter("username", username)
    	        .appendQueryParameter("password", password)
    	        .build();
    	    	
    	    	//String q = null;
    	    	try {
				    JSONObject object = JSONFromUri(uri);

				    if (object.has("error"))
				    {
				    	//TODO: DENIED
				    	Toast.makeText(c, "Incorrect username/password", Toast.LENGTH_SHORT).show();
				    }
				    else if (object.has("tenant"))
					{
						object = object.getJSONObject("tenant");
						Intent intent = new Intent(c, ShowReportsTenant.class);
						intent.putExtra("tenantid", object.getString("id"));
						startActivity(intent);
					}
					else // manager
					{
				    	object = object.getJSONObject("manager");
				    	Intent intent = new Intent(c, ShowReportsMgr.class);
						intent.putExtra("managerid", object.getString("id"));
						startActivity(intent);
					}
				    
				} catch (Exception e) {e.printStackTrace();}

    	    	//Log.v("DEBUG", q);
    		}
    	});
    }
    
    public static JSONObject JSONFromUri(Uri uri)
    {
    	JSONObject object = null;
    	try {
		    String result = StringFromUri(uri);
		    object = (JSONObject) new JSONTokener(result).nextValue();
    	} catch (Exception e) {e.printStackTrace();}
    	
    	return object;
    }
    
    public static String StringFromUri(Uri uri)
    {
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpget = new HttpGet(uri.toString());
    	String result = null;
    	try {
			InputStream is = httpclient.execute(httpget).getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		    StringBuilder sb = new StringBuilder();
		    sb.append(reader.readLine() + "\n");
		    String line="0";
		    while ((line = reader.readLine()) != null) {
		    	sb.append(line + "\n");
		    }
		    
		    is.close();
		    result = sb.toString();
    	} catch (Exception e) {e.printStackTrace();}
    	
    	return result;
    }
    
    private TenManComm c;
}
