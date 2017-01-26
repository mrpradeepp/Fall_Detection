package com.example.lenovo.sensor_sample;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Sensor sensor,sensor_main;
    SensorManager sm;
    TextView xText,yText,zText,total,svalue,samples,help_text,heading;
    double sum,check_sum,value;
    boolean min,max,flag;
    int i,count;
    Calendar c;
    CountDownTimer timer;
    private Button mButton;

    private PopupWindow mPopupWindow;
    private ScrollView mRelativeLayout;

    // Examples myClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRelativeLayout=(ScrollView) findViewById(R.id.r1);
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_main=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        count = 0;
        heading=(TextView)findViewById(R.id.Heading);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Myfont.ttf");
        heading.setTypeface(tf);




       // Toast.makeText()
      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent j = new Intent(MainActivity.this,Setting.class);
            startActivity(j);
           // return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {


        sum = Math.round(Math.sqrt(Math.pow(event.values[0], 2)
                + Math.pow(event.values[1], 2)
                + Math.pow(event.values[2], 2)));
       // total.setText("Total::" + sum);
       // svalue.setText("Standard Gravity::" + sm.STANDARD_GRAVITY);

        if (sum <= 5.0) {
            min = true;

        }

        if (min == true) {
            i++;
            if (sum >= 16.5) {
                max = true;
            }
        }

        if (min == true && max == true) {
             sm.unregisterListener(this);
            Toast.makeText(this, "Suspected Fall", Toast.LENGTH_SHORT).show();
            Intent test= new Intent(MainActivity.this,Fall_test.class);
            startActivityForResult(test,2);

            min = false;
            max = false;




            if (count > 45) {
                Toast.makeText(this,"Fall Confirmed",Toast.LENGTH_LONG).show();
                i = 0;
                count=0;
                min = false;
                max = false;

            }
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
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }

    public void examples(View view) {
        Intent i = new Intent(this,Examples.class);
        startActivity(i);
    }

    public void go_setting(View view) {
        Intent i = new Intent(this,Setting.class);
        startActivity(i);
    }

    public void go_falltest(View view) {
       // Intent i=new Intent(this,Fall_test.class);
       // startActivity(i);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup,null);
        mPopupWindow = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        help_text=(TextView)customView.findViewById(R.id.tv);

        help_text.setText("This Mobile Application can be used to trace whether you have fallen or not.Once the app is running it will trace your movements" +
                " \n If a fall is suspected you will be given an option to confirm you are OK by cancelling an SMS alert \n If you" +
                "are not cancelling the SMS alert in a predefined time then an sms will be send to the number you selected along with your geographical Location" +
                "\n \n SETTINGS \n 1: The User can specify the number to which an SMS should be send \n 2: User can select the Ringtone that should be dialled when a fall is detected" +
                "\n 3:User can specify the Response mode(Low ,medium or high)");

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            int message=data.getIntExtra("count_value", 0);
            String msg=String.valueOf(message);
            Log.e("Result is",msg);
            if(message>25)
            {
                Intent alert_intent=new Intent(MainActivity.this,Examples.class);
                startActivity(alert_intent);
            }
        }
    }
}
