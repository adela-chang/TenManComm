package edu.berkeley.cs160.tenmancomm;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ShowReportsMgr extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showreportsmgr);
        
        c = this;
    	id = getIntent().getExtras().getString("managerid");

    	setListeners();
        displayReports();
    }
    
    public void refresh() {
    	displayReports();
    }
    
    private void displayReports()
    {
    	reportMap = new HashMap<Integer, Report>();
    	
    	final float scale = getResources().getDisplayMetrics().density;
        int pad = (int) (10 * scale + 0.5f);
        
        ((TextView)findViewById(R.id.hdrReports)).setPadding(0, pad, 0, 0);
        ((TextView)findViewById(R.id.hdrDate)).setPadding(0, pad, 0, 0);
        ((TextView)findViewById(R.id.hdrSeverity)).setPadding(pad, pad, pad, 0);
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
	           		new DetailsDialogManager(c, r.id).show();
	           	}
        	});    
        	
        	ImageView flag = new ImageView(this);
            TextView date = new TextView(this);
            TextView severity = new TextView(this);

        	TextView title = new TextView(this);
        	title.setText(r.title);
        	title.setTextColor(0xFF000000);
        	title.setPadding(0, pad, 0, 0);
            
            String dateFormatted = r.date.substring(5,7) + "/" + r.date.substring(8,10);
            date.setText(dateFormatted);
            date.setTextColor(0xFF000000);
            date.setPadding(0, pad, 0, 0);
            
            if (r.flag == 1) {
            flag.setImageResource(R.drawable.redflag);
            flag.setPadding(0,pad,pad,0);
            }
            
            int sev = r.severity;
            String severe;
			if (sev == 3) {
				severe = "high";
			} else if (sev == 2) {
				severe = "medium";
			} else if (sev == 1) {
				severe = "low";
			} else {
				severe = "unknown";
			}
            severity.setText(String.valueOf(severe));
            severity.setTextColor(0xFF000000);
            severity.setPadding(pad, pad, pad, 0);
            
            tr.addView(flag);
            tr.addView(title);
            tr.addView(date);
            tr.addView(severity); 
            
            t.addView(tr);  
        }
    }
    
    private ArrayList<Report> getReports(String managerid)
    {
    	Uri uri = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "managerreports")
        .appendQueryParameter("managerid", managerid)
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

    }
        
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REPORTED) {
        	displayReports();
        }
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.managermenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
            	Intent intent = new Intent(c, AddBuilding.class);
				intent.putExtra("managerid", id);
				startActivity(intent);
            	break;
        }
        return true;
    }
    
    private String id = null;
    private Activity c;
    static final int REPORTED = 4;
    static HashMap<Integer, Report> reportMap;
}
