package com.example.ipark;

import com.google.gson.Gson;
import com.wandoujia.mms.model.dao.ParkingLot;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class FigureActivity extends FragmentActivity {

	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_figure);
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		String gsonStr = getIntent().getExtras().getString("data");
		bundle = new Bundle();
		bundle.putString("data", gsonStr);
		
		if (null == savedInstanceState) {
			ChartFragment chartFragment = new ChartFragment();
			chartFragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().replace(R.id.chart, chartFragment).commit();
		}
	}
	
	public void onDayStatClick(View v) {
		ChartFragment chartFragment = new ChartFragment();
		chartFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.chart, chartFragment).commit();
	}
	
	public void onPeriodStatClick(View v) {
		PeriodChartFragment chartFragment = new PeriodChartFragment();
		chartFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.chart, chartFragment).commit();
	}
}
