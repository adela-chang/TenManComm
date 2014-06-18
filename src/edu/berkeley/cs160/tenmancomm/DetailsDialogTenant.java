package edu.berkeley.cs160.tenmancomm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class DetailsDialogTenant extends Dialog {
	
	private int rid;
	private Context c;
	private Button button1;
	private Button button2;
	private RelativeLayout container;
	private TextView pictureField;
	private ImageView imageHolder;
	private TextView commentField;
	private EditText commentBox;
	private LinearLayout commentContainer;
	private boolean noComments = true;
	
	public DetailsDialogTenant(Context context, int reportID) {
		super(context);
    	rid = reportID;
    	c = context;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        setTitle("Pending Report");
        
        button1 = (Button)findViewById(R.id.comment);
        button2 = (Button)findViewById(R.id.close);
        container = (RelativeLayout)findViewById(R.id.formSide);
        pictureField = (TextView)findViewById(R.id.pictureField);
        imageHolder = (ImageView)findViewById(R.id.imageHolder);
        commentField = (TextView)findViewById(R.id.commentField);
        commentBox = (EditText)findViewById(R.id.commentBox);
        commentContainer = (LinearLayout)findViewById(R.id.commentContainer);
        TextView t = (TextView)findViewById(R.id.problemForm);
        TextView d = (TextView)findViewById(R.id.descriptionForm);
        TextView s = (TextView)findViewById(R.id.severityForm);

        Report r = ShowReportsTenant.reportMap.get(rid);
        t.setText(r.title);
        d.setText(r.desc);
        if (r.severity == 3) {
        	s.setText("high"); 
		} else if (r.severity == 2) {
			s.setText("medium"); 
		} else if (r.severity == 1) {
			s.setText("low"); 
		} else {}
        
        ArrayList<Comment> comments = getComments(String.valueOf(r.id));
        
        if (!noComments) {
        	RelativeLayout.LayoutParams lpComment = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        	lpComment.addRule(RelativeLayout.ALIGN_TOP, commentContainer.getId());
        	lpComment.setMargins(convertDip(5),0,0,0);
        	commentField.setLayoutParams(lpComment);
        
        	for (final Comment co : comments)
        	{
        		TextView comment = new TextView(this.c);
        		comment.setText(co.content);
        		comment.setTextColor(0xFF000000);
        		commentContainer.addView(comment);
        		TextView time = new TextView(this.c);
        		time.setText(co.time);
        		commentContainer.addView(time);
        	} 
        }

    	button1.setOnClickListener(new CommentListener());
    	button2.setOnClickListener(new OKListener());
    	
    	if (!r.picture.equals(""))
    	{
    		pictureField.setVisibility(View.VISIBLE);
	    	try {
				Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(Base64.decode(r.picture)));
				((ImageView)findViewById(R.id.imageHolder)).setImageBitmap(bm);
			} catch (IOException e) {e.printStackTrace();}
    	}
    }
    
    private int convertDip(float dp) {
    	final float scale = c.getResources().getDisplayMetrics().density;
    	int px = (int) (dp * scale + 0.5f);
    	return px;
    }
    
    private ArrayList<Comment> getComments(String rid)
    {
    	Uri uri = new Uri.Builder()
        .scheme("http")
        .authority("designbyjens.se")
        .path("tmc/")
        .appendQueryParameter("action", "getreport")
        .appendQueryParameter("reportid", rid)
        .build();
    	
    	ArrayList<Comment> commentlist = new ArrayList<Comment>();
    	try {
		    JSONObject object = TenManComm.JSONFromUri(uri);
		    JSONArray ja = object.getJSONArray("comments");
		    if (ja.length() > 0) {
		    	noComments = false;
		    	for (int i = 0; i < ja.length(); i++)
		    		commentlist.add(new Comment(ja.getJSONObject(i)));
		    }
		} catch (Exception e) {e.printStackTrace();}
		
		return commentlist;
    }
    
    
    private class OKListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	dismiss();
        }
    }
    
    private class CommentListener implements android.view.View.OnClickListener {
    	public void onClick(View v) {
    		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    		lp.addRule(RelativeLayout.BELOW, R.id.commentContainer);
    		lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.commentContainer);
    		lp.setMargins(0,convertDip(20),0,0);
    		commentBox.setLayoutParams(lp);
    		commentBox.setMinHeight(convertDip(80));
    		commentBox.setVisibility(View.VISIBLE);

    		if (noComments)
    		{
    		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		lp2.addRule(RelativeLayout.ALIGN_TOP, commentBox.getId());
    		lp2.setMargins(convertDip(5), 0, 0, 0);
    		commentField.setLayoutParams(lp2);
    		commentField.setVisibility(View.VISIBLE);
    		}
    		
    		button1.setText("Save");
    		button1.setOnClickListener(new CommentSaveListener());
    	}
    }
    
    private class CommentSaveListener implements android.view.View.OnClickListener {
    	public void onClick(View v) {
        	if (commentBox.getText().toString() != "") {
        		HttpClient httpclient = new DefaultHttpClient();
    			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("reportid", String.valueOf(rid)));
                nameValuePairs.add(new BasicNameValuePair("content", commentBox.getText().toString()));
       			HttpPost httppost = new HttpPost("http://designbyjens.se/tmc/?action=addcomment");         
       			HttpResponse response = null;
        		try {
        			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					response = httpclient.execute(httppost);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}        		
        		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
        		lp.addRule(RelativeLayout.BELOW, imageHolder.getId());
        		lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.imageHolder);
        		commentBox.setLayoutParams(lp);
        		commentBox.setVisibility(View.INVISIBLE);

        		if (noComments)
        		{
        		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0);
        		lp2.addRule(RelativeLayout.ALIGN_TOP, commentBox.getId());
        		commentField.setLayoutParams(lp2);
        		commentField.setVisibility(View.INVISIBLE);
        		}
        		
        		button1.setText("Add Comment");
        		button1.setOnClickListener(new CommentListener());
        	} else {
        		// insert Toast here
        	}
    	}
    }

}
