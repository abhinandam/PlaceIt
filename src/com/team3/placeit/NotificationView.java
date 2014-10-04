package com.team3.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class NotificationView extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Store the values about the Place It
		String title = getIntent().getStringExtra("title");
		String desc = getIntent().getStringExtra("description");
		int id = getIntent().getIntExtra("id", 0);
		
		// Pass along the values to the List View
		Intent list = new Intent(this, ListView.class);
		list.putExtra("alert", true);
		list.putExtra("title", title);
		list.putExtra("description", desc);
		list.putExtra("id", id);
		startActivity(list); // Start the List View
	}
}