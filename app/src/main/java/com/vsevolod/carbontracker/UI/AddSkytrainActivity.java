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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bus;
import com.vsevolod.carbontracker.Model.BusCollection;
import com.vsevolod.carbontracker.Model.CSVTransReader;
import com.vsevolod.carbontracker.Model.DatePickerFragment;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.JourneyCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.Model.Skytrain;
import com.vsevolod.carbontracker.Model.SkytrainCollection;
import com.vsevolod.carbontracker.R;

import java.util.Date;
import java.util.List;

public class AddSkytrainActivity extends AppCompatActivity {


    private PreferenceManager preferenceManager = new PreferenceManager(this);


    private static final String SKYTRAIN_NAME_KEY = "skytrain name";
    private static final String SKYTRAIN_STARTING_STATION_KEY = "skytrain starting station";
    private static final String SKYTRAIN_DESTINATION_STATION_KEY = "skytarin destination station";
    private static final String SINGLETON_KEY = "singleton";
    private Singleton singleton = Singleton.getInstance();

    private static final String SKYTRAIN_NAME_INDEX = "skytrain name index";
    private static final String SKYTRAIN_STARTING_STATION_INDEX = "skytrain starting station index";
    private static final String SKYTRAIN_DESTINATION_STATION_INDEX = "skytrain destination station index";
    private static final String SKYTRAIN_INDEX = "skytrain index";
    private static boolean isEdit = false;
    private static boolean elementEdit = false;

    private CSVTransReader reader = new CSVTransReader(this);
    private String currentSTName = null;
    private String currentSTStartingStation = null;
    private String currentSTDestinationStation = null;

    private static Skytrain skytrain = new Skytrain();

    private Spinner nameSTSpinner;
    private Spinner startingSTSpinner;
    private Spinner destinationSTSpinner;

    private int skytrainIndex = 0;
    private int nameSTIndex = 0;
    private int startingSTIndex = 0;
    private int destinationSTIndex = 0;
    private Date date = new Date();
    private SkytrainCollection collection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skytrain);

        getSingleton(getIntent());

        readSkytrainCSV(reader);
        updateNameSpinner(reader);
        setupSpinnerCallBack(reader);

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
        bar.setTitle(R.string.add_skytrain_title);
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
            //back button
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

    private void setupOkBtn() {
        Button okBtn = (Button)findViewById(R.id.add_skytrain_ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataBack();
                finish();
            }

            private void sendDataBack() {


                Intent intent = new Intent();
                Skytrain skytrain = new Skytrain(currentSTName, currentSTStartingStation, currentSTDestinationStation, startingSTIndex, destinationSTIndex, nameSTIndex,  true);
                System.out.println("Starting index for skytrain is " + startingSTIndex);
                System.out.println("Ending index for skytrain is " + destinationSTIndex);



                collection.addSkytrain(skytrain);
                singleton.setSkytrainsCollection(collection);

                Journey journey = new Journey(skytrain.getName(), skytrain, date);
                JourneyCollection journeyCollection = singleton.getJourneyCollection();
                journeyCollection.addJourney(journey);
                singleton.setJourneyCollection(journeyCollection);

                singleton.setLastJourney(journey);
                Gson gson = new Gson();
                String json = gson.toJson(singleton);
                intent.putExtra(SINGLETON_KEY, json);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);


                if(isEdit){
                    intent.putExtra(SKYTRAIN_INDEX, skytrainIndex);
                }
                setResult(Activity.RESULT_OK, intent);
            }
        });
    }

    private void setupCancelBtn() {
        Button cancelBtn = (Button)findViewById(R.id.add_skytrain_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                finish();
            }
        });
    }

    private void getSingleton(Intent intent) {
        collection = singleton.getSkytrainsCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }
    private void setupEditText(){
        final EditText text = (EditText)findViewById(R.id.add_skytrain_date);
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


    public static Skytrain getSkytrainFromIntent(Intent data){

        String name = data.getStringExtra(SKYTRAIN_NAME_KEY);
        String startingStation = data.getStringExtra(SKYTRAIN_STARTING_STATION_KEY);
        String destinationStation = data.getStringExtra(SKYTRAIN_DESTINATION_STATION_KEY);

        int nameIndex = data.getIntExtra(SKYTRAIN_NAME_INDEX, 0);
        int startingStationIndex = data.getIntExtra(SKYTRAIN_STARTING_STATION_INDEX, 0);
        int destinationStationIndex = data.getIntExtra(SKYTRAIN_DESTINATION_STATION_INDEX, 0);

        return new Skytrain(name, startingStation, destinationStation,
                startingStationIndex, destinationStationIndex, nameIndex, true);
    }

    private void updateNameSpinner(CSVTransReader reader){
        Spinner nameSTSpinner = (Spinner)findViewById(R.id.skytrain_name_spinner);
        updateSpinner(nameSTSpinner, reader.getSkytrains());
    }

    private void readSkytrainCSV(CSVTransReader reader) {
        reader.readTheFile(R.raw.skytrain);
    }

    private void setupSpinnerCallBack(final CSVTransReader reader) {
        nameSTSpinner = (Spinner) findViewById(R.id.skytrain_name_spinner);
        startingSTSpinner = (Spinner) findViewById(R.id.starting_station_spinner);
        destinationSTSpinner = (Spinner) findViewById(R.id.destination_station_spinner);

        nameSTSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nameSTIndex = position;
                System.out.println("nameSTIndex: " + nameSTIndex);
                currentSTName = parent.getItemAtPosition(position).toString();
                updateSpinner(startingSTSpinner, reader.getCurrentStation(currentSTName));
                updateSpinner(destinationSTSpinner, reader.getCurrentStation(currentSTName));
                if (isEdit && elementEdit) {
                    nameSTSpinner.setSelection(skytrain.getNameIndex());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startingSTSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Starting index for skytrain is " + startingSTIndex);

                startingSTIndex = position;

                currentSTStartingStation = parent.getItemAtPosition(position).toString();
                if(isEdit && elementEdit){
                    startingSTSpinner.setSelection(skytrain.getStartingStationIndex());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destinationSTSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Ending index for skytrain is " + destinationSTIndex);
                destinationSTIndex = position;

                currentSTDestinationStation = parent.getItemAtPosition(position).toString();
                if(isEdit && elementEdit){
                    startingSTSpinner.setSelection(skytrain.getDestinationStationIndex());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

        private <T> void updateSpinner(Spinner spinner, List<T> list){
            ArrayAdapter<T> adapter = new ArrayAdapter<T>(
                    this,
                    R.layout.support_simple_spinner_dropdown_item,
                    list
            );
        spinner.setAdapter(adapter);
        }



    public static Intent generateIntent(Context context) {
        isEdit = false;
        return new Intent(context, AddSkytrainActivity.class);
    }


    public static Intent makeIntent(Context context, SkytrainCollection collection, int index){
        isEdit = true;
        elementEdit = true;
        skytrain = collection.getSkytrain(index);
        return new Intent(context,AddSkytrainActivity.class);
    }
        @Override
        public void onBackPressed() {
            preferenceManager.saveObject(SINGLETON_KEY, singleton);
            super.onBackPressed();
        }
    }

