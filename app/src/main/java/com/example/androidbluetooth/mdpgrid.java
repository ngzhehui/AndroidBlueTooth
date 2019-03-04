package com.example.androidbluetooth;

import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class mdpgrid extends AppCompatActivity implements View.OnTouchListener {


    Activity_Animation animation_LayoutView;
    float x,y;
    TextView tv;
    boolean block = false;
    boolean robotset = false;
    int action = 0; // action 0, 1 brick function, action 2 robot function
    Button BlockBtn, RobotBtn;
    Button StartBtn;
    Button RotateRightBtn,RotateLeftBtn;

    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MDP";

    DatabaseHelper myDb;

    public BluetoothConnectionService mBluetoothConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBluetoothConnection = getIntent().getExtras().getParcelable("btConnection");

        setContentView(R.layout.activity_mdpgrid);

        //animation_LayoutView = new Activity_Animation(this);
        animation_LayoutView = (Activity_Animation) findViewById(R.id.activity_Animation_View);

        animation_LayoutView.setOnTouchListener(this);

        //setContentView(animation_LayoutView);

        tv = (TextView) findViewById(R.id.StatusView);

        BlockBtn = findViewById(R.id.Block);

        RobotBtn = findViewById(R.id.robot);


        StartBtn = findViewById(R.id.Start);

        RotateLeftBtn = findViewById(R.id.rotateleft);
        RotateRightBtn = findViewById(R.id.rotateright);


        BlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = "testing123456".getBytes(Charset.defaultCharset());
                //send those byte to the connection service using the write method in Connectedthread
                mBluetoothConnection.write(bytes);
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
                animation_LayoutView.rotateleft();
            }
        });

        RotateRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation_LayoutView.rotateright();
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
        tv.setText(buffer);

    }

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

        tv.setText("x="+me.getX()+", y:" + me.getY());
        return false;
    }

    public void AddBlock(float x, float y)
    {
        animation_LayoutView.AddBlock((int)(x/43),(int)(y/43));
    }

    public void RemoveBlock(float x, float y)
    {
        animation_LayoutView.RemoveBlock((int)(x/43),(int)(y/43));
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


        animation_LayoutView.SelectRobot(adjustmentX,adjustmentY);
    }



}
