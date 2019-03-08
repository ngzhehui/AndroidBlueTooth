package com.example.androidbluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Activity_Animation extends SurfaceView implements Runnable {

    Thread thread = null;
    boolean CanDraw = false;
    boolean createRobot = true;
    boolean createWaypoint = false;
    boolean test = false;
    boolean reading = true;
    boolean writing = false;
    boolean waiting = false;
    boolean hide = true;
    int manualTimer = 1;
    Bitmap background,scaled;

    HashMap<String, Cell> blocklist = new HashMap<String, Cell>();
    HashMap<String, Cell> unexplorelist1 = new HashMap<String, Cell>();
    HashMap<String, Cell> unexplorelist2 = new HashMap<String, Cell>();
    Cell waypoint = new Cell(0,0);

    int gridSize = 42;


    Robot myRobot = new Robot();


    Paint red,blue,green,white, yellow,rred;

    int timing = 0;

    Canvas canvas;
    SurfaceHolder surfaceHolder;



    public Activity_Animation(Context context)
    {
        super(context);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

        float scale = (float)background.getHeight()/861;
        int newWidth = Math.round(background.getWidth()/scale);
        int newHeight = Math.round(background.getHeight()/scale);
        scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

        red = new Paint();
        red.setColor(Color.BLACK);

        blue = new Paint();
        blue.setColor(Color.BLUE);

        white = new Paint();
        white.setColor(Color.GRAY);

        yellow = new Paint();
        yellow.setColor(Color.YELLOW);

        rred = new Paint();
        rred.setColor(Color.RED);


    }

    public Activity_Animation(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

        float scale = (float)background.getHeight()/861;
        int newWidth = Math.round(background.getWidth()/scale);
        int newHeight = Math.round(background.getHeight()/scale);
        scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

        red = new Paint();
        red.setColor(Color.BLACK);

        blue = new Paint();
        blue.setColor(Color.BLUE);

        green = new Paint();
        green.setColor(Color.GREEN);

        white = new Paint();
        white.setColor(Color.GRAY);

        yellow = new Paint();
        yellow.setColor(Color.YELLOW);

        rred = new Paint();
        rred.setColor(Color.RED);

    }

    public Activity_Animation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

        float scale = (float)background.getHeight()/861;
        int newWidth = Math.round(background.getWidth()/scale);
        int newHeight = Math.round(background.getHeight()/scale);
        scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

        red = new Paint();
        red.setColor(Color.BLACK);

        blue = new Paint();
        blue.setColor(Color.BLUE);

        green = new Paint();
        green.setColor(Color.GREEN);

        white = new Paint();
        white.setColor(Color.GRAY);

        yellow = new Paint();
        yellow.setColor(Color.YELLOW);

        rred = new Paint();
        rred.setColor(Color.RED);

    }


    @Override
    public void run() {

        while(CanDraw)
        {

            if(!surfaceHolder.getSurface().isValid())
            {
                continue;
            }

            draw();

        }

    }

    protected void resume() {
        CanDraw = true;
        thread = new Thread(this);
        thread.start();

    }

    private void draw()
    {

            canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(scaled, 0, 0, null);

            for (Cell p : blocklist.values()) {
                canvas.drawRect((p.x + 1) + (gridSize * p.x), (19-p.y + 1) + (gridSize * (19-p.y)), (p.x + 1) + (gridSize * (p.x + 1)), (19-p.y + 1) + (gridSize * (19-p.y + 1)), red); //
            }



        for(int y=0;y<20;y++)//y
        {
            for(int x=0;x<15;x++) //x
            {
                if(!(unexplorelist1.containsKey(""+x+y)||unexplorelist2.containsKey(""+x+y)))
                {
                    canvas.drawRect((x + 1) + (gridSize * x), (19 - y + 1) + (gridSize * (19 - y)), (x + 1) + (gridSize * (x + 1)), (19 - y + 1) + (gridSize * (19 - y + 1)), white); //
                }

            }
        }


            if(createWaypoint)
                canvas.drawRect((waypoint.x + 1) + (gridSize * waypoint.x), (19-waypoint.y + 1) + (gridSize * (19-waypoint.y)), (waypoint.x + 1) + (gridSize * (waypoint.x + 1)), (19-waypoint.y + 1) + (gridSize * (19-waypoint.y + 1)), yellow); //


            canvas.drawRect((13) + (gridSize * 12), 1, (15) + (gridSize * (15)), (3) + (gridSize * (3)), rred); //





            drawRobot();


            surfaceHolder.unlockCanvasAndPost(canvas);

        if(manualTimer==0)
        {
            manualTimer=1;

            pause();
        }

    }

    protected void pause() {


        CanDraw = false;
        while(true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        thread = null;
    }


    public void drawRobot()
    {
        if(createRobot) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    canvas.drawRect((myRobot.x + i) + (gridSize * (myRobot.x + i - 1)), (19-myRobot.y + j) + (gridSize * (19-myRobot.y + j - 1)), (myRobot.x + i) + (gridSize * (myRobot.x + i)), (19-myRobot.y + j) + (gridSize * (19-myRobot.y + j)), blue); //

            canvas.drawRect((myRobot.x + myRobot.dirx) + (gridSize * (myRobot.x + myRobot.dirx)), (19-myRobot.y + myRobot.diry) + (gridSize * (19-myRobot.y + myRobot.diry)), (myRobot.x + myRobot.dirx + 1) + (gridSize * (myRobot.x + myRobot.dirx + 1)), (19-myRobot.y + myRobot.diry + 1) + (gridSize * (19-myRobot.y + myRobot.diry + 1)), green);

        }

    }


    public void auto(boolean b){
        if(b)
            resume();
        else
            pause();
    }

    public void manual(){
        manualTimer = 0;
        resume();
    }



    public void fixRobot(int x, int y, int d) //for amdtool
    {
        myRobot.x = x;
        myRobot.y = y;


        switch (d) {
            case 0://upward
                myRobot.dirx = 0;
                myRobot.diry = -1;
                myRobot.direction = 0;
                break;
            case 1://right
                myRobot.dirx=1;
                myRobot.diry=0;
                myRobot.direction = 1;
                break;
            case 2://downwards
                myRobot.dirx = 0;
                myRobot.diry = 1;
                myRobot.direction = 2;
                break;
            case 3://left
                myRobot.dirx = -1;
                myRobot.diry = 0;
                myRobot.direction = 3;
                break;
            default:
                break;
        }
    }


    public void AddBlock(int x, int y)
    {
        if(!blocklist.containsKey(""+x+y))
        blocklist.put(""+x+y, new Cell(x,y));
    }

    public  void RemoveBlock(int x, int y)
    {

        blocklist.remove(""+x+y);
    }

    public  void SelectRobot(int x, int y)
    {
        test = false;
        createRobot = true;
        myRobot.x = x;
        myRobot.y = y;
    }

    public void UpdatePath(int x, int y)
    {
        if(x<7) {
            if(!unexplorelist1.containsKey(""+x+y))
            unexplorelist1.put("" + x + y, new Cell(x, y));
        }
        else {

            if(!unexplorelist2.containsKey("" + x + y))
            unexplorelist2.put("" + x + y, new Cell(x, y));
        }

    }

    public void AddWaypoint(int x, int y)
    {
        createWaypoint = true;
        waypoint.x = x;
        waypoint.y = y;
    }

    public void HideUnexplorePath()
    {
        hide = true;
    }

    public void UnhideUnexplorePath()
    {
        hide = false;
    }


    //this function will only indicate the block part in the map
    public void PassMapDetail()
    {

        int index = 0;
        String hex= String.format("%d,%d,", waypoint.x, waypoint.y);
        String binary="";
        for(int y=0;y<20;y++)
        {
            for(int x=0;x<15;x++)
            {
                if(index<4)//allow 4bit
                {
                    if(blocklist.containsKey(""+x+y))
                        binary+="1";
                    else
                        binary+="0";


                    index++;

                }
                else
                {
                    hex += Long.toHexString(Long.parseLong(binary,2));
                    binary="";
                    index = 0;
                }
            }
        }

        byte[] bytes = hex.getBytes(Charset.defaultCharset());
        //send those byte to the connection service using the write method in Connectedthread
        BluetoothConnectionService.write(bytes);

    }


}
