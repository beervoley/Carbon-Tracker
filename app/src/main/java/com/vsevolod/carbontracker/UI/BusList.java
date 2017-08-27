package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bus;
import com.vsevolod.carbontracker.Model.BusCollection;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.R;

public class BusList extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private static final int REQUEST_CODE_FOR_BUS_DATA = 1111;
    private static final int REQUEST_CODE_FOR_CHANGE_DATA = 2222;
    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final String SINGLETON_KEY = "singleton";
    private static final int ROUTE_LIST_CODE = 10;
    private static final String BUS_KEY = "bus";

    private static final String BUS_INDEX = "bus index";
    private Singleton singleton = Singleton.getInstance();

    private String name = null;
    private String startingStop = null;
    private String destinationStop = null;
    private ActionBar bar;
    private int index = 0;

    private BusCollection busCollection = new BusCollection();
    private String[] busDescription = new String[busCollection.countBuses()];
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list2);

        restoreBusCollection(getIntent());

        populateListView();
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
        bar.setTitle(R.string.title_bus_list);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        //hide those graph buttons on the action bar
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

    private void restoreBusCollection(Intent intent) {
        busCollection = singleton.getBusesCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }

    private void populateListView() {
        busDescription = busCollection.getBusDescriptions();
        ArrayAdapter<String> adaptor = new BusList.MyListAdaptor();
        list = (ListView)findViewById(R.id.busListView);
        list.setAdapter(adaptor);
    }

    private class MyListAdaptor extends ArrayAdapter<String>{
        public MyListAdaptor() {
            super(BusList.this,R.layout.item_view,busDescription);
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
            String currentBusDescription = busDescription[position];

            final TextView text = (TextView)itemView.findViewById(R.id.item_Info);
            text.setText(currentBusDescription);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_Icon);
            imageView.setImageResource(R.mipmap.bus_icon);

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
                busCollection.hideBus(info.position);
                Log.i("app","deleted index is：" + index);
                singleton.setBusesCollection(busCollection);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                break;
            case R.id.edit_item:
                Intent intent = AddBusActivity.makeIntent(BusList.this, busCollection,info.position);
                Log.i("app", "edited index is: "+ index);
                startActivityForResult(intent,REQUEST_CODE_FOR_CHANGE_DATA);
                break;
        }

        return true;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        restoreBusCollection(data);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_FOR_BUS_DATA:
                if(resultCode == Activity.RESULT_OK){
                    populateListView();
                    break;
                }else {
                    Log.i("My App","Activity cancelled");
                }
                break;

            case REQUEST_CODE_FOR_CHANGE_DATA:
                if(resultCode == Activity.RESULT_OK){
                    Bus bus = singleton.getJourneyCollection().getLastJourney().getBus();


                    busCollection.changeBus(bus, data.getIntExtra(BUS_INDEX, -5));
                    busCollection.deleteBus(busCollection.countBuses() - 1);
                    singleton.setBusesCollection(busCollection);
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
    }

    private void setupAddBtn(){
        Button addBtn = (Button)findViewById(R.id.busList_addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddBusActivity.generateIntent(BusList.this);
                startActivityForResult(intent, REQUEST_CODE_FOR_BUS_DATA);
            }
        });
    }

    public static Intent generateIntent(Context context){
        return new Intent(context, BusList.class);
    }

    @Override
    public void onBackPressed() {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onBackPressed();
    }

}
