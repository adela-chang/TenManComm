package edu.berkeley.cs160.tenmancomm;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class formSubmission extends Activity {
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureAct = this;
        setContentView(R.layout.submitreport);
        
    	setInfo();
    	setListeners();
        
        Button pictureButton = (Button) findViewById(R.id.pictureForm);
		pictureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
		        pictureAct.startActivityForResult(camera, 2);
			}
		});
		
	    Button submitButton = (Button) findViewById(R.id.submitForm);
		submitButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					//sends information to server and manager
			
					submit();
	        		Toast.makeText(formSubmission.this, "Your problem has been reported!", Toast.LENGTH_SHORT).show();
	        		setResult(Activity.RESULT_OK);
	        		finish();
				}
			});

	}
    
    void submit()
    {
    	HttpClient httpclient = new DefaultHttpClient();
		String title = ((EditText)findViewById(R.id.problemForm)).getText().toString();
		String desc = ((EditText)findViewById(R.id.descriptionForm)).getText().toString();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tenantid", id));
        nameValuePairs.add(new BasicNameValuePair("title", title));
        nameValuePairs.add(new BasicNameValuePair("description", desc));
        nameValuePairs.add(new BasicNameValuePair("severity", Integer.toString(severity)));
        
        HttpPost httppost = new HttpPost("http://designbyjens.se/tmc/?action=addreport");

    	HttpResponse response = null;
    	try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			String result = EntityUtils.toString(response.getEntity());
			Log.v("DEBUG", result);
			JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
			submitPicture(object.getString("reportid"));
		} catch (Exception e) {e.printStackTrace();}
    }
    
    private void submitPicture(String id)
    {
    	if (pic == null)
    		return;
    	
    	ByteArrayOutputStream out = null;
		try {
		       out = new ByteArrayOutputStream();
		       pic.compress(Bitmap.CompressFormat.JPEG, 90, out);
		} catch (Exception e) {e.printStackTrace();}

		byte[] binary = out.toByteArray();
		
		HttpClient httpclient = new DefaultHttpClient();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("image", Base64.encodeBytes(binary)));

        HttpPost httppost = new HttpPost("http://designbyjens.se/tmc/?action=uploadimage&reportid=" + id);

    	HttpResponse response = null;
    	try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			Log.v("DEBUG", EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {e.printStackTrace();}
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
           // Display image received on the view

				Bundle b = data.getExtras(); // Kept as a Bundle to check for other things in my actual code
				pic = (Bitmap) b.get("data");

				if (pic != null) { // Display your image in an ImageView in your layout (if you want to test it)

					pictureHolder = (ImageView) this.findViewById(R.id.imageHolder);
					pictureHolder.setImageBitmap(pic);
					pictureHolder.invalidate();

				}
			}
    	}
	}
	
	private void setInfo()
	{
		id = getIntent().getExtras().getString("id");
		((TextView)findViewById(R.id.nameForm)).setText(getIntent().getExtras().getString("name"));
		String addr = String.format("%s #%s", getIntent().getExtras().getString("addr"), getIntent().getExtras().getString("apt"));
		((TextView)findViewById(R.id.addressForm1)).setText(addr);
		
		//TODO: 2nd addr line
		((TextView)findViewById(R.id.addressForm2)).setText("");
	}
	
	private void setListeners()
	{
		final RadioButton radio_low = (RadioButton) findViewById(R.id.radio0);
        final RadioButton radio_med = (RadioButton) findViewById(R.id.radio1);
        final RadioButton radio_high = (RadioButton) findViewById(R.id.radio2);

		OnClickListener radioListener = new OnClickListener() {
		    public void onClick(View v) {
		        RadioButton rb = (RadioButton) v;
		        if (rb == radio_low && rb.isChecked()){
		        	severity = 1;
		        }
		        else if (rb == radio_med && rb.isChecked()){
		        	severity = 2;
		        }
		        else {
		        	severity = 3;
		        }
		    }
        };
        
        radio_low.setOnClickListener(radioListener);
        radio_med.setOnClickListener(radioListener);
        radio_high.setOnClickListener(radioListener);
	}
	
	private Bitmap pic = null;
	Activity pictureAct;
	ImageView pictureHolder;
	private int severity = 1;
	private String id = null;
}