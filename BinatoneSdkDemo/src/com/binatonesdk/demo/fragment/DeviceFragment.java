package com.binatonesdk.demo.fragment;

import java.io.IOException;
import java.io.InputStream;

import com.binatonesdk.demo.AlarmSettingActivity;
import com.binatonesdk.demo.DemoApp;
import com.binatonesdk.demo.MainActivity;
import com.binatonesdk.demo.R;
import com.binatonesdk.demo.SearchBleDeviceActivity;
import com.sleepace.sdk.binatone.domain.AlarmConfig;
import com.sleepace.sdk.binatone.domain.BatteryBean;
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
import android.widget.TextView;
import android.widget.Toast;

public class DeviceFragment extends BaseFragment {
	private EditText etUserId;
	private Button btnConnectDevice, btnDeviceName, btnDeviceId, btnPower, btnMac, btnVersion, btnUpgrade;
	private TextView tvDeviceName, tvDeviceId, tvPower, tvMac, tvVersion;
	private TextView tvLabelAlarmSwitch, tvSetAlarmTimeRange;
	private CheckBox cbAlarmSwitch;
	private boolean upgrading = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_device, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
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
		cbAlarmSwitch = (CheckBox) root.findViewById(R.id.cb_alarm_switch);
		tvSetAlarmTimeRange = (TextView) root.findViewById(R.id.set_alarm_time_range);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getBinatoneHelper().addConnectionStateCallback(stateCallback);
		btnConnectDevice.setOnClickListener(this);
		btnDeviceName.setOnClickListener(this);
		btnDeviceId.setOnClickListener(this);
		btnPower.setOnClickListener(this);
		btnMac.setOnClickListener(this);
		btnVersion.setOnClickListener(this);
		btnUpgrade.setOnClickListener(this);
		tvSetAlarmTimeRange.setOnClickListener(this);
		cbAlarmSwitch.setOnCheckedChangeListener(checkedChangeListener);
	}


	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.device);
		tvDeviceName.setText(MainActivity.deviceName);
		tvDeviceId.setText(MainActivity.deviceId);
		tvPower.setText(MainActivity.power);
		tvVersion.setText(MainActivity.version);
		cbAlarmSwitch.setEnabled(AlarmSettingActivity.alarmConfig.isEnable());
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getBinatoneHelper().isConnected();
		initPageState(isConnected);
	}
	
	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
		if(!isConnected) {
			tvDeviceName.setText(null);
			tvDeviceId.setText(null);
			tvPower.setText(null);
			tvVersion.setText(null);
			tvMac.setText(null);
		}
	}
	
	private void initBtnConnectState(boolean isConnected) {
		if(isConnected) {
			btnConnectDevice.setText(R.string.disconnect);
			btnConnectDevice.setTag("disconnect");
		}else {
			btnConnectDevice.setText(R.string.connect_device);
			btnConnectDevice.setTag("connect");
		}
	}
	
	private void setPageEnable(boolean enable){
		btnDeviceName.setEnabled(enable);
		btnDeviceId.setEnabled(enable);
		btnPower.setEnabled(enable);
		btnMac.setEnabled(enable);
		btnVersion.setEnabled(enable);
		btnUpgrade.setEnabled(enable);
		tvLabelAlarmSwitch.setEnabled(enable);
		tvSetAlarmTimeRange.setEnabled(enable);
		cbAlarmSwitch.setEnabled(enable);
	}
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			AlarmSettingActivity.alarmConfig.setEnable(isChecked);
			getBinatoneHelper().setAlarm(AlarmSettingActivity.alarmConfig.isEnable(), AlarmSettingActivity.alarmConfig.getHour(), AlarmSettingActivity.alarmConfig.getMinute(), AlarmSettingActivity.alarmConfig.getDuration(), 3000, setAlarmCallback);
		}
	};
	
	private IResultCallback<Void> setAlarmCallback = new IResultCallback<Void>() {
		@Override
		public void onResultCallback(CallbackData<Void> cd) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onResultCallback " + cd);
			if(cd.getCallbackType() == IMonitorManager.METHOD_ALARM_SET) {
				if(cd.isSuccess()) {
					
				}else {
					
				}
			}
		}
	};
	
	private IResultCallback<AlarmConfig> getAlarmCallback = new IResultCallback<AlarmConfig>() {
		@Override
		public void onResultCallback(CallbackData<AlarmConfig> cd) {
			// TODO Auto-generated method stub
			if(!isAdded()) {
				return;
			}
			SdkLog.log(TAG+" onResultCallback " + cd);
			if(cd.getCallbackType() == IMonitorManager.METHOD_ALARM_GET) {
				if(cd.isSuccess()) {
					AlarmSettingActivity.alarmConfig = cd.getResult();
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							cbAlarmSwitch.setChecked(AlarmSettingActivity.alarmConfig.isEnable());
						}
					});
				}else {
					
				}
			}
		}
	};
	
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			
			if(!isAdded()){
				return;
			}
			
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initPageState(state == CONNECTION_STATE.CONNECTED);
					
					if(state == CONNECTION_STATE.DISCONNECT){
						
						if(upgrading){
							upgrading = false;
							mActivity.hideUpgradeDialog();
							mActivity.setUpgradeProgress(0);
//							tvUpgrade.setText(R.string.update_completed);
						}
						
					}else if(state == CONNECTION_STATE.CONNECTED){
						
						if(upgrading){
							upgrading = false;
							btnUpgrade.setEnabled(true);
							mActivity.hideUpgradeDialog();
//							tvUpgrade.setText(R.string.update_completed);
						}
						
						//getBinatoneHelper().getAlarmConfig(3000, getAlarmCallback);
					}
				}
			});
		}
	};
	

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getBinatoneHelper().removeConnectionStateCallback(stateCallback);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == btnConnectDevice) {//233 - 10000
			Object tag = v.getTag();
			if(tag == null || "connect".equals(tag)) {
				if(!BleHelper.getInstance(mActivity).isBluetoothOpen()) {
					Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enabler, BleHelper.REQCODE_OPEN_BT);
				}else {
					String strId = etUserId.getText().toString().trim();
					if(TextUtils.isEmpty(strId)) {
						Toast.makeText(mActivity, R.string.userid_not_empty, Toast.LENGTH_SHORT).show();
						return;
					}
					
					int uid = Integer.valueOf(strId);
					if(uid <= 0 ) {
						Toast.makeText(mActivity, R.string.userid_error, Toast.LENGTH_SHORT).show();
						return;
					}
					
					DemoApp.USER_ID = uid;
					Intent intent = new Intent(mActivity, SearchBleDeviceActivity.class);
					startActivity(intent);
				}
			}else {//断开设备
				getBinatoneHelper().disconnect();
			}
		}else if(v == btnUpgrade){
			FirmwareBean bean = getFirmwareBean();
			if(bean == null){
				return;
			}
			
			btnUpgrade.setEnabled(false);
			mActivity.showUpgradeDialog();
			mActivity.setUpgradeProgress(0);
			upgrading = true;
//			InputStream is = getResources().getAssets().open("Z2_V1.11.des");
			getBinatoneHelper().upgradeDevice(bean.crcDes, bean.crcBin, bean.is, new IResultCallback<Integer>() {
				@Override
				public void onResultCallback(final CallbackData<Integer> cd) {
					// TODO Auto-generated method stub
//					SdkLog.log(TAG+" upgradeDevice " + cd);
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(cd.isSuccess()){
								int progress =  cd.getResult();
								mActivity.setUpgradeProgress(progress);
								if(progress == 100){
									upgrading = false;
									btnUpgrade.setEnabled(true);
									mActivity.hideUpgradeDialog();
									Toast.makeText(mActivity, R.string.up_success, Toast.LENGTH_SHORT).show();
									getBinatoneHelper().disconnect();
								}
							}else{
								upgrading = false;
								btnUpgrade.setEnabled(getBinatoneHelper().isConnected());
								mActivity.hideUpgradeDialog();
								Toast.makeText(mActivity, R.string.up_failed, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		}else if(v == btnDeviceName){
			tvDeviceName.setText(MainActivity.deviceName);
		}else if(v == btnDeviceId){
			tvDeviceId.setText(MainActivity.deviceId);
		}else if(v == btnPower){
			getBinatoneHelper().getBattery(1000, new IResultCallback<BatteryBean>() {
				@Override
				public void onResultCallback(final CallbackData<BatteryBean> cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(cd.isSuccess()){
								BatteryBean bean =  cd.getResult();
								MainActivity.power = bean.getQuantity()+"%";
								tvPower.setText(MainActivity.power);
							}
						}
					});
				}
			});
		}else if(v == btnMac){
			tvMac.setText(MainActivity.mac);
		}else if(v == btnVersion){
			tvVersion.setText(MainActivity.version);
			/*getBinatoneHelper().getDeviceVersion(1000, new IResultCallback<String>() {
				@Override
				public void onResultCallback(final CallbackData<String> cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(cd.isSuccess()){
								MainActivity.version =  cd.getResult();
								tvVersion.setText(MainActivity.version);
							}
						}
					});
				}
			});*/
		}else if(v == tvSetAlarmTimeRange) {
			Intent intent = new Intent(mActivity, AlarmSettingActivity.class);
			startActivity(intent);
		}
	}
	
	
	class FirmwareBean{
		InputStream is;
		long crcBin;
		long crcDes;
	}
	
	private FirmwareBean getFirmwareBean(){
		try {
//			InputStream is = mActivity.getResources().getAssets().open("MBP89SN_20180827_1.20_beta.des");
//			FirmwareBean bean = new FirmwareBean();
//			bean.is = is;
//			bean.crcBin = 248574792l;
//			bean.crcDes = 3706033076l;
			
//			InputStream is = mActivity.getResources().getAssets().open("MBP89SN_20180907_1.23_beta.des");
//			FirmwareBean bean = new FirmwareBean();
//			bean.is = is;
//			bean.crcBin = 3113387137l;
//			bean.crcDes = 3163670667l;
			
			//v1.24 修复：1、优化24小时数据下载乱码；2、增加恢复出厂设置接口
//			InputStream is = mActivity.getResources().getAssets().open("MBP89SN_20180910_1.24_beta.des");
//			FirmwareBean bean = new FirmwareBean();
//			bean.is = is;
//			bean.crcBin = 1082793749l;
//			bean.crcDes = 530097278l;
			
			//v1.26修复：反复操作开关实时数据，下载过去24小时历史数据时，设备断开无法连接问题
//			InputStream is = mActivity.getResources().getAssets().open("MBP89SN_20181031_1.26_beta.des");
//			FirmwareBean bean = new FirmwareBean();
//			bean.is = is;
//			bean.crcBin = 3401879512l;
//			bean.crcDes = 922055888l;
			
			/**
			 * v1.29更新如下：
			 * 1、优化数据混乱，优化机制如下：
             * 2、设备开关机时初始化算法
             * 3、设备未掉电结束采集初始化算法
             * 4、设备切换用户时初始化算法
             * 5、设备开关机时初始化算法
		     * 6、算法优化，离床优化，心率、呼吸率优化
		     * 7、优化设备没电时充电，充电指示灯不指示的问题
			 */
			InputStream is = mActivity.getResources().getAssets().open("MBP89SN_20181207_1.29_beta.des");
			FirmwareBean bean = new FirmwareBean();
			bean.is = is;
			bean.crcBin = 921413085l;
			bean.crcDes = 587897703l;
			
			return bean;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}










