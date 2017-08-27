package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bike;
import com.vsevolod.carbontracker.Model.BikeCollection;
import com.vsevolod.carbontracker.Model.Bus;
import com.vsevolod.carbontracker.Model.BusCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.R;

public class BikeList extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private static final int REQUEST_CODE_FOR_BIKE_DATA = 1111;
    private static final int REQUEST_CODE_FOR_CHANGE_DATA = 2222;
    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final String SINGLETON_KEY = "singleton";
    private static final int ROUTE_LIST_CODE = 10;

    private static final String BIKE_KEY = "bike";

    private Singleton singleton = Singleton.getInstance();

    private String name = null;

    private int index = 0;

    private BikeCollection bikeCollection = new BikeCollection();
    private String[] bikeDescription = new String[bikeCollection.countBikes()];
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_list);

        getSingleton(getIntent());

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
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.title_bike_list);
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
                setResult(ROUTE_LIST_CODE, intent);
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getSingleton(Intent intent) {
        bikeCollection = singleton.getBikesCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }


    private void populateListView() {
        bikeDescription = bikeCollection.getBikeDescriptions();
        ArrayAdapter<String> adaptor = new BikeList.MyListAdaptor();
        list = (ListView)findViewById(R.id.bikeListView);
        list.setAdapter(adaptor);
    }

    private class MyListAdaptor extends ArrayAdapter<String>{
        public MyListAdaptor() {
            super(BikeList.this,R.layout.item_view,bikeDescription);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            index = position;
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }
            String currentBikeDescription = bikeDescription[position];

            final TextView text = (TextView)itemView.findViewById(R.id.item_Info);
            text.setText(currentBikeDescription);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_Icon);
            imageView.setImageResource(R.mipmap.bike_walk_icon);

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
                bikeCollection.hideBike(info.position);
                singleton.setBikesCollection(bikeCollection);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                break;
            case R.id.edit_item:
                Intent intent = AddBikeActivity.makeIntent(BikeList.this, bikeCollection,info.position);
                startActivityForResult(intent,REQUEST_CODE_FOR_CHANGE_DATA);
                break;
        }

        return true;
    }

    private void launchRouteActivity() {
        ListView listView = (ListView)findViewById(R.id.bikeListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Intent intent = RouteListActivity.makeIntent(BikeList.this);
                updateBikeValues(position);
                Bike bike = new Bike(name,true);
                singleton.setBikesCollection(bikeCollection);
                Gson gson = new Gson();
                String json = gson.toJson(bike);
                intent.putExtra(BIKE_KEY, json);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                startActivityForResult(intent, ROUTE_LIST_CODE);
            }
        });

    }

    private void updateBikeValues(int position) {
        Bike bike = singleton.getBikesCollection().getBike(position);
        name = bike.getName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSingleton(data);

        switch (requestCode){
            case REQUEST_CODE_FOR_BIKE_DATA:
                if(resultCode == Activity.RESULT_OK){
                    populateListView();
                    break;
                }else {
                    Log.i("My App","Activity cancelled");
                }
                break;

            case REQUEST_CODE_FOR_CHANGE_DATA:
                if(resultCode == Activity.RESULT_OK){
                    Bike bike = singleton.getJourneyCollection().getLastJourney().getBike();


                    bikeCollection.changeBike(bike, data.getIntExtra(BIKE_KEY, -5));
                    singleton.setBikesCollection(bikeCollection);
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

    private void setupAddBtn(){
        Button addBtn = (Button)findViewById(R.id.bikeList_add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddBikeActivity.generateIntent(BikeList.this);
                startActivityForResult(intent, REQUEST_CODE_FOR_BIKE_DATA);
            }
        });
    }

    public static Intent generateIntent(Context context){
        return new Intent(context, BikeList.class);
    }

    @Override
    public void onBackPressed() {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onBackPressed();
    }

}
