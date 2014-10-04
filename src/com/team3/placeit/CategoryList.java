package com.team3.placeit;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class CategoryList extends ListActivity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create an Action Bar instance
		ActionBar actionBar = getActionBar();
		// Create tabs in the action bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the tabs for the Action Bar
		Tab tab1 = actionBar.newTab()
				.setText("Categories")
				.setTabListener(new CustomTabListener<CategoryFragment>(this, "category", CategoryFragment.class));  
		actionBar.addTab(tab1);
		//
	}
	
	@Override
	public void onBackPressed()
	{
		// Overwrite this to restart the MainActivity so map is updated
		startActivity(new Intent(this, MainActivity.class));
	}

}