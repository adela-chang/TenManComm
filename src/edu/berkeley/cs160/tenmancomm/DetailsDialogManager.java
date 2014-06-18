package edu.berkeley.cs160.tenmancomm;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class DetailsDialogManager extends Dialog {
	
	private Context c;
	private int id;
	private int rid;
	private int tid;
	private int sev;
	private String title = "failed";
	private String desc = "failed";
	private String severity = "failed";
	private String name = "failed";
	private String address1 = "failed";
	private String address2 = "failed";
	
	private JSONObject jo;
	private boolean flagged = false;
	private boolean flagChanged = false;
	private boolean severityChanged = false;
	private String newSeverity = "";
	
	private ImageView flag;
	private ImageView reportImage;
	private EditText comment;
	private RadioGroup editSeverity;
	private RadioButton rb1, rb2, rb3;
	
    public DetailsDialogManager(Context context, int reportID)
    {
    	super(context);
    	c = context;
    	rid = reportID;
    	
    	Uri report = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "getreport")
        .appendQueryParameter("reportid", String.valueOf(rid))
        .build();
    	try {
        	JSONObject object = TenManComm.JSONFromUri(report);
    		jo = object.getJSONObject("data");
			id = jo.getInt("id");
			tid = jo.getInt("tenantid");
			title = jo.getString("title");
			desc = jo.getString("description");
			sev = jo.getInt("severity");
			if (sev == 3) {
				severity = "high";
			} else if (sev == 2) {
				severity = "medium";
			} else if (sev == 1) {
				severity = "low";
			} else {
				severity = "unknown";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
    	Uri reportT = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "tenantinfo")
        .appendQueryParameter("tenantid", String.valueOf(tid))
        .build();
    	try {
        	JSONObject object = TenManComm.JSONFromUri(reportT);
    		jo = object.getJSONObject("data");
			name = jo.getString("name");
			address1 = jo.getString("address");
			address2 = jo.getString("address2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsmanager);
        setTitle("Problem Report");

        Report r = ShowReportsMgr.reportMap.get(rid);

        flag = (ImageView)findViewById(R.id.flag);
        reportImage = (ImageView)findViewById(R.id.imageHolder);
        editSeverity = (RadioGroup)findViewById(R.id.radioGroup1);
        rb1 = (RadioButton)findViewById(R.id.radio0);
        rb2 = (RadioButton)findViewById(R.id.radio1);
        rb3 = (RadioButton)findViewById(R.id.radio2);
              
        ((TextView)findViewById(R.id.nameForm)).setText(name);
        ((TextView)findViewById(R.id.addressForm1)).setText(address1);
        ((TextView)findViewById(R.id.addressForm2)).setText(address2);
        ((TextView)findViewById(R.id.problemForm)).setText(title);
        ((TextView)findViewById(R.id.descriptionForm)).setText(desc);
        ((TextView)findViewById(R.id.severityForm)).setText(severity);
        ((TextView)findViewById(R.id.commentLink)).setOnClickListener(new commentListener());
    	flag.setOnClickListener(new flagListener());
    	
    	((TextView)findViewById(R.id.editSeverity)).setOnClickListener(new severeListener());
    	((Button)findViewById(R.id.close)).setOnClickListener(new OKListener());
    	((Button)findViewById(R.id.save)).setOnClickListener(new saveListener());
    	
    	if (!r.picture.equals(""))
    	{
	    	try {
				Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(Base64.decode(r.picture)));
				((ImageView)findViewById(R.id.imageHolder)).setImageBitmap(bm);
			} catch (IOException e) {e.printStackTrace();}
    	}

    }
    
    private class OKListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	dismiss();
        }
    }
    
    private class flagListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	if (flagged == false) {
        		flag.setImageResource(R.drawable.redflag);
        		flagged = true;
        	} else {
        		flag.setImageResource(R.drawable.grayflag);
        		flagged = false;
        	}
        	flagChanged = true;
        }
    }
    
    private class commentListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	LayoutParams commentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
   //     	comment.setLayoutParams(commentParams);
        }
    }
    
    private class severeListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) reportImage.getLayoutParams();
        	lp.addRule(RelativeLayout.BELOW, editSeverity.getId());
        	reportImage.setLayoutParams(lp);
        	editSeverity.setVisibility(View.VISIBLE); 
        	severityChanged = true;
        } 
    }
    
    private class saveListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	HttpClient httpclient = new DefaultHttpClient();
			try {
        	HttpPost httppost = new HttpPost("");
        	HttpResponse response = null;
        	if (flagChanged == true) {
        		httppost = new HttpPost("http://designbyjens.se/tmc/?action=switchflag&reportid=" + String.valueOf(rid));
				response = httpclient.execute(httppost);
        	}
        	if (severityChanged == true) {
        		if (rb1.isChecked() == true) {
            		httppost = new HttpPost("http://designbyjens.se/tmc/?action=setseverity&reportid=" + String.valueOf(rid) + "&severity=1");
        	    	newSeverity = "low";
        		} else if (rb2.isChecked() == true) {
            		httppost = new HttpPost("http://designbyjens.se/tmc/?action=setseverity&reportid=" + String.valueOf(rid) + "&severity=2");
        	    	newSeverity = "medium";
        		} else if (rb3.isChecked() == true) {
            		httppost = new HttpPost("http://designbyjens.se/tmc/?action=setseverity&reportid=" + String.valueOf(rid) + "&severity=3");
        	    	newSeverity = "high";
        		}
				response = httpclient.execute(httppost);
        	}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	String toastText;
        	String flagStatus;
        	if (flagged == true) {
        		flagStatus = "flagged";
        	} else {
        		flagStatus = "unflagged";
        	}
        	if (flagChanged == true && severityChanged == true) {
        		toastText = "Successfully changed severity to " + newSeverity + " and " + flagStatus + " report!";
        	} else if (flagChanged == true) {
        		toastText = "Successfully " + flagStatus + " report!";
        	} else if (severityChanged == true) {
        		toastText = "Successfully changed severity to " + newSeverity + "!";
        	} else {
        		toastText = "No changes made.";
        	}
        	flagChanged = false;
        	newSeverity = null;
        	Toast.makeText(c, toastText, Toast.LENGTH_SHORT).show();
        	dismiss();
        	((ShowReportsMgr) c).refresh();
        }
    }
}
