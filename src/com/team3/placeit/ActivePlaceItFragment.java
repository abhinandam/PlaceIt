package com.team3.placeit;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;

public class ActivePlaceItFragment extends ListFragment {
	
	private SharedPreferences sharedPreferences;
	private ArrayList<String> activeList;
	private ArrayList<String> idList;
	private ArrayAdapter<String> adapter;

	private String placeItTitle;
	private String placeItDesc;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		sharedPreferences = this.getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
		int locationCount = sharedPreferences.getInt("locationCount", 0);
		activeList = new ArrayList<String>();
		idList = new ArrayList<String>();
		
		if (locationCount != 0) {

			// Iterating through all the locations stored
			for (int i = 0; i < locationCount; i++) {
				placeItTitle = sharedPreferences.getString("title" + i, "");
				placeItDesc = sharedPreferences.getString("description" + i, "");	
				if (sharedPreferences.getBoolean("isActive" + i, false)) {
					activeList.add(placeItTitle); // Keep track of the active Place Its
					idList.add("" + i); // Keep track of their ID's
				}
			}
		}
		
		// Creating array adapter to set data in List View
		adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_activated_1, activeList);
		
		// Setting the array adapter to the List View
		setListAdapter(adapter);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();	
	}
	
	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
		placeItTitle = sharedPreferences.getString("title" + idList.get(position), "");
		placeItDesc = sharedPreferences.getString("description" + idList.get(position), "");
		alert.setTitle(placeItTitle);
		alert.setMessage(placeItDesc);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// We should just show the information here
			}
		});
		alert.setNeutralButton("Pull Down", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(ActivePlaceItFragment.this.getActivity(), "Place It pulled down!",
						Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor editor = sharedPreferences
						.edit();
				// Set the Place It to pulled down and not active
				editor.putBoolean("isPulledDown" + idList.get(position), true);
				editor.putBoolean("isActive" + idList.get(position), false);
				editor.commit();		
				idList.remove(position);
				activeList.remove(position); // Remove the item from the list
				adapter.notifyDataSetChanged(); // Update the list view
			}
		});
		alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(ActivePlaceItFragment.this.getActivity(), "Place It discarded!",
						Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor editor = sharedPreferences
						.edit();
				// Set the Place It to discarded and not active
				editor.putBoolean("isDiscarded" + idList.get(position), true);
				editor.putBoolean("isActive" + idList.get(position), false);
				editor.commit();
				idList.remove(position);
				activeList.remove(position); // Remove the item from the list
				adapter.notifyDataSetChanged(); // Update the list view
			}
		});
		alert.show();
	}	
}
