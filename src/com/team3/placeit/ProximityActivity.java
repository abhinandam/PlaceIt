package com.team3.placeit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class ProximityActivity extends Activity {
	
	private SharedPreferences sharedPreferences;
		
	String notificationTitle; // Store notification title
	String notificationContent; // Store notification content
	String tickerMessage; // Store the ticker message
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check if user has entered into proximity of a Place It
		boolean proximity_entering = getIntent().getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
		// Get information about Place It
		String title = getIntent().getStringExtra("title");
		String description = getIntent().getStringExtra("description");
		int id = getIntent().getIntExtra("id", 0);
		
        sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
				
		// If the user is in the proximity of a Place It
		if (proximity_entering) {
			notificationTitle = title;
			notificationContent = description;
			tickerMessage = title;
			SharedPreferences.Editor editor = sharedPreferences
					.edit();
			// Mark Place It as pulled down and not active
			editor.putBoolean("isPulledDown" + id, true);
			editor.putBoolean("isActive" + id, false);
			editor.commit();
			if (MainActivity.mMarkers.size() > 0)
				MainActivity.customRemoveProximityAlert(MainActivity.context, id, MainActivity.mMarkers.get(id));
		}		
		
		// Create the intent for the Notification
		Intent notificationIntent = new Intent(getApplicationContext(), NotificationView.class);
		
		// Add information about the notification
		notificationIntent.putExtra("title", title);
		notificationIntent.putExtra("description", description);
		notificationIntent.putExtra("id", id);
		
		// This is needed to make this intent different from its previous intents
		notificationIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
		
		// Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        
		// Get the Notification Manager
        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		
        // Create the notification with all of the properties
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
							.setWhen(System.currentTimeMillis())
							.setContentText(notificationContent)
							.setContentTitle(notificationTitle)
							.setSmallIcon(R.drawable.place_it_icon)
							.setAutoCancel(true)
							.setTicker(title)
							.setContentIntent(pendingIntent)
							.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		
		// Creating a notification from the notification builder
		Notification notification = notificationBuilder.build();
		
		// Send notification to system tray
		// The first argument ensures that each notification has a unique id 
		// If two notifications share same notification id, then the last notification replaces the first notification 
		nManager.notify((int)System.currentTimeMillis(), notification);
		
		// Finishes the execution of this activity
		finish();
		
		
	}
}
