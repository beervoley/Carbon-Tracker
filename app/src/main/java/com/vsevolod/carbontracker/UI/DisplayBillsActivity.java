package com.vsevolod.carbontracker.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bill;
import com.vsevolod.carbontracker.Model.BillCollection;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Singleton;
import com.vsevolod.carbontracker.R;

public class DisplayBillsActivity extends AppCompatActivity {


    private PreferenceManager preferenceManager = new PreferenceManager(this);


    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final int GET_BILL_REQUEST_CODE = 9192;
    private static final String SINGLETON_KEY = "singleton";
    private static final String BILL_KEY = "bill";
    private static final int EDIT_BILL_REQUEST_CODE = 7321;
    private Singleton singleton = Singleton.getInstance();
    private BillCollection collection;

    private int editedBill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreBillsCollection(getIntent());
        setContentView(R.layout.activity_display_bills);
        setUpAddButton();
        populateListView();
        setUpContextMenu();
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
        bar.setTitle(R.string.title_bill);
        //up button, back to main menu
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void populateListView() {
        ListView listView = (ListView) findViewById(R.id.bills_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_activated_1,
                collection.getBillsDescription()
        );
        listView.setAdapter(adapter);
    }

    private void setUpAddButton() {
        Button btn = (Button) findViewById(R.id.add_new_bill);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AddBillActivity.generateIntent(DisplayBillsActivity.this), GET_BILL_REQUEST_CODE);
            }
        });
    }

    private void restoreBillsCollection(Intent intent) {
        collection = singleton.getBillCollection();
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }

    @Override
    public void onBackPressed() {
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case(GET_BILL_REQUEST_CODE):
                if (data != null) {
                 String json = data.getStringExtra(BILL_KEY);
                    Gson gson = new Gson();
                    Bill bill = new Bill();
                    bill = gson.fromJson(json, bill.getClass());
                    collection.addBill(bill);
                    singleton.setBillCollection(collection);
                    preferenceManager.saveObject(SINGLETON_KEY, singleton);
                    populateListView();
                }
                break;
            case(EDIT_BILL_REQUEST_CODE):
                if (data != null) {
                    String json = data.getStringExtra(BILL_KEY);
                    Gson gson = new Gson();
                    Bill bill = new Bill();
                    bill = gson.fromJson(json, bill.getClass());
                    collection.addBillOnIndex(bill, editedBill);
                    singleton.setBillCollection(collection);
                    preferenceManager.saveObject(SINGLETON_KEY, singleton);
                    populateListView();
                }
        }
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                editedBill = info.position;
                Intent intent = AddBillActivity.generateIntent(DisplayBillsActivity.this);
                intent.putExtra(BILL_KEY, encodeBill(collection.getBillAtIndex(editedBill)));
                startActivityForResult(intent, EDIT_BILL_REQUEST_CODE);
                return true;
            case R.id.delete:
                collection.removeBill(info.position);
                singleton.setBillCollection(collection);
                populateListView();
                preferenceManager.saveObject(SINGLETON_KEY, singleton);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public void setUpContextMenu() {
        ListView lv = (ListView) findViewById(R.id.bills_list);
        registerForContextMenu(lv);
    }

    private String encodeBill(Bill bill) {
        Gson gson = new Gson();
        return gson.toJson(bill);
    }

    public static Intent generateIntent(Context context) {
        return new Intent(context, DisplayBillsActivity.class);
    }
}
