package com.example.ipark;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "ParkWhizActivity"; 
	
	private EditText mSearchEditText;

	private TextView hello;
	
	private SeekBar mDistanceBar;
	
	private TextView mDistText;
	
	private OnSeekBarChangeListener distBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			mDistText.setText(String.valueOf(seekBar.getProgress() / 10.0 + " kms"));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
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
    	mDistText = (TextView) findViewById(R.id.kms);
    	mDistanceBar = (SeekBar) findViewById(R.id.distanceBar);
    	mDistanceBar.setOnSeekBarChangeListener(distBarListener);
    	mDistanceBar.setMax(50);	// dist unit is 0.1km
    	mDistanceBar.setProgress(10);

    	hello = (TextView)findViewById(R.id.hello);
    }

    public void onSearchParkingClick (View sender) {
    	String where = mSearchEditText.getText().toString();
    	double dist = mDistanceBar.getProgress() / 10.0;
    	
    	
    	hello.setText("hello");
    	if (where == null || where.length() <= 0) {
    		where = "@here";
    	}
		Log.i(TAG, "Begin search for location with name " + where);
		new AsysncTaskSearch(this, where).execute();
    	
    }
    

    
}
