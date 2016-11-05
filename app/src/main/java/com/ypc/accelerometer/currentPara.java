package com.ypc.accelerometer;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class currentPara extends AppCompatActivity implements SensorEventListener{
    private SensorManager sm;
    private Sensor sensor_Acc;
    private Sensor sensor_Magn;

    private Button button;
    public LineGraphSeries<DataPoint> series1,series2,series3;
    public LineGraphSeries<DataPoint> series6,series4,series5,series7;
    private final int LENGTH=5;
    TextView biasX;
    TextView biasY;
    TextView biasZ;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private float[] magneticValue=null;

    final float alpha = 0.8f;
    int count1=0;
    int count2=0;
    GraphView graph1;
    GraphView graph2;
    GraphView graph3;
    public float [] tempX=new float[LENGTH];
    public float [] tempY=new float[LENGTH];
    public float [] tempZ=new float[LENGTH];
    float[] smoothAcc=new float[3];
    float magnitude;

    float[] bias=new float[3];
    DataBaseHelper AccDb;
    Thread dBthread;

    boolean stopFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_para);
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor_Acc=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_Magn=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        button=(Button)findViewById(R.id.pause);

        biasX=(TextView)findViewById(R.id.biasX);
        biasY=(TextView)findViewById(R.id.biasY);
        biasZ=(TextView)findViewById(R.id.biasZ);

        try{
            getBias(bias);
        }
        catch (IOException e){}
        biasX.setText(String.valueOf(bias[0]));
        biasY.setText(String.valueOf(bias[1]));
        biasZ.setText(String.valueOf(bias[2]));

        graph1=(GraphView)findViewById(R.id.view1);
        graph2=(GraphView)findViewById(R.id.view2);
        graph3=(GraphView)findViewById(R.id.view3);

        graph1.getViewport().setScalable(true);
        graph1.getViewport().setScrollable(true);
        graph1.getViewport().setScalableY(true);
        graph1.getViewport().setScrollableY(true);
        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setMinY(-10);
        graph1.getViewport().setMaxY(10);
        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(200);
        graph1.getViewport().setMaxX(500);
        graph1.getGridLabelRenderer().setHorizontalAxisTitle("count");

        series1=new LineGraphSeries<DataPoint>();
        series2=new LineGraphSeries<DataPoint>();
        series3=new LineGraphSeries<DataPoint>();
        series1.setColor(Color.GREEN);
        series1.setTitle("linear_X");
        series2.setColor(Color.BLUE);
        series2.setTitle("linear_Y");
        series3.setColor(Color.RED);
        series3.setTitle("linear_Z");

        series4=new LineGraphSeries<DataPoint>();
        series5=new LineGraphSeries<DataPoint>();
        series6=new LineGraphSeries<DataPoint>();
        series4.setColor(Color.GREEN);
        series4.setTitle("linear_X");
        series5.setColor(Color.BLUE);
        series5.setTitle("linear_Y");
        series6.setColor(Color.RED);
        series6.setTitle("linear_Z");

        graph1.addSeries(series1);
        graph1.addSeries(series2);
       graph1.addSeries(series3);

        graph2.getViewport().setScalable(true);
        graph2.getViewport().setScrollable(true);
        graph2.getViewport().setScalableY(true);
        graph2.getViewport().setScrollableY(true);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(-10);
        graph2.getViewport().setMaxY(10);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(200);
        graph2.getViewport().setMaxX(500);
        graph2.getGridLabelRenderer().setHorizontalAxisTitle("count");
        graph2.addSeries(series4);
        graph2.addSeries(series5);
        graph2.addSeries(series6);

       series7=new LineGraphSeries<DataPoint>();
        series7.setColor(Color.GREEN);
        graph3.getViewport().setScalable(true);
        graph3.getViewport().setScrollable(true);
        graph3.getViewport().setScalableY(true);
        graph3.getViewport().setScrollableY(true);
        graph3.getViewport().setYAxisBoundsManual(true);
        graph3.getViewport().setMinY(-10);
        graph3.getViewport().setMaxY(10);
        graph3.getViewport().setXAxisBoundsManual(true);
        graph3.getViewport().setMinX(200);
        graph3.getViewport().setMaxX(500);
        graph3.getGridLabelRenderer().setHorizontalAxisTitle("count");
        graph3.addSeries(series7);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.unregisterListener(currentPara.this);
            }
        });
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0]-bias[0];
            linear_acceleration[1] = event.values[1] - gravity[1]-bias[1];
            linear_acceleration[2] = event.values[2] - gravity[2]-bias[2];

            int index = count2 % LENGTH;
            tempX[index] = linear_acceleration[0];
            tempY[index] = linear_acceleration[1];
            tempZ[index] = linear_acceleration[2];


            smoothAcc[0] = SensorUtil.realTimeSmooth(tempX);
            smoothAcc[1] = SensorUtil.realTimeSmooth(tempY);
            smoothAcc[2] =  SensorUtil.realTimeSmooth(tempZ);


            series4.appendData(new DataPoint(count2, smoothAcc[0]), true, 500);
            series5.appendData(new DataPoint(count2, smoothAcc[1]), true, 500);
            series6.appendData(new DataPoint(count2, smoothAcc[2]), true, 500);

            magnitude= (float) Math.sqrt(smoothAcc[0]*smoothAcc[0]+smoothAcc[1]*smoothAcc[1]+smoothAcc[2]*smoothAcc[2]);
            if(magnitude<0.5)
                magnitude=0;
            series7.appendData(new DataPoint(count2++, magnitude),true,500);

          if(magneticValue!=null) {
              float[] R = new float[9], I = new float[9], earthAcc = new float[3];
              SensorManager.getRotationMatrix(R, I, gravity, magneticValue);
           //   android.opengl.Matrix.transposeM(t, 0, R, 0);
           //   android.opengl.Matrix.invertM(inv, 0, t, 0);
              //android.opengl.Matrix.multiplyMV(earthAcc, 0, R, 0, smoothAcc, 0);
              earthAcc[0]=R[0]*smoothAcc[0]+R[1]*smoothAcc[1]+R[2]*smoothAcc[2];
              earthAcc[1]=R[3]*smoothAcc[0]+R[4]*smoothAcc[1]+R[5]*smoothAcc[2];
              earthAcc[2]=R[6]*smoothAcc[0]+R[7]*smoothAcc[1]+R[8]*smoothAcc[2];
              series1.appendData(new DataPoint(count1, earthAcc[0]), true, 500);
              series2.appendData(new DataPoint(count1, earthAcc[1]), true, 500);
              series3.appendData(new DataPoint(count1++, earthAcc[2]), true, 500);
              //linearX.setText(String.valueOf(earthAcc[0]));
              //linearY.setText(String.valueOf(earthAcc[1]));
              //linearZ.setText(String.valueOf(earthAcc[2]));
          }
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magneticValue=event.values;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onResume(){
        super.onResume();
        stopFlag=false;
        AccDb=new DataBaseHelper(this);
        if(dBthread==null) {
            dBthread = new Thread(new DBthread());
            dBthread.start();
        }
        Toast.makeText(this,AccDb.SQL_CREATE_ACC,Toast.LENGTH_SHORT).show();
        sm.registerListener(currentPara.this,sensor_Acc,SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(currentPara.this,sensor_Magn,SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onPause(){
        super.onPause();
        stopFlag=true;
        sm.unregisterListener(this);
        //dBthread.stop();
        AccDb.close();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFlag=true;
        sm.unregisterListener(this);
        //dBthread.stop();
        AccDb.close();
    }

    public void getBias(float [] bias) throws FileNotFoundException {
        bias[0]=0;
        bias[1]=0;
        bias[2]=0;
        int count=0;
        File sdcard=Environment.getExternalStorageDirectory();
        File inurl = new File (sdcard.getAbsolutePath() + "/SensorData/acc.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(inurl), "UTF-8");
            BufferedReader br=new BufferedReader(isr);
            String str=null;
            String[] temp;
            while((str=br.readLine())!=null){
                temp=str.split(",");
                for(int i=0;i<temp.length;i++){
                    bias[i]+=Float.parseFloat(temp[i]);
                }
                count++;
            }
            Log.d("bias",String.valueOf(bias[0]));
            Log.d("bias",String.valueOf(bias[1]));
            Log.d("bias",String.valueOf(bias[2]));
        }

        catch (IOException e){
            Log.e("bias",e.toString());
        }
        for(float f:bias){
            f /= count;
            //Log.d("bias",String.valueOf(f));
        }
    }
    public class DBthread implements Runnable{
        int count=0;

        @Override
        public void run() {
            if (!stopFlag&&count != count2) {
                count=count2;
                AccDb.insertAccData(smoothAcc[0], smoothAcc[1], smoothAcc[2], magnitude);
            }
        }
    }
}
