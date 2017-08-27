package com.vsevolod.carbontracker.UI;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Car;
import com.vsevolod.carbontracker.Model.CarsCollection;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.JourneyCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Route;
import com.vsevolod.carbontracker.Model.RouteCollection;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.R;
import java.util.ArrayList;
import java.util.List;
public class JourneyList extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private ImageButton myCar;
    private ImageButton skytrain;
    private ImageButton bike;
    private ImageButton bus;
    private Button show;
    private int flag = 0;

    private static final int REQUEST_CODE_FOR_CAR_DATA = 1111;
    private static final int REQUEST_CODE_FOR_SKYTRAIN_DATA = 3333;
    private static final int REQUEST_CODE_FOR_BUS_DATA = 4444;
    private static final int REQUEST_CODE_FOR_BIKE_DATA = 5555;


    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final int REQUEST_CODE_ADD_JOURNEY = 188;
    private static final int SHOW_SELECT_JOURNEY_FOOTPRINT = 666;
    private static final int REQUEST_CODE_EDIT_DATA = 155;
    private static final String SINGLETON_KEY = "singleton";
    public static final String SELECT_INDEX = "select_index";
    private static Car car = new Car();
    private static Route route = new Route();
    private static float emission = 0;
    private  String name = null;
    private ActionBar bar;
    private Singleton singleton = Singleton.getInstance();
    private JourneyCollection journeyCollection = new JourneyCollection();
    private RouteCollection routeCollection = new RouteCollection();
    private CarsCollection carCollection = new CarsCollection();

    private List<String> journey_list;
    private List<String> journey_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_list);
        restoreSharedPreferences();
        getSingleton(getIntent());
        clicksSelectedJourney();
        popButtons();
        setupCarButton();
        setUpBusButton();
        setupSkytrainButton();
        setupBikeButton();
        populateListView();

        setLongClick();
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

    private void restoreSharedPreferences() {
        if(preferenceManager.getObject(MODE_PRIVATE, SINGLETON_KEY) != null) {
            singleton = preferenceManager.getObject(MODE_PRIVATE, SINGLETON_KEY);
        }
        Singleton.setInstance(singleton);

    }
    private void setTextStyle() {
        TextView textview = (TextView) findViewById(R.id.textView12);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Minnie.ttf");
        textview.setTypeface(typeface);
    }
    private void setUpActionBar() {
        bar = getSupportActionBar();
        bar.setTitle(R.string.title_journey_list);
        //up button, back to main menu
        bar.setDisplayHomeAsUpEnabled(true);
    }


    private void getSingleton(Intent intent) {
//        Gson gson = new Gson();
//        String json = intent.getStringExtra(SINGLETON_KEY);
//        singleton = gson.fromJson(json, singleton.getClass());
//        System.out.println(singleton);
        journeyCollection = Singleton.getInstance().getJourneyCollection();
//        System.out.println("In journey list : " + journeyCollection.getJourneyDescription().size());
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        populateListView();

    }

    //refresh Journey List
    private void populateListView() {
        journey_list = journeyCollection.getJourneyDescription();
        journey_type = journeyCollection.getJourneyType();
        String[] descriptions = new String[journey_list.size()];
        Log.i("app", "journey list size is: " + journey_list.size());
        for (int i = 0; i < journey_list.size(); i++) {
            descriptions[i] = journey_list.get(i);
        }
        ArrayAdapter<String> adapter = new MyListAdaptor();
        ListView list = (ListView) findViewById(R.id.journey_list);

        list.setAdapter(adapter);
    }

    public static Intent generateJourneyListIntent(Context context) {
        return new Intent(context, JourneyList.class);
    }
    //click list item, show graph
    private void clicksSelectedJourney() {
        ListView listView = (ListView) findViewById(R.id.journey_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                Journey journey = singleton.getJourneyCollection().getJourney(i);
                singleton.setLastJourney(journey);
                Intent intent = DisplayFootPrint.getIntent(JourneyList.this);
                Gson gson = new Gson();
                intent.putExtra(SELECT_INDEX, i);
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                startActivity(intent);
            }
        });
    }
    //long click to delete
    public void setLongClick() {
        ListView listview = (ListView) findViewById(R.id.journey_list);
        listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, R.string.delete_journey);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            //delete journey
            case 0:
               journeyCollection.removeJourney(info.position);
                populateListView();
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Gson gson = new Gson();
        String json = gson.toJson(singleton);
        intent.putExtra(SINGLETON_KEY, json);
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        setResult(MAIN_MENU_REQUEST_CODE_JOURNEY, intent);
        super.onBackPressed();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSingleton(data);
        journeyCollection = singleton.getJourneyCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        populateListView();
        switch (requestCode) {
            //add journey
            case REQUEST_CODE_ADD_JOURNEY:
                getSingleton(data);
                journeyCollection = singleton.getJourneyCollection();
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                super.onActivityResult(requestCode, resultCode, data);
                //edit journey
            case REQUEST_CODE_EDIT_DATA:
                getSingleton(data);
                journeyCollection = singleton.getJourneyCollection();
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                populateListView();
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void popButtons() {
        myCar = (ImageButton) findViewById(R.id.addCarImageBtn);
        skytrain = (ImageButton) findViewById(R.id.imageButton_skytrain);
        bike = (ImageButton) findViewById(R.id.imageButton_bike);
        bus = (ImageButton) findViewById(R.id.imageButton_bus);
        show = (Button) findViewById(R.id.btn_showPath);
        myCar.setVisibility(View.INVISIBLE);
        skytrain.setVisibility(View.INVISIBLE);
        bus.setVisibility(View.INVISIBLE);
        bike.setVisibility(View.INVISIBLE);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0){
                    myCar.setVisibility(View.VISIBLE);
                    skytrain.setVisibility(View.VISIBLE);
                    bus.setVisibility(View.VISIBLE);
                    bike.setVisibility(View.VISIBLE);
                    show.setBackgroundResource(R.mipmap.close);
                    flag = 1;
                }
                else{
                    show.setBackgroundResource(R.mipmap.show);
                    myCar.setVisibility(View.INVISIBLE);
                    skytrain.setVisibility(View.INVISIBLE);
                    bus.setVisibility(View.INVISIBLE);
                    bike.setVisibility(View.INVISIBLE);
                    flag = 0;
                }
            }
        });
    }

    private void setupCarButton() {
        myCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CarList.generateIntent(JourneyList.this);
                startActivityForResult(intent,REQUEST_CODE_FOR_CAR_DATA);
            }
        });
    }

    private void setUpBusButton(){
        bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BusList.generateIntent(JourneyList.this);
                startActivityForResult(intent, REQUEST_CODE_FOR_BUS_DATA);
            }
        });
    }

    private void setupSkytrainButton(){
        skytrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JourneyList.this, "You clicked skyTrain",
                        Toast.LENGTH_SHORT).show();
                Intent intent = SkytrainList.generateIntent(JourneyList.this);
                startActivityForResult(intent, REQUEST_CODE_FOR_SKYTRAIN_DATA);
            }
        });
    }

    private void setupBikeButton(){
        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JourneyList.this, "You clicked bike",
                        Toast.LENGTH_SHORT).show();
                Intent intent = BikeList.generateIntent(JourneyList.this);
                startActivityForResult(intent, REQUEST_CODE_FOR_BIKE_DATA);

            }
        });
    }

    private class MyListAdaptor extends ArrayAdapter<String>{
        public MyListAdaptor() {
            super(JourneyList.this,R.layout.item_view, journey_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("app", "position is: " + position);
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }
            String currentJourneyDescription = journey_list.get(position);
            String type = journey_type.get(position);

            final TextView text = (TextView)itemView.findViewById(R.id.item_Info);
            text.setText(currentJourneyDescription);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_Icon);
            if(type.equals("Skytrain")){
                imageView.setImageResource(R.mipmap.skytrain_icon);
            }else if(type.equals("Bus")){
                imageView.setImageResource(R.mipmap.bus_icon);
            }else if(type.equals("Bike")){
                imageView.setImageResource(R.mipmap.bike_walk_icon);
            }else {
                imageView.setImageResource(R.mipmap.car_icon1);
            }

            return itemView;
        }
    }
}