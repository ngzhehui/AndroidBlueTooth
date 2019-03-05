package com.example.androidbluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonWriter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

public class mdpgrid extends AppCompatActivity implements View.OnTouchListener {


    Activity_Animation animation_LayoutView;
    float x,y;
    TextView tv,status;
    boolean block = false;
    boolean robotset = false;
    boolean auto = true;
    int action = 0; // action 0, 1 brick function, action 2 robot function
    Button BlockBtn, RobotBtn;
    Button StartBtn;
    Button RotateRightBtn,RotateLeftBtn, ForwardBtn, fastbtn, explorebtn, autobtn, manualbtn;;

    // Appends the incoming messages and then posting them to the textview
    StringBuilder messages;


    DatabaseHelper myDb;

    String[] hashtable = new String[16];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_mdpgrid);
        messages = new StringBuilder();




        //Use the local broadcast manager again to register the broadcast receiver that we are going to use
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));



        TimerExample tel = new TimerExample("task1");

        Timer t = new Timer();
        t.scheduleAtFixedRate(tel, 0, 2000);


        //hash table
        hashtable[0] = "0000";
        hashtable[1] = "0001";
        hashtable[2] = "0010";
        hashtable[3] = "0011";
        hashtable[4] = "0100";
        hashtable[5] = "0101";
        hashtable[6] = "0110";
        hashtable[7] = "0111";
        hashtable[8] = "1000";
        hashtable[9] = "1001";
        hashtable[10] = "1010";
        hashtable[11] = "1011";
        hashtable[12] = "1100";
        hashtable[13] = "1101";
        hashtable[14] = "1110";
        hashtable[15] = "1111";

        /////////////////////////////////////////////////////ANY CODE BELOW HERE WON"T RUN/////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        //animation_LayoutView = new Activity_Animation(this);
        animation_LayoutView = (Activity_Animation) findViewById(R.id.activity_Animation_View);

        animation_LayoutView.setOnTouchListener(this);

        //setContentView(animation_LayoutView);

        status = (TextView) findViewById(R.id.status);
        tv = (TextView) findViewById(R.id.StatusView);
        tv.setMovementMethod(new ScrollingMovementMethod());

        BlockBtn = findViewById(R.id.Block);

        RobotBtn = findViewById(R.id.robot);


        StartBtn = findViewById(R.id.Start);

        RotateLeftBtn = findViewById(R.id.rotateleft);
        RotateRightBtn = findViewById(R.id.rotateright);
        ForwardBtn = findViewById(R.id.straight);
        fastbtn = findViewById(R.id.fast);
        explorebtn = findViewById(R.id.explore);
        autobtn = findViewById(R.id.auto);
        manualbtn=findViewById(R.id.manu);;


        BlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeBlockstatus();
            }
        });

        RobotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeRobotstatus();
            }
        });

        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation_LayoutView.startrobot();
            }
        });

        RotateLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = "tl".getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                BluetoothConnectionService.write(bytes);
                animation_LayoutView.rotateleft();
            }
        });

        RotateRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = "tr".getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                BluetoothConnectionService.write(bytes);
                animation_LayoutView.rotateright();
            }
        });

        ForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = "f".getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                BluetoothConnectionService.write(bytes);
                //animation_LayoutView.rotateright();
            }
        });

        fastbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = "beginFastest".getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                BluetoothConnectionService.write(bytes);
                //animation_LayoutView.rotateright();
            }
        });

        explorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = "beginExplore".getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                BluetoothConnectionService.write(bytes);
                //animation_LayoutView.rotateright();
            }
        });

        autobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auto)
                {
                    auto=false;
                    autobtn.setBackgroundColor(Color.GRAY);
                }
                else {
                    auto=true;
                    autobtn.setBackgroundColor(Color.CYAN);
                }
                animation_LayoutView.auto(auto);
            }
        });

        manualbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation_LayoutView.manual();

            }
        });


        myDb = new DatabaseHelper(this);


        //this part is to insert data
        //tv.setText((myDb.insertData("testing123"))+"");



        //This part is to get data from table
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0)
        {
            //no data exist
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext())
        {
            buffer.append(res.getString(1));
        }
        //tv.setText(buffer);



    }

    ///////////////////////////////////////////BLUETOOTH RECECIVER///////////////////////////////////////////

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //set the string builder to the textview
            String text = intent.getStringExtra("theMessage");

            if(text.length()>50)
            {
                String[] split = text.split(",");

                int index =0;
                String BinaryHex = "";

                for(int k=0;k<75;k++)
                {
                    BinaryHex += hashtable[Integer.parseInt(Character.toString(split[3].charAt(k)),16)];
                    //int y= Integer.parseInt(Character.toString(split[3].charAt(k)),16);
                }


                //for AMDtool
                animation_LayoutView.fixRobot(Integer.parseInt(split[0])+1,Integer.parseInt(split[1])+1,Integer.parseInt(split[2]));

                messages.append(split[2]+"\n");
                tv.setText(messages);

                        for(int i=0;i<20;i++)//y
                        {
                            for(int j=0;j<15;j++) //x
                            {
                                if(BinaryHex.charAt(index) == '1') {
                                    animation_LayoutView.AddBlock(j,i);
                                }
                                index++;
                            }
                        }

            }
            else if(text.substring(0,3).equals("sta"))
            {
                int l = text.length();
                status.setText("Status: "+text.substring(3,l));
            }
            else
                messages.append("Bluetooth: " + text + "\n");
            //tv.setText(messages);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        animation_LayoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animation_LayoutView.pause();
    }

    private void ChangeBlockstatus()
    {
        if (!block) {
            block = true;
            BlockBtn.setBackgroundResource(R.drawable.brickselected);
            action = 1;
        }
        else {
            block = false;
            BlockBtn.setBackgroundResource(R.drawable.brick);
            action = 0;
        }
    }

    private void ChangeRobotstatus()
    {
        if (!robotset) {
            robotset = true;
            RobotBtn.setBackgroundResource(R.drawable.bot2);
            action = 2;
        }
        else {
            robotset = false;
            RobotBtn.setBackgroundResource(R.drawable.bot);
            action = 0;
        }
    }




    @Override
    public boolean onTouch(View v, MotionEvent me) {

        switch (action)
        {
            case 0:
                RemoveBlock(me.getX(),me.getY());
                break;
            case 1:
                AddBlock(me.getX(),me.getY());
                break;
            case 2:
                SelectRobot(me.getX(),me.getY());
                break;
            default:
                break;
        }

        //tv.setText("x="+me.getX()+", y:" + me.getY());
        return false;
    }

    public void AddBlock(float x, float y)
    {
        int x1= (int)(x/43);
        int y1=(int)(y/43);
        animation_LayoutView.AddBlock(x1,y1);
        String msg = String.format("Waypont(%d,%d) selected", x1, y1);
        byte[] bytes = msg.getBytes(Charset.defaultCharset());
        //send those byte to the connection service using the write method in Connectedthread
        BluetoothConnectionService.write(bytes);
        messages.append("Android: " + msg + "\n");
        tv.setText(messages);

    }

    public void RemoveBlock(float x, float y)
    {
        int x1= (int)(x/43);
        int y1=(int)(y/43);
        animation_LayoutView.RemoveBlock(x1,y1);
       String msg = String.format("Waypont(%d,%d) unselected", x1, y1);
        byte[] bytes = msg.getBytes(Charset.defaultCharset());
        //send those byte to the connection service using the write method in Connectedthread
        BluetoothConnectionService.write(bytes);
       messages.append("Android: " + msg + "\n");
       tv.setText(messages);
    }

    public void SelectRobot(float x, float y)
    {
        int adjustmentX, adjustmentY;

        switch((int)(x/43))
        {
            case 0:
                adjustmentX = 1;
                break;
            case 14:
                adjustmentX = 13;
                break;
            default:
                adjustmentX = (int)(x/43);

        }

        switch((int)(y/43))
        {
            case 0:
                adjustmentY = 1;
                break;
            case 19:
                adjustmentY = 18;
                break;
            default:
                adjustmentY = (int)(y/43);

        }

        String msg = String.format("coordinate (%d,%d)", adjustmentX-1, adjustmentY-1);
        messages.append("Android: " + msg + "\n");
        byte[] bytes = msg.getBytes(Charset.defaultCharset());
        //send those byte to the connection service using the write method in Connectedthread
        BluetoothConnectionService.write(bytes);



        animation_LayoutView.SelectRobot(adjustmentX,adjustmentY);
    }



    public class TimerExample extends TimerTask {
        private String name;

        public TimerExample(String n) {
            this.name = n;
        }

        @Override
        public void run() {

            byte[] bytes = "sendArena".getBytes(Charset.defaultCharset());
            //send those byte to the connection service using the write method in Connectedthread
            BluetoothConnectionService.write(bytes);


            if ("Task1".equalsIgnoreCase(name)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }



}
