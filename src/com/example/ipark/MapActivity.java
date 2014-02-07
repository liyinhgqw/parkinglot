package com.example.ipark;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Ground;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.ipark.DemoApplication;
import com.wandoujia.mms.model.dao.ParkingLot;
import com.wandoujia.mms.model.dao.ParkingLot.Location;

import android.os.Bundle;
import android.app.Activity;
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

	private MapView mMapView;
	private MapController mMapController;
	private MyOverlay mOverlay;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private TextView  popupText = null;
	private View viewCache = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private Button button = null;
	private MapView.LayoutParams layoutParam = null;
	private OverlayItem mCurItem = null;

	private String place;
	
	private double dist;

	private BMapManager mBMapMan;

	private MKSearch mSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.initEngineManager(getApplicationContext());
        }
		setContentView(R.layout.map_activity);
		
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
					
					new AsysncTaskSearch(MapActivity.this, position, dist).execute();
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
        mMapController.setZoom(14);
        /**
         * 显示内置缩放控件
         */
        mMapView.setBuiltInZoomControls(true);
        
        initOverlay();
	}

	private void initOverlay() {
		GetMockParkingLots();
		// 设定地图中心点
		Location center = GetCenterLocation();
        GeoPoint p = new GeoPoint((int)(center.lat * 1E6), (int)(center.lng* 1E6));
		mMapController.setCenter(p);
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_gcoding), mMapView);
		for (int i = 0; i < parking_lots.size(); ++i) {
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
         popupInfo = (View) viewCache.findViewById(R.id.popinfo);
         popupLeft = (View) viewCache.findViewById(R.id.popleft);
         popupRight = (View) viewCache.findViewById(R.id.popright);
         popupText =(TextView) viewCache.findViewById(R.id.textcache);
         
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
	private Location GetCenterLocation() {
		return parking_lots.get(0).getLocation();
	}
	
	private int GetDrawableIconId(int seq) {
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
		loc.lng = (float) 116.482;
		loc.lat = (float) 39.9257;
		parking_lot.setLocation(loc);
		parking_lot.setPrice(5.0f);
		parking_lot.setMaxNum(262);
		parking_lot.setIdleNum(50);
		parking_lots.add(parking_lot);
	}

	private ArrayList<ParkingLot> parking_lots;
	
    public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}
		

		@Override
		public boolean onTap(int index){
			OverlayItem item = getItem(index);
			mCurItem = item;
			popupText.setText(getItem(index).getTitle());
			Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(popupLeft),
					BMapUtil.getBitmapFromView(popupInfo),
					BMapUtil.getBitmapFromView(popupRight) };
			pop.showPopup(bitMaps, item.getPoint(), 32);
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
		Bundle request = getIntent().getExtras().getBundle("request");
		this.place = request.getString("place");
		this.dist = request.getDouble("dist");
		Log.i("##", place + " " + dist);
		mSearch.geocode(place, "北京");
	}

}
