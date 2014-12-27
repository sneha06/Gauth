package com.sneha.googleauth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GreetActivity extends Activity {

	private static final String TAG = "PlayHelloActivity";
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";
    
	TextView greet;
	Button button;
	
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    String mEmail;
	
    private Type requestType;

    public static String TYPE_KEY = "type_key";
	
    public static enum Type {FOREGROUND}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_greet);
		
		greet = (TextView) findViewById(R.id.textView1);
		button = (Button) findViewById(R.id.greet_me_button);
		
		 Bundle extras = getIntent().getExtras();
	        requestType = Type.valueOf(extras.getString(TYPE_KEY));
	        setTitle(getTitle() + " - " + requestType.name());
	        if (extras.containsKey(EXTRA_ACCOUNTNAME)) {
	            mEmail = extras.getString(EXTRA_ACCOUNTNAME);
	            getTask(GreetActivity.this, mEmail, SCOPE).execute();
	        }
		//pickUserAccount();
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				greetTheUser(v);
				
			}
		});
	}
	private void pickUserAccount() {
	    String[] accountTypes = new String[]{"com.google"};
	    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
	            accountTypes, false, null, null, null, null);
	    startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must pick an account", Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            handleAuthorizeResult(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	 public void greetTheUser(View view) {
	        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GreetActivity.this);
	        if (statusCode == ConnectionResult.SUCCESS) {
	            getUsername();
	        } else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
	                    statusCode, this, 0 /* request code not used */);
	            dialog.show();
	        } else {
	            Toast.makeText(this,"unrecoverable error", Toast.LENGTH_SHORT).show();
	        }
	    }
	
	 private void getUsername() {
	        if (mEmail == null) {
	            pickUserAccount();
	        } else {
	            if (isDeviceOnline()) {
	                getTask(GreetActivity.this, mEmail, SCOPE).execute();
	            //AbstractGetNameTask(GreetActivity.this , mEmail, SCOPE);
	            } else {
	                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
	            }
	        }
	    }
	
	

	
		
	
	private void handleAuthorizeResult(int resultCode, Intent data) {
        if (data == null) {
            show("Unknown error, click the button again");
            return;
        }
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "Retrying");
            getTask(GreetActivity.this, mEmail, SCOPE).execute();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            show("User rejected authorization.");
            return;
        }
        show("Unknown error, click the button again");
    }
	
	private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
	
	public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                greet.setText(message);
            }
        });
    }
	
	 public void handleException(final Exception e) {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                if (e instanceof GooglePlayServicesAvailabilityException) {
	                    // The Google Play services APK is old, disabled, or not present.
	                    // Show a dialog created by Google Play services that allows
	                    // the user to update the APK
	                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
	                            .getConnectionStatusCode();
	                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
	                            GreetActivity.this,
	                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	                    dialog.show();
	                } else if (e instanceof UserRecoverableAuthException) {
	                    // Unable to authenticate, such as when the user has not yet granted
	                    // the app access to the account, but the user can fix this.
	                    // Forward the user to an activity in Google Play services.
	                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
	                    startActivityForResult(intent,
	                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	                }
	            }
	        });
	    }


	 private AbstractGetNameTask getTask(
	            GreetActivity activity, String email, String scope) {
		 switch(requestType){
		 case FOREGROUND:
			 return new GetName(activity, email, scope);
			 
		 default:
             return new GetName(activity, email, scope);
		 }

	    }
	 

}
