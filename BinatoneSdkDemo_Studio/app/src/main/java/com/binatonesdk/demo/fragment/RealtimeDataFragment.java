package com.binatonesdk.demo.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.binatonesdk.demo.DemoApp;
import com.binatonesdk.demo.MainActivity;
import com.binatonesdk.demo.R;
import com.binatonesdk.demo.ReportActivity;
import com.sleepace.sdk.binatone.domain.HistoryData;
import com.sleepace.sdk.binatone.domain.RealTimeData;
import com.sleepace.sdk.binatone.interfs.RealtimeDataListener;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RealtimeDataFragment extends BaseFragment {
	private ListView listView;
	private ProgressDialog progressDialog;
	private MyAdapter adapter;

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
		listView = root.findViewById(R.id.list);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		getDeviceHelper().addRealtimeDataListener(realtimeDataListener);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.time_data);
		
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		
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
//		SdkLog.log(TAG + " onResume connect:" + getDeviceHelper().isConnected());
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
		getDeviceHelper().removeConnectionStateCallback(stateCallback);
		getDeviceHelper().removeRealtimeDataListener(realtimeDataListener);
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
						
					} else if (state == CONNECTION_STATE.CONNECTED) {
						
					}
				}
			});
		}
	};
	
	private static HistoryData historyData;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}
	
	class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<RealtimeBean> list = new ArrayList<RealtimeBean>();
		
		MyAdapter(){
			inflater = LayoutInflater.from(mActivity);
			for(int i=MainActivity.getDeviceList().size()- 1; i>=0; i--) {
				RealtimeBean bean = new RealtimeBean();
				bean.deviceName = MainActivity.getDeviceList().get(i).getDeviceName();
				bean.address = MainActivity.getDeviceList().get(i).getAddress();
				bean.realTimeData = new RealTimeData();
				list.add(bean);
			}
		}
		
		class RealtimeBean {
			String deviceName;
			String address;
			RealTimeData realTimeData;
		}
		
		class ViewHolder {
            TextView tvDevice;
            TextView tvSleepStatus;
            TextView tvHeartRate;
            TextView tvBreathRate;
            Button btnRealtimeData;
            View vLast24HourData;
        }
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		
		@Override
		public RealtimeBean getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}
		
		public void refreshRealtimeData(String address, RealTimeData realTimeData) {
			for(RealtimeBean bean : list) {
				if(bean.address.equals(address)) {
					bean.realTimeData = realTimeData;
					break;
				}
			}
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_device_realtime_data, null);
                holder = new ViewHolder();
                holder.tvDevice = (TextView) convertView.findViewById(R.id.tv_device);
                holder.tvSleepStatus = (TextView) convertView.findViewById(R.id.tv_sleep_status);
                holder.tvHeartRate = (TextView) convertView.findViewById(R.id.tv_heartrate);
                holder.tvBreathRate = (TextView) convertView.findViewById(R.id.tv_breathrate);
                holder.btnRealtimeData = (Button) convertView.findViewById(R.id.btn_realtime_data);
                holder.vLast24HourData = convertView.findViewById(R.id.last_24_hour_data);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            final RealtimeBean item = getItem(position);
            holder.tvDevice.setText(getString(R.string.device) + (position + 1) + " " + item.deviceName);
        	boolean realtimeDataOpen = false;
			if(MainActivity.deviceRealtimeDataOpen.containsKey(item.address)) {
				realtimeDataOpen = MainActivity.deviceRealtimeDataOpen.get(item.address);
			}
			
			SdkLog.log(TAG+" getView pos:" + position+",item:" + item.realTimeData+",open:" + realtimeDataOpen);
            initBtnState(holder.btnRealtimeData, realtimeDataOpen);
            if(realtimeDataOpen) {
            	initRealtimeDataView(realtimeDataOpen, holder.tvSleepStatus, holder.tvHeartRate, holder.tvBreathRate, item.realTimeData);
            }else {
            	initRealtimeDataView(realtimeDataOpen, holder.tvSleepStatus, holder.tvHeartRate, holder.tvBreathRate, null);
            }
            
			holder.btnRealtimeData.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Object tag = v.getTag();
					if (tag == null || "open".equals(tag)) {
						getDeviceHelper().startRealTimeData(item.address, 3000, callback);
					} else {
						getDeviceHelper().stopRealTimeData(item.address, 3000, callback);
					}
				}
			});
		
			holder.vLast24HourData.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Calendar cal = Calendar.getInstance();
					// cal.set(Calendar.MINUTE, 0);
					// cal.set(Calendar.SECOND, 0);
					int endTime = (int) (cal.getTimeInMillis() / 1000);
					SdkLog.log(TAG + " getLast24HourData date:" + StringUtil.DATE_FORMAT.format(cal.getTime()));
					progressDialog.show();
					getDeviceHelper().getLast24HourData(item.address, endTime, DemoApp.USER_SEX, new IResultCallback<HistoryData>() {
						@Override
						public void onResultCallback(final IDeviceManager manager, final CallbackData<HistoryData> cd) {
							// TODO Auto-generated method stub
							SdkLog.log(TAG + " getLast24HourData " + cd);
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (isAdded()) {
										progressDialog.dismiss();
										if (cd.isSuccess()) {
											if(cd.getResult() == null) {
												Toast.makeText(mActivity, R.string.tips_no_report, Toast.LENGTH_SHORT).show();
											}else {
												historyData = cd.getResult();
												Intent intent = new Intent(mActivity, ReportActivity.class);
												intent.putExtra(ReportActivity.EXTRA_REPORT_TYPE, ReportActivity.REPORT_TYPE_24);
												intent.putExtra(ReportActivity.EXTRA_DATA, historyData);
												startActivity(intent);
											}
										} else {
											Toast.makeText(mActivity, R.string.sync_falied, Toast.LENGTH_SHORT).show();
										}
									}
								}
							});
						}
					});
				}
			});
            return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		private void initBtnState(Button btn, boolean realtimeDataOpen) {
			if(realtimeDataOpen) {
				btn.setTag("close");
				btn.setText(R.string.end_real_data);
			}else {
				btn.setTag("open");
				btn.setText(R.string.obtain_24data);
			}
		}

		private void initRealtimeDataView(boolean realtimeDataOpen, TextView tvSleepStatus, TextView tvHeartRate, TextView tvBreathRate, RealTimeData data) {
			if (data == null || data.getStatus() == SleepStatusType.SLEEP_INIT) {
				if(realtimeDataOpen) {
					tvSleepStatus.setText(R.string.initialization);
				}else {
					tvSleepStatus.setText("--");
				}
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
	}
	
	
	private IResultCallback callback = new IResultCallback() {
		@Override
		public void onResultCallback(final IDeviceManager manager, final CallbackData cd) {
			// TODO Auto-generated method stub
			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA_OPEN) {
						MainActivity.deviceRealtimeDataOpen.put(manager.getAddress(), true);
						adapter.notifyDataSetChanged();
					}else if (cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA_CLOSE) {
						MainActivity.deviceRealtimeDataOpen.put(manager.getAddress(), false);
						adapter.notifyDataSetChanged();
					}
				}
			});
		}
	};
	
	private RealtimeDataListener realtimeDataListener = new RealtimeDataListener() {
		@Override
		public void onRealtimeData(final IDeviceManager manager, final RealTimeData realTimeData) {
			// TODO Auto-generated method stub
			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.refreshRealtimeData(manager.getAddress(), realTimeData);
				}
			});
		}
	};
}











