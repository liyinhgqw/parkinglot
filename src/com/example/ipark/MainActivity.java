package com.example.ipark;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "ParkWhizActivity"; 
	
	private EditText mSearchEditText;

	private TextView hello;
	
	private View mDistanceBar;

    private Handler searchResultHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);  
			hello.setText("Search result : " + msg.getData());
		}
    };

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
        setupView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
    private void setupView() {
    	mSearchEditText = (EditText)findViewById(R.id.searchText);
    	mDistanceBar = findViewById(R.id.distanceBar);
    	hello = (TextView)findViewById(R.id.hello);
    }

    public void onSearchParkingClick (View sender) {
    	String where = mSearchEditText.getText().toString();
    	hello.setText("hello");
    	if (where == null || where.length() <= 0) {
    		where = "@here";
    	}
		Log.i(TAG, "Begin search for location with name " + where);
		new AsysncTaskSearch(this, where).execute();
    	
    }
    

    
}
