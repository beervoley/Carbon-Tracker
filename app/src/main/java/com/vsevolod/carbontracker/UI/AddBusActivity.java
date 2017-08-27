package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
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
import com.vsevolod.carbontracker.Model.Car;
import com.vsevolod.carbontracker.Model.CarsCollection;
import com.vsevolod.carbontracker.Model.DatePickerFragment;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.JourneyCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.R;

import java.util.Date;
import java.util.List;

import static android.webkit.MimeTypeMap.getSingleton;

public class AddBusActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private static final String BUS_NAME_KEY = "bus name";
    private static final String BUS_STARTING_STOP_KEY = "bus starting stop";
    private static final String BUS_DESTINATION_STOP_KEY = "bus destination stop";
    private static final String SINGLETON_KEY = "singleton";
    private static final String DATE_KEY = "date";
    private static final String DELETE_KEY = "delete";

    private Singleton singleton = Singleton.getInstance();

    private static final String BUS_NAME_INDEX = "bus name index";
    private static final String BUS_STARTING_STOP_INDEX = "bus starting stop index";
    private static final String BUS_DESTINATION_STOP_INDEX = "bus destination stop index";
    private static final String BUS_INDEX = "bus index";

    private static boolean isEdit = false;
    private static boolean elementEdit = false;

    private CSVTransReader reader = new CSVTransReader(this);
    private String currentBusName = null;
    private String currentBusStartingStop = null;
    private String currentBusDestinationStop = null;

    private int busIndex = 0;
    private int busNameIndex = 0;
    private int busStartingStopIndex = 0;
    private int busDestinationStopIndex = 0;

    private static Bus bus = new Bus();
    private BusCollection collection;
    private Date date = new Date();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        getSingleton(getIntent());

        readBusCSV(reader);
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
        bar.setTitle(R.string.add_bus_menu);
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

    private void setupOkBtn(){

        Button okBtn = (Button)findViewById(R.id.add_bus_okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataBack();
                finish();
            }
        });
    }

    private void sendDataBack(){
        Intent intent = new Intent();
        Bus bus = new Bus(currentBusName, currentBusStartingStop, currentBusDestinationStop, busStartingStopIndex, busDestinationStopIndex, busNameIndex, true);
        collection.addBus(bus);
        singleton.setBusesCollection(collection);

        Journey journey = new Journey(bus.getName(), bus, date);
        JourneyCollection journeyCollection = singleton.getJourneyCollection();
        journeyCollection.addJourney(journey);
        singleton.setJourneyCollection(journeyCollection);

        singleton.setLastJourney(journey);
        preferenceManager.saveObject(SINGLETON_KEY, singleton);


        if(isEdit){
            intent.putExtra(BUS_INDEX, busIndex);
        }
        setResult(Activity.RESULT_OK, intent);
    }

    private void setupCancelBtn() {
        Button cancelBtn = (Button)findViewById(R.id.add_bus_cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                finish();
            }
        });
    }

    private void getSingleton(Intent intent) {
        collection = singleton.getBusesCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);

    }

    private void setupEditText(){
        final EditText text = (EditText)findViewById(R.id.add_bus_date);
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


    private void updateNameSpinner(CSVTransReader reader){
        Spinner nameBusSpinner = (Spinner)findViewById(R.id.add_busName_spinner);
        updateSpinner(nameBusSpinner, reader.getSkytrains());
    }

    private void readBusCSV(CSVTransReader reader) {
        reader.readTheFile(R.raw.buscsv);
    }


    public static Intent generateIntent(Context context){
        isEdit = false;
        return new Intent(context, AddBusActivity.class);
    }

    public static Intent makeIntent(Context context, BusCollection collection, int index){
        isEdit = true;
        elementEdit = true;
        bus = collection.getBus(index);
        return new Intent(context,AddBusActivity.class);
    }

    public static Bus getBusFromIntent(Intent data){

        String name = data.getStringExtra(BUS_NAME_KEY);
        String startingStop = data.getStringExtra(BUS_STARTING_STOP_KEY);
        String destinationStop = data.getStringExtra(BUS_DESTINATION_STOP_KEY);

        int nameIndex = data.getIntExtra(BUS_NAME_INDEX, 0);
        int startingStopIndex = data.getIntExtra(BUS_STARTING_STOP_INDEX, 0);
        int destinationStopIndex = data.getIntExtra(BUS_DESTINATION_STOP_INDEX, 0);

        return new Bus(name, startingStop, destinationStop,
                startingStopIndex, destinationStopIndex, nameIndex, true);
    }

    public static int getIndexFromIntent(Intent data){
        return data.getIntExtra(DELETE_KEY, 0);
    }


    private void setupSpinnerCallBack(final CSVTransReader reader) {
        final Spinner nameBusSpinner = (Spinner) findViewById(R.id.add_busName_spinner);
        final Spinner startingBusSpinner = (Spinner) findViewById(R.id.add_bus_startingStop_Spinner);
        final Spinner destinationBusSpinner = (Spinner) findViewById(R.id.add_bus_destinationStop_spinner);

        nameBusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                busNameIndex = position;
                currentBusName = parent.getItemAtPosition(position).toString();
                updateSpinner(startingBusSpinner, reader.getCurrentStation(currentBusName));
                updateSpinner(destinationBusSpinner, reader.getCurrentStation(currentBusName));
                if (isEdit && elementEdit) {
                    nameBusSpinner.setSelection(bus.getNameIndex());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startingBusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                busStartingStopIndex = position;
                currentBusStartingStop = parent.getItemAtPosition(position).toString();
                if(isEdit && elementEdit){
                    startingBusSpinner.setSelection(bus.getStartingStopIndex());
                    elementEdit = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destinationBusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                busDestinationStopIndex = position;
                currentBusDestinationStop = parent.getItemAtPosition(position).toString();
                if(isEdit && elementEdit){
                    startingBusSpinner.setSelection(bus.getDestinationStopIndex());
                    elementEdit = false;
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

    @Override
    public void onBackPressed() {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);

        super.onBackPressed();
    }
}
