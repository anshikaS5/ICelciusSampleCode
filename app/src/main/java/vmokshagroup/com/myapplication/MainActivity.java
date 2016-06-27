package vmokshagroup.com.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.BoolRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mSensorIdText, mSensorIdValue, mMac_address_text, mMac_address_value, mTimestamptext, mTimestampValue,
            mBateryText, mBateryValue, mSensor_type_value, mSensor_type_text, mCommMode_value, mRssi_text, mBaterry_mode_text, mBaterry_mode_value,
            mCommMode_text, mRssi_value, mCodeVersion_text, mCodeVersion_value,
            mHumSHT_text, mHumSHT_value, mTemp2_text, mTemp2_value, mTemp1_value, mProbname_value, mProbname_text;
    String key, value, AccessPointMessage, PasswordValue;
    LinearLayout mTemp2_linear, mHumidity_linear, mProbname_linear, mSsensor_id_linear, mProbIdLinear, mRssi_linear,
            mBaterry_Mode_Linear,
            mHumidityLinear, mTemp1_linear, mButton_linear, mScanIcBlueLinear, mMain_LinearLayout, mSwitch_batteryMode_linear;
    float tempvalue1, tempvalue2, humidityvalue;
    Integer communicationModeValue, sensorIdValue;
    Button mSend_button, mCheck_access_point, mScanIcBlueBtn, mYes_button, mNo_button, mSwitch_batteryMode_button;
    public String ipAddress;
    Thread ScanApThread, changeDirecttoInfraThread, PromtPassForOnethread;
    /*DatagramSocket clientSocket;*/
    public static DatagramPacket socketPacket;
    Dialog ShowApDialog, showPasswordDialog, ConnectBluetoothDialog, ScanLeDialog;
    ListView mSSIDListView, mLeDevice_list;
    TextView mSSID_Text, mSSID_Value;
    /* ArrayList<String> mArray;*/
    ArrayList<SSIDDetailsModel> mArray;
    EditText mPassEdittext;
    public static int Position;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    boolean isNetworkExist = false;
    private int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothManager bluetoothManager;
    ProgressDialog progressDialog;

    private static final int ACCESS_COARSE_LOCATION_PERMISSION = 1;
    private static final int ACCESS_FINE_LOCATION_PERMISSION = 2;
    LocationManager locationManager;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    // private ScanSettings settings;
    // private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    /* ArrayList<String> BLEScanList;*/
    ArrayList<BluetoothDevice> BLEScanList;
    /* ArrayAdapter<BluetoothDevice> bleListAdapter;*/
    ScanSettings settings;
    ICBlueCustomAdapter icblueAdapter;
    BluetoothDevice itemValue;
    static String macAddress;
    static String ACTION_BOND_STATE_CHANGED = "ACTION_BOND_STATE_CHANGED";
    UUID uuid = UUID.fromString("07811600-dc0e-11e3-b754-0002a5d5c51b");
    static String DEVICE_CONNECTED = "DeviceConnected";
    static String DEVICE_DISCONNECTED = "DeviceDisConnected";
    static String RSSI_STATUS = "Rssi_status";
    static String BATTERY_DATA = "BATTERY_DATA";
    static String BATTERY_MODE_DATA = "BATTERY_MODE_DATA";
    static String PRODUCT_ID = "PRODUCT_ID";
    static String TEMPERATURE = "TEMPERATURE";

    //TODO:
    public static final UUID PRESSURE_SERVICE = UUID.fromString("07811600-dc0e-11e3-b754-0002a5d5c51b");
    public static final UUID PRESSURE_DATA_CHAR = UUID.fromString("b46e6240-dc0f-11e3-b30d-0002a5d5c51b");
    public static final UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_CHAR = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_MODE_CHAR = UUID.fromString("aeedff60-dc0f-11e3-831b-0002a5d5c51b");
    public static final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public static final String ACTION_BATTERY_MODE = "ACTION_BATTERY_MODE";
    public static final String ACTION_BATTERY = "ACTION_BATTERY";

    private byte ModeByte;
    private byte[] batteryModeArray;
    boolean isWriteMode = false;


    Intent intent;
    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArray = new ArrayList<SSIDDetailsModel>();
        startService(new Intent(getBaseContext(), MyService.class));
        initializeUi();


        final IntentFilter filter = new IntentFilter();
        filter.addAction(MyService.UDP_BROADCAST);
        filter.addAction(MyService.UDP_ACCESS_Point);
        filter.addAction(DEVICE_CONNECTED);
        filter.addAction(DEVICE_DISCONNECTED);
        filter.addAction(RSSI_STATUS);
        filter.addAction(ACTION_BATTERY);
        filter.addAction(ACTION_BATTERY_MODE);
        filter.addAction(ACTION_DATA_AVAILABLE);

        progressDialog = new ProgressDialog(MainActivity.this);

        registerReceiver(receiver, filter);
        sharedPreferences = getApplicationContext().getSharedPreferences(ModelClass.Preference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        BLEScanList = new ArrayList<BluetoothDevice>();


        //TODO:check ble is supported or not

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mHandler = new Handler();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    private void initializeUi() {
        mSensorIdText = (TextView) findViewById(R.id.sensorIdText);
        mSsensor_id_linear = (LinearLayout) findViewById(R.id.sensor_id_linear);
        mMac_address_text = (TextView) findViewById(R.id.mac_address_text);
        //  mTimestamptext = (TextView) findViewById(R.id.timestamptext);
        mBateryText = (TextView) findViewById(R.id.bateryText);
        mSensor_type_text = (TextView) findViewById(R.id.sensor_type_text);
        mCommMode_text = (TextView) findViewById(R.id.commMode_text);
        /*mCodeVersion_text = (TextView) findViewById(R.id.codeVersion_text);*/
        mHumSHT_text = (TextView) findViewById(R.id.humSHT_text);
        mTemp1_value = (TextView) findViewById(R.id.temp1_text);

        mTemp2_linear = (LinearLayout) findViewById(R.id.temp2_linear);
        mTemp2_text = (TextView) findViewById(R.id.temp2_text);
        mTemp2_value = (TextView) findViewById(R.id.temp2_value);


        mSensorIdValue = (TextView) findViewById(R.id.sensorIdValue);
        mMac_address_value = (TextView) findViewById(R.id.mac_address_value);
        // mTimestampValue = (TextView) findViewById(R.id.timestampValue);
        mBateryValue = (TextView) findViewById(R.id.bateryValue);
        mSensor_type_value = (TextView) findViewById(R.id.sensor_type_value);
        mCommMode_value = (TextView) findViewById(R.id.commMode_value);
       /* mCodeVersion_value = (TextView) findViewById(R.id.codeVersion_value);*/
        mHumidity_linear = (LinearLayout) findViewById(R.id.humSHT_linear);
        mHumSHT_value = (TextView) findViewById(R.id.humSHT_value);
        mTemp1_value = (TextView) findViewById(R.id.temp1_value);
        mRssi_value = (TextView) findViewById(R.id.rssi_value);
        mProbname_linear = (LinearLayout) findViewById(R.id.probname_linear);
        mProbIdLinear = (LinearLayout) findViewById(R.id.sensor_type_linear);
        mHumidityLinear = (LinearLayout) findViewById(R.id.humSHT_linear);

        mTemp1_linear = (LinearLayout) findViewById(R.id.temp1_linear);

        mProbname_value = (TextView) findViewById(R.id.probname_value);
        mProbname_text = (TextView) findViewById(R.id.probname_text);

        //TODO:
        mMain_LinearLayout = (LinearLayout) findViewById(R.id.main_layout);
        //Update the ICBLue UI

        mRssi_linear = (LinearLayout) findViewById(R.id.rssi_linear);
        mRssi_text = (TextView) findViewById(R.id.rssi_linear_text);

        mBaterry_Mode_Linear = (LinearLayout) findViewById(R.id.baterry_mode_linear);
        mBaterry_mode_text = (TextView) findViewById(R.id.baterry_mode_text);
        mBaterry_mode_value = (TextView) findViewById(R.id.baterry_mode_value);

        //battery mode
        mSwitch_batteryMode_linear = (LinearLayout) findViewById(R.id.switch_batteryMode_linear);
        mSwitch_batteryMode_button = (Button) findViewById(R.id.switch_batteryMode_button);
        mSwitch_batteryMode_button.setOnClickListener(this);

        //TODO:change to infra mode

        mButton_linear = (LinearLayout) findViewById(R.id.button_linear);
        mSend_button = (Button) findViewById(R.id.send_button);
        mSend_button.setOnClickListener(this);

        //TODO:change for icblue
        mScanIcBlueLinear = (LinearLayout) findViewById(R.id.scan_ic_blue_linear);
        mScanIcBlueBtn = (Button) findViewById(R.id.scan_ic_blue_btn);

        mScanIcBlueBtn.setOnClickListener(this);
        ShowApDialog = new Dialog(this);
        ShowApDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ShowApDialog.setContentView(R.layout.ssid_dialog);
        ShowApDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ShowApDialog.setCancelable(true);


        //TODO:Enable Bluetooth dialog


        ConnectBluetoothDialog = new Dialog(this);
        ConnectBluetoothDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConnectBluetoothDialog.setContentView(R.layout.enable_bluetooth);
        ConnectBluetoothDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ConnectBluetoothDialog.setCancelable(true);

        mYes_button = (Button) ConnectBluetoothDialog.findViewById(R.id.yes_button);
        mNo_button = (Button) ConnectBluetoothDialog.findViewById(R.id.no_button);
        mYes_button.setOnClickListener(this);
        mNo_button.setOnClickListener(this);


        //TODO: showPasswordDialog

        showPasswordDialog = new Dialog(this);
        showPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showPasswordDialog.setContentView(R.layout.ssid_password_dialog);
        showPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showPasswordDialog.setCancelable(true);
        mCheck_access_point = (Button) showPasswordDialog.findViewById(R.id.check_access_point);
        mPassEdittext = (EditText) showPasswordDialog.findViewById(R.id.pass_edittext);

        mCheck_access_point.setOnClickListener(this);

        //TODO:Show the dialog box for icble devices

        ScanLeDialog = new Dialog(this);
        ScanLeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScanLeDialog.setContentView(R.layout.icblue_device_dialog);
        ScanLeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ScanLeDialog.setCancelable(true);
        mLeDevice_list = (ListView) ScanLeDialog.findViewById(R.id.icble_device_list);


    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //final String action = intent.getAction();
            String action = intent.getAction();

            if (intent.getAction().equals(MyService.UDP_BROADCAST)) {
               /* mSensorIdValue.put*/
                Log.e("BroadcastReceiver message", intent.getStringExtra("message"));
                ipAddress = intent.getStringExtra("sender");

                String message = intent.getStringExtra("message");
                Log.e("BroadcastReceiver message", message);
                String[] separated = message.split("&");

                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < separated.length; i++) {
                    String[] s = separated[i].split("=");
                    map.put(s[0], s[1]);
                }


                communicationModeValue = Integer.parseInt(map.get("commMode"));
                //TODO:Checking communication mode for updating the ui
                if ((Integer.parseInt(map.get("commMode"))) == 1) {


                    mSensorIdValue.setText(map.get("SensorID"));
                    mMac_address_value.setText(map.get("Mac"));

                    if ((map.get("sensorType") != null) && (!map.get("sensorType").isEmpty())) {

                        if (map.get("sensorType").contentEquals("0")) {
                            mSensor_type_value.setText("0");
                            mProbname_value.setText("No probe Connected");
                        } else {
                            mSensor_type_value.setText(map.get("sensorType"));
                            sensorIdValue = Integer.parseInt(map.get("sensorType"));
                            if (sensorIdValue == 102) {
                                mProbname_value.setText("RH");
                            } else if (sensorIdValue == 106) {
                                mProbname_value.setText("Inspection");
//                        mHumidityLinear.setVisibility(View.GONE);
                            } else if (sensorIdValue == 105) {
                                mProbname_value.setText("BBQ Dual ");
//                        mHumidityLinear.setVisibility(View.GONE);
                            }
                        }

                        //TODO:for checking the sensor id

                    }

                    Float batery = (Float.parseFloat(map.get("battery"))) / 1000;
                    float baterryInVolt = round(batery);
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    decimalFormat.format(Double.parseDouble(String.valueOf(baterryInVolt)));
                    mBateryValue.setText(baterryInVolt + " v");
                    mRssi_value.setText(map.get("RSSI"));

                    communicationModeValue = Integer.parseInt(map.get("commMode"));
                    if (communicationModeValue == 1) {
                        mCommMode_value.setText("Direct Mode");
                    } else if (communicationModeValue == 2) {
                        mCommMode_value.setText("Infra Mode");
                    } else if (communicationModeValue == 3) {
                        mCommMode_value.setText("Remote Mode");

                    }

               /* //TODO:Formula to calculate tempSHT
                round(((Float.parseFloat(x.get("tempSHT")) / 100) - 250));*/


                    if (map.get("tempSHT") != null && !map.get("tempSHT").isEmpty()) {

                        mTemp2_linear.setVisibility(View.GONE);
                        mTemp1_linear.setVisibility(View.VISIBLE);
                        mHumidity_linear.setVisibility(View.VISIBLE);
//                    mTemp2_linear.setVisibility(View.VISIBLE);
//                    mTemp1_linear.setVisibility(View.VISIBLE);
                        //TODO:Formula to calculate tempSHT
                        tempvalue1 = round(((Float.parseFloat(map.get("tempSHT")) / 100) - 250));


                        mTemp1_value.setText(tempvalue1 + "" + getResources().getString(R.string.degree_celsius));
//                    mTemp2_linear.setVisibility(View.GONE);

                        //TODO: Formula to calculate humSHT
                        humidityvalue = round((Float.parseFloat(map.get("humSHT")) / 100));
                        mHumSHT_value.setText(humidityvalue + "%");
                        mTemp2_linear.setVisibility(View.GONE);


                    } else if ((map.get("temp1") != null && map.get("temp2") != null) && (!map.get("temp1").isEmpty() && !map.get("temp2").isEmpty())) {

                        //TODO:Formula to calculate tempSHT
                        mTemp2_linear.setVisibility(View.VISIBLE);
                        mTemp1_linear.setVisibility(View.VISIBLE);
                        mHumidity_linear.setVisibility(View.GONE);
                        tempvalue1 = round(((Float.parseFloat(map.get("temp1")) / 100) - 250));
                        mTemp1_value.setText(tempvalue1 + "" + getResources().getString(R.string.degree_celsius));

                        tempvalue2 = round(((Float.parseFloat(map.get("temp2")) / 100) - 250));

                        mTemp2_value.setText(tempvalue2 + "" + getResources().getString(R.string.degree_celsius));

                    } else if ((map.get("temp1") != null && !map.get("temp1").isEmpty())) {
                        mTemp2_linear.setVisibility(View.GONE);
                        mTemp1_linear.setVisibility(View.VISIBLE);
                        mHumidity_linear.setVisibility(View.GONE);
                        tempvalue1 = round(((Float.parseFloat(map.get("temp1")) / 100) - 250));
                        mTemp1_value.setText(tempvalue1 + "" + getResources().getString(R.string.degree_celsius));
//                    mTemp2_value.setVisibility(View.GONE);
//                    mHumidity_linear.setVisibility(View.GONE);
                    } else {
                        mHumidityLinear.setVisibility(View.GONE);
                        mTemp1_linear.setVisibility(View.GONE);
                        mTemp2_linear.setVisibility(View.GONE);
                    }

                } else if (Integer.parseInt(map.get("commMode")) == 2) {

                    String InfraModeSensorIdValue = sharedPreferences.getString(ModelClass.InfraModeSensorId, "");
                    if (InfraModeSensorIdValue.contains(map.get("SensorID"))) {

                        UpdateUiforInfra(map);

                    }
                }


            } else if (intent.getAction().equals(MyService.UDP_ACCESS_Point)) {

                // Log.e("Access points", intent.getStringExtra("message"));
                ipAddress = intent.getStringExtra("sender");
                AccessPointMessage = intent.getStringExtra("message");
                // Log.e("AccessPoint message", AccessPointMessage);
               /* String[] separated = AccessPointMessage.split("&");*/


                if (AccessPointMessage.contains("SSID")) {
                    if (!ShowApDialog.isShowing())
                        showDialogforSSID();

                }

            } else if (intent.getAction().equals(DEVICE_CONNECTED)) {
                Log.e("DEVICE_CONNECTED", "STATE_CONNECTED");
                Toast.makeText(getApplicationContext(), "Device Connected", Toast.LENGTH_SHORT).show();
                mSwitch_batteryMode_linear.setVisibility(View.VISIBLE);
                mScanIcBlueBtn.setText("DisConnected");

            } else if (intent.getAction().equals(DEVICE_DISCONNECTED)) {
                Log.e("DEVICE_DISCONNECTED", "STATE_CONNECTED");
                mScanIcBlueBtn.setText("CHANGE TO ICBLUE");
                mSwitch_batteryMode_linear.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "Device DisConnected", Toast.LENGTH_SHORT).show();


            } else if (intent.getAction().equals(RSSI_STATUS)) {
                Log.e("Rssi_status", intent.getExtras().get("RSSI_VALUE") + "");
                intent.getExtras().get("RSSI_VALUE");
                mRssi_linear.setVisibility(View.VISIBLE);

                mRssi_value.setText(intent.getExtras().get("RSSI_VALUE") + " dBm");


            } else if (intent.getAction().equals(BATTERY_DATA)) {

                Log.e("BATTERY_DATA", intent.getExtras().get("BATTERY_DATA") + " v");


            } else if (intent.getAction().equals(ACTION_BATTERY)) {

                Log.i("BATTERY_LEVEL_CHAR", intent.getExtras().get(BATTERY_DATA) + "");
                intent.getExtras().get(BATTERY_DATA);
                mBateryValue.setText(intent.getExtras().get(BATTERY_DATA) + "v");
                mGatt.readRemoteRssi();


            } else if (intent.getAction().equals(ACTION_BATTERY_MODE)) {
                Log.i("ACTION_BATTERY_MODE", intent.getExtras().get(BATTERY_MODE_DATA) + "");
                String baterrymode = intent.getStringExtra(BATTERY_MODE_DATA);
                mBaterry_Mode_Linear.setVisibility(View.VISIBLE);
                if (baterrymode.contentEquals("1")) {
                    mBaterry_mode_value.setText("Always On");
                } else {
                    mBaterry_mode_value.setText("Battery Save");
                }

                // mBateryValue.setText(intent.getExtras().get(BATTERY_MODE_DATA) + "");
            } else if (intent.getAction().equals(ACTION_DATA_AVAILABLE)) {
                Log.i("ACTION_DATA_AVAILABLE", intent.getExtras().get(ACTION_DATA_AVAILABLE) + "");
                int productId = intent.getIntExtra(PRODUCT_ID, 0);
                float temp = intent.getFloatExtra(TEMPERATURE, 0);
                mTemp1_linear.setVisibility(View.VISIBLE);
                if (productId == 202) {
                    mProbname_linear.setVisibility(View.VISIBLE);
                    mProbname_value.setText("iCBlue Pro");
                }

                mTemp1_value.setText(temp + "" + getResources().getString(R.string.degree_celsius));
               /* mSensorID*/
                mProbIdLinear.setVisibility(View.VISIBLE);
                mSensor_type_value.setText(productId + "");

                mMac_address_value.setText(macAddress);
                mCommMode_value.setText("Bluetooth");
                mSsensor_id_linear.setVisibility(View.GONE);
                mHumidity_linear.setVisibility(View.GONE);

                mTemp2_linear.setVisibility(View.GONE);
            }


        }
    };

    public static float round(float value) {
        int tempValue;
        tempValue = Math.round(value * 10f);
        return tempValue / 10f;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getBaseContext(), MyService.class));
        unregisterReceiver(receiver);

        if (mGatt == null) {
            return;
        }
        mGatt.disconnect();
        mGatt.close();
        mGatt = null;
        try {
            unregisterReceiver(mReceiverforIcblue);
        } catch (Exception e) {

        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_button:

                //TODO:check communication mode ,if it is direct then scan the network else dont't scan
                if (communicationModeValue == 1) {
                    try {

                        final InetAddress broadcastIP = InetAddress.getByName(ipAddress);

                        ScanApThread = new Thread(new Runnable() {
                            public void run() {
                                try {

                                    DatagramSocket clientSocket = new DatagramSocket(null);
                                    String messageStr = "ScanAp=1&";
                                    int server_port = 50001;
                                    InetAddress local = InetAddress.getByName(broadcastIP.getHostAddress());
                                    int msg_length = messageStr.length();
                                    byte[] message = messageStr.getBytes();
                                    clientSocket.setReuseAddress(true);
                                    clientSocket.setBroadcast(true);
                                    clientSocket.bind(new InetSocketAddress(server_port));
                                    DatagramPacket sendPacket = new DatagramPacket(message, msg_length, local, server_port);
                                    clientSocket.send(sendPacket);
                                    clientSocket.disconnect();
                                    clientSocket.close();
                                    //  Log.e("messageStr", messageStr);


                                } catch (Exception e) {
                                    Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                                }
                            }
                        });
                        ScanApThread.start();
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Sensor is in Inframode", Toast.LENGTH_SHORT).show();
                }


                break;

            case R.id.check_access_point:
                PasswordValue = mPassEdittext.getText().toString().trim();
                showPasswordDialog.dismiss();
                if (PasswordValue != null && !PasswordValue.isEmpty()) {

                    // TODO:Command to change sensor from direct to infra


                    try {

                        final InetAddress broadcastIP = InetAddress.getByName(ipAddress);

                        changeDirecttoInfraThread = new Thread(new Runnable() {
                            public void run() {
                                try {

                                    DatagramSocket socket = new DatagramSocket(null);
                                    String changeDirecttoInframessageStr = "ChangeSSID=" + mArray.get(Position).getSSID() + "&ChangeWpa=" + PasswordValue + "&CommMode=2&ChangeSampling=15&ChangeULP=1&ChangeStoreAndForward=0";
                                    Log.e("changeDirecttoInframessageStr", changeDirecttoInframessageStr + "show");
                                    int server_port = 50001;
                                    InetAddress local = InetAddress.getByName(broadcastIP.getHostAddress());
                                    int msg_length = changeDirecttoInframessageStr.length();
                                    byte[] message = changeDirecttoInframessageStr.getBytes();
                                    socket.setReuseAddress(true);
                                    socket.setBroadcast(true);
                                    socket.bind(new InetSocketAddress(server_port));
                                    DatagramPacket sendPacket = new DatagramPacket(message, msg_length, local, server_port);
                                    socket.send(sendPacket);
                                    socket.disconnect();
                                    socket.close();
                                    //   Log.e("messageStr", changeDirecttoInframessageStr);


                                } catch (Exception e) {
                                    Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                                }
                            }
                        });
                        changeDirecttoInfraThread.start();
                    } catch (Exception e) {

                    }


                    ConnectToWifi();


                }
                break;

            case R.id.scan_ic_blue_btn:

                if (mScanIcBlueBtn.getText().toString().contentEquals("DisConnected")) {
                    mGatt.disconnect();
                    mMac_address_value.setText("");
                    mBateryValue.setText("");
                    mProbname_value.setText("");
                    mSensor_type_value.setText("");
                    mCommMode_value.setText("");
                    mTemp1_value.setText("");
                    mRssi_value.setText("");
                    mBateryValue.setText("");
                    mBaterry_mode_value.setText("");

                } else {
                    //TODO:show Progress dialog
                    if (!progressDialog.isShowing()) {
                        progressDialog.setMessage("Scanning...");
                        progressDialog.show();
                    }

                    BLEScanList = new ArrayList<BluetoothDevice>();
                    if (mBluetoothAdapter == null) {
                        // TODO: Device does not support Bluetooth
                    } else {
                        if (mBluetoothAdapter.isEnabled()) {
                            //Bluetooth enable
                            //TODO:Scan for ble device

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                                getPermissionToReadUserContacts();


                            } else {
                                ScanLeDeviceList(true);
                            }


                            // ScanLeDialog.show();


                        } else {

                            ConnectBluetoothDialog.show();
                            // Bluetooth is not enable :)
                            //TODO:show the dialog to enable the bluetooth

                        }
                    }

                }

                break;

            case R.id.yes_button:
                /*Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);*/

                mBluetoothAdapter.enable();
                ConnectBluetoothDialog.dismiss();


                break;

            case R.id.no_button:
                ConnectBluetoothDialog.dismiss();
                break;

            case R.id.switch_batteryMode_button:

                switchMode(ModeByte);
                break;
        }
    }


    private void showDialogforSSID() {

        ListView mSSIDListView = (ListView) ShowApDialog.findViewById(R.id.ssid_list);
        mArray = new ArrayList<SSIDDetailsModel>();

        String[] separated = AccessPointMessage.split("&");


        if (separated[0].contains("SensorID")) {
            String[] preSensorValue = separated[0].split("=");
            String InfraModeSensorId = preSensorValue[1];
            Log.e("SensorIdValue", InfraModeSensorId);


            editor = sharedPreferences.edit();
            editor.putString(ModelClass.InfraModeSensorId, InfraModeSensorId);
            editor.commit();


        }

        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < separated.length; i++) {

            SSIDDetailsModel ssidDetailsModel = new SSIDDetailsModel();


            if (separated[i].contains("SSID")) {

                String[] s = separated[i].split("=");
                map.put(s[0], s[1]);


                String[] s1 = separated[i + 1].split("=");
                map.put(s1[0], s1[1]);

                String[] s2 = separated[i + 2].split("=");
                map.put(s2[0], s2[1]);
                ssidDetailsModel.setSSID(map.get("SSID"));
                ssidDetailsModel.setChannel(map.get("Channel"));
                ssidDetailsModel.setSecurity(map.get("Security"));
                mArray.add(ssidDetailsModel);

            }


        }


        SSIDDetailsAdapter adapter = new SSIDDetailsAdapter(this, mArray);
        mSSIDListView.setAdapter(adapter);
        ShowApDialog.show();

        mSSIDListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Log.e("Position",Position+"");
                Position = position;

                //Connect to


                if (mArray.get(position).getSecurity().contains("3")) {
                    ShowApDialog.dismiss();
                    showPasswordDialog();
                } else {
                    // ConnectToWifi();
                    PromtPassForOne();
                }


            }
        });

//        }

    }

    private void showPasswordDialog() {

        showPasswordDialog.show();
    }

    private void ConnectToWifi() {


        //   Log.e("ConnectToWifi","ConnectToWifi");
        //TODO: CONNECT TO access point
        String networkSSID = mArray.get(Position).getSSID();
        String networkPass = PasswordValue;

        WifiConfiguration conf = new WifiConfiguration();


        //TODO:
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes


     /*   conf.wepKeys[0] = "\"" + networkPass + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);*/

        //For WPA network you need to add passphrase like this:


        conf.preSharedKey = "\"" + networkPass + "\"";
        //Then, you need to add it to Android wifi manager settings:

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);


        //And finally, you might need to enable it, so Android connects to it:


        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {


            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {

                isNetworkExist = true;

                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();


            }
        }

        if (!isNetworkExist) {
            wifiManager.addNetwork(conf);
        }

    }

    private void PromtPassForOne() {

        try {

            final InetAddress broadcastIP = InetAddress.getByName(ipAddress);

            PromtPassForOnethread = new Thread(new Runnable() {
                public void run() {
                    try {

                        DatagramSocket socket = new DatagramSocket(null);
                        //String changeDirecttoInframessageStr = "ChangeSSID=" + mArray.get(Position).getSSID() + "&ChangeWpa=" + PasswordValue + "&CommMode=2&ChangeSampling=15&ChangeULP=1&ChangeStoreAndForward=0";

                        String changeDirecttoInframessageStr = "ChangeSSID=" + mArray.get(Position).getSSID() + "&CommMode=2&ChangeSampling=15&ChangeULP=1&ChangeStoreAndForward=0";

                        //   Log.e("PasswordValue",PasswordValue+"show" );
                        int server_port = 50001;
                        InetAddress local = InetAddress.getByName(broadcastIP.getHostAddress());
                        int msg_length = changeDirecttoInframessageStr.length();
                        byte[] message = changeDirecttoInframessageStr.getBytes();
                        socket.setReuseAddress(true);
                        socket.setBroadcast(true);
                        socket.bind(new InetSocketAddress(server_port));
                        DatagramPacket sendPacket = new DatagramPacket(message, msg_length, local, server_port);
                        socket.send(sendPacket);
                        socket.disconnect();
                        socket.close();
                        //  Log.e("messageStr", changeDirecttoInframessageStr);


                    } catch (Exception e) {
                        Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                    }
                }
            });
            PromtPassForOnethread.start();
        } catch (Exception e) {

        }


        ConnectToWifi();


    }


    private void UpdateUiforInfra(Map<String, String> map) {

        Log.e("UpdateUiforInfra", "UpdateUiforInfra");

        mSensorIdValue.setText(map.get("SensorID"));
        mMac_address_value.setText(map.get("Mac"));

        if ((map.get("sensorType") != null) && (!map.get("sensorType").isEmpty())) {

            if (map.get("sensorType").contentEquals("0")) {
                mSensor_type_value.setText("0");
                mProbname_value.setText("No probe Connected");
            } else {
                mSensor_type_value.setText(map.get("sensorType"));
                sensorIdValue = Integer.parseInt(map.get("sensorType"));
                if (sensorIdValue == 102) {
                    mProbname_value.setText("RH");
                } else if (sensorIdValue == 106) {
                    mProbname_value.setText("Inspection");
//                        mHumidityLinear.setVisibility(View.GONE);
                } else if (sensorIdValue == 105) {
                    mProbname_value.setText("BBQ Dual ");
//                        mHumidityLinear.setVisibility(View.GONE);
                }
            }

            //TODO:for checking the sensor id

        }

        Float batery = (Float.parseFloat(map.get("battery"))) / 1000;
        float baterryInVolt = round(batery);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.format(Double.parseDouble(String.valueOf(baterryInVolt)));
        mBateryValue.setText(baterryInVolt + " v");
        mRssi_value.setText(map.get("RSSI"));

        communicationModeValue = Integer.parseInt(map.get("commMode"));
        if (communicationModeValue == 1) {
            mCommMode_value.setText("Direct Mode");
        } else if (communicationModeValue == 2) {
            mCommMode_value.setText("Infra Mode");
        } else if (communicationModeValue == 3) {
            mCommMode_value.setText("Remote Mode");

        }

               /* //TODO:Formula to calculate tempSHT
                round(((Float.parseFloat(x.get("tempSHT")) / 100) - 250));*/


        if (map.get("tempSHT") != null && !map.get("tempSHT").isEmpty()) {

            mTemp2_linear.setVisibility(View.GONE);
            mTemp1_linear.setVisibility(View.VISIBLE);
            mHumidity_linear.setVisibility(View.VISIBLE);
//                    mTemp2_linear.setVisibility(View.VISIBLE);
//                    mTemp1_linear.setVisibility(View.VISIBLE);
            //TODO:Formula to calculate tempSHT
            tempvalue1 = round(((Float.parseFloat(map.get("tempSHT")) / 100) - 250));


            mTemp1_value.setText(tempvalue1 + "" + getResources().getString(R.string.degree_celsius));
//                    mTemp2_linear.setVisibility(View.GONE);

            //TODO: Formula to calculate humSHT
            humidityvalue = round((Float.parseFloat(map.get("humSHT")) / 100));
            mHumSHT_value.setText(humidityvalue + "%");
            mTemp2_linear.setVisibility(View.GONE);


        } else if ((map.get("temp1") != null && map.get("temp2") != null) && (!map.get("temp1").isEmpty() && !map.get("temp2").isEmpty())) {

            //TODO:Formula to calculate tempSHT
            mTemp2_linear.setVisibility(View.VISIBLE);
            mTemp1_linear.setVisibility(View.VISIBLE);
            mHumidity_linear.setVisibility(View.GONE);
            tempvalue1 = round(((Float.parseFloat(map.get("temp1")) / 100) - 250));
            mTemp1_value.setText(tempvalue1 + "" + getResources().getString(R.string.degree_celsius));

            tempvalue2 = round(((Float.parseFloat(map.get("temp2")) / 100) - 250));

            mTemp2_value.setText(tempvalue2 + "" + getResources().getString(R.string.degree_celsius));

        } else if ((map.get("temp1") != null && !map.get("temp1").isEmpty())) {
            mTemp2_linear.setVisibility(View.GONE);
            mTemp1_linear.setVisibility(View.VISIBLE);
            mHumidity_linear.setVisibility(View.GONE);
            tempvalue1 = round(((Float.parseFloat(map.get("temp1")) / 100) - 250));
            mTemp1_value.setText(tempvalue1 + "" + getResources().getString(R.string.degree_celsius));
//                    mTemp2_value.setVisibility(View.GONE);
//                    mHumidity_linear.setVisibility(View.GONE);
        } else {
            mHumidityLinear.setVisibility(View.GONE);
            mTemp1_linear.setVisibility(View.GONE);
            mTemp2_linear.setVisibility(View.GONE);
        }

        Log.e("UpdateUiforInfra", "UpdateUiforInfra");
    }


    private void ScanLeDeviceList(final boolean enable) {

        if (enable) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        // mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        //TODO:I Added runon UI thread to update the ui


                        ICBlueCustomAdapter icBlueCustomAdapter = new ICBlueCustomAdapter(MainActivity.this, BLEScanList);

                        mLeDevice_list.setAdapter(icBlueCustomAdapter);

                        if (BLEScanList.size() != 0) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            ScanLeDialog.show();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(MainActivity.this, "No devices found", Toast.LENGTH_SHORT).show();
                        }
                        mHandler.removeCallbacks(this);


                        // ListView Item Click Listener
                        mLeDevice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                                //TODO:dismiss the dialog after click in listview

                                ScanLeDialog.dismiss();


                                // ListView Clicked item index
                                int itemPosition = position;

                                // ListView Clicked item value

                                // itemValue = (String) mLeDevice_list.getItemAtPosition(position);
                                itemValue = (BluetoothDevice) mLeDevice_list.getItemAtPosition(itemPosition);
                                macAddress = itemValue.getAddress();
                                BLEScanList.get(position);


                                int status = BLEScanList.get(position).getBondState();
                                if (status == BluetoothDevice.BOND_NONE) {
                                    pairDevice(itemValue);
                                    IntentFilter filter1 = new IntentFilter();
                                    filter1.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                                    registerReceiver(mReceiverforIcblue, filter1);
                                    connectToDevice(itemValue);
                                } else {
                                    connectToDevice(itemValue);
                                }


                                Log.e("filter", "filter");
                               /* connectToDevice(itemValue);*/

                            }

                        });

                    }
                }
            }, SCAN_PERIOD);


            //TODO:passing the filter and setting


            List<ScanFilter> scanFilters = new ArrayList<ScanFilter>(1);
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(uuid))
                    .build();
            scanFilters.add(filter);
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .build();


            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, settings, mScanCallback);

        } /*else {
            if (Build.VERSION.SDK_INT < 21) {
                // mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
               *//* mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);*//*

                mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, settings, mScanCallback);
            }
        }*/
    }


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            /*if(BLEScanList.size() != 0) {
                if(!BLEScanList.contains(btDevice)) {
                    BLEScanList.add(btDevice);
                }
            } else {
                BLEScanList.add(btDevice);
            }*/
            //TODO:I commented below method for showing the icpro

            if (BLEScanList.indexOf(btDevice) == -1)
                BLEScanList.add(btDevice);

           /* BLEScanList.add(btDevice.getUuids().toString());*/
//            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };


    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            ScanLeDeviceList(false);// will stop after first device detection
        }
    }

  /*  private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };*/


    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    intent = new Intent(DEVICE_CONNECTED);
                    sendBroadcast(intent);

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");

                    intent = new Intent(DEVICE_DISCONNECTED);
                    sendBroadcast(intent);
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            BluetoothGattCharacteristic batteryLevel = mGatt.getService(PRESSURE_SERVICE).getCharacteristic(BATTERY_MODE_CHAR);
            mGatt.readCharacteristic(batteryLevel);

            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));

            BluetoothGattCharacteristic mLiveCharacter = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_DATA_CHAR);
            if (mLiveCharacter != null) {
                // Enable local notifications
                gatt.setCharacteristicNotification(mLiveCharacter, true);

                waitMethod(500);

                BluetoothGattDescriptor mLiveDesc = mLiveCharacter.getDescriptors().get(0);
                if (mLiveDesc != null) {
                    Log.e(TAG, "Enable Live Indication added to queue");
                    mLiveDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(mLiveDesc);
                }
            }
            mGatt.readRemoteRssi();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());

            if (BATTERY_LEVEL_CHAR.equals(characteristic.getUuid()))
                broadcastUpdate(ACTION_BATTERY, characteristic);
            else if (BATTERY_MODE_CHAR.equals(characteristic.getUuid()))
                broadcastUpdate(ACTION_BATTERY_MODE, characteristic);
            else
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);


//            gatt.disconnect();
        }


        private void broadcastUpdate(final String action,
                                     final BluetoothGattCharacteristic characteristic) {
            Intent intent = new Intent(action);
            Log.e(TAG, "In broadcastUpdate");


            if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
                if (!isWriteMode) {
                    Log.e("PRESSURE_DATA_CHAR", "PRESSURE_DATA_CHAR");
                    BluetoothGattCharacteristic batteryLevel = mGatt.getService(BATTERY_SERVICE).getCharacteristic(BATTERY_LEVEL_CHAR);

                    int miCBlueProductID = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT8, 0);

             /*   For Temperature*/

                    Integer lowerByte = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT8, 4);
                    Integer upperByte = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT8, 3);
                    double t_a = ((upperByte << 8) + lowerByte);
                    double temp = t_a / 100;
                    float temp1 = round((float) temp);
                    intent.putExtra("PRODUCT_ID", miCBlueProductID);
                    intent.putExtra("TEMPERATURE", temp1);
                    sendBroadcast(intent);
                    mGatt.readCharacteristic(batteryLevel);
                }

            } else if (BATTERY_LEVEL_CHAR.equals(characteristic.getUuid())) {
                if (!isWriteMode) {
                    Log.e(TAG, "In broadcastUpdate BATTERY_DATA");

                    String volts = String.valueOf((int) (double) characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                    intent.putExtra(BATTERY_DATA, volts);
                    sendBroadcast(intent);

                    waitMethod(500);

                    BluetoothGattCharacteristic batteryLevel = mGatt.getService(PRESSURE_SERVICE).getCharacteristic(BATTERY_MODE_CHAR);
                    mGatt.readCharacteristic(batteryLevel);
                }

            } else if (BATTERY_MODE_CHAR.equals(characteristic.getUuid())) {
                isWriteMode = false;
                batteryModeArray = characteristic.getValue();
                ModeByte = batteryModeArray[0];

                String StrModeByte = String.valueOf((int) (double) characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));

                Log.e(TAG, "BATTERY_MODE_CHAR" + StrModeByte);
                intent.putExtra(BATTERY_MODE_DATA, StrModeByte);

                sendBroadcast(intent);


            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            BluetoothGattCharacteristic batteryLevel = mGatt.getService(PRESSURE_SERVICE).getCharacteristic(BATTERY_MODE_CHAR);
            Log.e("onCharacteristicWrite", "onCharacteristicWrite");
            mGatt.readCharacteristic(batteryLevel);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //Pass the rssi value and status and in receiver update the ui
            intent = new Intent(RSSI_STATUS);
            intent.putExtra("RSSI_VALUE", rssi);
            sendBroadcast(intent);
        }
    };


    public void getPermissionToReadUserContacts() {


        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {


            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            }


            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION);
        }

    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.v("onActivityResult", " Location providers: " + provider);

            ScanLeDeviceList(true);
        } else {
            //
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            ScanLeDeviceList(false);
        }
    }

    private final BroadcastReceiver mReceiverforIcblue = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("action", "action");
            final String action = intent.getAction();


            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                Log.e("state", state + "");
                switch (state) {
                    case BluetoothDevice.BOND_BONDING:
                        // Bonding...
                        /*connectToDevice(itemValue);*/
                        break;

                    case BluetoothDevice.BOND_BONDED:
                        // Bonded...
                     /*   MainActivity.unregisterReceiver(mReceiver);*/

                        connectToDevice(itemValue);
                        break;

                    case BluetoothDevice.BOND_NONE:
                        // Not bonded...
                       /* pairDevice(itemValue);
                        connectToDevice(itemValue);*/
                        break;
                }
            }
        }
    };


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitMethod(final long time) {
        long t = System.currentTimeMillis();
        long end = t + time;
        while (System.currentTimeMillis() < end) {
        }
    }

    private void switchMode(int mode) {

        if (batteryModeArray != null) {
            try {
                byte ModeByte = batteryModeArray[0];
                Log.e("switchToMode", "" + mode);
                if (mode == 1) {
                    // To set the eighth bit to 1:
                    ModeByte = (byte) (ModeByte & (~0x01));
                } else {
                    // To set the eighth bit to zero:

                    ModeByte = (byte) (ModeByte | 0x01);
                }
                Log.e("ModeByte ", "" + ModeByte);
                byte[] mSwitchByteArray = ByteBuffer.allocate(1).put(ModeByte).array();

                String stringByte = String.format("%8s", Integer.toBinaryString(mSwitchByteArray[0] & 0xFF)).replace(' ', '0');
                Log.e("mSwitchByteArray ", "" + stringByte);

                BluetoothGattCharacteristic batteryLevel = mGatt.getService(PRESSURE_SERVICE).getCharacteristic(BATTERY_MODE_CHAR);
                if (batteryLevel != null) {
                    isWriteMode = true;
                    batteryLevel.setValue(mSwitchByteArray);
                    Log.e("mGatt.writeCharacteristic", "mGatt.writeCharacteristic");
                    mGatt.writeCharacteristic(batteryLevel);

                }


            } catch (Exception e) {
                Log.e("switchMode Exception", "switchModeException");
            }
        } else {
            Log.e("switchToMode ", "characteristic is null");
        }


    }

}
