package com.vsevolod.carbontracker.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Notifications;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.Model.Tips;
import com.vsevolod.carbontracker.R;

import java.util.Random;

/*
 * MainMenu activity
 */

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle myToggle;
    private Toolbar myToolbar;
    private final String JOURNEYS_KEY = "journeys";
    private final String JOURNEY_KEY = "journey";
    private static final String SINGLETON_KEY = "singleton";
    private static final String MAIN_MENU_KEY = "main menu";
    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private Singleton singleton = Singleton.getInstance();
    private Notifications notifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        restoreSharedPreferences();
        setUpTips();
        addTip();
        resetTipPriority();
        nextTipButton();
        setUpNotifications();
        triggerNotifications();

        setUpActionBar();
        setUpNavigationMenu();
        setUpFullScreen();
    }

    //set up full screen
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
        getSupportActionBar().hide();
        myToolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        myToolbar.setTitle(R.string.title_main_menu);
    }

    //set up the slide menu
    private void setUpNavigationMenu() {
        NavigationView myNavigationView = (NavigationView)findViewById(R.id.nav_view);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        myNavigationView.setItemIconTintList(null);
        myNavigationView.setNavigationItemSelectedListener(this);

        myToggle = new ActionBarDrawerToggle(this,
                myDrawerLayout,
                myToolbar,
                R.string.open,
                R.string.close);
        myDrawerLayout.addDrawerListener(myToggle);
        myToggle.syncState();

    }
    private void restoreSharedPreferences() {
        if(preferenceManager.getObject(MODE_PRIVATE, SINGLETON_KEY) != null) {
            singleton = preferenceManager.getObject(MODE_PRIVATE, SINGLETON_KEY);
        }
        Singleton.setInstance(singleton);

    }

    private void triggerNotifications() {
        notifications = new Notifications(this);
        notifications.runNotifications(singleton, this);
    }

    private void setUpTips() {
        singleton.setTips(this);
    }

    private void setUpNotifications() {
        singleton.setNotifications(this);
    }

    private void resetTipPriority() {
        float electricEmission =singleton.getBillCollection().getAverageElectricityCO2Emissions();
        float gasEmission = singleton.getBillCollection().getAverageGasCO2Emissions();
        //GET TRANSPORT EMISSION
        float transportEmission = singleton.getJourneyCollection().getC02ForLast28Days();
        singleton.getTipMaker().resetTipPriority(electricEmission, gasEmission, transportEmission);
    }

    //set tips
    private void addTip() {
        TextView textView = (TextView) findViewById(R.id.text_tip);
        Random random = new Random();
        float electricEmission =singleton.getBillCollection().getAverageElectricityCO2Emissions();
        float gasEmission = singleton.getBillCollection().getAverageGasCO2Emissions();
        float transportEmission = singleton.getJourneyCollection().getC02ForLast28Days(); //GET TRANSPORT EMISSION
//        String electricExplanation = (this.getResources().getString(R.string.tip_start) + electricEmission + this.getResources().getString(R.string.tip_electric));
//        String gasExplanation = (this.getResources().getString(R.string.tip_start) + gasEmission + this.getResources().getString(R.string.tip_gas));
//        String transportExplanation = (this.getResources().getString(R.string.tip_start) + transportEmission + this.getResources().getString(R.string.tip_transport));
        String tempTip;
        //GET TRANSPORT EMISSION
//        float transportEmission = singleton.getJourneyCollection().getC02ForLast28Days();
        String electricExplanation = (getString(R.string.you_generate) + electricEmission
                + getString(R.string.kg_of_co2_from_electricity));
        String gasExplanation = (getString(R.string.you_generate_1) + gasEmission
                + getString(R.string.ke_of_co2_today_from_natural_gas_use));
        String transportExplanation = (getString(R.string.you_generate_2) + transportEmission
                + getString(R.string.ke_of_co2_on_average_per_day_from_traveling));
        if (singleton.getTipMaker().getTopPriority() == Tips.TipType.ELECTRIC) {
            tempTip = electricExplanation;
        } else if (singleton.getTipMaker().getTopPriority() == Tips.TipType.GAS) {
            tempTip = gasExplanation;
        } else {
            tempTip = transportExplanation;
        }
        tempTip += singleton.getTipMaker().getPriorityTip();
        textView.setText(tempTip);
    }

    private void nextTipButton() {
        Button button = (Button) findViewById(R.id.btn_tip);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTip();
            }
        });
    }

    //set action for the menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String itemTitle = (String) item.getTitle();
        Toast.makeText(MainMenu.this,"Clicked " + itemTitle, Toast.LENGTH_SHORT).show();
        drawerClose();
        switch(item.getItemId()){
            case(R.id.nav_about):
                Intent intent = new Intent(MainMenu.this, AboutScreen.class);
                startActivity(intent);
                break;
            case(R.id.nav_display_footprint):
                Intent intent2 = DisplayFootPrint.getIntent(MainMenu.this);
                startActivity(intent2);
                break;
            case(R.id.nav_journey_list):
                Intent intent3 = JourneyList.generateJourneyListIntent(MainMenu.this);
                startActivity(intent3);
                break;
            case (R.id.nav_monthly_utility):
                Intent intent4 = DisplayBillsActivity.generateIntent(MainMenu.this);
                startActivity(intent4);
                break;
            case (R.id.exit1):
                super.onBackPressed();
                break;
        }
        return true;
    }

    private void drawerClose() {
        myDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setUpTips();
        addTip();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerClose();
        }
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onBackPressed();
    }

}
