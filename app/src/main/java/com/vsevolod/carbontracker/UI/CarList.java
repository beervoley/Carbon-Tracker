package com.vsevolod.carbontracker.UI;
/**
 *  Display a list of current existing cars.
 *  Short click will launch to the Route activity.
 *  Long click allow user delete of edit the existing car.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Car;
import com.vsevolod.carbontracker.Model.CarsCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.R;
import com.vsevolod.carbontracker.Model.Singleton;


public class CarList extends AppCompatActivity {


    private PreferenceManager preferenceManager = new PreferenceManager(this);


    private static final String DATE_KEY = "date";
    private static final int REQUEST_CODE_FOR_DATA = 1111;
    private static final int REQUEST_CODE_FOR_CHANGE_DATA = 2222;
    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final String CAR_KEY = "car";
    private static final String SINGLETON_KEY = "singleton";
    private static final int ROUTE_LIST_CODE = 10;


    private Singleton singleton = Singleton.getInstance();

    private String name  = null;
    private String make  = null;
    private String model = null;
    private int year = 0;
    private String fuelType = null;
    private double engineDisplacement = 0;
    private String transmission = null;
    private int cityMPG;
    private int highwayMPG;
    private ActionBar bar;
    private int index = 0;
    private ListView list;


    CarsCollection carCollection = new CarsCollection();
    private String[] carDescriptionList = new String[carCollection.countCars()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        restoreCarCollection(getIntent());
        populateListView();
        launchRouteActivity();
        registerForContextMenu(list);
        setupAddBtn();
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
        bar = getSupportActionBar();
        bar.setTitle(R.string.title_car_list);
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
                Intent intent = new Intent();
                Gson gson = new Gson();
                String json = gson.toJson(singleton);
                intent.putExtra(SINGLETON_KEY, json);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                setResult(MAIN_MENU_REQUEST_CODE_JOURNEY, intent);
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupAddBtn() {
        Button addBtn = (Button)findViewById(R.id.carList_add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddCarActivity.generateIntent(CarList.this);
                Gson gson = new Gson();
                String json = gson.toJson(singleton);
                intent.putExtra(SINGLETON_KEY, json);
                startActivityForResult(intent, REQUEST_CODE_FOR_DATA);
            }
        });
    }


    private void restoreCarCollection(Intent intent) {
        carCollection = singleton.getCarsCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }


    private void populateListView() {
        carDescriptionList = carCollection.getCarDescriptions();
        ArrayAdapter<String> adaptor = new MyListAdaptor();
        list = (ListView)findViewById(R.id.carListView);
        list.setAdapter(adaptor);
    }

    private class MyListAdaptor extends ArrayAdapter<String>{
        public MyListAdaptor() {
            super(CarList.this,R.layout.item_view,carDescriptionList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            index = position;
            Log.i("app", "position is: " + position);
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }
            String currentCarDescription = carDescriptionList[position];
            Car currentCar = carCollection.getCar(position);

            final TextView text = (TextView)itemView.findViewById(R.id.item_Info);
            text.setText(currentCarDescription);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_Icon);
            int id = currentCar.getIconID();
            imageView.setImageResource(id);

            return itemView;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.delete_item:
                carCollection.deleteCar(info.position);
                Log.i("app","deleted index is：" + index);
                singleton.setCarsCollection(carCollection);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                break;
            case R.id.edit_item:
                Intent intent = AddCarActivity.makeIntent(CarList.this,carCollection,info.position);
                Log.i("app", "edited index is: "+ index);
                startActivityForResult(intent,REQUEST_CODE_FOR_CHANGE_DATA);
                break;
        }

        return true;
    }


    private void launchRouteActivity() {
        ListView listView = (ListView)findViewById(R.id.carListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Intent intent = RouteListActivity.makeIntent(CarList.this);
                updateCarValues(position);
                Car car = new Car(name, make, model, transmission, engineDisplacement, year,
                        cityMPG, highwayMPG, fuelType, true);
                singleton.setCarsCollection(carCollection);
                Gson gson = new Gson();
                String json = gson.toJson(car);
                intent.putExtra(CAR_KEY, json);
                startActivityForResult(intent, ROUTE_LIST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        restoreCarCollection(data);

        switch (requestCode){
            case REQUEST_CODE_FOR_DATA:
                if(resultCode == Activity.RESULT_OK){
                    carCollection.addCar(AddCarActivity.getCarFromIntent(data));
                    singleton.setCarsCollection(carCollection);
                    preferenceManager.saveObject(SINGLETON_KEY, singleton);
                    populateListView();
                    break;
                }else {
                    Log.i("My App","Activity cancelled");
                }
                break;

            case REQUEST_CODE_FOR_CHANGE_DATA:
                if(resultCode == Activity.RESULT_OK){
                    carCollection.changeCar(AddCarActivity.getCarFromIntent(data), AddCarActivity.getIndexFromIntent(data));
                    singleton.setCarsCollection(carCollection);
                    preferenceManager.saveObject(SINGLETON_KEY, singleton);
                    populateListView();
                    break;
                }
                else {
                    Log.i("My App","Activity cancelled");
                }
                break;

            case ROUTE_LIST_CODE:
                Gson gson = new Gson();
                String json = data.getStringExtra(SINGLETON_KEY);
                singleton = gson.fromJson(json, singleton.getClass());
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Intent generateIntent(Context context){
        return new Intent(context, CarList.class);
    }


    private void updateCarValues(int position) {
        Car car = singleton.getCarsCollection().getCar(position);
        name = car.getName();
        make = car.getMake();
        model= car.getModel();
        year = car.getYear();
        fuelType = car.getFuelType();
        engineDisplacement = car.getEngineDisplacement();
        transmission = car.getTransmission();
        cityMPG = car.getCityMPG();
        highwayMPG = car.getHighwayMPG();
    }


    @Override
    public void onBackPressed() {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onBackPressed();
    }
}