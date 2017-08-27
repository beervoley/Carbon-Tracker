package com.vsevolod.carbontracker.UI;

        import android.app.Activity;
        import android.app.DatePickerDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.annotation.RequiresApi;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.AppCompatActivity;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.vsevolod.carbontracker.Model.DatePickerFragment;
        import com.vsevolod.carbontracker.Model.PreferenceManager;
        import com.vsevolod.carbontracker.R;
        import com.vsevolod.carbontracker.Model.Route;
        import com.vsevolod.carbontracker.Model.RouteCollection;

        import java.util.Date;

/**
 * Activity to add a route.
 */

public class AddRouteActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private static final String EXTRA_ROUTE_NAME = "routeName";
    private static final String EXTRA_CITY_SEGMENT = "citySegment";
    private static final String EXTRA_HIGHWAY_SEGMENT = "highwaySegment";
    private static final String EXTRA_ROUTE_INDEX = "routeIndex";


    private static final String EXTRA_DELETE = "delete";
    private double cityEntry = 0;
    private double highwayEntry = 0;
    private static int routeIndex;
    private TextView text;
    private static boolean isEditRoute = false;
    private static Route currentRoute;
    private Date date = new Date();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        if (isEditRoute) {
            displayCurrentRoute();
        }
        deleteButton();
        updateTotalDistance();
        calculateTotalDistance();
        doneButton();
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
        bar.setTitle(R.string.add_route_title);
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
        switch(item.getItemId()) {
           // back button
            case R.id.backButton:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayCurrentRoute() {
        EditText routeName = (EditText) findViewById(R.id.edittxt_route_name);
        EditText citySegment = (EditText) findViewById(R.id.edittxt_city_segment);
        EditText highwaySegment = (EditText) findViewById(R.id.edittxt_highway_segment);
        routeName.setText(currentRoute.getName());
        citySegment.setText(Double.toString(currentRoute.getCityRouteSegment()));
        highwaySegment.setText(Double.toString(currentRoute.getHighwayRouteSegment()));
        cityEntry = currentRoute.getCityRouteSegment();
        highwayEntry = currentRoute.getHighwayRouteSegment();
    }

    private void deleteButton() {
        Button button = (Button) findViewById(R.id.btn_route_delete);
        if (!isEditRoute) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddRouteActivity.this, RouteListActivity.class);
                    intent.putExtra(EXTRA_ROUTE_INDEX, routeIndex);
                    intent.putExtra(EXTRA_DELETE, true);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    private void doneButton() {
        Button button = (Button) findViewById(R.id.btn_route_done);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String routeName = editTextToString(R.id.edittxt_route_name);
                    TextView total = (TextView)findViewById(R.id.total_distance);
                    double distance = Double.parseDouble(total.getText().toString());
                    if (routeName.isEmpty()) {
                        Toast.makeText(AddRouteActivity.this, R.string.enter_valid_name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //total distance cannot be 0
                    else if(distance==0){
                        Toast.makeText(AddRouteActivity.this, R.string.total_distance_cannot_be_0, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Intent intent = new Intent(AddRouteActivity.this, RouteListActivity.class);
                        intent.putExtra(EXTRA_ROUTE_NAME, routeName);
                        intent.putExtra(EXTRA_CITY_SEGMENT, cityEntry);
                        intent.putExtra(EXTRA_HIGHWAY_SEGMENT, highwayEntry);
                        intent.putExtra(EXTRA_DELETE, false);
                        setResult(Activity.RESULT_OK, intent);
                        if (isEditRoute) {
                            intent.putExtra(EXTRA_ROUTE_INDEX, routeIndex);
                        }
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });
    }




    private void updateTotalDistance() {
        double totalDistance = cityEntry + highwayEntry;
            TextView totalDist = (TextView) findViewById(R.id.total_distance);
            totalDist.setText("" + totalDistance);

    }

    private void calculateTotalDistance() {
        final EditText editCity = (EditText) findViewById(R.id.edittxt_city_segment);
        final EditText editHighway = (EditText) findViewById(R.id.edittxt_highway_segment);
        final int ENTRY_CONSTRAINT = 9;

        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //only calculate total distance with valid entries to prevent crash while editing
                if (editCity.getText().toString().length() < ENTRY_CONSTRAINT && editCity.getText().toString().length() > 0) {
                    cityEntry = editTextToDouble(R.id.edittxt_city_segment);
                    updateTotalDistance();
                }
            }
        });

        editHighway.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //only calculate total distance with valid entries to prevent crash while editing
                if (editHighway.getText().toString().length() < ENTRY_CONSTRAINT && editHighway.getText().toString().length() > 0) {
                    highwayEntry = editTextToDouble(R.id.edittxt_highway_segment);
                    updateTotalDistance();
                }

            }
        });
    }

    private double editTextToDouble(int id) {
        EditText entry = (EditText) findViewById(id);
        Double temp = Double.parseDouble(entry.getText().toString());
        return temp;
    }
    private String editTextToString(int id) {
        EditText entry = (EditText) findViewById(id);
        return entry.getText().toString();
    }

    public static Route getRouteFromIntent(Intent data) {
        String routeName = data.getStringExtra(EXTRA_ROUTE_NAME);
        double citySegment = data.getDoubleExtra(EXTRA_CITY_SEGMENT, 0);
        double highwaySegment = data.getDoubleExtra(EXTRA_HIGHWAY_SEGMENT, 0);
        return new Route(routeName, citySegment, highwaySegment, true);
    }

    public static int getIndexFromIntent(Intent data) {
        return data.getIntExtra(EXTRA_ROUTE_INDEX, 0);
    }

    public static boolean getDeleteStatusFromIntent(Intent data) {
        return data.getBooleanExtra(EXTRA_DELETE, false);
    }

    //intent for adding route
    public static Intent makeIntent(Context context) {
        isEditRoute = false;
        return new Intent(context, AddRouteActivity.class);
    }

    //intent for editing route
    public static Intent makeIntent(Context context, RouteCollection routeCollection, int position) {
        isEditRoute = true;
        routeIndex = position;
        currentRoute = routeCollection.getRoute(routeIndex);

        return new Intent(context, AddRouteActivity.class);
    }
}
