package com.example.ipark;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

public class AsysncTaskSearch extends AsyncTask<Void, Void, String> {

	Activity c;
	
	private String place;
	
	private SearchService searchService = new SearchService();
	
	public AsysncTaskSearch(Activity c, String place) {
		this.c = c;
		this.place = place;
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		String result = searchService.searchParking(place);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		TextView helloText = (TextView) c.findViewById(R.id.hello);
		helloText.setText("hi"+result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

}
