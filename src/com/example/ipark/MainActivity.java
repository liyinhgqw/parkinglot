package com.example.ipark;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "ParkWhizActivity";

	private AutoCompleteTextView mSearchEditText;

	private SeekBar mDistanceBar;

	private TextView mDistText;

	private MKSearch mSearch = null;

	private ArrayAdapter<String> sugAdapter = null;

	private TimePicker timePicker = null;
			
	
	private OnSeekBarChangeListener distBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			mDistText.setText(String.valueOf(seekBar.getProgress() / 10.0
					+ " kms"));
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

	private BMapManager mBMapMan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBMapMan=new BMapManager(getApplication());  
		mBMapMan.init("NecqNMa7GH2aW1UhDGyIVbuG", null); 

		setContentView(R.layout.activity_main);

		setupView();

		// 初始化搜索模块，注册搜索事件监听
		mSearch = new MKSearch();
		mSearch.init(mBMapMan, new MKSearchListener() {
			// 在此处理详情页结果
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				if (error != 0) {
					Toast.makeText(MainActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "成功，查看详情页面",
							Toast.LENGTH_SHORT).show();
				}
			}

			/**
			 * 在此处理poi搜索结果
			 */
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}
				// 将地图移动到第一个POI中心点
//				if (res.getCurrentNumPois() > 0) {
//					// 将poi结果显示到地图上
//					MyPoiOverlay poiOverlay = new MyPoiOverlay(
//							PoiSearchDemo.this, mMapView, mSearch);
//					poiOverlay.setData(res.getAllPoi());
//					mMapView.getOverlays().clear();
//					mMapView.getOverlays().add(poiOverlay);
//					mMapView.refresh();
//					// 当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
//					for (MKPoiInfo info : res.getAllPoi()) {
//						if (info.pt != null) {
//							mMapView.getController().animateTo(info.pt);
//							break;
//						}
//					}
//				} else if (res.getCityListNum() > 0) {
//					// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//					String strInfo = "在";
//					for (int i = 0; i < res.getCityListNum(); i++) {
//						strInfo += res.getCityListInfo(i).city;
//						strInfo += ",";
//					}
//					strInfo += "找到结果";
//					Toast.makeText(PoiSearchDemo.this, strInfo,
//							Toast.LENGTH_LONG).show();
//				}
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			/**
			 * 更新建议列表
			 */
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if (res == null || res.getAllSuggestions() == null) {
					return;
				}
				
				sugAdapter = new ArrayAdapter<String>(MainActivity.this,
						android.R.layout.simple_dropdown_item_1line);
				
				for (MKSuggestionInfo info : res.getAllSuggestions()) {
					if (info.key != null) {
						sugAdapter.add(info.key);
						Log.i("---", info.key);
					}
				}
				Log.i("===", ""+sugAdapter.getCount());
				
				mSearchEditText.setAdapter(sugAdapter);
				sugAdapter.notifyDataSetChanged();

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}
		});

		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		mSearchEditText.setAdapter(sugAdapter);

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		mSearchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				Log.i("**", cs.toString() + " " + arg1 + " " + arg2 + " " + arg3);
				if (cs.length() < 0) {
					return;
				}
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSearch.suggestionSearch(cs.toString(), "北京");
				Log.i("**", mSearchEditText.getText().toString());
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setupView() {
		mSearchEditText = (AutoCompleteTextView) findViewById(R.id.searchText);
		mDistText = (TextView) findViewById(R.id.kms);
		mDistanceBar = (SeekBar) findViewById(R.id.distanceBar);
		mDistanceBar.setOnSeekBarChangeListener(distBarListener);
		mDistanceBar.setMax(50); // dist unit is 0.1km
		mDistanceBar.setProgress(10);

		timePicker = (TimePicker) findViewById(R.id.timePicker);
	}

	public void onSearchParkingClick(View sender) {
		String where = mSearchEditText.getText().toString();
		double dist = mDistanceBar.getProgress() / 10.0;
		int hour = timePicker.getCurrentHour();
		int mins = timePicker.getCurrentMinute();
		Log.i("$$", hour + ":" + mins);
		
		if (where == null || where.length() <= 0) {
			where = "nearby";
		}
		
		ParkRequest req = new ParkRequest(where, dist);
		Bundle request = new Bundle();
		request.putString("place", where);
		request.putDouble("dist", dist);
		request.putInt("hour", hour);
		request.putInt("mins", mins);
		startActivity(new Intent(this, MapActivity.class).putExtra("request", request));
		
//		Log.i(TAG, "Begin search for location with name " + where);
//		new AsysncTaskSearch(this, where).execute();

	}

}
