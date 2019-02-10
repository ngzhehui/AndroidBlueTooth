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
    boolean side = false;
    boolean test = false;
    Bitmap background;

    HashMap<String, Cell> blocklist = new HashMap<String, Cell>();


    Robot myRobot = new Robot();


    Paint red,blue;

    int timing = 0;

    Canvas canvas;
    SurfaceHolder surfaceHolder;



    public Activity_Animation(Context context)
    {
        super(context);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

        red = new Paint();
        red.setColor(Color.BLACK);

        blue = new Paint();
        blue.setColor(Color.BLUE);



    }

    public Activity_Animation(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

        red = new Paint();
        red.setColor(Color.BLACK);

        blue = new Paint();
        blue.setColor(Color.BLUE);




    }

    public Activity_Animation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

        red = new Paint();
        red.setColor(Color.BLACK);

        blue = new Paint();
        blue.setColor(Color.BLUE);




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
        canvas.drawBitmap(background,0,0,null);




        for(Cell p : blocklist.values())
        {
            canvas.drawRect((p.x+1)+(46*p.x),(p.y+1)+(46*p.y),(p.x+1)+(46*(p.x+1)),(p.y+1)+(46*(p.y+1)),red); //
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
        for(int i = 0; i<3;i++)
            for(int j=0;j<3;j++)
                canvas.drawRect((myRobot.x+i)+(46*(myRobot.x+i-1)),(myRobot.y+j)+(46*(myRobot.y+j-1)),(myRobot.x+i)+(46*(myRobot.x+i)),(myRobot.y+j)+(46*(myRobot.y+j)),blue); //

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
