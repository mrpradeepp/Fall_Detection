package com.example.lenovo.sensor_sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Setting extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String contactNumber,chosenRingtone,response_mode;
    long response_time;
   Ringtone r;
    int count;
    TextView sampletext,setting_heading;
    final long[] spinnervalues={180000,120000,60000};
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      setting_heading=(TextView)findViewById(R.id.Settings_title);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Myfont.ttf");
        setting_heading.setTypeface(tf);
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.response_spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
       // Add values to spinner
        List<String> categories = new ArrayList<String>();
        categories.add("Low(3 minute)");

        categories.add("Medium(2 minute)");
        categories.add("High(1 minute)");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



     //  mp = MediaPlayer.create(this, Uri.parse(chosenRingtone));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    public void select_ringtone(View view) {

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
            }
            else
            {
                this.chosenRingtone = null;
            }
            Toast.makeText(this,chosenRingtone,Toast.LENGTH_LONG).show();
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == 4)
            {
                Uri phone_uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(phone_uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                contactNumber = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                //sampletext.setText("Name is"+name+"Number is "+contactNumber);
                //Toast.makeText(this,"Name is"+name+"Number is "+number,Toast.LENGTH_LONG).show();
            }
        else
        {
            Toast.makeText(this,"No result",Toast.LENGTH_LONG).show();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }



   //Function to save the settings as shared preference

    public void save_settings(View view) {
        boolean flag;
        if ((chosenRingtone == null) ||(contactNumber == null) || (response_mode == null)) {
            Toast.makeText(getApplicationContext(), "Please Select all setting options", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("Contact_number",contactNumber);
            editor.putString("Ringtone", chosenRingtone);
            editor.putLong("Response",response_time);
            flag=editor.commit();
            if(flag==true)
            {
                Toast.makeText(getApplicationContext(),"Settings Added",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        response_mode = parent.getItemAtPosition(position).toString();
        response_time=spinnervalues[position];

        // Showing selected spinner item
       // Toast.makeText(parent.getContext(), "Selected: " + response_time, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Function to select a phone number
    public void select_phonenum(View view) {
        Uri uri = Uri.parse("content://contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, 4);
    }
}
