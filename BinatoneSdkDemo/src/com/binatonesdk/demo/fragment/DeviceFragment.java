package com.binatonesdk.demo.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import com.binatonesdk.demo.AlarmSettingActivity;
import com.binatonesdk.demo.DemoApp;
import com.binatonesdk.demo.MainActivity;
import com.binatonesdk.demo.R;
import com.binatonesdk.demo.SearchBleDeviceActivity;
import com.binatonesdk.demo.util.BleUtil;
import com.sleepace.sdk.binatone.domain.AlarmConfig;
import com.sleepace.sdk.binatone.domain.BatteryBean;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.ble.BleHelper;
import com.sleepace.sdk.util.SdkLog;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceFragment extends BaseFragment {
	private EditText etUserId;
	private View vUserDeviceTips, vSetBreathPauseAlarmTime, vSetOutOfBedAlarmTime;
	private Button btnConnectDevice, btnDeviceName, btnDeviceId, btnPower, btnMac, btnVersion, btnUpgrade;
	private TextView tvDeviceName, tvDeviceId, tvPower, tvMac, tvVersion;
	private TextView tvLabelAlarmSwitch, tvOutOfBedAlarm;
	private CheckBox cbBreathPauseAlarm, cbOutOfBedAlarm;
	private boolean upgrading = false;
	private LinearLayout deviceListLayout;
	private TextView tvBreathPauseAlarmTime, tvOutOfBedTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_device, null);
		// SdkLog.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		vUserDeviceTips = root.findViewById(R.id.tv_tips_user_device);
		deviceListLayout = root.findViewById(R.id.layout_device_list);
		etUserId = root.findViewById(R.id.et_userid);
		tvDeviceName = (TextView) root.findViewById(R.id.tv_device_name);
		tvDeviceId = (TextView) root.findViewById(R.id.tv_device_id);
		tvPower = (TextView) root.findViewById(R.id.tv_device_battery);
		tvMac = (TextView) root.findViewById(R.id.tv_mac);
		tvVersion = (TextView) root.findViewById(R.id.tv_device_version);
		btnConnectDevice = (Button) root.findViewById(R.id.btn_connect_device);
		btnDeviceName = (Button) root.findViewById(R.id.btn_get_device_name);
		btnDeviceId = (Button) root.findViewById(R.id.btn_get_device_id);
		btnPower = (Button) root.findViewById(R.id.btn_get_device_battery);
		btnMac = (Button) root.findViewById(R.id.btn_get_mac);
		btnVersion = (Button) root.findViewById(R.id.btn_device_version);
		btnUpgrade = (Button) root.findViewById(R.id.btn_upgrade_fireware);
		tvLabelAlarmSwitch = (TextView) root.findViewById(R.id.label_alarm_switch);
		tvOutOfBedAlarm = (TextView) root.findViewById(R.id.label_outofbed_switch);
		cbBreathPauseAlarm = (CheckBox) root.findViewById(R.id.cb_alarm_switch);
		cbOutOfBedAlarm = (CheckBox) root.findViewById(R.id.cb_outofbed_switch);
		vSetBreathPauseAlarmTime = (TextView) root.findViewById(R.id.set_alarm_time_range);
		vSetOutOfBedAlarmTime = (TextView) root.findViewById(R.id.set_outofbed_alarm_time_range);
		tvBreathPauseAlarmTime = (TextView) root.findViewById(R.id.tv_apnea_alarm_time_range);
		tvOutOfBedTime = (TextView) root.findViewById(R.id.tv_outofbed_alarm_time_range);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		btnConnectDevice.setOnClickListener(this);
		btnDeviceName.setOnClickListener(this);
		btnDeviceId.setOnClickListener(this);
		btnPower.setOnClickListener(this);
		btnMac.setOnClickListener(this);
		btnVersion.setOnClickListener(this);
		btnUpgrade.setOnClickListener(this);
		vSetBreathPauseAlarmTime.setOnClickListener(this);
		vSetOutOfBedAlarmTime.setOnClickListener(this);
		cbBreathPauseAlarm.setOnCheckedChangeListener(checkedChangeListener);
		cbOutOfBedAlarm.setOnCheckedChangeListener(checkedChangeListener);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.device);
		etUserId.setText("1");
		initDeviceName();
		initDeviceId();
		initPower();
		initMac();
		initVersion();
		cbBreathPauseAlarm.setEnabled(AlarmSettingActivity.bpAlarmConfig.isEnable());
		cbOutOfBedAlarm.setEnabled(AlarmSettingActivity.oobAlarmConfig.isEnable());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SdkLog.log(TAG + " onResume------------");
		initDeviceList();
	}

	private void initDeviceName() {
		if (MainActivity.getCurDevice() != null) {
			tvDeviceName.setText(MainActivity.getCurDevice().getDeviceName());
		} else {
			tvDeviceName.setText("");
		}
	}

	private void initDeviceId() {
		if (MainActivity.getCurDevice() != null) {
			tvDeviceId.setText(MainActivity.getCurDevice().getDeviceId());
		} else {
			tvDeviceId.setText("");
		}
	}

	private void initPower() {
		tvPower.setText(MainActivity.getDevicePower());
	}

	private void initMac() {
		if (MainActivity.getCurDevice() != null) {
			tvMac.setText(MainActivity.getCurDevice().getAddress());
		} else {
			tvMac.setText("");
		}
	}

	private void initVersion() {
		if (MainActivity.getCurDevice() != null) {
			tvVersion.setText(MainActivity.getCurDevice().getVersionName());
		} else {
			tvVersion.setText("");
		}
	}

	private void initDeviceList() {
		if (MainActivity.getDeviceList().size() > 0) {
			vUserDeviceTips.setVisibility(View.VISIBLE);
		} else {
			vUserDeviceTips.setVisibility(View.GONE);
		}
		deviceListLayout.removeAllViews();
		for (int i = 0; i < MainActivity.getDeviceList().size(); i++) {
			final BleDevice device = MainActivity.getDeviceList().get(i);
			final View itemView = LayoutInflater.from(mActivity).inflate(R.layout.list_device_item, null);
			TextView tv = itemView.findViewById(R.id.tv_name);
			tv.setText(getString(R.string.device) + (MainActivity.getDeviceList().size() - i) + ":" + device.getDeviceName());
			ImageView ivDel = itemView.findViewById(R.id.iv_del);
			ivDel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getDeviceHelper().disconnect(device.getAddress());
					if (MainActivity.getCurDevice() == device) {
						clearDeviceInfo();
					}
					MainActivity.deleteDevice(device);
					initDeviceList();
				}
			});

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (MainActivity.getCurDevice() != device) {
						MainActivity.setCurDevice(device);
						initDeviceList();
					}
				}
			});

			if (MainActivity.getCurDevice() == device) {
				tv.setTextColor(getResources().getColor(R.color.COLOR_1));
			} else {
				tv.setTextColor(getResources().getColor(R.color.COLOR_4));
			}
			deviceListLayout.addView(itemView, 0);
		}

		clearDeviceInfo();
		boolean isConnected = false;
		if (MainActivity.getCurDevice() != null) {
			isConnected = getDeviceHelper().isConnected(MainActivity.getCurDevice().getAddress());
		}
		initPageState(isConnected);
	}

	private void initPageState(boolean isConnected) {
		setPageEnable(isConnected);
		if (!isConnected) {
			clearDeviceInfo();
		}else {
			getDeviceHelper().getBreathPauseAlarm(MainActivity.getCurDevice().getAddress(), 3000, getAlarmCallback);
		}
	}

	private void setPageEnable(boolean enable) {
		btnDeviceName.setEnabled(enable);
		btnDeviceId.setEnabled(enable);
		btnPower.setEnabled(enable);
		btnMac.setEnabled(enable);
		btnVersion.setEnabled(enable);
		btnUpgrade.setEnabled(enable);
		tvLabelAlarmSwitch.setEnabled(enable);
		tvOutOfBedAlarm.setEnabled(enable);
		vSetBreathPauseAlarmTime.setEnabled(enable);
		cbBreathPauseAlarm.setEnabled(enable);
		cbOutOfBedAlarm.setEnabled(enable);
	}

	private void clearDeviceInfo() {
		tvDeviceName.setText("");
		tvDeviceId.setText("");
		tvPower.setText("");
		tvMac.setText("");
		tvVersion.setText("");
		tvBreathPauseAlarmTime.setText("");
		tvOutOfBedTime.setText("");
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG + " onCheckedChanged buttonView:" + buttonView + ",isChecked:" + isChecked);
			if (buttonView == cbBreathPauseAlarm) {
				AlarmSettingActivity.bpAlarmConfig.setEnable(isChecked);
				if (MainActivity.getCurDevice() != null) {
					getDeviceHelper().setBreathPauseAlarm(MainActivity.getCurDevice().getAddress(), AlarmSettingActivity.bpAlarmConfig.isEnable(), AlarmSettingActivity.bpAlarmConfig.getHour(), AlarmSettingActivity.bpAlarmConfig.getMinute(), AlarmSettingActivity.bpAlarmConfig.getDuration(), 3000,
							setAlarmCallback);
				}
			} else {
				AlarmSettingActivity.oobAlarmConfig.setEnable(isChecked);
				if (MainActivity.getCurDevice() != null) {
					getDeviceHelper().setOutOfBedAlarm(MainActivity.getCurDevice().getAddress(), AlarmSettingActivity.oobAlarmConfig.isEnable(), AlarmSettingActivity.oobAlarmConfig.getHour(), AlarmSettingActivity.oobAlarmConfig.getMinute(), AlarmSettingActivity.oobAlarmConfig.getDuration(), 3000,
							setAlarmCallback);
				}
			}
		}
	};

	private IResultCallback<Void> setAlarmCallback = new IResultCallback<Void>() {
		@Override
		public void onResultCallback(IDeviceManager manager, CallbackData<Void> cd) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG + " onResultCallback " + cd);
			if (!isAdded()) {
				return;
			}
			if (cd.getCallbackType() == IMonitorManager.METHOD_BP_ALARM_SET) {
				if (cd.isSuccess()) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							initBreathPauseTimeView();
						}
					});
				} else {

				}
			} else if (cd.getCallbackType() == IMonitorManager.METHOD_OUT_OF_BED_ALARM_SET) {
				if (cd.isSuccess()) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							initOutOfBedTimeView();
						}
					});
				} else {

				}
			}
		}
	};

	private void initBreathPauseTimeView() {
		int hour = AlarmSettingActivity.bpAlarmConfig.getHour();
		int min = AlarmSettingActivity.bpAlarmConfig.getMinute();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		calendar.add(Calendar.MINUTE, AlarmSettingActivity.bpAlarmConfig.getDuration());
		int eHour = calendar.get(Calendar.HOUR_OF_DAY);
		int eMin = calendar.get(Calendar.MINUTE);
		tvBreathPauseAlarmTime.setText(String.format("%02d:%02d~%02d:%02d", hour, min, eHour, eMin));
	}
	
	private void initOutOfBedTimeView() {
		int hour = AlarmSettingActivity.oobAlarmConfig.getHour();
		int min = AlarmSettingActivity.oobAlarmConfig.getMinute();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		calendar.add(Calendar.MINUTE, AlarmSettingActivity.oobAlarmConfig.getDuration());
		int eHour = calendar.get(Calendar.HOUR_OF_DAY);
		int eMin = calendar.get(Calendar.MINUTE);
		tvOutOfBedTime.setText(String.format("%02d:%02d~%02d:%02d", hour, min, eHour, eMin));
	}

	private IResultCallback<AlarmConfig> getAlarmCallback = new IResultCallback<AlarmConfig>() {
		@Override
		public void onResultCallback(IDeviceManager manager, CallbackData<AlarmConfig> cd) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG + " onResultCallback " + cd);
			if (cd.getCallbackType() == IMonitorManager.METHOD_BP_ALARM_GET) {
				getDeviceHelper().getOutOfBedAlarm(MainActivity.getCurDevice().getAddress(), 3000, getAlarmCallback);
				if (cd.isSuccess()) {
					AlarmSettingActivity.bpAlarmConfig = cd.getResult();
					if (!isAdded()) {
						return;
					}
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							cbBreathPauseAlarm.setChecked(AlarmSettingActivity.bpAlarmConfig.isEnable());
							initBreathPauseTimeView();
						}
					});
				} else {

				}
			} else if (cd.getCallbackType() == IMonitorManager.METHOD_OUT_OF_BED_ALARM_GET) {
				if (cd.isSuccess()) {
					AlarmSettingActivity.oobAlarmConfig = cd.getResult();
					if (!isAdded()) {
						return;
					}
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							cbOutOfBedAlarm.setChecked(AlarmSettingActivity.oobAlarmConfig.isEnable());
							initOutOfBedTimeView();
						}
					});
				} else {

				}
			}
		}
	};

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(final IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (MainActivity.getCurDevice() != null && MainActivity.getCurDevice().getAddress().equals(manager.getAddress())) {
						initPageState(state == CONNECTION_STATE.CONNECTED);
						SdkLog.log(TAG + " onStateChanged state:" + state);
						if (state == CONNECTION_STATE.DISCONNECT) {
							if (upgrading) {
								upgrading = false;
								mActivity.hideUpgradeDialog();
								mActivity.setUpgradeProgress(0);
								// tvUpgrade.setText(R.string.update_completed);
							}
						} else if (state == CONNECTION_STATE.CONNECTED) {
							if (upgrading) {
								upgrading = false;
								btnUpgrade.setEnabled(true);
								mActivity.hideUpgradeDialog();
								// tvUpgrade.setText(R.string.update_completed);
							}

							getDeviceHelper().getBreathPauseAlarm(MainActivity.getCurDevice().getAddress(), 3000, getAlarmCallback);
						}
					}
				}
			});
		}
	};

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getDeviceHelper().removeConnectionStateCallback(stateCallback);
	}
	
	private void upgradeDevice(FirmwareBean bean) {
		btnUpgrade.setEnabled(false);
		mActivity.showUpgradeDialog();
		mActivity.setUpgradeProgress(0);
		upgrading = true;
		// InputStream is = getResources().getAssets().open("xxx.des");
		getDeviceHelper().upgradeDevice(MainActivity.getCurDevice().getAddress(), bean.crcDes, bean.crcBin, bean.is, new IResultCallback<Integer>() {
			@Override
			public void onResultCallback(IDeviceManager manager, final CallbackData<Integer> cd) {
				// TODO Auto-generated method stub
				// SdkLog.log(TAG+" upgradeDevice " + cd);
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (cd.isSuccess()) {
							int progress = cd.getResult();
							mActivity.setUpgradeProgress(progress);
							if (progress == 100) {
								upgrading = false;
								btnUpgrade.setEnabled(true);
								mActivity.hideUpgradeDialog();
								Toast.makeText(mActivity, R.string.up_success, Toast.LENGTH_SHORT).show();
							}
						} else {
							upgrading = false;
							mActivity.hideUpgradeDialog();
							Toast.makeText(mActivity, R.string.up_failed, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnConnectDevice) {// 233 - 10000
			if (!BleUtil.isBluetoothOpen(mActivity)) {
				Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enabler, BleHelper.REQCODE_OPEN_BT);
			} else {
				String strId = etUserId.getText().toString().trim();
				if (TextUtils.isEmpty(strId)) {
					Toast.makeText(mActivity, R.string.userid_not_empty, Toast.LENGTH_SHORT).show();
					return;
				}

				int uid = Integer.valueOf(strId);
				if (uid <= 0 || strId.startsWith("0")) {
					Toast.makeText(mActivity, R.string.userid_error, Toast.LENGTH_SHORT).show();
					return;
				}

				DemoApp.USER_ID = uid;
				Intent intent = new Intent(mActivity, SearchBleDeviceActivity.class);
				startActivity(intent);
			}
		} else if (v == btnUpgrade) {
			final FirmwareBean firmwareBean = getFirmwareBean();
			if (firmwareBean == null || MainActivity.getCurDevice() == null) {
				return;
			}
			
			getDeviceHelper().getBattery(MainActivity.getCurDevice().getAddress(), 1000, new IResultCallback<BatteryBean>() {
				@Override
				public void onResultCallback(IDeviceManager manager, final CallbackData<BatteryBean> cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							SdkLog.log(TAG + " getBattery cd:" + cd);
							if (cd.isSuccess()) {
								BatteryBean bean = cd.getResult();
								if (bean.getChargingState() == 1) {
									upgradeDevice(firmwareBean);
								} else {
									if(bean.getQuantity() >= 20) {
										upgradeDevice(firmwareBean);
									}else {
										Toast.makeText(mActivity, R.string.low_battery, Toast.LENGTH_SHORT).show();
									}
								}
							}
						}
					});
				}
			});
			
		} else if (v == btnDeviceName) {
			initDeviceName();
		} else if (v == btnDeviceId) {
			initDeviceId();
		} else if (v == btnPower) {
			if (MainActivity.getCurDevice() != null) {
				getDeviceHelper().getBattery(MainActivity.getCurDevice().getAddress(), 1000, new IResultCallback<BatteryBean>() {
					@Override
					public void onResultCallback(IDeviceManager manager, final CallbackData<BatteryBean> cd) {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								SdkLog.log(TAG + " getBattery cd:" + cd);
								if (cd.isSuccess()) {
									BatteryBean bean = cd.getResult();
									if (bean.getChargingState() == 1) {
										String power = getString(R.string.charging);
										MainActivity.setDevicePower(MainActivity.getCurDevice().getAddress(), power);
										initPower();
									} else {
										String power = bean.getQuantity() + "%";
										MainActivity.setDevicePower(MainActivity.getCurDevice().getAddress(), power);
										initPower();
									}
								}
							}
						});
					}
				});
			}
		} else if (v == btnMac) {
			initMac();
		} else if (v == btnVersion) {
			initVersion();
			/*
			 * getBinatoneHelper().getDeviceVersion(1000, new IResultCallback<String>() {
			 * 
			 * @Override public void onResultCallback(final CallbackData<String> cd) { //
			 * TODO Auto-generated method stub mActivity.runOnUiThread(new Runnable() {
			 * 
			 * @Override public void run() { // TODO Auto-generated method stub
			 * if(cd.isSuccess()){ MainActivity.version = cd.getResult();
			 * tvVersion.setText(MainActivity.version); } } }); } });
			 */
		} else if (v == vSetBreathPauseAlarmTime) {
			Intent intent = new Intent(mActivity, AlarmSettingActivity.class);
			intent.putExtra("type", "breathPause");
			startActivity(intent);
		} else if (v == vSetOutOfBedAlarmTime) {
			Intent intent = new Intent(mActivity, AlarmSettingActivity.class);
			intent.putExtra("type", "outOfBed");
			startActivity(intent);
		}
	}

	class FirmwareBean {
		InputStream is;
		long crcBin;
		long crcDes;
	}

	private FirmwareBean getFirmwareBean() {
		try {
			InputStream is = mActivity.getResources().getAssets().open("MBP89SN-v1.52b(v2.0.02b)-ug-20211115.des");
			FirmwareBean bean = new FirmwareBean();
			bean.is = is;
			bean.crcBin = 1774760471l;
			bean.crcDes = 854794341l;
			return bean;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
