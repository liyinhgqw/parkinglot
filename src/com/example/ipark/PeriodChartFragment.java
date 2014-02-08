package com.example.ipark;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import com.google.gson.Gson;
import com.wandoujia.mms.model.dao.ParkingLot;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract.Colors;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class PeriodChartFragment extends Fragment implements OnClickListener {

	private static Random RAND = new Random();
	private static final String TIME = "H:mm";
	private static final String[] ITEMS = { "A", "B", "C", "D", "E", "F" };
	private final static int[] COLORS = { randomColor(), randomColor(), randomColor(), randomColor(), randomColor(), randomColor() };
	private static final int[] THRESHOLD_VALUES = { 30, 60, 80 };
	private static final int[] THRESHOLD_COLORS = { Color.RED, Color.YELLOW, Color.GREEN };
	private static final String[] THRESHOLD_LABELS = { "Bad", "Good", "Excellent" };

	private static final int TEN_SEC = 10000;
	private static final int TWO_SEC = 2000;
	private static final float RATIO = 0.618033988749895f;

	private GraphicalView mChartView;
	private XYSeriesRenderer[] mThresholdRenderers;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset mDataset;
	private HashMap<String, TimeSeries> mSeries;
	private TimeSeries[] mThresholds;
	private ArrayList<String> mItems;
	private double mYAxisMin = Double.MAX_VALUE;
	private double mYAxisMax = Double.MIN_VALUE;
	private double mZoomLevel = 1;
	private double mLastItemChange;
	private int mItemIndex;
	private int mYAxisPadding = 5;
	
	private ParkingLot lot = null;
	private Integer[] idleCount = new Integer[100];
	
	private void mockData() {
		for (int i=0; i < 100 ; i++) {
			idleCount[i] = i;
		}
	}

	private final CountDownTimer mTimer = new CountDownTimer(15 * 60 * 1000, 2000) {
		@Override
		public void onTick(final long millisUntilFinished) {
			addValue();
		}

		@Override
		public void onFinish() {}
	};

	private final ZoomListener mZoomListener = new ZoomListener() {
		@Override
		public void zoomReset() {
			mZoomLevel = 1;
			scrollGraph(new Date().getTime());
		}

		@Override
		public void zoomApplied(final ZoomEvent event) {
			if (event.isZoomIn()) {
				mZoomLevel /= 2;
			}
			else {
				mZoomLevel *= 2;
			}
			scrollGraph(new Date().getTime());
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mockData();
		
		Bundle bundle = getArguments();
		String gsonStr = bundle.getString("data");
		Gson gson = new Gson();
		this.lot = gson.fromJson(gsonStr, ParkingLot.class);
		
		mItems = new ArrayList<String>();
		mSeries = new HashMap<String, TimeSeries>();
		mDataset = new XYMultipleSeriesDataset();
		mRenderer = new XYMultipleSeriesRenderer();

		mRenderer.setLabelsColor(Color.LTGRAY);
		mRenderer.setAxesColor(Color.LTGRAY);
		mRenderer.setGridColor(Color.rgb(136, 136, 136));
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setApplyBackgroundColor(true);

		mRenderer.setLegendTextSize(20);
		mRenderer.setLabelsTextSize(20);
		mRenderer.setPointSize(8);
		mRenderer.setMargins(new int[] { 60, 60, 60, 60 });

		mRenderer.setFitLegend(true);
		mRenderer.setShowGrid(true);
		mRenderer.setZoomEnabled(true);
		mRenderer.setExternalZoomEnabled(true);
		mRenderer.setAntialiasing(true);
		mRenderer.setInScroll(true);

		mLastItemChange = new Date().getTime();
		mItemIndex = Math.abs(RAND.nextInt(ITEMS.length));
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
			mYAxisPadding = 9;
			mRenderer.setYLabels(15);
		}

		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_chart, container, false);
		mChartView = ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, TIME);
		mChartView.addZoomListener(mZoomListener, true, false);
		view.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return view;
	}


	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		
//		mThresholds = new TimeSeries[3];
//		mThresholdRenderers = new XYSeriesRenderer[3];
//
//		for (int i = 0; i < THRESHOLD_COLORS.length; i++) {
//			mThresholdRenderers[i] = new XYSeriesRenderer();
//			mThresholdRenderers[i].setColor(THRESHOLD_COLORS[i]);
//			mThresholdRenderers[i].setLineWidth(3);
//
//			mThresholds[i] = new TimeSeries(THRESHOLD_LABELS[i]);
//			final long now = new Date().getTime();
//			mThresholds[i].add(new Date(now - 1000 * 60 * 10), THRESHOLD_VALUES[i]);
//			mThresholds[i].add(new Date(now + 1000 * 60 * 10), THRESHOLD_VALUES[i]);
//
//			mDataset.addSeries(mThresholds[i]);
//			mRenderer.addSeriesRenderer(mThresholdRenderers[i]);
//		}
//
//		mTimer.start();
		
		String title = "平均每日空闲车位数：from 0:00 to 24:00";
		
//		Date[] xvs = new Date[3];
//		xvs[0] = new Date(); xvs[0].setHours(1); xvs[0].setMinutes(0);
//		xvs[1] = new Date(); xvs[1].setHours(13); xvs[1].setMinutes(30);
//		xvs[2] = new Date(); xvs[2].setHours(2); xvs[2].setMinutes(0);
//		double[] yvs = new double[] {1.0, 2.0, 3.0};
		
		int size = 48;
		Date[] xvs = new Date[size];
		double[] yvs = new double[size];
		Random rand = new Random();
		for (int i=0; i<size; i++) {
			xvs[i] = new Date();
			xvs[i].setHours(i/2);
			if (i%2 == 0)
				xvs[i].setMinutes(0);
			else 
				xvs[i].setMinutes(30);
			
//			yvs[i] = rand.nextDouble();
			yvs[i] = lot.getOneDayRecorde()[i];
		}
		
		addXYSeries(title, xvs, yvs, 0);
		
	}

	public void addXYSeries(String title, Date[] xValues,  
		      double[] yValues, int scale) { 
	    int length = xValues.length;  
	    Log.i("**", "" + length);
//	    for (int i = 0; i < length; i++) {  
//	      XYSeries series = new XYSeries(titles[i], scale); //这里注意与TimeSeries区别.  
//	      int seriesLength = xValues.length;  
//	      for (int k = 0; k < seriesLength; k++) {  
//	        series.add(xValues[k], yValues[k]);  
//	      }  
//	      mDataset.addSeries(series);  
//	      mRenderer.addSeriesRenderer(getSeriesRenderer(COLORS[2]));
//	    }  
	    
	    TimeSeries series = new TimeSeries(title);
	    for (int i = 0; i < length; i++) {  
		      series.add(xValues[i], yValues[i]);  
		}  
	    mDataset.addSeries(series); 
	    mRenderer.addSeriesRenderer(getSeriesRenderer(COLORS[1]));
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
		if (null != mTimer) {
			mTimer.cancel();
		}
	}


	private double randomValue() {
		final int value = Math.abs(RAND.nextInt(32));
		final double percent = (value * 100) / 31.0;
		return ((int) (percent * 10)) / 10.0;
	}

	private void addValue() {
		final double value = randomValue();
		if (mYAxisMin > value) mYAxisMin = value;
		if (mYAxisMax < value) mYAxisMax = value;

		final Date now = new Date();
		final long time = now.getTime();

		if (time - mLastItemChange > 10000) {
			mLastItemChange = time;
			mItemIndex = Math.abs(RAND.nextInt(ITEMS.length));
		}

		final String item = ITEMS[mItemIndex];
		final int color = COLORS[mItemIndex];
		final int lastItemIndex = mItems.lastIndexOf(item);
		mItems.add(item);

		if (lastItemIndex > -1) {
			boolean otherItemBetween = false;
			for (int i = lastItemIndex + 1; i < mItems.size(); i++) {
				if (!item.equals(mItems.get(i))) {
					otherItemBetween = true;
					break;
				}
			}
			if (otherItemBetween) {
				addSeries(null, now, value, item, color);
			}
			else {
				mSeries.get(item).add(now, value);
			}
		}
		else {
			addSeries(item, now, value, item, color);
		}

		scrollGraph(time);
		mChartView.repaint();
	}

	private void addSeries(final String title, final Date time, final double value, final String item, final int color) {
		for (int i = 0; i < THRESHOLD_COLORS.length; i++) {
			mThresholds[i].add(new Date(time.getTime() + 1000 * 60 * 5), THRESHOLD_VALUES[i]);
		}

		final TimeSeries series = new TimeSeries(title);
		series.add(time, value);
		mSeries.put(item, series);
		mDataset.addSeries(series);
		mRenderer.addSeriesRenderer(getSeriesRenderer(color));
	}

	private void scrollGraph(final long time) {
		final double[] limits = new double[] { time - TEN_SEC * mZoomLevel, time + TWO_SEC * mZoomLevel, mYAxisMin - mYAxisPadding,
				mYAxisMax + mYAxisPadding };
		mRenderer.setRange(limits);
	}

	private XYSeriesRenderer getSeriesRenderer(final int color) {
		final XYSeriesRenderer r = new XYSeriesRenderer();
		r.setDisplayChartValues(false);
		r.setChartValuesTextSize(30);
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(color);
		r.setFillPoints(true);
		r.setLineWidth(4);
		return r;
	}

	private static int randomColor() {
		final float hue = (RAND.nextInt(360) + RATIO);
		return Color.HSVToColor(new float[] { hue, 0.8f, 0.9f });
	}
	
	private static int getColor(double value) {
		int level = (int) (value * 6);
		if (level < 0) 
			level = 0;
		if (level > 5)
			level = 5;
		return COLORS[level];
	}

	@Override
	public void onClick(final View v) {
	}
	
}
