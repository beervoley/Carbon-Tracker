package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Car;
import com.vsevolod.carbontracker.Model.DatePickerFragment;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.R;
import com.vsevolod.carbontracker.Model.RouteCollection;
import com.vsevolod.carbontracker.Model.Singleton;

import java.util.Date;
import java.util.List;

/**
 * Activity to display list of routes
 */

public class RouteListActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private RouteCollection routeCollection = new RouteCollection();
    private static final String SINGLETON_KEY = "singleton";
    private static final int GET_MY_JOURNEYS_BACK_PLEASE = 666;
    private Singleton singleton = Singleton.getInstance();
    private static final String DATE_KEY = "date";
    private static final int ROUTE_LIST_CODE = 10;
    private final int RESULT_CODE_ADD_ROUTE = 0;
    private final int RESULT_CODE_EDIT_ROUTE = 1;
    private final String JOURNEY_KEY = "journey";
    private static final String CAR_KEY = "car";
    private static Car car = new Car();
    private static Date date = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        getCar(getIntent());
        getRoutes(getIntent());;
        populateListView();
        clickRouteButtons();
        longClickRouteButtons();
        addRouteButton();
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
        bar.setTitle(R.string.title_route_list);
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
            //back to last activity
            case R.id.backButton:
                Gson gson = new Gson();
                String json = gson.toJson(singleton);
                Intent intent = new Intent();
                intent.putExtra(SINGLETON_KEY, json);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                setResult(ROUTE_LIST_CODE, intent);
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getRoutes(Intent intent) {
//        Gson gson = new Gson();
//        singleton = gson.fromJson(intent.getStringExtra(SINGLETON_KEY), singleton.getClass());
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        if (singleton.getRouteCollection().getDescriptionSize() != 0) {
            routeCollection = singleton.getRouteCollection();
        }

    }

    private void getCar(Intent intent) {
        Gson gson = new Gson();
        String json = intent.getStringExtra(CAR_KEY);
        car = gson.fromJson(json, car.getClass());
    }

    private void populateListView() {
        List<String> temp = routeCollection.getRouteDescription();
        String[] routeDescriptions = new String[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            routeDescriptions[i] = temp.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.route_view, R.id.textview_resource, routeDescriptions);
        ListView list = (ListView) findViewById(R.id.route_list);
        list.setAdapter(adapter);
    }

    private void clickRouteButtons() {
        ListView listView = (ListView) findViewById(R.id.route_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int k = i;
                view.setSelected(true);
                DatePickerFragment newFragment =
                        DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setDate(dayOfMonth);
                                date.setYear(year - 1900);
                                date.setMonth(month);
                                Intent intent = DisplayFootPrint.getIntent(RouteListActivity.this);
                                Gson gson = new Gson();
                                Journey currentJourney = new Journey(routeCollection.getRoute(k).getName(),
                                        car, routeCollection.getRoute(k), date);

                                singleton.addJourney(currentJourney);
                                singleton.setRouteCollection(routeCollection);

                                intent.putExtra(SINGLETON_KEY, gson.toJson(singleton));
                                intent.putExtra("select_index",k);
                                System.out.println(k);
                                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                                startActivityForResult(intent, GET_MY_JOURNEYS_BACK_PLEASE);

                            }
                        });
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    private void longClickRouteButtons() {
        ListView listview = (ListView) findViewById(R.id.route_list);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = AddRouteActivity.makeIntent(RouteListActivity.this, routeCollection,  i);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                startActivityForResult(intent, RESULT_CODE_EDIT_ROUTE);
                return true;
            }
        });
    }

    private void addRouteButton() {
        Button button = (Button) findViewById(R.id.btn_add_route);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddRouteActivity.makeIntent(RouteListActivity.this);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                startActivityForResult(intent, RESULT_CODE_ADD_ROUTE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);


        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case RESULT_CODE_ADD_ROUTE:
                routeCollection.addNewRoute(AddRouteActivity.getRouteFromIntent(data));
                singleton.setRouteCollection(routeCollection);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                break;
            case RESULT_CODE_EDIT_ROUTE:
                if (AddRouteActivity.getDeleteStatusFromIntent(data)) {
                    routeCollection.hideRoute(AddRouteActivity.getIndexFromIntent(data));
                } else {
                    routeCollection.changeRoute(AddRouteActivity.getRouteFromIntent(data), AddRouteActivity.getIndexFromIntent(data));
                    Log.i("app","right now index is:" + AddRouteActivity.getIndexFromIntent(data));
                }
                singleton.setRouteCollection(routeCollection);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                break;
            case GET_MY_JOURNEYS_BACK_PLEASE:
                Gson gson = new Gson();
                String json = data.getStringExtra(SINGLETON_KEY);
                singleton = gson.fromJson(json, singleton.getClass());
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                break;
        }
    }
    public static Intent makeIntent(Context context){
        return new Intent(context,RouteListActivity.class);
    }


    @Override
    public void onBackPressed() {
        Gson gson = new Gson();
        String json = gson.toJson(singleton);
        Intent intent = new Intent();
        intent.putExtra(SINGLETON_KEY, json);
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        setResult(ROUTE_LIST_CODE, intent);

        super.onBackPressed();
    }
}
