package com.binatonesdk.demo.fragment;

import java.util.Calendar;

import com.binatonesdk.demo.DemoApp;
import com.binatonesdk.demo.MainActivity;
import com.binatonesdk.demo.R;
import com.binatonesdk.demo.ReportActivity;
import com.sleepace.sdk.binatone.domain.HistoryData;
import com.sleepace.sdk.binatone.domain.RealTimeData;
import com.sleepace.sdk.binatone.util.SleepStatusType;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.StringUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RealtimeDataFragment extends BaseFragment {
	private View vLast24HourData;
	private Button btnRealtimeData;
	private TextView tvSleepStatus, tvHeartRate, tvBreathRate;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_control, null);
		// SdkLog.log(TAG+" onCreateView-----------");
		findView(view);
		initListener();
		initUI();
		return view;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		vLast24HourData = root.findViewById(R.id.last_24_hour_data);
		btnRealtimeData = (Button) root.findViewById(R.id.btn_realtime_data);
		tvSleepStatus = (TextView) root.findViewById(R.id.tv_sleep_status);
		tvHeartRate = (TextView) root.findViewById(R.id.tv_heartrate);
		tvBreathRate = (TextView) root.findViewById(R.id.tv_breathrate);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getBinatoneHelper().addConnectionStateCallback(stateCallback);
		vLast24HourData.setOnClickListener(this);
		btnRealtimeData.setOnClickListener(this);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.time_data);
		
		progressDialog = new ProgressDialog(mActivity);
		progressDialog.setIcon(android.R.drawable.ic_dialog_info);
		progressDialog.setTitle(R.string.data24);
		progressDialog.setMessage(getString(R.string.syncing));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SdkLog.log(TAG + " onResume connect:" + getBinatoneHelper().isConnected());
		btnRealtimeData.setEnabled(getBinatoneHelper().isConnected());
		initBtnState();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		getBinatoneHelper().stopRealTimeData(3000, new IResultCallback<Void>() {
//			@Override
//			public void onResultCallback(CallbackData<Void> cd) {
//				// TODO Auto-generated method stub
//				SdkLog.log(TAG + " onPause " + cd);
//			}
//		});
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		SdkLog.log(TAG + " onDestroyView----------------");
		getBinatoneHelper().removeConnectionStateCallback(stateCallback);
	}

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub

			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (state == CONNECTION_STATE.DISCONNECT) {
						MainActivity.realtimeDataOpen = false;
						btnRealtimeData.setEnabled(false);
						initBtnState();
						initRealtimeDataView(null);
					} else if (state == CONNECTION_STATE.CONNECTED) {
						btnRealtimeData.setEnabled(true);
					}
				}
			});
		}
	};
	
	private void initBtnState() {
		if(MainActivity.realtimeDataOpen) {
			btnRealtimeData.setTag("close");
			btnRealtimeData.setText(R.string.end_real_data);
		}else {
			btnRealtimeData.setTag("open");
			btnRealtimeData.setText(R.string.obtain_24data);
		}
	}

	private void initRealtimeDataView(RealTimeData data) {
		if (data == null || data.getStatus() == SleepStatusType.SLEEP_INIT) {
			tvSleepStatus.setText(R.string.initialization);
			tvHeartRate.setText("--");
			tvBreathRate.setText("--");
		} else {
			if (data.getStatus() == SleepStatusType.SLEEP_LEAVE) {
				tvSleepStatus.setText(R.string.left_bed);
				tvHeartRate.setText("--");
				tvBreathRate.setText("--");
			} else {
				//4-bodymove 6-turnover
				tvSleepStatus.setText(getString(R.string.in_bed));
				tvHeartRate.setText(data.getHeartRate() + getString(R.string.beats_min));
				tvBreathRate.setText(data.getBreathRate() + getString(R.string.times_min));
			}
		}
	}
	
	private static HistoryData historyData;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == vLast24HourData) {
			if(!getBinatoneHelper().isConnected()) {
				return;
			}
			
//			if(historyData == null) {
				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.MINUTE, 0);
//				cal.set(Calendar.SECOND, 0);
				int endTime = (int) (cal.getTimeInMillis() / 1000);
				SdkLog.log(TAG+" getLast24HourData date:" + StringUtil.DATE_FORMAT.format(cal.getTime()));
				progressDialog.show();
				getBinatoneHelper().getLast24HourData(endTime, DemoApp.USER_SEX, new IResultCallback<HistoryData>() {
					@Override
					public void onResultCallback(final CallbackData<HistoryData> cd) {
						// TODO Auto-generated method stub
						SdkLog.log(TAG+" getLast24HourData " + cd);
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(isAdded()) {
									progressDialog.dismiss();
									if(cd.isSuccess()) {
										historyData = cd.getResult();
										Intent intent = new Intent(mActivity, ReportActivity.class);
										intent.putExtra(ReportActivity.EXTRA_REPORT_TYPE, ReportActivity.REPORT_TYPE_24);
										intent.putExtra(ReportActivity.EXTRA_DATA, historyData);
										startActivity(intent);
									}else {
										Toast.makeText(mActivity, R.string.sync_falied, Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
					}
				});
//			} else {
//				Intent intent = new Intent(mActivity, ReportActivity.class);
//				intent.putExtra(ReportActivity.EXTRA_REPORT_TYPE, ReportActivity.REPORT_TYPE_24);
//				intent.putExtra(ReportActivity.EXTRA_DATA, historyData);
//				startActivity(intent);
//			}
		} else if (v == btnRealtimeData) {
			Object tag = v.getTag();
			if(tag == null || "open".equals(tag)) {
				getBinatoneHelper().startRealTimeData(3000, realtimeCallback);
			}else {
				getBinatoneHelper().stopRealTimeData(3000, realtimeCallback);
			}
		}
	}

	private IResultCallback realtimeCallback = new IResultCallback() {
		@Override
		public void onResultCallback(final CallbackData cd) {
			// TODO Auto-generated method stub
//			 SdkLog.log(TAG+" realtimeCB cd:" + cd +",isAdd:" + isAdded());
			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA_OPEN) {
						MainActivity.realtimeDataOpen = true;
						initBtnState();
					} else if (cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA) {// 实时数据
						RealTimeData realTimeData = (RealTimeData) cd.getResult();
						initRealtimeDataView(realTimeData);
					} else if (cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA_CLOSE) {
						MainActivity.realtimeDataOpen = false;
						initBtnState();
						initRealtimeDataView(null);
					}
				}
			});
		}
	};
}
