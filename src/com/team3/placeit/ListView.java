package com.team3.placeit;

import android.os.Bundle;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class ListView extends Activity {
	
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create an Action Bar instance
		ActionBar actionBar = getActionBar();
		// Create tabs in the action bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
		// Get information from the Place It
        boolean notificationAlert = getIntent().getBooleanExtra("alert", false);
        String placeItTitle = getIntent().getStringExtra("title");
        String placeItDesc = getIntent().getStringExtra("description");
        final int id = getIntent().getIntExtra("id", 0);
        
        sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
		
        // Create the tabs for the Action Bar
		Tab tab1 = actionBar.newTab()
        		.setText("Active")
        		.setTabListener(new CustomTabListener<ActivePlaceItFragment>(this, "active", ActivePlaceItFragment.class));  

        Tab tab2 = actionBar.newTab()
        		.setText("Pulled Down")
        		.setTabListener(new CustomTabListener<PulledDownPlaceItFragment>(this, "pulled down", PulledDownPlaceItFragment.class));
        if (notificationAlert) {
        	actionBar.addTab(tab1, false);
            actionBar.addTab(tab2, true);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
    		alert.setTitle(placeItTitle);
    		alert.setMessage(placeItDesc);
    		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				// Just close the alert with no action
    			}
    				
    		});
    		alert.setNeutralButton("Repost Place It", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Toast.makeText(ListView.this, "Place It reposted!",
    						Toast.LENGTH_SHORT).show();
    				SharedPreferences.Editor editor = sharedPreferences
    						.edit();
    				// Mark the Place It as not pulled down and active again
    				editor.putBoolean("isPulledDown" + id, false);
    				editor.putBoolean("isActive" + id, true);
    				editor.commit();
    				// Re-populate the lists
    				ActionBar actionBar = getActionBar();
    				actionBar.removeAllTabs();
    				Tab tab1 = actionBar.newTab()
    		        		.setText("Active")
    		        		.setTabListener(new CustomTabListener<ActivePlaceItFragment>(ListView.this, "active", ActivePlaceItFragment.class));  

    		        Tab tab2 = actionBar.newTab()
    		        		.setText("Pulled Down")
    		        		.setTabListener(new CustomTabListener<PulledDownPlaceItFragment>(ListView.this, "pulled down", PulledDownPlaceItFragment.class));
    		        actionBar.addTab(tab1, false);
    		    	actionBar.addTab(tab2, true);
    			}
    		});
    		alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Toast.makeText(ListView.this, "Place It discarded!",
    						Toast.LENGTH_SHORT).show();
    				SharedPreferences.Editor editor = sharedPreferences
    						.edit();
    				// Mark the Place It as discarded, not pulled down, and not active
    				editor.putBoolean("isDiscarded" + id, true);
    				editor.putBoolean("isPulledDown" + id, false);
    				editor.putBoolean("isActive" + id, false);
    				editor.commit();
    				// Re-populate the lists
    				ActionBar actionBar = getActionBar();
    				actionBar.removeAllTabs();
    				Tab tab1 = actionBar.newTab()
    		        		.setText("Active")
    		        		.setTabListener(new CustomTabListener<ActivePlaceItFragment>(ListView.this, "active", ActivePlaceItFragment.class));  

    		        Tab tab2 = actionBar.newTab()
    		        		.setText("Pulled Down")
    		        		.setTabListener(new CustomTabListener<PulledDownPlaceItFragment>(ListView.this, "pulled down", PulledDownPlaceItFragment.class));
    		        actionBar.addTab(tab1, false);
    		    	actionBar.addTab(tab2, true);
    			}
    				
    		});
    		alert.show();
    		notificationAlert = false;
        } else {
        	actionBar.addTab(tab1);
        	actionBar.addTab(tab2);
        }
	}

	@Override
	public void onBackPressed()
	{
		// Overwrite this to restart the MainActivity so map is updated
		startActivity(new Intent(this, MainActivity.class));
	}

}