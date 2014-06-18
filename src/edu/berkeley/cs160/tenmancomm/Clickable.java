package edu.berkeley.cs160.tenmancomm;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Clickable {
	private int reportID;
	private Context c;
	public TextView mgrText;
	public TextView tenText;

	public Clickable(int id, String title, int pad, Context context) {
		reportID = id;
		c = context;

		mgrText = new TextView(c);
		mgrText.setText(title);
        mgrText.setTextColor(0xFF000000);
        mgrText.setClickable(true);
        
		mgrText.setOnClickListener(new OnClickListener() { 
        	public void onClick(View v) {
        		new DetailsDialogManager(c,reportID).show();
        	}
        });
		
		tenText = new TextView(c);
		tenText.setText(title);
        tenText.setTextColor(0xFF000000);
        tenText.setClickable(true);
        tenText.setPadding(pad, pad, pad, 0);
        
		tenText.setOnClickListener(new OnClickListener() { 
        	public void onClick(View v) {
        		new DetailsDialogTenant(c,reportID).show();
        	}
        });
	}
}