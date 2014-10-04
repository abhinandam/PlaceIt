package com.team3.placeit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMapClickListener,
		OnInfoWindowClickListener {
//
	private GoogleMap mMap; // Instance of Google Maps
	public static ArrayList<Marker> mMarkers; // Used to store the markers
	private final int MAX_TITLE_LENGTH = 100; // Max character limit for Title
	private final int MAX_DESC_LENGTH = 1000; // Max character limit for
												// Description
	private SharedPreferences sharedPreferences; // Used for storing data
	private int locationCount = 0; // Store the number of Place Its added
	private String placeItTitle = "";
	private boolean isActive = false; // To see whether Place It is active
	private String lat; // Store latitude
	private String lng; // And longitude for the Place It

	public static LocationManager locationManager; // For getting user location
	public static PendingIntent pendingIntent; // To use for proximity alerts
	public static Context context;
	public static final String PLACE_IT_URI = "http://cse110syncsample.appspot.com/item"; // store
																						// placeits
	public static final String USERS_URI = "http://cse110syncsample.appspot.com/product";

	public String username = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setUpMapIfNeeded(); // Set up the map
		mMap.setMyLocationEnabled(true); // Enable location for user
		mMap.setOnMapClickListener(this); // Enable adding markers to the map
		// Enable clicking on info windows for each marker
		mMap.setOnInfoWindowClickListener((OnInfoWindowClickListener) this);

		// Getting LocationManager object from System Service LOCATION_SERVICE
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mMarkers = new ArrayList<Marker>();
		context = getBaseContext();

		// Get the SharedPreferences for the id of location
		sharedPreferences = getSharedPreferences("location", 0);

		// Store the number of markers added to the map
		locationCount = sharedPreferences.getInt("locationCount", 0);

		// Store the zoom view of the map
		String zoom = sharedPreferences.getString("zoom", "14");

		// Get the last known location of the user
		Location knownLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (knownLocation == null) {
			knownLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

		// If the location of the user is known, get the latitude and longitude
		if (knownLocation != null) {
			double latNum = knownLocation.getLatitude();
			double lngNum = knownLocation.getLongitude();
			lat = Double.toString(latNum);
			lng = Double.toString(lngNum);
		} else {
			// Create temporary value for UCSD
			lat = "32.8810";
			lng = "-117.2380";
		}

		// Moving CameraPosition to last clicked position
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double
				.parseDouble(lat), Double.parseDouble(lng))));

		// Setting the zoom level in the map on last position is clicked
		mMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));

		new UpdateSpinnerTask().execute(MainActivity.PLACE_IT_URI); // get info
																	// from
																	// server

		/*if (locationCount != 0) {

			placeItTitle = ""; // Title for the Place It
			String placeItDesc = "";
			// Description for the Place It
			// Iterating through all the locations stored
			for (int i = 0; i < locationCount; i++) {
				isActive = sharedPreferences.getBoolean("isActive" + i, false);

				// Getting the latitude of the i-th location lat =
				sharedPreferences.getString("lat" + i, "0");

				// Getting the longitude of the i-th location lng =
				sharedPreferences.getString("lng" + i, "0");

				placeItTitle = sharedPreferences.getString("title" + i, "");
				placeItDesc = sharedPreferences
						.getString("description" + i, "");

				// Drawing marker on the map
				drawMarker(
						new LatLng(Double.parseDouble(lat),
								Double.parseDouble(lng)), placeItTitle,
						placeItDesc, isActive);
			}
		}*/

		//clearAllMarkers(); // TODO remove function call

		// This button is used to navigate to the List of Active and Completed
		// Place Its
		Button listBtn = (Button) findViewById(R.id.list);
		listBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayList();
			}
		});

		// This button is used to navigate to the List of Categories for a
		// Place It
		Button categoryBtn = (Button) findViewById(R.id.category);
		categoryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayCategories();
			}
		});
		Button logoutBtn = (Button) findViewById(R.id.logout);
		logoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				displayLogout();
			}
		});
		
		// Retrieve user name
		username = getIntent().getStringExtra("user");
		
	}

	// TODO remove method. It's only being used for debugging
	private void clearAllMarkers() {
		while (locationCount > 0) {
			context = getBaseContext();
			customRemoveProximityAlert(context, locationCount - 1,
					mMarkers.get(locationCount - 1));
			locationCount--;
		}
		context = getBaseContext();
		customRemoveProximityAlert(context, 0, mMarkers.get(0));
		// This is to reset the map from scratch with no markers.
		mMap.clear();
		// Opening the editor object to delete data from sharedPreferences
		SharedPreferences.Editor editor = sharedPreferences.edit();
		// Clearing the editor
		editor.clear();
		// Committing the changes
		editor.commit();
		// Setting locationCount to zero
		locationCount = 0;
		mMarkers.clear();
	}
	
	private void displayLogout(){
		Intent myIntent = new Intent(this, LoginActivity.class);
		startActivity(myIntent);
	}

	/*
	 * This method is for displaying the List of Place Its when the List button
	 * is clicked
	 */
	private void displayList() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		/** Storing the zoom level to the shared preferences */
		editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));
		editor.commit();
		Intent showList = new Intent(this, ListView.class);
		showList.putExtra("alert", false);
		startActivity(showList);
	}

	/*
	 * This method is for displaying the List of Place Its when the List button
	 * is clicked
	 */
	private void displayCategories() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		/** Storing the zoom level to the shared preferences */
		editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));
		editor.commit();
		Intent showCategories = new Intent(this, CategoryList.class);
		startActivity(showCategories);
	}

	/*
	 * This method is used to set up the Google Maps variable
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
			}
		}
	}

	/*
	 * This method is called to draw Place Its onto the map
	 */
	private void drawMarker(LatLng point, String title, String desc,
			boolean isActive) {
		Marker added = mMap.addMarker(new MarkerOptions()
				.position(point)
				.title(title)
				.snippet(desc)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.place_it_marker)));
		mMarkers.add(added);
		if (!isActive) {
			added.remove();
		} else {
			String id = added.getId();
			id = id.substring(1);
			context = getBaseContext();
			customAddProximityAlert(context, point, Integer.parseInt(id),
					title, desc);
		}
	}

	/*
	 * This method is used to add a proximity alert to a specific Place It
	 */
	public static void customAddProximityAlert(Context context,
			LatLng position, int id, String title, String desc) {
		// This intent will call the activity ProximityActivity
		Intent proximityIntent = new Intent(
				"com.team3.placeit.activity.proximity");

		// Passing latitude to the PendingActivity
		proximityIntent.putExtra("lat", position.latitude);

		// Passing longitude to the PendingActivity
		proximityIntent.putExtra("lng", position.longitude);

		// Passing title and description to the PendingActivity
		proximityIntent.putExtra("title", title);
		proximityIntent.putExtra("description", desc);
		proximityIntent.putExtra("id", id);

		// Creating a pending intent which will be invoked by
		// LocationManager when the specified region is
		// entered or exited
		pendingIntent = PendingIntent.getActivity(context, id, proximityIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		// Setting proximity alert
		// The pending intent will be invoked when the device enters
		// or exits the region 800 meters
		// away from the marked point
		// The -1 indicates that, the monitor will not be expired

		locationManager.addProximityAlert(position.latitude,
				position.longitude, 800, -1, pendingIntent);
	}

	/* This method is used to remove a proximity alert for a specific Place It */
	public static void customRemoveProximityAlert(Context context, int id,
			Marker marker) {
		// Get the specific proximity alert
		Intent proximityIntent = new Intent(
				"com.team3.placeit.activity.proximity");

		pendingIntent = PendingIntent.getActivity(context, id, proximityIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		// Removing the specific proximity alert
		locationManager.removeProximityAlert(pendingIntent);
		marker.remove();
	}

	@Override
	public void onMapClick(final LatLng position) {
		final LatLng pos = position;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("New Place It");
		alert.setMessage("Please enter a Place It Title and Description:");

		// Set an EditText view to get user input
		final EditText placeItTitle = new EditText(this);
		final EditText placeItDesc = new EditText(this);
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
		LinearLayout ll = new LinearLayout(this);
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
						} //
					}
					title = temp;
				}
				// Make sure that the user entered in a title or description for
				// the Place It
				if (!title.isEmpty()) {
					Toast.makeText(MainActivity.this, "Place It added!",
							Toast.LENGTH_SHORT).show();
					Marker added = mMap.addMarker(new MarkerOptions()
							.position(pos)
							.title(title)
							.snippet(desc)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.place_it_marker)));
					mMarkers.add(added);

					// markerHashMap<locationCount-1, added>

					locationCount++; // increment the location count

					context = getBaseContext();
					customAddProximityAlert(context, position,
							(locationCount - 1), title, desc);

					/**
					 * Opening the editor object to write data to
					 * sharedPreferences
					 */
					SharedPreferences.Editor editor = sharedPreferences.edit();

					// Storing the latitude for the i-th location
					editor.putString(
							"lat" + Integer.toString((locationCount - 1)),
							Double.toString(pos.latitude));

					// Storing the longitude for the i-th location
					editor.putString(
							"lng" + Integer.toString((locationCount - 1)),
							Double.toString(pos.longitude));

					// Storing the title of the Place It
					editor.putString(
							"title" + Integer.toString((locationCount - 1)),
							added.getTitle());

					// Storing the description of the Place It
					editor.putString(
							"description"
									+ Integer.toString((locationCount - 1)),
							added.getSnippet());

					// Storing the boolean for whether the Place It has been
					// removed
					editor.putBoolean(
							"isDiscarded"
									+ Integer.toString((locationCount - 1)),
							false);

					// Storing the boolean for whether the Place It has been
					// completed
					editor.putBoolean(
							"isPulledDown"
									+ Integer.toString((locationCount - 1)),
							false);

					// Storing the boolean for whether the Place It is active
					editor.putBoolean(
							"isActive" + Integer.toString((locationCount - 1)),
							true);

					// Storing the id of the marker
					editor.putInt("id" + Integer.toString((locationCount - 1)),
							0);

					// Storing whether this is due to a notification
					editor.putBoolean("notification", false);

					// Storing the count of locations or marker count
					editor.putInt("locationCount", locationCount);

					// Saving the values stored in the Shared Preferences
					editor.commit();

					postdata(title, desc, Double.toString(pos.latitude),
							Double.toString(pos.longitude),
							Boolean.toString(false), Boolean.toString(false),
							Boolean.toString(true));
				} else {
					// Display a Toast telling the user to enter in a Place It
					Toast.makeText(MainActivity.this,
							"Please enter a title for your Place It",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(MainActivity.this, "Nothing added!",
								Toast.LENGTH_SHORT).show();
					}
				});
		alert.show();
	}

	@Override
	public void onInfoWindowClick(final Marker marker) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(marker.getTitle());
		alert.setMessage(marker.getSnippet());
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Place  it is retained
			}
		});
		alert.setNeutralButton("Pull Down",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Remove the Place It and mark it as completed
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						String id = marker.getId().substring(1);
						editor.putBoolean("isPulledDown" + id, true);
						editor.putBoolean("isActive" + id, false);
						editor.commit();
						context = getBaseContext();
						customRemoveProximityAlert(context,
								Integer.parseInt(id),
								mMarkers.get(Integer.parseInt(id)));
						marker.remove();
					}
				});
		alert.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Remove the Place It and mark it as removed
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						String id = marker.getId().substring(1);
						editor.putBoolean("isDiscarded" + id, true);
						editor.putBoolean("isActive" + id, false);
						editor.commit();
						context = getBaseContext();
						customRemoveProximityAlert(context,
								Integer.parseInt(id),
								mMarkers.get(Integer.parseInt(id)));
						marker.remove();
					}
				});
		alert.show();

	}

	@Override
	public void onBackPressed() {
		// Do nothing
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		/** Storing the zoom level to the shared preferences */
		editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));
		editor.commit();
	}

	private void postdata(final String title, final String desc,
			final String lat, final String lng, final String isDiscarded,
			final String isPulledDown, final String isActive) {
		final ProgressDialog dialog = ProgressDialog.show(this,
				"Posting Data...", "Please wait...", false);
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(MainActivity.PLACE_IT_URI);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							10);
					nameValuePairs.add(new BasicNameValuePair("username",
							username));
					nameValuePairs.add(new BasicNameValuePair("name", title));
					nameValuePairs.add(new BasicNameValuePair("description",
							desc));
					nameValuePairs.add(new BasicNameValuePair("lat", lat));
					nameValuePairs.add(new BasicNameValuePair("lng", lng));
					nameValuePairs.add(new BasicNameValuePair("isActive",
							isActive));
					nameValuePairs.add(new BasicNameValuePair("isDiscarded",
							isDiscarded));
					nameValuePairs.add(new BasicNameValuePair("isPulledDown",
							isPulledDown));
					nameValuePairs.add(new BasicNameValuePair("action", "put"));

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						// Log.d(TAG, line);
					}

				} catch (IOException e) {
					// Log.d(TAG, "IOException while trying to conect to GAE");
				}
				dialog.dismiss();
			}
		};

		t.start();
		dialog.show();
	}

	private class UpdateSpinnerTask extends
			AsyncTask<String, Void, List<String>> {
		@Override
		protected List<String> doInBackground(String... url) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			List<String> list = new ArrayList<String>();
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);

				JSONObject myjson;

				try {
					myjson = new JSONObject(data);
					JSONArray array = myjson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if(obj.get("username").toString().equals(username)){

							list.add(obj.get("name").toString());
							list.add(obj.get("description").toString());
							list.add(obj.get("lat").toString());
							list.add(obj.get("lng").toString());
							list.add(obj.get("isDiscarded").toString());
							list.add(obj.get("isPulledDown").toString());
							list.add(obj.get("isActive").toString());
							list.add(obj.get("username").toString());
						}
					}

				} catch (JSONException e) {

					// Log.d(TAG, "Error in parsing JSON");
				}

			} catch (ClientProtocolException e) {

				// Log.d(TAG,
				// "ClientProtocolException while trying to connect to GAE");
			} catch (IOException e) {

				// Log.d(TAG, "IOException while trying to connect to GAE");
			}
			return list;
			
		}

		protected void onPostExecute(List<String> list) {

			int count = 0;
			locationCount = list.size() / 8;
			if (locationCount != 0) {
				for (int i = 0; i < locationCount; i++) {
					String placeItTitle = list.get(count);
					String placeItDesc = list.get(1+count);
					String lat = list.get(2+count);
					String lng = list.get(3+count);
					String isActive = list.get(6+count);
					drawMarker(
							new LatLng(Double.parseDouble(lat),
									Double.parseDouble(lng)), placeItTitle,
							placeItDesc, Boolean.parseBoolean(isActive));
					count += 8;
				}

			}
		}

	}
}
