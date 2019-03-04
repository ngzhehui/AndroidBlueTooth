package com.example.androidbluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;

public class Activity_Animation extends SurfaceView implements Runnable {

    Thread thread = null;
    boolean CanDraw = false;
    boolean createRobot = false;
    boolean test = false;
    Bitmap background,scaled;

    HashMap<String, Cell> blocklist = new HashMap<String, Cell>();

    int gridSize = 42;


    Robot myRobot = new Robot();


    Paint red,blue,green,white;

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
        white.setColor(Color.WHITE);

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
        white.setColor(Color.WHITE);
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
        white.setColor(Color.WHITE);
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
        canvas.drawBitmap(scaled,0,0,null);

        /*
        canvas.drawPaint(white);


        for(int j=0; j<21;j++)
        {
            canvas.drawLine(0, gridSize*j, 646, gridSize*j, red);
        }
        */




        for(Cell p : blocklist.values())
        {
            canvas.drawRect((p.x+1)+(gridSize*p.x),(p.y+1)+(gridSize*p.y),(p.x+1)+(gridSize*(p.x+1)),(p.y+1)+(gridSize*(p.y+1)),red); //
        }


        //this part is for slowing down robot movement
        timing++;

        if(timing == 10) {
            moveRobot();
            timing = 0;
        }

        drawRobot();



        surfaceHolder.unlockCanvasAndPost(canvas);
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
                    canvas.drawRect((myRobot.x + i) + (gridSize * (myRobot.x + i - 1)), (myRobot.y + j) + (gridSize * (myRobot.y + j - 1)), (myRobot.x + i) + (gridSize * (myRobot.x + i)), (myRobot.y + j) + (gridSize * (myRobot.y + j)), blue); //

            canvas.drawRect((myRobot.x + myRobot.dirx) + (gridSize * (myRobot.x + myRobot.dirx)), (myRobot.y + myRobot.diry) + (gridSize * (myRobot.y + myRobot.diry)), (myRobot.x + myRobot.dirx + 1) + (gridSize * (myRobot.x + myRobot.dirx + 1)), (myRobot.y + myRobot.diry + 1) + (gridSize * (myRobot.y + myRobot.diry + 1)), green);

        }

    }

    public void moveRobot()
    {
        if(test) {
            if (blocklist.get((myRobot.x + (myRobot.dirx*2)) + "" + (myRobot.y + (myRobot.diry*2))) == null) {

                if (myRobot.y + (myRobot.diry) != 0 && myRobot.y + (myRobot.diry) != 19 && myRobot.x + (myRobot.dirx) != 0 && myRobot.x + (myRobot.dirx) != 14) {
                    myRobot.x += myRobot.dirx;
                    myRobot.y += myRobot.diry;
                }

            }
            else
                test = false;
        }

    }


    public void AddBlock(int x, int y)
    {
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

    public void startrobot()
    {
        test= true;
    }

    public void rotateright()
    {
        myRobot.rotateRight();
    }

    public void rotateleft()
    {
        myRobot.rotateLeft();
    }

    public void stoprobot()
    {
        test=false;
    }
}
