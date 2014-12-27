 package com.sneha.googleauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthUtil;

import android.os.AsyncTask;
import android.util.Log;

public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void>{
	
		private static final String TAG = "TokenInfoTask";
		private static final String NAME_KEY = "given_name";
		protected GreetActivity mActivity;

		protected String mScope;
		protected String mEmail;

		AbstractGetNameTask(GreetActivity activity, String email, String scope) {
			this.mActivity = activity;
			this.mScope = scope;
			this.mEmail = email;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
		        fetchNameFromProfileServer();
		      } catch (IOException ex) {
		        onError("Following Error occured, please try again. " + ex.getMessage(), ex);
		      } catch (JSONException e) {
		        onError("Bad response: " + e.getMessage(), e);
		      }
		      
			return null;
		}
		
		protected void onError(String msg, Exception e) {
	        if (e != null) {
	          Log.e(TAG, "Exception: ", e);
	        }
	        mActivity.show(msg);  // will be run in UI thread
	    }

		 protected abstract String fetchToken() throws IOException;
		
		 private void fetchNameFromProfileServer() throws IOException, JSONException {
		        String token = fetchToken();
		        if (token == null) {
		          // error has already been handled in fetchToken()
		          return;
		        }
		        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
		        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		        int sc = con.getResponseCode();
		        if (sc == 200) {
		          InputStream is = con.getInputStream();
		          String name = getFirstName(readResponse(is));
		          mActivity.show("Hello " + name + "!");
		          is.close();
		          return;
		        } else if (sc == 401) {
		            try{
		                GoogleAuthUtil.clearToken(mActivity, token);
		            } catch (Exception e) {
		                onError("Server auth error, please try again.", e);
		            }
		            onError("Server auth error, please try again.", null);
		            Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
		            return;
		        } else {
		          onError("Server returned the following error code: " + sc, null);
		          return;
		        }
		    }

		 private static String readResponse(InputStream is) throws IOException {
		        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        byte[] data = new byte[2048];
		        int len = 0;
		        while ((len = is.read(data, 0, data.length)) >= 0) {
		            bos.write(data, 0, len);
		        }
		        return new String(bos.toByteArray(), "UTF-8");
		    }

		 private String getFirstName(String jsonResponse) throws JSONException {
		      JSONObject profile = new JSONObject(jsonResponse);
		      return profile.getString(NAME_KEY);
		    }
	}

	



