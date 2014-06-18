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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        final RadioButton radio_tenant = (RadioButton) findViewById(R.id.radio_tenant);
        final RadioButton radio_manager = (RadioButton) findViewById(R.id.radio_manager);
        final Button btn_register = (Button) findViewById(R.id.btn_register);
        final TextView lbl_aptno = (TextView) findViewById(R.id.TextView02);
        final EditText txt_aptno = (EditText) findViewById(R.id.txt_apt);
        final TextView lbl_code = (TextView) findViewById(R.id.TextView01);
        final EditText txt_code = (EditText) findViewById(R.id.txt_code);
        final EditText txt_name = (EditText) findViewById(R.id.txt_name);
        
        OnClickListener radioListener =  new OnClickListener() {
		    public void onClick(View v) {
		        // Perform action on clicks
		        RadioButton rb = (RadioButton) v;
		        if(rb == radio_tenant && rb.isChecked()){
		        	lbl_aptno.setVisibility(View.VISIBLE);
		        	txt_aptno.setVisibility(View.VISIBLE);
		        	lbl_code.setVisibility(View.VISIBLE);
		        	txt_code.setVisibility(View.VISIBLE);
		        	isTenant = true;
		        }
		        else{
		        	lbl_aptno.setVisibility(View.GONE);
		        	txt_aptno.setVisibility(View.GONE);
		        	lbl_code.setVisibility(View.GONE);
		        	txt_code.setVisibility(View.GONE);
		        	isTenant = false;
		        }
		    }

        };
        radio_tenant.setOnClickListener(radioListener);
        radio_manager.setOnClickListener(radioListener);
        
        btn_register.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        // Perform action on clicks
		        Toast.makeText(Register.this, "Congratulations " + txt_name.getText() + "\nYou are now registered!" , Toast.LENGTH_SHORT).show();
		        register();
		        finish();
		    }
        });
    }
    
    void register()
    {
    	HttpClient httpclient = new DefaultHttpClient();
    	String name = ((EditText)findViewById(R.id.txt_name)).getText().toString();
		String username = ((EditText)findViewById(R.id.txt_username)).getText().toString();
		String password = ((EditText)findViewById(R.id.txt_password)).getText().toString();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        
        HttpPost httppost;

    	if (isTenant)
    	{
    		httppost = new HttpPost("http://designbyjens.se/tmc/?action=addtenant");
    		
    		String code = ((EditText)findViewById(R.id.txt_code)).getText().toString();
    		String apt = ((EditText)findViewById(R.id.txt_apt)).getText().toString();
            nameValuePairs.add(new BasicNameValuePair("code", code));
            nameValuePairs.add(new BasicNameValuePair("apt", apt));
    	}
    	else
    	{
    		httppost = new HttpPost("http://designbyjens.se/tmc/?action=addmanager");
    	}
    	
    	HttpResponse response = null;
    	try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			Log.v("DEBUG", EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {e.printStackTrace();}
    }
    
    private boolean isTenant = true;
}