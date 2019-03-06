package com.example.androidbluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class accelerometerDialog extends AppCompatDialogFragment implements SensorEventListener {
    private TextView direction;
    private SensorManager sensorManager;
    Sensor accelerometer;


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.values[0]>2)
            direction.setText("LEFT");
        else if(event.values[0]<-2)
            direction.setText("RIGHT");
        else if(event.values[1]>2)
            direction.setText("UP");
        else if(event.values[1]<-2)
            direction.setText("DOWN");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.accelerometer_diialog, null);

        builder.setView(view)
                .setTitle("Accelerometer Controls")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        direction = view.findViewById(R.id.Direction);



        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return builder.create();


    }



}
