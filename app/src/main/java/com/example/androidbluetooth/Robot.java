package com.example.androidbluetooth;

public class Robot {
    int direction = 0;// Upward = 0, Right = 1; Downwards = 2, Left = 3
    int x = 1, y=18,dirx=0,diry=-1; //coordinate

    public void rotateRight()
    {
        switch (direction) {
            case 0://upward
                dirx = 1;
                diry = 0;
                direction = 1;//right
                break;
            case 1://right
                dirx=0;
                diry=1;
                direction = 2;//downwards
                break;
            case 2://downwards
                dirx = -1;
                diry = 0;
                direction = 3;//left
                break;
            case 3://left
                dirx = 0;
                diry = -1;
                direction = 0;//upwards
                break;
            default:
                break;
        }
    }

    public void rotateLeft()
    {
        switch (direction) {
            case 0://upwards
                dirx = -1;
                diry = 0;
                direction = 3;//left
                break;
            case 1://right
                dirx=0;
                diry=-1;
                direction = 0;//upwards
                break;
            case 2://downwards
                dirx = 1;
                diry = 0;
                direction = 1;//right
                break;
            case 3://left
                dirx = 0;
                diry = 1;
                direction = 2;//downwards
                break;
            default:
                break;
        }

    }

}
