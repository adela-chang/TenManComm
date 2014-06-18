package edu.berkeley.cs160.tenmancomm;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment
{
    public Comment(JSONObject jo)
    {
    	try {
			content = jo.getString("content");
			time = jo.getString("time");
			id = jo.getInt("id");
			rid = jo.getInt("rid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    public int id = -1;
    public int rid = -1;
    public String content = "failed";
    public String time = "failed";
    
}
