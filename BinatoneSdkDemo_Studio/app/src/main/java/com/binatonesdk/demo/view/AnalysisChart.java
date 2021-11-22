package com.binatonesdk.demo.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.binatonesdk.demo.R;
import com.binatonesdk.demo.util.DensityUtil;
import com.sleepace.sdk.binatone.domain.Analysis;
import com.sleepace.sdk.binatone.domain.HistoryData;
import com.sleepace.sdk.binatone.util.SleepState;
import com.sleepace.sdk.util.SdkLog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AnalysisChart extends View {
	private static final String TAG = AnalysisChart.class.getSimpleName();
	private int rowCount = 4;
	private Paint dividerPaint, solidLinePaint, timePaint;
	private float lineWidth;
	private int lineColor, textColor, outOfBedColor, awakeColor, asleepColor;
	private int margin;
	private int radius;
	
	private HistoryData historyData;
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	public AnalysisChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	public AnalysisChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public AnalysisChart(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init() {
		margin = DensityUtil.dip2px(getContext(), 15);
		radius = DensityUtil.dip2px(getContext(), 3);
		lineWidth = DensityUtil.dip2px(getContext(), 1);
		lineColor = Color.argb(25, 0, 0, 0);
		textColor = Color.argb(200, 0, 0, 0);
		outOfBedColor = getResources().getColor(R.color.out_of_bed);
		awakeColor = getResources().getColor(R.color.awake);
		asleepColor = getResources().getColor(R.color.asleep);
		
		dividerPaint = new Paint();
		dividerPaint.setStyle(Paint.Style.STROKE);
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(lineWidth);
		dividerPaint.setColor(lineColor);
		//绘制长度为4的实线后再绘制长度为4的空白区域，0为间隔
		dividerPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
		
		solidLinePaint = new Paint();
		solidLinePaint.setStyle(Paint.Style.FILL);
		solidLinePaint.setAntiAlias(true);
		solidLinePaint.setStrokeWidth(lineWidth);
		solidLinePaint.setColor(lineColor);
		
		timePaint = new Paint();
		timePaint.setStyle(Paint.Style.FILL);
		timePaint.setAntiAlias(true);
		timePaint.setColor(textColor);
		timePaint.setTextSize(24);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		int height = getHeight();
		int width = getWidth();
		float itemHeight = height / (float)rowCount;
		for(int i=1;i<=rowCount;i++) {
			if(i < rowCount) {
				canvas.drawLine(0, i * itemHeight, width, i * itemHeight, dividerPaint);
			}else {
				canvas.drawLine(0, i * itemHeight- lineWidth, width, i * itemHeight- lineWidth, solidLinePaint);
			}
		}
		
		String stime = "00:00";
		String etime = stime;
		
		if(historyData != null) {
			int startTime = historyData.getSummary().getStartTime();
			stime = timeFormat.format(new Date(startTime * 1000l));
			etime = timeFormat.format(new Date(startTime * 1000l + historyData.getSummary().getRecordCount() * 60 * 1000l));
		}
		
		canvas.drawText(stime, margin - timePaint.measureText(stime)/2, itemHeight/3, timePaint);
		canvas.drawText(etime, width - margin - timePaint.measureText(stime)/2, itemHeight/3, timePaint);
		
		canvas.drawCircle(margin, itemHeight/2, radius, solidLinePaint);
		canvas.drawCircle(width - margin, itemHeight/2, radius, solidLinePaint);
		
		canvas.drawLine(margin, itemHeight/2 + radius, margin, height, dividerPaint);
		canvas.drawLine(width - margin, itemHeight/2 + radius, width - margin, height, dividerPaint);
		
		
		List<int[]> list = null;
		if(historyData != null) {
			list = getSleepState(historyData.getAnalysis());
		}
				
		int size = list == null ? 0 : list.size();
		SdkLog.log(TAG+" onDraw-----size:" + size);
		if(size > 0) {
			int totalDuration = 0;
			for(int i=0;i<size;i++) {
				totalDuration += list.get(i)[1];
			}
			
			float contentWidth = width - 2f * margin;
			float startX = margin;
			for(int i=0; i<size; i++) {
				int[] state = list.get(i);
				float columenWidth = (state[1] / (float)totalDuration) * contentWidth;
				if(state[0] == SleepState.SLEEP_STATE_NONE) {
					solidLinePaint.setColor(Color.TRANSPARENT);
					canvas.drawRect(startX, height, startX + columenWidth, height - lineWidth, solidLinePaint);
				}else if(state[0] == SleepState.SLEEP_STATE_OUT_OF_BED) {
					solidLinePaint.setColor(outOfBedColor);
					canvas.drawRect(startX, itemHeight * 3, startX + columenWidth, height - lineWidth, solidLinePaint);
				}else if(state[0] == SleepState.SLEEP_STATE_AWAKE) {
					solidLinePaint.setColor(awakeColor);
					canvas.drawRect(startX, itemHeight * 2, startX + columenWidth, height - lineWidth, solidLinePaint);
				}else if(state[0] == SleepState.SLEEP_STATE_ASLEEP) {
					solidLinePaint.setColor(asleepColor);
					canvas.drawRect(startX, itemHeight, startX + columenWidth, height - lineWidth, solidLinePaint);
				}
				
				startX += columenWidth;
			}
		}
    }
	
	public void refreshChart(HistoryData historyData) {
		this.historyData = historyData;
		invalidate();
	}

	private List<int[]> getSleepState(Analysis analysis){
		List<int[]> list = new ArrayList<int[]>();
		if(analysis != null) {
			byte[] tmp = analysis.getSca_array();
			int len = tmp == null ? 0 : tmp.length;
			if(len > 0) {
				int[] state = new int[2];
				state[0] = tmp[0];
				state[1] = 1;
				
				for(int i=1;i<len;i++) {
					if(state[0] == tmp[i]) {
						state[1]++;
					}else {
						list.add(state);
						state = new int[2];
						state[0] = tmp[i];
						state[1] = 1;
					}
				}
				
				list.add(state);
			}
			
		}
		return list;
	}
	
}
