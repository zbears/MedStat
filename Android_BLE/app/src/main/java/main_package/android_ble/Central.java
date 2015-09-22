package main_package.android_ble;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Negatu on 9/22/15.
 */
public class Central extends Activity implements BluetoothAdapter.LeScanCallback {

    private TextView statusView;
    private LinearLayout devicesList;

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> btDevices;
    private BluetoothGatt btGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.central_layout);
        devicesList = (LinearLayout) findViewById(R.id.device_list);
        statusView = (TextView) findViewById(R.id.statusView);
        statusView.setText("Starting...");

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btDevices = new ArrayList<>();
        //Check if bluetooth is enabled on central device
        btAdapter = btManager.getAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            statusView.setText("Requesting Authorization...");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            finish();
            return;
        }

        //Check if BLE is supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //startScan();


    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        btDevices.add(device);
        final DeviceEntry deviceEntry = new DeviceEntry(this);
        deviceEntry.setIndex(btDevices.indexOf(device));
        deviceEntry.setText(device.getName());
        devicesList.addView(deviceEntry);
        deviceEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice selectedDevice = btDevices.get(deviceEntry.getIndex());
                btGatt = selectedDevice.connectGatt(deviceEntry.getMyContext(), true, btGattCallBack);
            }
        });
    }

    private BluetoothGattCallback btGattCallBack = new BluetoothGattCallback() {
        //This is where communication callbacks to communication functions(with peripheral device should occur.

    };

    private void startScan(){
        btAdapter.startLeScan(this);
    }

    private void stopScan(){
        btAdapter.stopLeScan(this);
    }


    @Override
    public void onStop(){
        super.onStop();
        //Disconnect active connection
        if (btGatt != null){
            btGatt.disconnect();
            btGatt = null;
        }
    }

}
