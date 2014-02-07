package com.example.ipark;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.ipark.DemoApplication;
import com.google.gson.Gson;
import com.wandoujia.mms.model.dao.ParkingLot;
import com.wandoujia.mms.model.dao.ParkingLot.Location;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

import android.util.Log;
import android.widget.Toast;

public class MapActivity extends Activity {

	public MapView mMapView;
	public MapController mMapController;
	public MyOverlay mOverlay;
	public PopupOverlay   pop  = null;
	public ArrayList<OverlayItem>  mItems = null; 
	public View viewCache = null;
	public TextView popupName = null;
	public TextView popupAddress = null;
	public TextView popupPrice = null;
	public TextView popupMaxNum = null;
	public TextView popupIdleNum = null;
	private TextView popupIndex = null;
	public Button button = null;
	public MapView.LayoutParams layoutParam = null;
	public OverlayItem mCurItem = null;
	public Button detailButton = null;

	public String place;
	
	public double dist;

	private int hour;
	
	private int mins;
	
	public MKSearch mSearch;
	
	private ParkingLot currentLot;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.initEngineManager(getApplicationContext());
        }
		setContentView(R.layout.map_activity);
		
		detailButton = (Button) findViewById(R.id.detailButton);
//		detailButton.setEnabled(false);
		
		// 初始化搜索模块，注册搜索事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("error：%d", error);
					Toast.makeText(MapActivity.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				//地图移动到该点
				if (res.type == MKAddrInfo.MK_GEOCODE){
					//地理编码：通过地址检索坐标点
					String position = String.format("%f,%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
					Log.i("##", position);
					Toast.makeText(MapActivity.this, position, Toast.LENGTH_LONG).show();
					
					new AsysncTaskSearch(MapActivity.this, position, dist, hour, mins).execute();
				}
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
			}
		});


		
        mMapView = (MapView)findViewById(R.id.bmapView);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(16);
        /**
         * 显示内置缩放控件
         */
        mMapView.setBuiltInZoomControls(true);
        
//        initOverlay();
              
		Bundle request = getIntent().getExtras().getBundle("request");
		this.place = request.getString("place");
		this.dist = request.getDouble("dist");
		this.hour = request.getInt("hour");
		this.mins = request.getInt("mins");
		Log.i("##", place + " " + dist + " " + hour + ":" + mins);
		mSearch.geocode(place, "北京");
	}

	public void ShowParkingLots() {
		// 设定地图中心点
		Location center = GetCenterLocation();
        GeoPoint p = new GeoPoint((int)(center.lat * 1E6), (int)(center.lng* 1E6));
		mMapController.setCenter(p);
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_gcoding), mMapView);
		for (int i = parking_lots.size() - 1; i >=0; --i) {  // from the last, so the 1st on the top
			/**
			 * 准备overlay 数据
			 */
			Location loc = parking_lots.get(i).getLocation();
			GeoPoint p1 = new GeoPoint((int) (loc.lat * 1E6), (int) (loc.lng * 1E6));
			OverlayItem item1 = new OverlayItem(p1, parking_lots.get(i).getName(), "");
			/**
			 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
			 */
			item1.setMarker(getResources().getDrawable(GetDrawableIconId(i)));
			/**
			 * 将item 添加到overlay中 注意： 同一个itme只能add一次
			 */
			mOverlay.addItem(item1);
		}
         /**
          * 保存所有item，以便overlay在reset后重新添加
          */
         mItems = new ArrayList<OverlayItem>();
         mItems.addAll(mOverlay.getAllItem());

         /**
          * 将overlay 添加至MapView中
          */
         mMapView.getOverlays().add(mOverlay);
         /**
          * 刷新地图
          */
         mMapView.refresh();
         // 向地图添加自定义View.
         viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
         popupName = (TextView) viewCache.findViewById(R.id.view_park_lot_name);
         popupAddress = (TextView) viewCache.findViewById(R.id.view_park_lot_address);
         popupPrice = (TextView) viewCache.findViewById(R.id.view_park_lot_price);
         popupMaxNum =(TextView) viewCache.findViewById(R.id.view_park_lot_max_num);
         popupIdleNum =(TextView) viewCache.findViewById(R.id.view_park_lot_idle_num);
         popupIndex =(TextView) viewCache.findViewById(R.id.view_park_lot_index);
         
         button = new Button(this);
         button.setBackgroundResource(R.drawable.popup);
         
          // 创建一个popup overlay
         PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
			}
         };
         pop = new PopupOverlay(mMapView,popListener);
         
	}


	public void initOverlay() {
		GetMockParkingLots();
	}

	public Location GetCenterLocation() {
		return parking_lots.get(0).getLocation();
	}
	
	public int GetDrawableIconId(int seq) {
		int[] icons = {
				R.drawable.icon_marka, 
				R.drawable.icon_markb, 
				R.drawable.icon_markc,
				R.drawable.icon_markd,
				R.drawable.icon_marke,
				R.drawable.icon_markf,
				R.drawable.icon_markg,
				R.drawable.icon_markh,
				R.drawable.icon_marki,
				R.drawable.icon_markj,
				};
		if (seq < icons.length) {
			return icons[seq];
		}
		else {
			return R.drawable.icon_gcoding;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	
	void GetMockParkingLots() {
		parking_lots = new ArrayList<ParkingLot>();
		ParkingLot parking_lot = new ParkingLot();
		parking_lot.setUid("629a8d76e40136e00c0a703f");
		parking_lot.setName("人民日报社住宅区停车场");
		parking_lot.setAddress("人民日报社住宅楼附近");
		Location loc = new Location();
		loc.lng = 116.482;
		loc.lat = 39.9257;
		parking_lot.setLocation(loc);
		parking_lot.setPrice(5.0);
		parking_lot.setMaxNum(262);
		parking_lot.setIdleNum(50);
		parking_lots.add(parking_lot);
	}

	public List<ParkingLot> parking_lots;
	
    public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}
		

		@Override
		public boolean onTap(int index){
			ParkingLot lot = parking_lots.get(parking_lots.size() - index - 1);
			if (lot == null) {
				Log.i("INDEX out of range", index + " of " + mItems.size());
				return false;
			}
			mCurItem = getItem(index);
			popupName.setText(lot.getName());
			popupAddress.setText(lot.getAddress());
			popupPrice.setText(lot.getPrice().toString());
			popupMaxNum.setText(lot.getMaxNum()+"");
			popupIdleNum.setText(lot.getIdleNum()+"");
//			popupIndex.setText(lot.getIndex() + "");
			Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(viewCache)};
			pop.showPopup(bitMaps, mCurItem.getPoint(), 32);
			
			detailButton.setEnabled(true);
			MapActivity.this.currentLot = lot;
			return true;
		}
		
		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){
			if (pop != null){
                pop.hidePop();
                mMapView.removeView(button);
			}
			return false;
		}
    	
    }

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	public void onDisplayClick(View sender) {
		ParkingLot lot = this.currentLot;
		Gson gson = new Gson();
		String gsonStr = gson.toJson(lot);
		Log.i("**", gsonStr);
		startActivity(new Intent(this, FigureActivity.class).putExtra("data", gsonStr));
	}
}
