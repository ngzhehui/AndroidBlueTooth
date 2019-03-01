
//1 button
//https://www.youtube.com/watch?v=QVD_8PrvG4Q&list=PLgCYzUzKIBE8KHMzpp6JITZ2JxTgWqDH2&index=2
//https://github.com/mitchtabian/EnableBluetooth/blob/master/BluetoothTutorial/app/src/main/java/com/example/user/bluetoothtutorial/MainActivity.java
//https://developer.android.com/guide/topics/connectivity/bluetooth
// https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi

package com.example.androidbluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidbluetooth.BluetoothConnectionService;
import com.example.androidbluetooth.DeviceListAdapter;
import com.example.androidbluetooth.R;

import org.w3c.dom.Text;

import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

//On click method when you click on an unpaired device
//implement OnItemClickListener
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    /**
     * ArrayList to hold our the information of the devices that we are searching for
     * It is going to be an array list of type bluetooth device
     * As it is going to hold the bluetooth devices that it discovers
     */

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView scanListView;
    BluetoothAdapter mBluetoothAdapter;
    Button sendButton, startconnectButton;
    EditText sendEditText;
    TextView receiveTextView, statusTextView, connectivityTextView;
    // Appends the incoming messages and then posting them to the textview
    StringBuilder messages;
    //Connection service object
    BluetoothConnectionService mBluetoothConnection;
    //UUID
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //Declare Bluetooth device
    BluetoothDevice mBTDevice;
    Boolean connectivity = false;
    Boolean ConnectIcon = false;
    Boolean check = false;
    int mytime = 0;

    /**
     * Create a BroadcastReceiver for ACTION_FOUND
     * Grab the broadcast receiver template from the link below
     * https://developer.android.com/guide/topics/connectivity/bluetooth
     */
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /** When discovery finds a device
             * We need to catch the action state changed request that we sent
             * with the intent and the activity inside the method below
             */
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                /** define an integer that defines the state
                 * we are going to get that from the intent
                 * passed as an get extra through the intent filter of the broadcast receiver
                 */
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                //It will catch every state changes of the bluetooth.
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        statusTextView.setText("Bluetooth Off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        statusTextView.setText("Bluetooth On");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Detecting a different mode
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                //Mode is represented as an integer
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        statusTextView.setText("Discoverable");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        statusTextView.setText("Connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        statusTextView.setText("Connected");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                /**
                 * throws an parcel extra in the form of a device
                 * we can store that device (through the intent.getParcelableExtra
                 */

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //store the device
                mBTDevices.add(device);
                /**
                 *get some property of the device
                 *getName and getAddress is from the bluetooth device class
                 *so any Bluetooth device you can use these methods on it
                 */
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                /**
                 * attach our mBTDeviceslist to our mDeviceListAdapter
                 * set the list to the adapter to do list
                 */
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                scanListView.setAdapter(mDeviceListAdapter);
                statusTextView.setText("Scanning");
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //create our action
            final String action = intent.getAction();
            //looking for action bond state change
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //catch when the state changes not going to do anything
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //assign Bluetooth device. Global bluetooth device to the device that is paired with
                    //when a bond is created, the broadcast receive will pick up the bond
                    //this if statement will execute,
                    //the bluetooth device will be assigned with the device it is paired with
                    mBTDevice = mDevice;
                    statusTextView.setText("Paired");
                }
                //case2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                    statusTextView.setText("Pairing");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    //So that the receiver gets closed when the application is paused or destroyed.
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        //Initialise button
        Button bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        //Send and Receive variables
        sendButton = (Button) findViewById(R.id.sendButton);
        startconnectButton = (Button) findViewById(R.id.startconnectButton);
        sendEditText = (EditText) findViewById(R.id.sendEditText);
        receiveTextView = (TextView) findViewById(R.id.receiveTextView);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        connectivityTextView = (TextView) findViewById(R.id.connectivityTextView);
        messages = new StringBuilder();

        //Use the local broadcast manager again to register the broadcast receiver that we are going to use
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        //Use the local broadcast manager again to register the broadcast receiver that we are going to use
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver1, new IntentFilter("connectionStatus"));

        //Array for the scanlistview
        mBTDevices = new ArrayList<>();
        scanListView = (ListView) findViewById(R.id.scanListView);

        //Create an intent filter that catches the bond start change when pairing occurs
        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        //getDefaultAdapter. Initialise Bluetooth adapter object
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //if an item is clicked...
        scanListView.setOnItemClickListener(MainActivity.this);

        //Action after user click on the bluetooth
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                //The method
                enableDisableBT();
            }
        });

        startconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Looking for unpaired devices.");
                startConnection();
                statusTextView.setText("Connected");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the byte that we are going to send
                //get from the Editview and send those bytes to the connection service using the write method
                //Create a byte array

                byte[] bytes = sendEditText.getText().toString().getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                mBluetoothConnection.write(bytes);
                //set the text blank after you have sent a message
                sendEditText.setText("");
                statusTextView.setText("Message sent");
            }
        });

        //this section is for function to access to next page
        Button mapbtn = findViewById(R.id.mapbtn);

        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), mdpgrid.class);
                startActivity(startIntent);
            }
        });
    }



    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //set the string builder to the textview
            String text = intent.getStringExtra("theMessage");
            messages.append(text + "\n");
            receiveTextView.setText(messages);
            statusTextView.setText("Message received");
        }
    };

    BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //set the string builder to the textview
            String text = intent.getStringExtra("connectionStatus");

            Log.d(TAG, "Not working"+ text);
            if(text.equals("Connected"))
            {connectivityTextView.setText("Connected");
                Log.d(TAG, "Connection: Connected");}

            if(text.equals("Disconnected"))
            {connectivityTextView.setText("Disconnected");
                Log.d(TAG, "Connection: Disconnected.");}

        }
    };

    //create method for startingconnectButton
    //***remember the connection will fail and app will crash if you haven't paired first
    public void startConnection() {
        // Our app is already connected
        // Acceptthread is started, start a connection and initiate the connectedthread
        startBTConnection(mBTDevice, MY_UUID_INSECURE);

        /**

         connectivityTextView.setText("Connected");
         TimerExample tel = new TimerExample("task1");

         Timer t = new Timer();
         t.scheduleAtFixedRate(tel, 0, 5 * 1000);
         *
         */
    }

    /**
     * A method that starts the chat service, starts the BTconnection
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        //it is the method from BluetoothConnectionService under AcceptThread
        mBluetoothConnection.startClient(device,uuid);
    }

    //The method, 3 possible scenario.
    public void enableDisableBT() {
        //Device do not have bluetooth functionality
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        //Bluetooth is not enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            /**Use an intent to enable it.
             *Pass it action request enable and then we start activity.
             *Can use to use start activity, start activity for result or broadcast receiver
             *Broadcast receiver is better for logging to see what you are doing.
             */
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
            /**Broadcast receive is better for logging
             * Step to create a Broadcast
             * intent filter that intercepts changes to your bluetooth status and it will log them
             */
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            /**send past the bluetooth adapter the action state changed
             * and will get caught by the broadcast receiver
             * It will catch the state changes of Bluetooth.
             * Send the Action_Enable_State_Change
             * Broadcast receiver will catch that state chane and it will log it
             */
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        //Bluetooth is already enabled, disable it
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();
            //To capture the state change
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    public void visibilityButton(View view) {
        //intent
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //put the extra to define the discoverable duration = 50s
        //This is a amount of time your device is discoverable by other devices
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 50);
        startActivity(discoverableIntent);

        //To capture the state change
        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }

    public void scanButton(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        //check if bluetooth is already in discovery mode
        if (mBluetoothAdapter.isDiscovering()) {

            //we cancelDiscovery first
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            //require special permission check to start discovery or your app won't be able to
            checkBTPermissions();

            //than startDiscovery again
            mBluetoothAdapter.startDiscovery();
            //intent filter is going to use the action and action found on the Bluetooth deviice
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        //if it is not in discovery start discovery
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond, this method cannot be use unless it is API 19 and above
        //NOTE: Requires API 19+? I think this is JellyBean
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            //This method will execute and create the bond once with click on the device
            mBTDevices.get(position).createBond();
            statusTextView.setText("Pairing");

            /**
             * Start connection service!
             * When the connection service starts,
             * the accept thread will sit there waiting for a connection until you click on the startconnectButton.
             * startconnectButton start connection then it's going to initiate the startconnection method
             * which will try to connect to the other devices acceptthread when it complete,
             * the connected thread will start and we can start sending data back and forth
             */
            //assign the bluetooth device
            mBTDevice = mBTDevices.get(position);
            //start a connection service
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }

    /**

     public class TimerExample extends TimerTask {
     private String name;
     public TimerExample(String n)
     {
     this.name = n;
     }
     @Override
     public void run() {
     mBluetoothAdapter.isDiscovering();//start search
     //array length - mBTDevices array length
     Log.d(TAG, "onItemClick: HELP!! status" + mBTDevices.size() + mBTDevice.getName() + mBTDevices.get(0).getName());
     int a = mBTDevices.size();
     for (int j=0;j<(a-1);j++) {
     if (mBTDevices.get(j).getName() == mBTDevice.getName()) {
     check = true;
     }
     }
     if(check == false)
     {
     connectivity = false;
     }
     if(connectivity == false && check == true) {
     startBTConnection(mBTDevice, MY_UUID_INSECURE);
     connectivity = true;
     }
     runOnUiThread(new Runnable() {
     @Override
     public void run() {
     //connectivityTextView.setText("Testing");
     if(connectivity)
     {
     connectivityTextView.setText("Why");
     Log.d(TAG, "onItemClick: connectivity status" + connectivity + check + mBTDevice.getName() + mBTDevices.get(0).getName() + mBTDevices.get(1).getName());
     }
     else {
     connectivityTextView.setText(" " + connectivity);
     Log.d(TAG, "onItemClick: connectivity status" + connectivity + check + mBTDevice.getName() + mBTDevices.get(0).getName()+ mBTDevices.get(1).getName());
     }
     }
     });
     mBluetoothAdapter.cancelDiscovery();

     if ("Task1".equalsIgnoreCase(name)) {
     try {
     Thread.sleep(10000);
     } catch (InterruptedException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     }
     }
     }
     *
     */
}

