package ece459.medstatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ece459.medstatapp.ErrorPopup;


public class BleService {

    SparseArray<BluetoothDevice> mDevices;
    List<BluetoothGattService> mServices;
    BluetoothGatt mConnectedGatt;
    BluetoothDevice myDevice;

    private boolean readMode;
    private boolean descMode;
    //BluetoothGattCharacteristic mCharateristic;

    private static final UUID serviceUuid =  UUID.fromString("624E957F-CB42-4CD6-BACC-84AEB898F69B");

    private static final UUID emergService = UUID.fromString("834e957f-cb42-4cd6-badc-a4abb8c8f69b");
    private static final UUID moistService = UUID.fromString("734e957f-cb42-4cd6-badc-a4abb8c8f69b");
    private static final UUID lowMoistService = UUID.fromString("634e957f-cb42-4cd6-badc-a4abb8c8f69b");
    private static final UUID highMoistService = UUID.fromString("534e957f-cb42-4cc7-badc-84aeb898f69b");
    private static final UUID heartService = UUID.fromString("20854170-d12a-46c0-b491-a6bff25b48a1");
    private static final UUID highHeartService = UUID.fromString("434e957f-cb42-4cd6-badc-84aeb898f69b");
    private static final UUID lowHeartService = UUID.fromString("324e957f-cb42-4cd6-badc-84aeb898f69b");

    private static final UUID heartCharx = UUID.fromString("932a5eac-e2e2-4968-8ec0-92cac3c9f72b");
    private static final UUID moistCharx = UUID.fromString("df342b03-5df9-43b4-acb6-62a63ca0615a");
    private static final UUID ledCharx =   UUID.fromString("DF342B03-53f9-43B4-ACB6-62A63CA0615A");

    private static final UUID hhCharx = UUID.fromString("df342b43-53f9-43b4-acb6-62a63ca0615a");
    private static final UUID lhCharx = UUID.fromString("df342b03-53f9-43b4-adb6-62a63ca0615a");
    private static final UUID hmCharx = UUID.fromString("df342b03-53fa-43b4-acb6-62a63ca0615a");
    private static final UUID lmCharx = UUID.fromString("df342b03-5df9-43b4-acb6-62a63ca0615a");

    private static final UUID emCharx = UUID.fromString("df342b03-5df9-43b4-acb6-62a63ca0615a");
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private static final ArrayList<scPair> myNotifyList = new ArrayList<>();
    static {
        myNotifyList.add(new scPair(heartService, heartCharx));
        myNotifyList.add(new scPair(moistService, moistCharx));
        myNotifyList.add(new scPair(emergService, emCharx));
    }
    private static final ArrayList<scPair> myWriteList = new ArrayList<>();
    static {
        myWriteList.add(new scPair(highHeartService, hhCharx));
        myWriteList.add(new scPair(lowHeartService, lhCharx));
        myWriteList.add(new scPair(highMoistService, hmCharx));
        myWriteList.add(new scPair(lowMoistService, lmCharx));
    }

    private BluetoothAdapter mBluetoothAdapter;
    private Handler myHandler;
    private static final Integer SCAN_PERIOD = 2000;
    private Context myContext;


    public BleService(Context context, Handler handler) {
        readMode = false;
        descMode = true;
        myHandler = handler;
        myContext = context;
        start();
    }

    private void start() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevices = new SparseArray<>();
        checkBluetooth();
        startScanning(SCAN_PERIOD);
    }

    private void checkBluetooth(){

        if( mBluetoothAdapter==null || !mBluetoothAdapter.isEnabled()){
            throwPopup("Error: Enable Bluetooth");
        }

        if(!myContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throwPopup("Error: No BLE feature");
        }

    }

    public void printTable(){
        StringBuilder sb = new StringBuilder();
        /*
        for(BluetoothGattService service : mServices){
            List<BluetoothGattCharacteristic> serChars = service.getCharacteristics();
            for (BluetoothGattCharacteristic xar : serChars){
                sb.append("> "+service.getUuid().toString()+" : "+xar.getUuid().toString()+"\n");
            }
        }
        throwPopup(sb.toString());
        */

        for (int i=0; i<myNotifyList.size(); i++){
            sb.append(myNotifyList.get(i).getService().toString() + " : " + myNotifyList.get(i).getCharx().toString());
        }
        throwPopup(sb.toString());
    }

    public void stop(){
        if(mConnectedGatt!=null){
            mConnectedGatt.disconnect();
            mConnectedGatt.close();
            mConnectedGatt = null;
        }
    }

    public void startScanning(final int scanPeriod) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            // Called after scanPeriod milliseconds elapsed
            // It stops scanning and sends broadcast
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(leScanCallback);
            }
        }, scanPeriod);
        mBluetoothAdapter.startLeScan(leScanCallback);
    }


    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            mBluetoothAdapter.stopLeScan(leScanCallback);
            Message msg = Message.obtain();
            try {
                myDevice = device;
                msg.arg1 = 1;
                msg.obj = device.getName();
                myHandler.sendMessage(msg);
            } catch (Exception e) {
                msg.arg1 = 0;
                myHandler.sendMessage(msg);
            }
        }
    };

    public void connectMe(){
        try {
            mConnectedGatt = myDevice.connectGatt(myContext, false, btleGattCallback); // ?autoConnect true.
            Boolean connect = mConnectedGatt.connect();
        } catch (Exception e) {
            throwPopup("Error: " + e.getMessage());
        }

    }

    public void startChar(){
        try {
            Boolean dServices = mConnectedGatt.discoverServices();
        } catch (Exception e) {
            throwPopup("Error starting char: " + e.getMessage());
        }
    }

    public void disconnectMe(){
        try {
            mConnectedGatt.disconnect();
            mConnectedGatt.close();
            mConnectedGatt = null;
        } catch (Exception e) {
            throwPopup("Failed to disconnect " + e.getMessage());
        }
    }


    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            if (readMode) {
                Message message = Message.obtain();
                message.arg1 = 14;
                message.obj = characteristic;
                myHandler.sendMessage(message);
            }
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            Message message = Message.obtain();
            if (newState == BluetoothProfile.STATE_CONNECTED){
                message.arg1 = 2;
                myHandler.sendMessage(message);
            } else {

            }

        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            mServices = gatt.getServices();
            Message message = Message.obtain();
            message.arg1 = 3;
            myHandler.sendMessage(message);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (readMode) {
                Message message = Message.obtain();
                message.arg1 = 13;
                message.obj = characteristic;
                myHandler.sendMessage(message);
            }
        }
    };

    public void setCharacteristic(){
            //Enabling notifications for all notify characteristics
            for (int i=0; i<myNotifyList.size(); i++) {
                UUID serviceUUID = myNotifyList.get(i).getService();
                UUID charUUID = myNotifyList.get(i).getCharx();
                try {
                    BluetoothGattCharacteristic characteristic = mConnectedGatt.getService(serviceUUID).getCharacteristic(charUUID);
                    mConnectedGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor mDescriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                    Boolean descSet = mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    Boolean descWrite = mConnectedGatt.writeDescriptor(mDescriptor);
                } catch (Exception e){
                    throwPopup("Failed to subscribe for "+charUUID.toString()+"\n"+e.getMessage());
                }
            }
            readMode = true;

            /*
            //Enabling read for write characteristics
            it = myWriteCharMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                UUID serviceUUID = (UUID) pair.getKey();
                UUID charUUID = (UUID) pair.getValue();
                try {
                    BluetoothGattCharacteristic characteristic = mConnectedGatt.getService(serviceUUID).getCharacteristic(charUUID);
                    mConnectedGatt.setCharacteristicNotification(characteristic, true);
                    it.remove();
                } catch (Exception e){
                    throwPopup("Failed to start read for "+charUUID.toString()+"\n"+e.getMessage());
                }
            }
            */

    }

    public void writeMChar(UUID xid, int value){
        Boolean found = false;
        for (int i=0; i<myWriteList.size(); i++){
            UUID serviceUUID = myWriteList.get(i).getService();
            UUID charUUID = myWriteList.get(i).getCharx();
            if (charUUID.equals(xid)){
                found = true;
                BluetoothGattCharacteristic characteristic = mConnectedGatt.getService(serviceUUID).getCharacteristic(charUUID);
                //byte[] bytes = new byte[] {(byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value};
                try {
                    byte b = (byte) value;
                    byte[] bytes = new byte[]{b};
                    characteristic.setValue(bytes);
                    mConnectedGatt.writeCharacteristic(characteristic);
                    return;
                } catch (Exception e){
                    throwPopup("Write Error: "+e.getMessage());
                    return;
                }
            }
        }
        if (!found) {
            throwPopup("Write Error: Character found = " + found.toString());
        }
    }

    public void readCharVal(UUID uuid){
        for (int i=0; i<myWriteList.size(); i++) {
            UUID serviceUUID = myWriteList.get(i).getService();
            UUID charUUID = myWriteList.get(i).getCharx();
            if (charUUID.equals(uuid)) {
                try {
                    BluetoothGattCharacteristic characteristic = mConnectedGatt.getService(serviceUUID).getCharacteristic(charUUID);
                    mConnectedGatt.readCharacteristic(characteristic);
                    return;
                } catch (Exception e){
                    throwPopup("Reading Error: "+e.getMessage());
                    return;
                }
            }
        }
        throwPopup("Error Reading: Not found "+uuid.toString());
    }



    private void throwPopup(String msg){
        ErrorPopup popup = new ErrorPopup(myContext, msg);
    }
}
