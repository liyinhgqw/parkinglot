package com.example.ipark;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

public class AsysncTaskSearch extends AsyncTask<Void, Void, String> {

	Activity c;
	
	private String place;
	
	private double dist;
	
	private SearchService searchService = new SearchService();
	
	public AsysncTaskSearch(Activity c, String place, double dist) {
		this.c = c;
		this.place = place;
		this.dist = dist * 1000.0;
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		String result = searchService.searchParking(place, dist);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		TextView posText = (TextView) c.findViewById(R.id.position);
		
		Gson gson = new Gson();
		ParkingLotResult pResult= gson.fromJson(result, ParkingLotResult.class);
		posText.setText(""+pResult);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

}
