package com.team3.placeit;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class CategoryFragment extends ListFragment{
	private SharedPreferences sharedPreferences;
	private ArrayList<String> categories;
	private ArrayList<String> idList;
	private ArrayAdapter<String> adapter;

	private final int MAX_TITLE_LENGTH = 100; // Max character limit for Title
	private final int MAX_DESC_LENGTH = 1000; // Max character limit for
												// Description
	
	private String placeItTitle;
	private String placeItDesc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*
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
		}CategoryFragment
		/** Creating array adapter to set data in List View */
		categories = new ArrayList<String>();
		categories.add("Food");
		categories.add("Shop");
		categories.add("Groceries");
		adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_activated_1, categories);

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
		alert.setTitle("New Place It");
		alert.setMessage("Please enter a Place It Title and Description:");

		// Set an EditText view to get user input
		final EditText placeItTitle = new EditText(this.getActivity());
		final EditText placeItDesc = new EditText(this.getActivity());
		placeItTitle.setHint("Title (" + MAX_TITLE_LENGTH + " characters)");
		placeItDesc.setHint("Description (" + MAX_DESC_LENGTH + " characters)");

		// Set max character limits on the title and description fields
		placeItTitle
		.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				MAX_TITLE_LENGTH) });
		placeItDesc
		.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				MAX_DESC_LENGTH) });
		

		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this.getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(placeItTitle);
		ll.addView(placeItDesc);
		alert.setView(ll);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String title = placeItTitle.getText().toString();
				String desc = placeItDesc.getText().toString();
				// If the user has not entered in a title, but has entered in a
				// description
				// grab the first few words of the description and set it as the
				// title
				if (title.isEmpty() && !desc.isEmpty()) {
					String[] trimmedDesc = desc.split(" ");
					int length = trimmedDesc.length;
					String temp = "";
					if (length > 3) {
						for (int i = 0; i < 3; i++) {
							if ((temp.length() + trimmedDesc[i].length()) < MAX_TITLE_LENGTH) {
								temp += trimmedDesc[i] + " ";
							}
						}
						if ((temp.length() + trimmedDesc[3].length()) < MAX_TITLE_LENGTH) {
							temp += trimmedDesc[3];
						}
						if (temp.length() == 0) {
							temp += trimmedDesc[0].substring(0, 10);
						}
					} else {
						for (int i = 0; i < length - 1; i++) {
							if ((temp.length() + trimmedDesc[i].length()) < MAX_TITLE_LENGTH) {
								temp += trimmedDesc[i] + " ";
							}
						}
						if ((temp.length() + trimmedDesc[length - 1].length()) < MAX_TITLE_LENGTH) {
							temp += trimmedDesc[length - 1];
						}
						if (temp.length() == 0) {
							temp += trimmedDesc[0].substring(0, 10);
						} 
					}
					title = temp;
				}
			}
		});
		alert.show();
	}
	
	
}
