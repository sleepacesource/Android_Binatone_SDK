package com.binatonesdk.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.binatonesdk.demo.util.ActivityUtil;
import com.binatonesdk.demo.util.BleDeviceNameUtil;
import com.sleepace.sdk.binatone.BinatoneHelper;
import com.sleepace.sdk.binatone.domain.DeviceInfo;
import com.sleepace.sdk.constant.StatusCode;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.ble.BleHelper;
import com.sleepace.sdk.util.SdkLog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SearchBleDeviceActivity extends BaseActivity {
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private View vRefresh;
    private ListView listView;
    private LayoutInflater inflater;
    private BleAdapter adapter;

    private final Handler mHandler = new Handler();
	private int scanTime = 5 * 1000;
	private boolean mScanning;
    
    private RotateAnimation animation;
    private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private ProgressDialog progressDialog;
	private BinatoneHelper mHelper;
	private BleDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ble);
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mHelper = BinatoneHelper.getInstance(this);
        findView();
        initListener();
        initUI();
    }


    public void findView() {
    	super.findView();
        vRefresh = findViewById(R.id.layout_refresh);
        tvRefresh = (TextView) findViewById(R.id.tv_refresh);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        listView = (ListView) findViewById(R.id.list);
    }

    public void initListener() {
    	super.initListener();
        vRefresh.setOnClickListener(this);
        listView.setOnItemClickListener(onItemClickListener);
    }

    public void initUI() {
        inflater = getLayoutInflater();

        animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);//设置动画持续时间
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        
        tvTitle.setText(R.string.search_ble);
        adapter = new BleAdapter();
        listView.setAdapter(adapter);
        listView.addFooterView(new View(this));  
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(android.R.drawable.ic_dialog_info);
        progressDialog.setMessage(getString(R.string.connect_device));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        
        vRefresh.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				vRefresh.performClick();
			}
		}, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleHelper.REQCODE_OPEN_BT) {
            
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	stopScan();
        	device = adapter.getItem(position);
        	progressDialog.show();
        	mHelper.setMtu(device.getAddress(), 509, new IResultCallback<Integer>() {
    			@Override
    			public void onResultCallback(IDeviceManager manager, CallbackData<Integer> cd) {
    				// TODO Auto-generated method stub
    				SdkLog.log(TAG+" setMtu cd:" + cd);
    				if(cd.getCallbackType() == IDeviceManager.METHOD_SET_MTU) {
    					if(cd.isSuccess()) {
    						//set mtu success
    						int mtu = cd.getResult();
    						SdkLog.log(TAG+" setMtu success mut:" + mtu);
    					}else {
    						//set mtu fail
    					}
    				}
    			}
    		});
        	
        	int timestamp = (int) (System.currentTimeMillis() / 1000 & 0xFFFFFFFF);
        	TimeZone tz = TimeZone.getDefault();
        	int timezone = tz.getRawOffset() / 1000;
        	Calendar calendar = Calendar.getInstance();
        	int dstOffset = calendar.get(Calendar.DST_OFFSET) / 1000;
        	mHelper.login(device.getDeviceName(), device.getAddress(), DemoApp.USER_ID, timestamp, timezone, dstOffset, 20000, loginCallback);
        }
    };
    
    private IResultCallback<DeviceInfo> loginCallback = new IResultCallback<DeviceInfo>() {
		@Override
		public void onResultCallback(IDeviceManager manager, final CallbackData<DeviceInfo> cd) {
			// TODO Auto-generated method stub
			if(!ActivityUtil.isActivityAlive(mContext)) {
				return;
			}
			SdkLog.log(TAG+" loginCallback " + cd);
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog.dismiss();
					if(cd.isSuccess()) {
						DeviceInfo deviceInfo = cd.getResult();
						device.setDeviceId(deviceInfo.getDeviceId());
						device.setVersionName(deviceInfo.getHardwareVersion());
						MainActivity.addDevice(device);
						MainActivity.setCurDevice(device);
						finish();
					} else {
						if(cd.getStatus() == StatusCode.NOT_ENABLE) {
							Toast.makeText(mContext, R.string.phone_bluetooth_not_open, Toast.LENGTH_SHORT).show();
						}else {
							new AlertDialog.Builder(mContext)
							.setTitle(R.string.device_connect_fail)
							.setMessage(R.string.device_w_ble_connect_failed_tip)
							.setPositiveButton(android.R.string.ok, null).create().show();
						}
					}
				}
			});
		}
	};


    private void initRefreshView() {
        //tvRefresh.setText(R.string.refresh);
        ivRefresh.clearAnimation();
        ivRefresh.setImageResource(R.drawable.bg_refresh);
    }

    private void initSearchView() {
        //tvRefresh.setText(R.string.refreshing);
        ivRefresh.setImageResource(R.drawable.device_loading);
        ivRefresh.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
    	super.onClick(v);
        if (v == vRefresh) {
        	
        	if(mScanning){
        		return;
        	}
        	
        	if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
        		scanBleDevice();
        	}else{
        		Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			startActivityForResult(enabler, BleHelper.REQCODE_OPEN_BT);
        	}
        }
    }
    
    
    private boolean scanBleDevice(){
    	
    	if(!checkPermission()) {
    		return false;
    	}
    	
		if (!mScanning) {
            mScanning = true;
    		initSearchView();
    		adapter.clearData();
            mHandler.postDelayed(stopScanTask, scanTime);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            return true;
        }
		return false;
	}
    
    
    private static final int REQUEST_ACCESS_LOCATION = 1;
	private boolean checkPermission(){
	    boolean grant = true;
        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                grant = false;
                SdkLog.log(TAG+" checkPermission not grant-------------");

                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_LOCATION);

                //向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    SdkLog.log(TAG+" checkPermission tips-------------");
                    //Toast.makeText(DeviceListActivity.this,"shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }
            }else{
                SdkLog.log(TAG+" checkPermission ok-------------");
            }
        }else{
            SdkLog.log(TAG+" checkPermission low system-------------");
        }

        return grant;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
    	super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SdkLog.log(TAG+" onRequestPermissionsResult permissions:" + Arrays.toString(permissions)+",grantResults:"+ Arrays.toString(grantResults));
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意使用该权限
                vRefresh.performClick();
            } else {
                // 用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Toast.makeText(this, R.string.tips_permission, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private final Runnable stopScanTask = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    
    public void stopScan() {
        if (mScanning) {
            mScanning = false;
    		initRefreshView();
            mHandler.removeCallbacks(stopScanTask);
            //由于stopScan是延时后的操作，为避免断开或其他情况时把对象置空，所以以下2个对象都需要非空判断
            if (mBluetoothAdapter != null && mLeScanCallback != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            SdkLog.log(TAG+" onScanResult result-------------" + result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            SdkLog.log(TAG+" onBatchScanResults-------------" + results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            SdkLog.log(TAG+" onScanFailed-------------" + errorCode);
        }
    };
    
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {// Z1-140900000
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String modelName = device.getName();
					if(modelName != null){
						modelName = modelName.trim();
					}
					
		            String deviceName = BleDeviceNameUtil.getBleDeviceName(0xff, scanRecord);
		            if(deviceName != null){
		            	deviceName = deviceName.trim();
		            }

		            if(!TextUtils.isEmpty(deviceName) && deviceName.startsWith("MBP")){
//		            if(!TextUtils.isEmpty(modelName) && !TextUtils.isEmpty(deviceName)){
		            	SdkLog.log(TAG+" onLeScan modelName:" + modelName+",deviceName:" + deviceName);
		            	BleDevice ble = new BleDevice();
		            	ble.setModelName(modelName);
		            	ble.setAddress(device.getAddress());
		            	ble.setDeviceName(deviceName);
		            	ble.setDeviceId(deviceName);
		            	adapter.addBleDevice(ble);
		            }
				}
			});
        }
    };
    

    class BleAdapter extends BaseAdapter {
        private List<BleDevice> list = new ArrayList<BleDevice>();
        

        class ViewHolder {
            TextView tvName;
            TextView tvDeviceId;
        }

        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public BleDevice getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_reston_item, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tvDeviceId = (TextView) convertView.findViewById(R.id.tv_deviceid);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BleDevice item = getItem(position);
            holder.tvName.setText(item.getModelName());
            holder.tvDeviceId.setText(item.getDeviceName());
            return convertView;
        }

        public void addBleDevice(BleDevice bleDevice) {
            boolean exists = false;
            for (BleDevice d : list) {
                if (d.getAddress().equals(bleDevice.getAddress())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                list.add(bleDevice);
                notifyDataSetChanged();
            }
        }

        public List<BleDevice> getData() {
            return list;
        }

        public void clearData() {
            list.clear();
            notifyDataSetChanged();
        }
    }
}
