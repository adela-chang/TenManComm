package edu.berkeley.cs160.tenmancomm;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Report
{
    public Report(JSONObject jo)
    {
    	try {
			id = jo.getInt("id");
			severity = jo.getInt("severity");
			flag = jo.getInt("flag");

			title = jo.getString("title");
			desc = jo.getString("description");
			status = jo.getString("status");
			date = jo.getString("date").substring(0, 10);
			picture = jo.getString("picture");

		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    public ArrayList<Comment> comments = new ArrayList<Comment>();
    
    public int id = -1;
    public int severity = -1;
    public int flag = -1;

    public String title = null;
    public String desc = null;
    public String status = null;
    public String date = null;
    public String picture = null;
}
