package com.ssu.sangjunianjuni.smartbabycare.BlunoBluetooth;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ssu.sangjunianjuni.smartbabycare.R;

public abstract  class BlunoLibrary  extends AppCompatActivity{

    //controlactivity
    private RFduinoService rfduinoService;
    private String mDeviceName;
    protected String mDeviceAddress;
    String TAG="er";
    //Button btnBeep, btnDisconnected;
    //TextView txtDevName, txtDevAddr, txtConnectState;
    private boolean mConnected = false;
    private Handler mHandler;
    SoundPool sp;
    int mid;

    private Context mainContext=this;


//	public BlunoLibrary(Context theContext) {
//
//		mainContext=theContext;
//	}

    //public abstract void onConectionStateChange(connectionStateEnum theconnectionStateEnum);
    public abstract void onSerialReceived(String thestring);
    public void serialSend(String theString){
        //mSCharacteristic.setValue(theString);
        rfduinoService.send(theString.getBytes());
        //rfduinoService.writeCharacteristic(mSCharacteristic);
    }

    private int mBaudrate=115200;	//set the default baud rate to 115200
    private String mPassword="AT+PASSWOR=DFRobot\r\n";


    private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";

//	byte[] mBaudrateBuffer={0x32,0x00,(byte) (mBaudrate & 0xFF),(byte) ((mBaudrate>>8) & 0xFF),(byte) ((mBaudrate>>16) & 0xFF),0x00};;


    public void serialBegin(int baud){
        mBaudrate=baud;
        mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
    //BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    protected LeDeviceListAdapter mLeDeviceListAdapter=null;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning =false;
    protected AlertDialog mScanDeviceDialog;
    //public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
    //public connectionStateEnum mConnectionState = connectionStateEnum.isNull;
    private static final int REQUEST_ENABLE_BT_INIT = 1, REQUEST_ENABLE_BT_ONMAIN=2;;

    /*private Runnable mConnectingOverTimeRunnable=new Runnable(){

        @Override
        public void run() {
            if(mConnectionState==connectionStateEnum.isConnecting)
                mConnectionState=connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            mBluetoothLeService.close();
        }};

    private Runnable mDisonnectingOverTimeRunnable=new Runnable(){

        @Override
        public void run() {
            if(mConnectionState==connectionStateEnum.isDisconnecting)
                mConnectionState=connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            mBluetoothLeService.close();
        }};*/

    /*public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";*/

    public void onCreateProcess()
    {
        if(!initiate())
        {
            //Toast.makeText(mainContext, R.string.error_bluetooth_not_supported,Toast.LENGTH_SHORT).show();
            ((Activity) mainContext).finish();
        }

        //controlactivity
        mHandler = new Handler();
        sp = new SoundPool( 1, AudioManager.STREAM_MUSIC, 0);
        mid = sp.load(this, R.raw.beep, 1);

        Intent gattServiceIntent = new Intent(this, RFduinoService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        // Initializes and show the scan Device Dialog
        mScanDeviceDialog = new AlertDialog.Builder(mainContext)
                .setTitle("BLE Device Scan...").setAdapter(mLeDeviceListAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(which);
                        if (device == null)
                            return;
                        scanLeDevice(false);

                        if(device.getName()==null || device.getAddress()==null)
                        {
                            //Toast.makeText(getApplicationContext(), "wrong device", Toast.LENGTH_SHORT).show();
                            //mConnectionState=connectionStateEnum.isToScan;
                            //onConectionStateChange(mConnectionState);
                        }
                        else{

                            System.out.println("onListItemClick " + device.getName().toString());

                            System.out.println("Device Name:"+device.getName() + "   " + "Device Name:" + device.getAddress());

                            mDeviceName=device.getName().toString();
                            mDeviceAddress=device.getAddress().toString();

                            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                            if (rfduinoService != null) {
                                final boolean result = rfduinoService.connect(mDeviceAddress);
                                //Toast.makeText(getApplicationContext(), "connect request result:"+result, Toast.LENGTH_SHORT).show();
                                //Log.d(TAG, "Connect request result=" + result);
                            }
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        System.out.println("mBluetoothAdapter.stopLeScan");

                        //mConnectionState = connectionStateEnum.isToScan;
                        //onConectionStateChange(mConnectionState);
                        mScanDeviceDialog.dismiss();

                        scanLeDevice(false);
                    }
                }).create();

    }

    //스마트 밴드 바로 연결을 위한 클래스
    public void connectDirectSmartBand(){
        Toast.makeText(getApplicationContext(), "direct connect", Toast.LENGTH_SHORT).show();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_ONMAIN);
                //블루투스가 안켜져있는 경우 여기서 다이얼로그로 넘어감, 다이얼로그가 안뜨고 블루투스 on 후 바로 HR Band에 연결되도록 수정 필요
            }
        }
        else{
            scanLeDevice(true);
            String DeviceName, DeviceAddress;
            DeviceName="HR Band";
            DeviceAddress="C9:8C:C2:90:F8:9B";
            mDeviceAddress=DeviceAddress;
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            if (rfduinoService != null) {
                final boolean result = rfduinoService.connect(DeviceAddress);
                //Toast.makeText(getApplicationContext(), "connect request result:"+result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Direct Connect request result=" + result);
            }
        }

        mainContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }
    //블루투스가 꺼져있으면 블루투스 켤지 다이얼로그 출력
    public void onResumeforOnCreate(){
        //Toast.makeText(getApplicationContext(), "onresumeforoncreate", Toast.LENGTH_SHORT).show();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                //Toast.makeText(getApplicationContext(), "not connected?", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_INIT);
            }
        }

        mainContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }



    //controlactivity
    public void onResumeProcess() {
        System.out.println("BlUNOActivity onResume");
        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_ONMAIN);
            }
        }
        else{
            /*registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            if (rfduinoService != null) {
                final boolean result = rfduinoService.connect(mDeviceAddress);
                Toast.makeText(getApplicationContext(), "connect request: "+result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Connect request result=" + result);
            }*/
            scanLeDevice(true);
            mScanDeviceDialog.show();
        }

        mainContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }

//controlactivity
    public void onPauseProcess() {
        System.out.println("BLUNOActivity onPause");
        scanLeDevice(false);
        //mainContext.unregisterReceiver(mGattUpdateReceiver);
        //mLeDeviceListAdapter.clear();
        //mScanDeviceDialog.dismiss();
        mSCharacteristic=null;

    }

//controlactivity
    public void onStopProcess() {
        System.out.println("MiUnoActivity onStop");
        mSCharacteristic=null;
    }

    //controlactivity
    public void onDestroyProcess() {
        //scanLeDevice(false);
        //mainContext.unregisterReceiver(mGattUpdateReceiver);
        //mLeDeviceListAdapter.clear();
        //mScanDeviceDialog.dismiss();
        unbindService(mServiceConnection);
        //mSCharacteristic=null;
        rfduinoService = null;
    }


    public void onActivityResultProcess(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT_INIT
                && resultCode == Activity.RESULT_CANCELED) {
            //Toast.makeText(getApplicationContext(), "enable bt init", Toast.LENGTH_SHORT).show();
            //((Activity) mainContext).finish();
            return;
        }
        else if(requestCode==REQUEST_ENABLE_BT_ONMAIN){
            if(resultCode==Activity.RESULT_OK){
                //Toast.makeText(getApplicationContext(), "enable bt onmain", Toast.LENGTH_SHORT).show();
                scanLeDevice(true);
                mScanDeviceDialog.show();
            }
        }
        /*if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            ((Activity) mainContext).finish();
            return;
        }
        else if(requestCode==REQUEST_ENABLE_BT&&resultCode==Activity.RESULT_OK){
            //Toast.makeText(getApplicationContext(), "enable bt", Toast.LENGTH_SHORT).show();
        }*/
    }

    boolean initiate()
    {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!mainContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) mainContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    //controlactivity
    private void updateConnectionState() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mConnected?"Connected":"Disconnected", Toast.LENGTH_SHORT).show();
                //txtConnectState.setText(mConnected ? "Connected" : "Disconnected");
            }
        });
    }

    //controlactivity
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Toast.makeText(getApplicationContext(), "action: "+action, Toast.LENGTH_SHORT).show();
            if (RFduinoService.ACTION_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState();
            } else if (RFduinoService.ACTION_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState();
            } else if (RFduinoService.ACTION_DATA_AVAILABLE.equals(action)) {
                onSerialReceived(intent.getStringExtra(RFduinoService.EXTRA_DATA));
                /*String result=intent.getStringExtra(RFduinoService.EXTRA_DATA);
                Toast.makeText(getApplicationContext(), "display:"+result, Toast.LENGTH_SHORT).show();
                onSerialReceived(result);
                System.out.println("displayData "+intent.getStringExtra(RFduinoService.EXTRA_DATA));*/

                /*byte[] data=intent.getByteArrayExtra(RFduinoService.EXTRA_DATA);
                if(data!=null && data.length>=1 && data[0]==1) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            sp.play(mid, 1f,1f, 0, 0, 1f);
                        }
                    });
                }
                //Toast.makeText(getApplicationContext(), "data: "+data.toString(), Toast.LENGTH_SHORT).show();
                onSerialReceived(new String(data));*/
                /*int data=intent.getIntExtra(RFduinoService.EXTRA_DATA, 0);
                if(data!=0){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            sp.play(mid, 1f,1f, 0, 0, 1f);
                        }
                    });
                }
                onSerialReceived(data);*/
            }
        }
    };

    /*void buttonScanOnClickProcess()
    {
        switch (mConnectionState) {
            case isNull:
                mConnectionState=connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                scanLeDevice(true);
                mScanDeviceDialog.show();
                break;
            case isToScan:
                mConnectionState=connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                scanLeDevice(true);
                mScanDeviceDialog.show();
                break;
            case isScanning:

                break;

            case isConnecting:

                break;
            case isConnected:
                mBluetoothLeService.disconnect();
                mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);

//			mBluetoothLeService.close();
                mConnectionState=connectionStateEnum.isDisconnecting;
                onConectionStateChange(mConnectionState);
                break;
            case isDisconnecting:

                break;

            default:
                break;
        }


    }*/

    void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.

            System.out.println("mBluetoothAdapter.startLeScan");

            if(mLeDeviceListAdapter != null)
            {
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
            }

            if(!mScanning)
            {
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if(mScanning)
            {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    // Code to manage Service lifecycle.
    //controlactivity
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            rfduinoService = ((RFduinoService.LocalBinder) service).getService();
            if (!rfduinoService.initialize()) {
                Log.e("er", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            rfduinoService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            rfduinoService = null;
        }
    };

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            ((Activity) mainContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("mLeScanCallback onLeScan run ");
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    /*private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        mModelNumberCharacteristic=null;
        mSerialPortCharacteristic=null;
        mCommandCharacteristic=null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            System.out.println("displayGattServices + uuid="+uuid);

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(ModelNumberStringUUID)){
                    mModelNumberCharacteristic=gattCharacteristic;
                    System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
                }
                else if(uuid.equals(SerialPortUUID)){
                    mSerialPortCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
                else if(uuid.equals(CommandUUID)){
                    mCommandCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
            }
            mGattCharacteristics.add(charas);
        }

        if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
            Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
            mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
        }
        else {
            mSCharacteristic=mModelNumberCharacteristic;
            mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
            mBluetoothLeService.readCharacteristic(mSCharacteristic);
        }

    }*/

    //controlactivity
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RFduinoService.ACTION_CONNECTED);
        intentFilter.addAction(RFduinoService.ACTION_DISCONNECTED);
        intentFilter.addAction(RFduinoService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator =  ((Activity) mainContext).getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.bluetooth_device_listitem, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                System.out.println("mInflator.inflate  getView");
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }


}