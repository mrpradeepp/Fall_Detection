package com.example.lenovo.sensor_sample;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class Fall_test extends AppCompatActivity implements SensorEventListener {
     Sensor sensor;
     SensorManager sensormanager;
    CountDownTimer timer;
    double sum;
    int count;
    int run_count,repeat_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sensormanager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        run_count=1;
        repeat_count=0;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        run_count++;
if(run_count==2) {
    timer = new CountDownTimer(30000, 1000) {

        public void onTick(long millisUntilFinished) {


            sum = Math.round(Math.sqrt(Math.pow(event.values[0], 2)
                    + Math.pow(event.values[1], 2)
                    + Math.pow(event.values[2], 2)));
            repeat_count++;
            Log.e("Check sum", String.valueOf(sum));
            if ((sum >= 9.80) && (sum <= 11.0)) {
                count++;
            }

        }


        public void onFinish() {
            String c = String.valueOf(count);
            String rep=String.valueOf(repeat_count);
            Log.e("Count is", c);
            Log.e("Loop count",rep);
            Intent intent=new Intent();
            intent.putExtra("count_value",count);
            setResult(2, intent);
            finish();


        }
    }.start();

}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensormanager.registerListener(this,
                sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensormanager.unregisterListener(this);
    }


}
