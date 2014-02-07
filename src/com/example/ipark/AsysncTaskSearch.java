package com.example.ipark;

import com.google.gson.Gson;
import com.wandoujia.mms.model.dao.ParkingLotResult;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class AsysncTaskSearch extends AsyncTask<Void, Void, String> {

	Activity c;
	
	private String place;
	
	private double dist;
	
	private int hour;
	
	private int mins;
	
	private SearchService searchService = new SearchService();
	
	public AsysncTaskSearch(Activity c, String place, double dist, int hour, int mins) {
		this.c = c;
		this.place = place;
		this.dist = dist * 1000.0;
		this.hour = hour;
		this.mins = mins;
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		String result = searchService.searchParking(place, dist, hour, mins);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		TextView posText = (TextView) c.findViewById(R.id.position);
		
		Gson gson = new Gson();
		ParkingLotResult pResult= gson.fromJson(result, ParkingLotResult.class);
		// posText.setText(""+pResult);
		if (pResult.getStatus() == -1 || pResult.getResults().size() == 0) {
			Toast.makeText(c, "未找到结果", Toast.LENGTH_LONG).show();
		} else {
			MapActivity mapActiviy = (MapActivity)(c);
			mapActiviy.parking_lots = pResult.getResults();
			mapActiviy.ShowParkingLots();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

}
