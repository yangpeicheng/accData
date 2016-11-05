package com.ypc.accelerometer;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class magnetic extends AppCompatActivity implements SensorEventListener{
    private SensorManager sm;
    private Sensor sensor;
    private TextView x;
    private TextView y;
    private TextView z;
    public LineGraphSeries<DataPoint> series1,series2,series3;
    public GraphView graphView;
    private int count=0;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic);
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        x=(TextView)findViewById(R.id.x);
        y=(TextView)findViewById(R.id.y);
        z=(TextView)findViewById(R.id.z);
        button=(Button)findViewById(R.id.button);
        graphView=(GraphView)findViewById(R.id.view);
        series1=new LineGraphSeries<>();
        series2=new LineGraphSeries<>();
        series3=new LineGraphSeries<>();
        graphView=(GraphView)findViewById(R.id.view);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalableY(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-100);
        graphView.getViewport().setMaxY(100);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(200);
        graphView.getViewport().setMaxX(500);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("count");
        series1.setColor(Color.BLUE);
        series1.setTitle("linear_X");
        series2.setColor(Color.GREEN);
        series2.setTitle("linear_Y");
        series3.setColor(Color.RED);
        series3.setTitle("linear_Z");
        graphView.addSeries(series1);
        graphView.addSeries(series2);
        graphView.addSeries(series3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.unregisterListener(magnetic.this);
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        sm.registerListener(magnetic.this,sensor,SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onPause(){
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x.setText(String.valueOf(event.values[0]));
        y.setText(String.valueOf(event.values[1]));
        z.setText(String.valueOf(event.values[2]));
        //if((count++)%10==0){
            series1.appendData(new DataPoint(count,event.values[0]),true,500);
            series2.appendData(new DataPoint(count,event.values[1]),true,500);
            series3.appendData(new DataPoint(count++,event.values[2]),true,500);
        //}
    }
}
