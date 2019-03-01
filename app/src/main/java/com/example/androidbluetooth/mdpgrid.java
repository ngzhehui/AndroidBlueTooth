package com.example.androidbluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class mdpgrid extends AppCompatActivity implements View.OnTouchListener {


    Activity_Animation animation_LayoutView;
    float x,y;
    TextView tv;
    boolean block = false;
    Button BlockBtn;
    Button StartBtn;
    Button RotateRightBtn,RotateLeftBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mdpgrid);

        //animation_LayoutView = new Activity_Animation(this);
        animation_LayoutView = (Activity_Animation) findViewById(R.id.activity_Animation_View);

        animation_LayoutView.setOnTouchListener(this);

        //setContentView(animation_LayoutView);

        tv = (TextView) findViewById(R.id.StatusView);

        BlockBtn = findViewById(R.id.Block);


        StartBtn = findViewById(R.id.Start);

        RotateLeftBtn = findViewById(R.id.rotateleft);
        RotateRightBtn = findViewById(R.id.rotateright);


        BlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeBlockstatus();
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
        }
        else {
            block = false;
            BlockBtn.setBackgroundResource(R.drawable.brick);
        }
    }




    @Override
    public boolean onTouch(View v, MotionEvent me) {

        if (!block)
            RemoveBlock(me.getX(),me.getY());
        else
            AddBlock(me.getX(),me.getY());

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


}
