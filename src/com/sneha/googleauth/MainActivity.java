package com.sneha.googleauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	// Project ID: local-now-787 Project Number: 226973172619+
	// private key = notasecret
	/*
	 * CLIENT ID
	 * 226973172619-d3ai8k9m8ibf07aak85nf5qbr0t8jrnf.apps.googleusercontent.com
	 * EMAIL ADDRESS
	 * 226973172619-d3ai8k9m8ibf07aak85nf5qbr0t8jrnf@developer.gserviceaccount
	 * .com PUBLIC KEY FINGERPRINTS a5aac79a4b07c4c5a930956590f68b86433bb930
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button;
			button = (Button) findViewById(R.id.button1);

			
			
			button.setOnClickListener(new View.OnClickListener() {
      
				
				
				@Override
				public void onClick(View v) {

					Intent intent = new Intent(MainActivity.this,
							GreetActivity.class);
					intent.putExtra(GreetActivity.TYPE_KEY,
							GreetActivity.Type.FOREGROUND.name());
					startActivity(intent);

				}
			 
			});
		
		
		{
			
		}
		

	}

}
