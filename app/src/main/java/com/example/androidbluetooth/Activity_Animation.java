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
    int manualTimer = 1;
    Bitmap background,scaled;

    HashMap<String, Cell> blocklist = new HashMap<String, Cell>();
    HashMap<String, Cell> unexplorelist1 = new HashMap<String, Cell>();
    HashMap<String, Cell> unexplorelist2 = new HashMap<String, Cell>();

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
        white.setColor(Color.GRAY);

        setupUnexplorePath();

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

        setupUnexplorePath();
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

        setupUnexplorePath();
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


        for (Cell t : unexplorelist1.values()) {
            canvas.drawRect((t.x + 1) + (gridSize * t.x), (19-t.y + 1) + (gridSize * (19-t.y)), (t.x + 1) + (gridSize * (t.x + 1)), (19-t.y + 1) + (gridSize * (19-t.y + 1)), white); //
        }

        for (Cell t : unexplorelist2.values()) {
            canvas.drawRect((t.x + 1) + (gridSize * t.x), (19-t.y + 1) + (gridSize * (19-t.y)), (t.x + 1) + (gridSize * (t.x + 1)), (19-t.y + 1) + (gridSize * (19-t.y + 1)), white); //
        }





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
            case 90://right
                myRobot.dirx=1;
                myRobot.diry=0;
                myRobot.direction = 1;
                break;
            case 180://downwards
                myRobot.dirx = 0;
                myRobot.diry = 1;
                myRobot.direction = 2;
                break;
            case 270://left
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

    public void setupUnexplorePath()
    {
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 7; x++)
                unexplorelist1.put(""+x+y, new Cell(x,y));
        }

        for (int y = 0; y < 20; y++) {
            for (int x = 7; x < 15; x++)
                unexplorelist2.put(""+x+y, new Cell(x,y));
        }
    }

    public void removePath(int x, int y)
    {
        if(x<7)
        unexplorelist1.remove(""+x+y);
        else
            unexplorelist2.remove(""+x+y);
    }

}
