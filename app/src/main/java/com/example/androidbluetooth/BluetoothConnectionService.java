//https://www.youtube.com/watch?v=Fz_GT7VGGaQ&list=PLgCYzUzKIBE8KHMzpp6JITZ2JxTgWqDH2&index=5
//https://github.com/mitchtabian/Sending-and-Receiving-Data-with-Bluetooth/blob/master/Bluetooth-Communication/app/src/main/java/com/example/user/bluetooth_communication/BluetoothConnectionService.java
//https://github.com/googlesamples/android-BluetoothChat
//https://github.com/googlesamples/android-BluetoothChat/blob/master/Application/src/main/java/com/example/android/bluetoothchat/BluetoothChatService.java

package com.example.androidbluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by User on 12/21/2016.
 * Send and receive data
 * Accept thread, Connect Thread and connected thread!
 */

public class BluetoothConnectionService {
    //BluetoothConnectionService debugging tag
    private static final String TAG = "BluetoothConnectionServ";
    //Give the chat service a name
    private static final String appName = "MYAPP";
    //Create a UUID, an address that is used to connect between one another
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //BluetoothAdapter that handles objects and commands
    private final BluetoothAdapter mBluetoothAdapter;
    //the context??
    Context mContext;
    //Because we are doing an insecure connection, the AcceptThread object
    private AcceptThread mInsecureAcceptThread;
    //create our connectthread object
    private ConnectThread mConnectThread;
    //create bluetooth device object
    private BluetoothDevice mmDevice;
    //global uuid
    private UUID deviceUUID;
    //progress dialog
    ProgressDialog mProgressDialog;
    //create connectedthread object
    public static ConnectedThread mConnectedThread;
    private Intent connectionStatus;

    //default constructor
    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /**
         * when the bluetooth object is created the start method will get called at public synchronized void start
         * it will initiate our accept thread
         */
        start();
    }

    /**
     * Initial this and it sit there and wait for a connection for something to try and connect to it
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     * The acceptThread is going to use on another thread so it doesn't use the main resources on the main activity thread
     * Sits there and wait for a connection...
     */
    private class AcceptThread extends Thread {

        // The local server socket, bluetooth service socket
        private final BluetoothServerSocket mmServerSocket;
        // Accept thread constructor
        public AcceptThread(){
            //declare the server socket
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket, RFcomm server socket
            // This send up our socket that other devices connect to with the app name
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }

        //run method, login, bluetooth socket
        // create a bluetooth socket this time not a server socket inside run method
        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");
            //BluetoothSocket this time not Server Socket
            BluetoothSocket socket = null;

            try{
                /**
                 * This is a blocking call and will only return on a
                 * successful connection or an exception
                 * start our socket, the accept thread will hang here until something connects to it
                 */
                Log.d(TAG, "run: RFCOM server socket start.....");

                //your code will sit here and wait until a connection is made or your connection fails
                socket = mmServerSocket.accept();

                //A message that say the connection is successful/made
                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            //talk about this is in the 3rd
            //check if a socket is null we want to move on to the next step
            if(socket != null){
                connected(socket,mmDevice);
            }

            Log.i(TAG, "END mAcceptThread ");
        }


        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                //will basically close the server socket
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }
    }

    /**
     * Create the connect thread class
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either succeeds or fails.
     * This thread basically connects two devices that are both sitting in socket = mmServerSocket.accept();
     * They are going to be sitting in the waiting state with their accept thread running until another device connect thread starts
     * it's going to grab this socket and connect to it
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        //ConnectThread default constructor
        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            //variable; show our bluetooth device and uuid
            mmDevice = device;
            deviceUUID = uuid;
        }

        /** a run method automatically execute inside a thread so you don't have to worry about
         * it would just automatically run when a connect thread object is created or an accept object is created
         */
        public void run(){
            //clear our Bluetooth socket
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            // Take temporary Bluetooth socket here and create an RF comm socket
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                //if it fails to create an InsecureRfcommSocket
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            // Assign socket to temporary variable
            mmSocket = tmp;

            // Always cancel discovery because it very memory intensive
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                // Connection is successful!
                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                // Close the socket if there is an exception
                // Connection fail exception thrown
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            //will talk about this in the 3rd video
            connected(mmSocket,mmDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     * Initiate the AcceptThread to sit there an listen for connection
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // If the connectThread exist, we want to cancel it and recreate a new one
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // If an acceptThread does not exist, we want to start one
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            //this start() is used to initiate our acceptthread
            //method native to the thread class so you can use dot start on any thread object and it initiate it
            mInsecureAcceptThread.start();
        }
    }

    /**
     * Method that will initiate the connector thread
     * AcceptThread starts and sits waiting for a connection.
     * Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/
    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        //initiate the progress dialog; will pop out when a connection is trying to be made
        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth"
                ,"Please Wait...",true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    /**
     * Manages the bluetooth connection
     * Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
     * receiving incoming data through input/output streams respectively.
     * ConnectedThread is the one that is going to manage the connection
     * It is a point in time when we know a connection has been made
     **/
    private class ConnectedThread extends Thread {
        //variables

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //default constructor
        public ConnectedThread(BluetoothSocket socket) {


            Log.d(TAG, "ConnectedThread: Starting.");
            //declare variables
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            Log.d(TAG, "ConnectionBCS: Connected");
            connectionStatus = new Intent("connectionStatus");
            connectionStatus.putExtra("connectionStatus","Connected"); //key and device
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(connectionStatus);

            //dismiss the progressdialogbox when connection is established
            //the connection with that device did not take place
            //try-catch as the device may try to close the dialog box that did not exist.
            try{
                mProgressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                //Need to get the input and output streams
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {

                e.printStackTrace();
            }
            //need to declare our input and output stream
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            //byte array object that is going to be getting the input from the input stream
            byte[] buffer = new byte[1024];  // buffer store for the stream
            //reads the input from the input stream since we are going to be reading bytes we need to use integer
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream for new information until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    //convert the buffer and the bytes into a string message
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    /** how to pass data into MainActivity = create intent
                     *  going to attach an extra in the form of a string and then send it over
                     *  name the intent call = incomingMessage
                     *  in order to reference it on main from main activity then
                     *  we are going to put an extra onto the intent in the form of a string
                     *  the extra is call the message and we are going to throw our incoming message right here
                     *  Using broadcast manager to send the broadcast
                     */
                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage",incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);

                } catch (IOException e) {
                    //if there is a problem with the input stream we want to end the connection and break the loop
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    Log.d(TAG, "ConnectionBCS: Disconnected");
                    connectionStatus = new Intent("connectionStatus");
                    connectionStatus.putExtra("connectionStatus","Disconnected"); //key and device
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(connectionStatus);
                    //there is a problem with the input stream we would want to break the loop to end the connection
                    break;
                }
            }
        }

        // Create a write method that is responsible for writing to the output stream
        // Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            //Create a String from the bytes that we are going to send through the write method
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                //Write it into the output stream
                mmOutStream.write(bytes);
            } catch (IOException e) {
                //Check if there is anything wrong with the output stream
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    //Connected method
    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");
        // Start the method to manage the connection and perform output stream transmissions and grab input stream transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     * As the write won't be able to access from the main activity
     * have to write a write method that can access the connection service which
     * can then access the connected thread
     */
    public static void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
    }
}


