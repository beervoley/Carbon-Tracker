package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.CSVReader;
import com.vsevolod.carbontracker.Model.Car;
import com.vsevolod.carbontracker.Model.CarsCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.R;
import com.vsevolod.carbontracker.Model.Singleton;

import java.util.List;

/**
 * Activity to add a car.
 */

public class AddCarActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);
    private static final String DELETE_KEY = "delete";
    private String CAR_INDEX = "carIndex";
    private static String ICON_KEY = "icon";
    private static final String SINGLETON_KEY = "singleton";
    private Singleton singleton = Singleton.getInstance();

    private static final String DATE_KEY = "date";

    private static final String NAME_KEY = "name";
    private static final String MAKE_KEY = "make";
    private static final String MODEL_KEY = "model";
    private static final String YEAR_KEY = "year";
    private static final String ENGINE_DISPLACEMENT_KEY = "engine displacement";
    private static final String TRANSMISSION_KEY = "transmission";
    private static final String CITY_MPG_KEY = "city";
    private static final String HIGHWAY_MPG_KEY = "highway";
    private static final String FUEL_TYPE_KEY = "fuel type";

    private static final String CAR_INDEX_KEY = "car index";
    private static final String MAKE_INDEX_KEY = "make index";
    private static final String MODEL_INDEX_KEY = "model index";
    private static final String YEAR_INDEX_KEY = "year index";
    private static final String ICON_INDEX_KEY = "icon index";
    private static final String TRANSMISSION_INDEX_KEY = "transmission index";
    private static final String ENGINE_DISPLACEMENT_INDEX = "engine displacement index";

    private String currentName = null;
    private static boolean isEdit = false;

    private CSVReader reader = new CSVReader(this);
    private String currentMake = null;
    private String currentModel = null;
    private int currentYear = 0;
    private String currentEngineDisplacement = null;
    private String currentTransmission = null;
    private String currentFuelType = null;
    private int currentIconID = 0;
    private static Car car = new Car();

    private Spinner makeSpinner;
    private Spinner modelSpinner;
    private Spinner yearSpinner;
    private Spinner engineDisplacementSpinner;
    private Spinner transmissionSpinner;
    private Spinner iconSpinner;

    private int carIndex;
    private int makeIndex = 0;
    private int modelIndex = 0;
    private int yearIndex = 0;
    private int transmissionIndex = 0;
    private int engineDisplacementIndex = 0;
    private int iconIndex = 0;
    private static boolean elementEdit = false;
    private int position;
    private int[] iconIds = {R.mipmap.car_icon1,R.mipmap.car_icon2,R.mipmap.car_icon3,
                                R.mipmap.car_icon4,R.mipmap.car_icon5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        getSingleton(getIntent());

        if (isEdit) {
            displayCurrentCar();
        }
        readCSV(reader);
        updateMakeSpinner(reader);
        setupSpinnersCallBack(reader);

        setUpOkButton();
        setUpCancelButton();
        setUpActionBar();
        setUpIconSpinner();
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

    private void setUpIconSpinner() {
        iconSpinner = (Spinner)findViewById((R.id.spinner_icon));
        iconSpinner.setAdapter(imageAdapter);
        iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout layout = (LinearLayout)view;
                View first_v = layout.getChildAt(0);
                iconIndex = position;
                currentIconID = iconIds[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private BaseAdapter imageAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return iconIds.length;
        }

        @Override
        public Object getItem(int position) {
            return iconIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //set spinner row
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout = new LinearLayout(AddCarActivity.this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            ImageView image = new ImageView(AddCarActivity.this);
            image.setImageResource(iconIds[position]);
            //resize image
             int width = 200;
            int height = 200;
            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(getResources(),iconIds[position]);
            bmp = Bitmap.createScaledBitmap(bmp,width,height,true);
            image.setImageBitmap(bmp);
            layout.addView(image);
            return layout;
        }
    };

    private void setUpActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.add_car_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        for (int i = 0; i <= 7; i++) {
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

    private void getSingleton(Intent intent) {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }

    private void setUpOkButton() {
        ImageButton btn = (ImageButton) findViewById(R.id.ok_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.addCar_nickName);
                if (name.length() == 0) {
                    Toast.makeText(AddCarActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                } else {
                    sendDataBack();
                    finish();
                }
            }
        });
    }

    private void setUpCancelButton() {
        ImageButton btn = (ImageButton) findViewById(R.id.cancel_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                finish();
            }
        });
    }

    // click ok and send data back
    private void sendDataBack() {
        Pair<Integer, Integer> mpgs = reader.getMPGS(currentMake, currentModel, "" + currentYear,
                currentEngineDisplacement, currentTransmission);

        EditText editText = (EditText) findViewById(R.id.addCar_nickName);
        currentName = editText.getText().toString();

        currentFuelType = reader.getFuelType(currentModel, currentMake, "" + currentYear, currentEngineDisplacement,
                currentTransmission);

        Intent intent = new Intent();
        intent.putExtra(NAME_KEY, currentName);
        intent.putExtra(ICON_KEY, currentIconID);
        intent.putExtra(MAKE_KEY, currentMake);
        intent.putExtra(MODEL_KEY, currentModel);
        intent.putExtra(YEAR_KEY, currentYear);
        intent.putExtra(ENGINE_DISPLACEMENT_KEY, currentEngineDisplacement);
        intent.putExtra(TRANSMISSION_KEY, currentTransmission);
        intent.putExtra(CITY_MPG_KEY, mpgs.first);
        intent.putExtra(HIGHWAY_MPG_KEY, mpgs.second);
        intent.putExtra(FUEL_TYPE_KEY, currentFuelType);
        System.out.println(currentFuelType);

        intent.putExtra(ICON_INDEX_KEY, iconIndex);
        intent.putExtra(MAKE_INDEX_KEY, makeIndex);
        intent.putExtra(MODEL_INDEX_KEY, modelIndex);
        intent.putExtra(YEAR_INDEX_KEY, yearIndex);
        intent.putExtra(TRANSMISSION_INDEX_KEY, transmissionIndex);
        intent.putExtra(ENGINE_DISPLACEMENT_INDEX, engineDisplacementIndex);


        preferenceManager.saveObject(SINGLETON_KEY, singleton);

        if (isEdit) {
            intent.putExtra(CAR_INDEX, carIndex);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void displayCurrentCar() {
        EditText name = (EditText) findViewById(R.id.addCar_nickName);
        name.setText(car.getName());
    }

    public static Car getCarFromIntent(Intent data) {

        String name = data.getStringExtra(NAME_KEY);
        String make = data.getStringExtra(MAKE_KEY);
        String model = data.getStringExtra(MODEL_KEY);
        int year = data.getIntExtra(YEAR_KEY, 0);
        String transmission = data.getStringExtra(TRANSMISSION_KEY);
        double engineDisplacement = Double.parseDouble(data.getStringExtra(ENGINE_DISPLACEMENT_KEY));
        int cityMPG = data.getIntExtra(CITY_MPG_KEY, 0);
        int highwayMPG = data.getIntExtra(HIGHWAY_MPG_KEY, 0);
        int iconID = data.getIntExtra(ICON_KEY,0);

        String fuelType = data.getStringExtra(FUEL_TYPE_KEY);
        int iconIndex = data.getIntExtra(ICON_INDEX_KEY, 0);
        int makeIndex = data.getIntExtra(MAKE_INDEX_KEY, 0);
        int modelIndex = data.getIntExtra(MODEL_INDEX_KEY, 0);
        int yearIndex = data.getIntExtra(YEAR_INDEX_KEY, 0);
        int transmissionIndex = data.getIntExtra(TRANSMISSION_INDEX_KEY, 0);
        int engineDisplacementIndex = data.getIntExtra(ENGINE_DISPLACEMENT_INDEX, 0);

        return new Car(name, make, model, transmission,
                engineDisplacement, year, cityMPG, highwayMPG,
                fuelType, true, makeIndex, modelIndex, yearIndex,
                transmissionIndex, engineDisplacementIndex, iconID, iconIndex);
    }

    public static int getIndexFromIntent(Intent data) {
        return data.getIntExtra(DELETE_KEY, 0);
    }

    public static Intent generateIntent(Context context) {
        isEdit = false;
        return new Intent(context, AddCarActivity.class);
    }

    public static Intent makeIntent(Context context, CarsCollection collection, int index) {
        isEdit = true;
        elementEdit = true;
        car = collection.getCar(index);
        return new Intent(context, AddCarActivity.class);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    private void updateMakeSpinner(CSVReader reader) {
        Spinner makeSpinner = (Spinner) findViewById(R.id.make_spinner);
        updateSpinner(makeSpinner, reader.getMakes());
    }


    private void readCSV(CSVReader reader) {
        reader.readTheFile();
    }


    private void setupSpinnersCallBack(final CSVReader reader) {
        makeSpinner = (Spinner) findViewById(R.id.make_spinner);
        modelSpinner = (Spinner) findViewById(R.id.model_spinner);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner);
        engineDisplacementSpinner = (Spinner) findViewById(R.id.engine_displacement_spinner);
        transmissionSpinner = (Spinner) findViewById(R.id.transmission_spinner);
        //  iconSpinner = (Spinner) findViewById(R.id.icon_spinner);

        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeIndex = position;
                currentMake = parent.getItemAtPosition(position).toString();
                updateSpinner(modelSpinner, reader.getCurrentModels(currentMake));
                if (isEdit && elementEdit) {
                    makeSpinner.setSelection(car.getMakeIndex());
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelIndex = position;
                currentModel = parent.getItemAtPosition(position).toString();
                updateSpinner(yearSpinner, reader.getCurrentYears(currentModel, currentMake));
                if (isEdit && elementEdit) {
                    modelSpinner.setSelection(car.getModelIndex());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearIndex = position;
                currentYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                updateSpinner(engineDisplacementSpinner, reader.getCurrentEngineDisplacements(currentModel, currentMake,
                        "" + currentYear));
                if (isEdit && elementEdit) {
                    yearSpinner.setSelection(car.getYearIndex());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        engineDisplacementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                engineDisplacementIndex = position;
                currentEngineDisplacement = parent.getItemAtPosition(position).toString();
                updateSpinner(transmissionSpinner, reader.getCurrentTransmissions(currentModel, currentMake,
                        "" + currentYear, currentEngineDisplacement));
                if (isEdit && elementEdit) {
                    engineDisplacementSpinner.setSelection(car.getEngineDisplacementIndex());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        transmissionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTransmission = parent.getItemAtPosition(position).toString();

                if (isEdit && elementEdit) {
                    transmissionSpinner.setSelection(car.getTransmissionIndex());
                    elementEdit = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // Shit

    private <T> void updateSpinner(Spinner spinner, List<T> list) {
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
