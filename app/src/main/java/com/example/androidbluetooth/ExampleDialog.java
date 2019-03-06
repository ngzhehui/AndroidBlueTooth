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

public class ExampleDialog extends AppCompatDialogFragment implements SensorEventListener {
    private EditText s1,s2;
    private  ExampleDialogListener listener;
    private SensorManager sensorManager;
    Sensor accelerometer;

    @Override
    public void onSensorChanged(SensorEvent event) {
        s1.setText("x:" +event.values[0] + " y:" + event.values[1] + " z:" + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Shortcut Settings")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s1Text = s1.getText().toString();
                        String s2Text = s2.getText().toString();
                        listener.applyText(s1Text,s2Text);
                    }
                });

        s1 = view.findViewById(R.id.s1edittxt);
        s2 = view.findViewById(R.id.s2edittxt);

        s1.setText(mdpgrid.tempString[0]);
        s2.setText(mdpgrid.tempString[1]);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return builder.create();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface ExampleDialogListener{
        void applyText(String s1, String s2);
    }
}
