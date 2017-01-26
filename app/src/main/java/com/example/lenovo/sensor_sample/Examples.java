package com.example.lenovo.sensor_sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.net.ConnectivityManager.TYPE_MOBILE;

public class Examples extends AppCompatActivity implements LocationListener {
    TextView values,timetest,title;
    private LocationManager locationManager;
    private String latitude;
    private String longitude;
    private String msg;
    Button alertbutton;
    CountDownTimer timer;
    private AudioManager myAudioManager;
    TextToSpeech t1;
    Ringtone r;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String sms_number,ringtone_value;
  long response_mode,resp_time;
    boolean isPlaying;

    TextView tv,alert_msg;
    int timer_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // tv=(TextView)findViewById(R.id.sp_text);
        isPlaying=true;
        timer_enabled=0;
        alert_msg=(TextView)findViewById(R.id.txt_alertHeading);
        title=(TextView)findViewById(R.id.example_values);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Myfont.ttf");
        title.setTypeface(tf);
        // Read the shared prefernce values for ringtone,sms number,and response mode
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sms_number=sharedpreferences.getString("Contact_number", "");
        ringtone_value=sharedpreferences.getString("Ringtone", "");
        response_mode=sharedpreferences.getLong("Response", 00);
        resp_time=response_mode/60000;
        if((sms_number==null)||(ringtone_value==null)||(response_mode==0.0f))
        {
            Toast.makeText(this,"Error in reading",Toast.LENGTH_LONG).show();
        }
        else
        {

           // tv.setText("ringtone vale::"+ringtone_value+"->"+"mode"+String.valueOf(response_mode));
        }

        alert_msg.setText("Click here to Cancel the SMS alert in "+ resp_time+ " min");
        Uri uri = Uri.parse(ringtone_value);
// Play the selected Ringtone as an alert tone
        r = RingtoneManager.getRingtone(this, uri);
            r.play();

// function to stop the ringtone after 10 second and start a voice message regarding fall alert
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                r.stop();
                t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.UK);
                            // this function will generate the voice message

                                speakout();


                        }
                    }
                });

            }
        }, 10000);

        values = (TextView) findViewById(R.id.example_values);

        alertbutton=(Button)findViewById(R.id.btn_sms);



      // Count Down Timer to activate the SMS alert,the count down timer is set for 2 minutes.
      // for every second the timer value in the button will be adjusted(reduced by 1 sec)
      // onTick function will be called as per the interval mentioned in the function attribute
       // onFinish will be called when the timer period value expires(here 2 min)

    timer = new CountDownTimer(response_mode, 1000) {

        public void onTick(long millisUntilFinished) {
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            alertbutton.setText(String.format("%02d", minutes)
                    + ":" + String.format("%02d", seconds));
            //here you can have your logic to set text to edittext
            alertbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    timer.cancel();
                    alertbutton.setText("SMS Alert Cancelled");
                    timer_enabled=1;

                }
            });
        }

        public void onFinish() {
            alertbutton.setText("SMS Alert Activated");
            // String phone_num="4166249722";
            String sms_msg = "The sender of this message may have been fallen,it happened in this location"+" " + "http://maps.google.com/maps/@" + latitude + "," + longitude + "/";
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sms_number, null, sms_msg, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }

            //Call function to activate speaker phone
            initialize_phone();
        }

    }.start();

        // locationManager is used to find the geographical location of the phone
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                2000, 10, this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // function to generate the message that a fall detection occured
  public void speakout()
  {
    Log.e("Speech", "In speech module");
      t1.speak("A fall have been detected.Please cancel the SMS alert if you are OK", TextToSpeech.QUEUE_FLUSH, null);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

      } else {
          t1.speak("A fall have been detected.Please cancel the SMS alert if you are OK", TextToSpeech.QUEUE_FLUSH, null);
      }
  }

    // function associated with Location manager to find new geographical location when a location change occurs
    @Override
    public void onLocationChanged(Location location) {

        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();
        //showLatitude(location.getLatitude());
        double lat = (double) (location.getLatitude());
        double longi=(double) (location.getLongitude());
        latitude=lat+"";
        longitude=longi+"";
       getLatitude();
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    // Function to check whether GPS is Disabled,if disabled user will be redirected to the settings menu to activate GPS
    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    // Function to check if GPS is activated
    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public String getLatitude() {
         msg="https://www.google.com/maps/@"+latitude+","+longitude;
       // values.setText(msg);
        return latitude;
    }
// Function to send sms
    public void sms_send(View view) {
        String phone_num="4166249722";
        String sms_msg="Please check this"+"http://maps.google.com/maps/@"+latitude+","+longitude+"/";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_num, null, sms_msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }

    // This function will Inititialize the Telephony Manager to check call status of the phone
    //If the phone call is taken then speaker phone will be activated
    public void initialize_phone()
    {

        TelephonyManager telephonyManager =
                (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                if(state==TelephonyManager.CALL_STATE_RINGING){
                    Toast.makeText(getApplicationContext(),"Phone Is Riging",
                            Toast.LENGTH_LONG).show();
                }
                if(state==TelephonyManager.CALL_STATE_OFFHOOK){
                    Toast.makeText(getApplicationContext(),"Phone is Currently in A call",
                            Toast.LENGTH_LONG).show();

                    myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    myAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    myAudioManager.setSpeakerphoneOn(true);


                }

                if(state==TelephonyManager.CALL_STATE_IDLE){
                    Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call",
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }


    @Override
    public void onBackPressed() {
        if(timer_enabled==1)
        {
        super.onBackPressed();
    }
        else {
            Toast.makeText(getApplicationContext(),"Please cancel the SMS alertbutton to return to main menu",Toast.LENGTH_LONG).show();
        }
        }
}







