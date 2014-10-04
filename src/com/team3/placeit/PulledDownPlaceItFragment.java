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

/** This is a List Fragment class */
public class PulledDownPlaceItFragment extends ListFragment {
	
	private SharedPreferences sharedPreferences;
	private ArrayList<String> pulledDownList;
	private ArrayList<String> idList;
	private ArrayAdapter<String> adapter;
	
	private String placeItTitle;
	private String placeItDesc;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		sharedPreferences = this.getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
		int locationCount = sharedPreferences.getInt("locationCount", 0);
		pulledDownList = new ArrayList<String>();
		idList = new ArrayList<String>();
		
		if (locationCount != 0) {

			// Iterating through all the locations stored
			for (int i = 0; i < locationCount; i++) {
				if (sharedPreferences.getBoolean("isPulledDown" + i, false)) {
					placeItTitle = sharedPreferences.getString("title" + i, "");
					placeItDesc = sharedPreferences.getString("description" + i, "");
					pulledDownList.add(placeItTitle);
					idList.add("" + i);
				}
			}
		}
		
		/** Creating array adapter to set data in List View */
		adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_activated_1, pulledDownList);
		
		/** Setting the array adapter to the List View */
		setListAdapter(adapter);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	
	@Override
	public void onStart() {
		super.onStart();	
	}
	
	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
		placeItTitle = sharedPreferences.getString("title" + idList.get(position), "");
		placeItDesc = sharedPreferences.getString("description" + idList.get(position), "");
		alert.setTitle(placeItTitle);
		alert.setMessage(placeItDesc);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// No action is taken
				
			}
		});
		
		alert.setNeutralButton("Repost Place It", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(PulledDownPlaceItFragment.this.getActivity(), "Place It reposted!",
						Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor editor = sharedPreferences
						.edit();
				editor.putBoolean("isPulledDown" + idList.get(position), false);
				editor.putBoolean("isActive" + idList.get(position), true);
				editor.commit();
				idList.remove(position);
				pulledDownList.remove(position);
				//adapter.remove(placeItTitle);
				adapter.notifyDataSetChanged();
			}
		});
		alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(PulledDownPlaceItFragment.this.getActivity(), "Place It discarded!",
						Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor editor = sharedPreferences
						.edit();
				editor.putBoolean("isDiscarded" + idList.get(position), true);
				editor.putBoolean("isPulledDown" + idList.get(position), false);
				editor.putBoolean("isActive" + idList.get(position), false);
				editor.commit();
				idList.remove(position);
				pulledDownList.remove(position);
				//adapter.remove(placeItTitle);
				adapter.notifyDataSetChanged();
			}
		});
		alert.show();
	}
	
}
