package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bike;
import com.vsevolod.carbontracker.Model.BikeCollection;
import com.vsevolod.carbontracker.Model.Bill;
import com.vsevolod.carbontracker.Model.BusCollection;
import com.vsevolod.carbontracker.Model.DatePickerFragment;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.JourneyCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.Model.Skytrain;
import com.vsevolod.carbontracker.R;

import java.util.Date;

public class AddBikeActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private static final String SINGLETON_KEY = "singleton";
    private static final String BIKE_INDEX = "bike index";
    private static final String BIKE_NAME_KEY = "bike name";
    private static final String DELETE_KEY = "delete";
    private static final String BIKE_KEY = "bike";
    private Singleton singleton = Singleton.getInstance();
    private static Bike bike = new Bike();
    private String currentName = null;
    private static boolean isEdit;
    private static boolean elementEdit;
    private int index = 0;
    private BikeCollection collection;
    private Date date = new Date();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

        getSingleton(getIntent());
        if(isEdit){
            displayCurrentBike();
        }
        setupOkBtn();
        setupCancelBtn();
        setupEditText();
        setUpActionBar();
        setUpFullScreen();
    }
    private void setUpFullScreen() {
        if(Build.VERSION.SDK_INT <19){
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        }
        else {
            View higherView = getWindow().getDecorView();
            int fullScreen = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            higherView.setSystemUiVisibility(fullScreen);
        }
    }

    private void setUpActionBar() {
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.add_bike_title);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        for(int i = 0; i <= 7;i++){
            menu.getItem(i).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //ack to last activity
            case R.id.backButton:
                Gson gson = new Gson();
                String json = gson.toJson(singleton);
                Intent intent = new Intent();
                intent.putExtra(SINGLETON_KEY, json);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                setResult(Activity.RESULT_CANCELED, intent);
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private float enterDistance() {
        EditText distance = (EditText)findViewById(R.id.add_bike_distance);
        return Float.parseFloat(distance.getText().toString());
    }

    private void getSingleton(Intent intent){
        Gson gson = new Gson();
        String json = intent.getStringExtra(SINGLETON_KEY);
        singleton = gson.fromJson(json, singleton.getClass());
        collection = singleton.getBikesCollection();
    }

    private void displayCurrentBike(){
        EditText name = (EditText)findViewById(R.id.bike_name);
        name.setText(bike.getName());
    }

    private void setupEditText(){
        final EditText text = (EditText)findViewById(R.id.add_bike_date);
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH) + 1;
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment =
                        DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                text.setText(dayOfMonth + "-" + (month + 1) + "-" + year);

                                date.setDate(dayOfMonth);
                                date.setYear(year - 1900);
                                date.setMonth(month);
                            }
                        });
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });

    }


    private void setupCancelBtn() {
        ImageButton cancelBtn = (ImageButton)findViewById(R.id.bike_cancelbtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Gson gson = new Gson();
                String json = gson.toJson(singleton);
                intent.putExtra(SINGLETON_KEY, json);
                setResult(Activity.RESULT_CANCELED, intent);
            }
        });
    }

    private void setupOkBtn() {
        ImageButton okBtn = (ImageButton)findViewById(R.id.bike_okbtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText)findViewById(R.id.bike_name);
                if(name.length() == 0){
                    Toast.makeText(AddBikeActivity.this,"please enter a name",Toast.LENGTH_SHORT).show();
                }else{
                    sendDataBack();
                    finish();
                }
            }
        });
    }

    private void sendDataBack(){
        EditText et = (EditText) findViewById(R.id.bike_name);
        currentName = et.getText().toString();
        Intent intent = new Intent();

        Bike bike = new Bike(currentName, true);
        Journey journey = new Journey(currentName, bike, date, enterDistance());




        collection.addBike(bike);
        singleton.setBikesCollection(collection);

        JourneyCollection journeyCollection = singleton.getJourneyCollection();
        journeyCollection.addJourney(journey);
        singleton.setJourneyCollection(journeyCollection);

        singleton.setLastJourney(journey);
        Gson gson = new Gson();
        String json = gson.toJson(singleton);
        intent.putExtra(SINGLETON_KEY, json);

        preferenceManager.saveObject(SINGLETON_KEY, singleton);


        if(isEdit){
            intent.putExtra(BIKE_NAME_KEY, index);
        }
        setResult(Activity.RESULT_OK, intent);
    }

    public static Bike getBikeFromIntent(Intent data){
        String name = data.getStringExtra(BIKE_NAME_KEY);
        return new Bike(name, true);
    }

    public static Intent generateIntent(Context context){
        isEdit = false;
        return new Intent(context, AddBikeActivity.class);
    }

    public static int getIndexFromIntent(Intent data){
        return data.getIntExtra(DELETE_KEY, 0);
    }

    public static Intent makeIntent(Context context, BikeCollection collection, int index){
        isEdit = true;
        elementEdit = true;
        bike = collection.getBike(index);
        return new Intent(context,AddBikeActivity.class);
    }

    @Override
    public void onBackPressed() {
        Gson gson = new Gson();
        String json = gson.toJson(singleton);
        Intent intent = new Intent();
        intent.putExtra(SINGLETON_KEY, json);
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        setResult(Activity.RESULT_CANCELED, intent);
        super.onBackPressed();
    }

}
