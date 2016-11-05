package com.ypc.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class showInfo extends AppCompatActivity implements SensorEventListener {
    private SensorManager sm;
    private Sensor sensorAcc;
    private Sensor sensorMagn;
    private Button bt_start;
    private Button bt_end;
    public boolean flag;
    public File acc_file;
    public File magn_file;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    final float alpha = 0.8f;
    int count_acc;
    int count_magn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAcc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagn=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_end = (Button) findViewById(R.id.bt_end);
        flag = false;
        File sdcard=Environment.getExternalStorageDirectory();
        File dir = new File (sdcard.getAbsolutePath() + "/SensorData");
        dir.mkdirs();
        acc_file=new File(dir,"acc.txt");
        magn_file=new File(dir,"magn.txt");
        if(acc_file.exists()){
            acc_file.delete();
        }
        if(magn_file.exists()){
            magn_file.delete();
        }
        count_acc=0;
        count_magn=0;
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
            }
        });
        bt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;

            }
        });

    }


    protected void onResume() {
        super.onResume();
        sm.registerListener(this,sensorAcc,SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this,sensorMagn,SensorManager.SENSOR_DELAY_GAME);

    }

    protected void onPause(){
        super.onPause();
        sm.unregisterListener(this);

    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        if(flag==false)
            return;
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER && count_acc<150){
            count_acc++;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            if(count_acc<50)
                return;
            String tempx=String.valueOf(linear_acceleration[0]);
            String tempy=String.valueOf(linear_acceleration[1]);
            String tempz=String.valueOf(linear_acceleration[2]);
            try{
                FileOutputStream fout = new FileOutputStream(acc_file,true);
                OutputStreamWriter osw=new OutputStreamWriter(fout);
                try{
                    osw.write(tempx+','+tempy+','+tempz+'\n');
                    osw.flush();
                    osw.close();
                }
                catch (IOException e){

                }
            }
            catch (IOException e){

            }

        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD && count_magn<100){
            count_magn++;
            String tempx=String.valueOf(event.values[0]);
            String tempy=String.valueOf(event.values[1]);
            String tempz=String.valueOf(event.values[2]);
            try{
                FileOutputStream fout = new FileOutputStream(magn_file,true);
                OutputStreamWriter osw=new OutputStreamWriter(fout);
                try{
                    osw.write(tempx+','+tempy+','+tempz+'\n');
                    osw.flush();
                    osw.close();
                }
                catch (IOException e){

                }
            }
            catch (IOException e){

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
