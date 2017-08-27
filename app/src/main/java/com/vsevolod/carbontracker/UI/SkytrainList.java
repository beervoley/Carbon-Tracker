package com.vsevolod.carbontracker.UI;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.Model.Skytrain;
import com.vsevolod.carbontracker.Model.SkytrainCollection;
import com.vsevolod.carbontracker.R;
public class SkytrainList extends AppCompatActivity {
    private PreferenceManager preferenceManager = new PreferenceManager(this);
    private static final int REQUEST_CODE_FOR_SKYTRAIN_DATA = 1111;
    private static final int REQUEST_CODE_FOR_CHANGE_DATA = 2222;
    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final String SINGLETON_KEY = "singleton";
    private static final String SKYTRAIN_INDEX = "skytrain index";
    private static final int ROUTE_LIST_CODE = 10;
    private static final String SKYTRAIN_KEY = "skytrain";
    private Singleton singleton = Singleton.getInstance();
    private String name = null;
    private String startingStation = null;
    private String destinationStation = null;
    private int index = 0;
    private SkytrainCollection skytrainCollection = new SkytrainCollection();
    private String[] skytrainDescription = new String[skytrainCollection.countSkytrains()];
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skytrain_list2);
        getSingletion(getIntent());
        populateListView();
        //launchRouteActivity();
        registerForContextMenu(list);
        setupAddBtn();
        setUpActionBar();
    }
    private void setUpActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.skytrain_list_title);
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
                Intent intent = new Intent();
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                setResult(ROUTE_LIST_CODE, intent);
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void getSingletion(Intent intent) {
//        Gson gson = new Gson();
//        String json = intent.getStringExtra(SINGLETON_KEY);
//        singleton = gson.fromJson(json, singleton.getClass());
        skytrainCollection = singleton.getSkytrainsCollection();
    }
    private void populateListView() {
        skytrainDescription = skytrainCollection.getSkytrainDescriptions();
        ArrayAdapter<String> adaptor = new SkytrainList.MyListAdaptor();
        list = (ListView)findViewById(R.id.skytrainListView);
        list.setAdapter(adaptor);
    }
    private class MyListAdaptor extends ArrayAdapter<String> {
        public MyListAdaptor() {
            super(SkytrainList.this, R.layout.item_view, skytrainDescription);
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            index = position;
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }
            String currentSTDescription = skytrainDescription[position];
            final TextView text = (TextView) itemView.findViewById(R.id.item_Info);
            text.setText(currentSTDescription);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_Icon);
            imageView.setImageResource(R.mipmap.skytrain_icon);
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
                skytrainCollection.hideSkytrain(info.position);
                Log.i("app","deleted index is：" + index);
                singleton.setSkytrainsCollection(skytrainCollection);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                break;
            case R.id.edit_item:
                Intent intent = AddSkytrainActivity.makeIntent(SkytrainList.this, skytrainCollection,info.position);
                Log.i("app", "edited index is: "+ index);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                startActivityForResult(intent,REQUEST_CODE_FOR_CHANGE_DATA);
                break;
        }
        return true;
    }
    //    private void launchRouteActivity() {
//        ListView listView = (ListView)findViewById(R.id.skytrainListView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = RouteListActivity.makeIntent(SkytrainList.this);
//                updateSkytrainValues(position);
//                Bus bus = new Bus(name, startingStation, destinationStation, true);
//                singleton.setSkytrainsCollection(skytrainCollection);
//                Gson gson = new Gson();
//                String json = gson.toJson(bus);
//                String jsonSingleton = gson.toJson(singleton);
//                intent.putExtra(SKYTRAIN_KEY, json);
//                intent.putExtra(SINGLETON_KEY, jsonSingleton);
//                preferenceManager.saveObject(SINGLETON_KEY, singleton);
//            }
//        });
//    }
    private void updateSkytrainValues(int position) {
        Skytrain skytrain = singleton.getSkytrainsCollection().getSkytrain(position);
        name = skytrain.getName();
        startingStation = skytrain.getStartingStation();
        destinationStation = skytrain.getDestinationStation();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        getSingletion(data);
        switch (requestCode){
            case REQUEST_CODE_FOR_SKYTRAIN_DATA:
                if(resultCode == Activity.RESULT_OK){
                    populateListView();
                    break;
                }else {
                    Log.i("My App","Activity cancelled");
                }
                break;
            case REQUEST_CODE_FOR_CHANGE_DATA:
                if(resultCode == Activity.RESULT_OK){
                    Skytrain skytrain = singleton.getJourneyCollection().getLastJourney().getSkytrain();
                    skytrainCollection.changeSkytrain(skytrain, data.getIntExtra(SKYTRAIN_INDEX, -5));
//                    busCollection.deleteBus(busCollection.countBuses() - 1);
                    singleton.setSkytrainsCollection(skytrainCollection);
                    preferenceManager.saveObject(SINGLETON_KEY, singleton);
                    populateListView();
                    break;
                }
                else {
                    Log.i("My App","Activity cancelled");
                }
                break;
            case ROUTE_LIST_CODE:
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setupAddBtn(){
        Button addBtn = (Button)findViewById(R.id.skytrainList_addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddSkytrainActivity.generateIntent(SkytrainList.this);
//                Gson gson = new Gson();
//                intent.putExtra(SINGLETON_KEY, gson.toJson(singleton));
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                startActivityForResult(intent, REQUEST_CODE_FOR_SKYTRAIN_DATA);
            }
        });
    }
    public static Intent generateIntent(Context context){
        return new Intent(context, SkytrainList.class);
    }
    @Override
    public void onBackPressed() {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onBackPressed();
    }
}