package com.binatonesdk.demo.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class BleUtil {

	public static boolean isBluetoothOpen(Context context) {
		BluetoothManager mBluetoothManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
		return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
	}
}
