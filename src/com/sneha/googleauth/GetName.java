package com.sneha.googleauth;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;

public  class GetName extends AbstractGetNameTask {

	  public GetName(GreetActivity activity, String email, String scope) {
	      super(activity, email, scope);
	      
	  }

	  /**
	   * Get a authentication token if one is not available. If the error was recoverable then a
	   * notification will automatically be pushed. The callback provided will be fired once the
	   * notification is addressed by the user successfully. If the error is not recoverable then
	   * it displays the error message on parent activity.
	   */
	  @Override
	  protected String fetchToken() throws IOException {
	      try {
	          return GoogleAuthUtil.getTokenWithNotification(
	                  mActivity, mEmail, mScope, null, makeCallback(mEmail));
	      } catch (UserRecoverableNotifiedException userRecoverableException) {
	          // Unable to authenticate, but the user can fix this.
	          // Because we've used getTokenWithNotification(), a Notification is
	          // created automatically so the user can recover from the error
	    	  mActivity.handleException(userRecoverableException);
	          //onError("Could not fetch token.", null);
	      } catch (GoogleAuthException fatalException) {
	          onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
	      }
	      return null;
	  }

	  private Intent makeCallback(String accountName) {
	      Intent intent = new Intent();
	      intent.setAction("com.google.android.gms.auth.sample.helloauth.Callback");
	      intent.putExtra(GreetActivity.EXTRA_ACCOUNTNAME, accountName);
	      
	      return intent;
	  }

	  /**
	   * Note: Make sure that the receiver can be called from outside the app. You can do that by adding
	   * android:exported="true" in the manifest file.
	   */
	  public static class CallbackReceiver extends BroadcastReceiver {
	    public static final String TAG = "CallbackReceiver";

	    @Override
	    public void onReceive(Context context, Intent callback) {
	        Bundle extras = callback.getExtras();
	        Intent intent = new Intent(context, GreetActivity.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.putExtras(extras);
	        Log.i(TAG, "Received broadcast. Resurrecting activity");
	        context.startActivity(intent);
	    }
	  }
}
	  