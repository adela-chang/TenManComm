package edu.berkeley.cs160.tenmancomm;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ShowReportsTenant extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showreportstenant);
        
        c = this;

        setInfo();
        setListeners();
        displayReports();
    }
    
    private void displayReports()
    {
    	reportMap = new HashMap<Integer, Report>();
    	
    	final float scale = getResources().getDisplayMetrics().density;
        int pad = (int) (10 * scale + 0.5f);
        
        TableLayout t = (TableLayout)findViewById(R.id.list);
        TableRow hdr = (TableRow)t.getChildAt(0);
        t.removeAllViews();
        t.addView(hdr);
        
        ArrayList<Report> reports = getReports(id);
        
        for (final Report r : reports)
        {
        	reportMap.put(r.id, r);

        	TableRow tr = new TableRow(this);
        	tr.setOnClickListener(new OnClickListener() { 
	           	public void onClick(View v) {
	           		new DetailsDialogTenant(c, r.id).show();
	           	}
        	});
            
        	TextView title = new TextView(this);
        	title.setText(r.title);
        	title.setTextColor(0xFF000000);
        	title.setPadding(pad, pad, pad, 0);

            TextView date = new TextView(this);
            TextView status = new TextView(this);

            String dateFormatted = r.date.substring(5,7) + "/" + r.date.substring(8,10);
            date.setText(dateFormatted);
            date.setTextColor(0xFF000000);
            date.setPadding(0, pad, 0, 0);
            status.setText(r.status);
            status.setTextColor(0xFF000000);
            status.setPadding(pad, pad, pad, 0);
            
            tr.addView(title);
            tr.addView(date);
            tr.addView(status);
            
            t.addView(tr);
        } 
    }
    
    private ArrayList<Report> getReports(String tenantid)
    {
    	Uri uri = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "userreports")
        .appendQueryParameter("tenantid", tenantid)
        .build();
    	
    	ArrayList<Report> reportlist = new ArrayList<Report>();
    	try {
		    JSONObject object = TenManComm.JSONFromUri(uri);
		    JSONArray ja = object.getJSONArray("reports");
		    
		    for (int i = 0; i < ja.length(); i++)
		    	reportlist.add(new Report(ja.getJSONObject(i)));
		    
		} catch (Exception e) {e.printStackTrace();}
		
		return reportlist;
    }
    
    private void setListeners()
    {
    	((Button)findViewById(R.id.report)).setOnClickListener(new OnClickListener() {
    		public void onClick(View v)
    		{
    			Intent intent = new Intent(c, formSubmission.class);
    			intent.putExtra("id", id);
    			intent.putExtra("name", name);
    			intent.putExtra("addr", addr);
    			intent.putExtra("addr2", addr2);
    			intent.putExtra("apt", apt);
    			startActivityForResult(intent, REPORTED); 
    		}
    	}); 
    
    	
    }
    
    private void setInfo()
    {
    	id = getIntent().getExtras().getString("tenantid");
    	Uri uri = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "tenantinfo")
        .appendQueryParameter("tenantid", id)
        .build();

    	JSONObject object = TenManComm.JSONFromUri(uri);
    	try {
			object = object.getJSONObject("data");
			name = object.getString("name");
			addr = object.getString("address");
			addr2 = object.getString("address2");
			apt = object.getString("apt");
		} catch (JSONException e) {e.printStackTrace();}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REPORTED) {
        	displayReports();
        }
    }
    
    private String id = null;
    private String name = null;
    private String addr = null;
    private String addr2 = null;
    private String apt = null;
    private Activity c;
    static final int REPORTED = 4;
    
    static HashMap<Integer, Report> reportMap;
}
