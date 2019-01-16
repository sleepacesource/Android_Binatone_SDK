package com.binatonesdk.demo.view;

import com.binatonesdk.demo.R;
import com.binatonesdk.demo.view.wheelview.NumericWheelAdapter;
import com.binatonesdk.demo.view.wheelview.OnItemSelectedListener;
import com.binatonesdk.demo.view.wheelview.WheelAdapter;
import com.binatonesdk.demo.view.wheelview.WheelView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SelectTimeDialog extends Dialog {
	private static final String TAG = SelectTimeDialog.class.getSimpleName();
	private TextView tvCancel, tvTitle, tvOk;
	private WheelView wvHour, wvMinute;
    private WheelAdapter hourAdapter, minuteAdapter;
    
    private String leftBtnLabel, title, rightBtnLabel;
    private TimeSelectedListener timeSelectedListener;
    private byte hour, min;

	public SelectTimeDialog(Context context) {
		super(context, R.style.myDialog);
		// TODO Auto-generated constructor stub
	}
	
	public void setLabel(String leftBtnLabel, String title, String rightBtnLabel) {
		this.leftBtnLabel = leftBtnLabel;
		this.title = title;
		this.rightBtnLabel = rightBtnLabel;
		initView();
	}
	
	public void setDefaultValue(byte sHour, byte sMin) {
		this.hour = sHour;
		this.min = sMin;
		initView();
	}
	
	public void setTimeSelectedListener(TimeSelectedListener timeSelectedListener) {
		this.timeSelectedListener = timeSelectedListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_time);
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = d.widthPixels;
		lp.gravity = Gravity.BOTTOM;
		dialogWindow.setAttributes(lp);
		
		tvCancel = (TextView) findViewById(R.id.tv_cancel);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvOk = (TextView) findViewById(R.id.tv_ok);
		
		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		tvOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				if(timeSelectedListener != null) {
					timeSelectedListener.onTimeSelected(hour, min);
				}
			}
		});
		
		wvHour = (WheelView) findViewById(R.id.hour);
        wvMinute = (WheelView) findViewById(R.id.minute);
		
        wvHour.setAdapter(new NumericWheelAdapter(0, 23));
        wvHour.setTextSize(20);
        wvHour.setCyclic(true);
        wvHour.setOnItemSelectedListener(onHourItemSelectedListener);

        wvMinute.setAdapter(new NumericWheelAdapter(0, 59));
        wvMinute.setTextSize(20);
        wvMinute.setCyclic(true);
        wvMinute.setOnItemSelectedListener(onMiniteItemSelectedListener);

        wvHour.setRate(5 / 4.0f);
        wvMinute.setRate(1 / 2.0f);
        
        initView();
	}
	
	private void initView() {
		if(tvCancel == null) {
			return;
		}
		
		tvCancel.setText(leftBtnLabel);
		tvTitle.setText(title);
		tvOk.setText(rightBtnLabel);
		
		wvHour.setCurrentItem(hour);
        wvMinute.setCurrentItem(min);
	}
	
	 //更新控件快速滑动
    private OnItemSelectedListener onHourItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
//            LogUtil.log(TAG+" hour onItemSelected idx:" + index+",val:" + wvHour.getCurrentItem());
        	hour = (byte) index;
        }
    };

    private OnItemSelectedListener onMiniteItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
//        	LogUtil.log(TAG+" min onItemSelected idx:" + index+",val:" + wvMinute.getCurrentItem());
        	min = (byte) index;
        }
    };
    
    public interface TimeSelectedListener{
    	void onTimeSelected(byte hour, byte minute);
    }

}
