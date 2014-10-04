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

import com.google.android.gms.maps.model.LatLng;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {


	public static final String USERS_URI = "http://cse110syncsample.appspot.com/product";

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUserID;
	private String mPassword;
	public boolean exists = false;
	public boolean correctPassword = false; 
	public boolean newUser = true;

	// UI references.
	private EditText mUserIDView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUserID = getIntent().getStringExtra(EXTRA_EMAIL);
		mUserIDView = (EditText) findViewById(R.id.email);
		mUserIDView.setText(mUserID);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptRegister(){
		if (mAuthTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		mUserIDView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserID = mUserIDView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserID)) {
			mUserIDView.setError(getString(R.string.error_field_required));
			focusView = mUserIDView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			new UpdateSpinnerTask().execute(USERS_URI);
			//mAuthTask = new UserLoginTask();
			//mAuthTask.execute((Void) null);
		}

	}
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserIDView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserID = mUserIDView.getText().toString();
		mPassword = mPasswordView.getText().toString();


		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserID)) {
			mUserIDView.setError(getString(R.string.error_field_required));
			focusView = mUserIDView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			new UserLoginTask().execute(USERS_URI);

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, List<String>> {
		protected List<String> doInBackground(String... url) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);

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
						if(obj.get("username").toString().equals(mUserID)){
							newUser = false;
							if(obj.get("password").toString().equals(mPassword)){
								correctPassword = true;
							}
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
			return null;
		}

		protected void onPostExecute(List<String> list) {
			if(!correctPassword && !newUser){
				AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
				alert.setMessage("Incorrect Password!");
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
						startActivity(getIntent());
					}
				});
				alert.show();
			}
			
			else if(newUser){
				AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
				alert.setMessage("Please register before signing in");
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
						startActivity(getIntent());
					}
				});
				alert.show();
			}
			else{
				postdata(mUserID, mPassword);
				Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
				myIntent.putExtra("user", mUserID);
				startActivity(myIntent);

			}
		}
	}


	private void postdata(final String username, final String password) {
		final ProgressDialog dialog = ProgressDialog.show(this,
				"Posting Data...", "Please wait...", false);
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(USERS_URI);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("username",
							username));
					nameValuePairs.add(new BasicNameValuePair("name", username));
					nameValuePairs.add(new BasicNameValuePair("password", password));

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

	@Override
	public void onBackPressed() {
		// Do nothing
	}


	private class UpdateSpinnerTask extends
	AsyncTask<String, Void, List<String>> {
		@Override
		protected List<String> doInBackground(String... url) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);

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
						if(obj.get("username").toString().equals(mUserID)){
							exists = true;

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
			return null;
		}

		protected void onPostExecute(List<String> list) {

			if(exists)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
				alert.setMessage("Username is already taken, please create unique user name");
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
						startActivity(getIntent());
					}
				});
				alert.show();
			}

			else{
				AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
				alert.setMessage("Your account has been succesfully created");
				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
						startActivity(getIntent());		
					}
				});
				alert.show();

				postdata(mUserID, mPassword);
			}
		}

	}
}
