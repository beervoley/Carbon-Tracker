package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bill;
import com.vsevolod.carbontracker.Model.Bus;
import com.vsevolod.carbontracker.Model.Car;
import com.vsevolod.carbontracker.Model.DatePickerFragment;
import com.vsevolod.carbontracker.Model.Journey;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.Model.Skytrain;
import com.vsevolod.carbontracker.R;
import com.vsevolod.carbontracker.Model.Route;
import com.vsevolod.carbontracker.Model.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Activity to display C02 footprint
 */

public class DisplayFootPrint extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private static final float AVERAGE_DAILY_C02_PER_CANADIAN = (float) 36.6;
    private static final float TARGET_DAILY_C02_PER_CANADIAN = (float) 25.0;
    private static final double KG_PER_TREE = 20;
    private final String JOURNEYS_KEY = "journeys";
    private final String JOURNEY_KEY = "journey";
    private static final String SINGLETON_KEY = "singleton";
    private static final String MAIN_MENU_KEY = "main menu";
    private static final int MAIN_MENU_REQUEST_CODE_JOURNEY = 160;
    private static final int MAIN_MENU_REQUEST_CODE_FOOTPRINT = 150;
    private static final int GET_MY_JOURNEYS_BACK_PLEASE = 666;
    private Singleton singleton = Singleton.getInstance();
    private int returnCode = 0;
    private String journeyCode = null;
    private boolean canChange = true;
    private int currentState = 0;





    private boolean humanRelatedUnit;

    private List<MenuItem> actionBarIcons = new ArrayList<>();
    private boolean ifSelectedYear = false;
    private boolean ifSelected28Days = false;
    private boolean ifSelectedCarbon = false;
    private boolean ifSelected1Day = false;
    private int index_tmp;
    private Date pickedDate = new Date();

    private Journey journeyForClass = new Journey();
    private Skytrain skytrainForClass = new Skytrain();
    private Bus busForClass = new Bus();
    private Bill billForClass = new Bill();
    private List<Journey> journeys = new ArrayList<>();
    private List<Bill> bills = new ArrayList<>();
    private List<Bus> buses = new ArrayList<>();
    private List<Skytrain> skyTrains = new ArrayList<>();

    private String currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_foot_print);
        getSingleton(getIntent());
        getTheBills();
        getTheJourneys();
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
        bar.setTitle(R.string.carbon);
        //back to main menu
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void getSingleton(Intent intent) {
        System.out.println(Singleton.getInstance().getCurrentMode());
//        Gson gson = new Gson();
//        String json = intent.getStringExtra(SINGLETON_KEY);
//        index_tmp = intent.getIntExtra("select_index", 0);
//        singleton = gson.fromJson(json, singleton.getClass());
        currentMode = singleton.getCurrentMode();
        humanRelatedUnit = singleton.getHumanRelatedUnit();
//        preferenceManager.saveObject(SINGLETON_KEY, singleton);
        System.out.println("I have a journey:" + Singleton.getInstance().getJourneyCollection().countJourney());
    }


    private void getTheJourneys() {

        if (singleton.getJourneyCollection().countJourney() == 0) {
            setUpPieChar(null, bills, false);
            return;
        }

        if (singleton.getJourneyCollection().getLastJourney() != null) {
            if (singleton.getJourneyCollection().countJourney() == 1) {

                journeys.add(singleton.getJourneyCollection().getJourney(0));
            } else {
                journeys.add(singleton.getJourneyCollection().getJourney(index_tmp));
            }

//            setUpPieChar(journeys, null, false);
        } else {
            journeys = singleton.getJourneyCollection().getJourneyList();
//            setUpPieChar(journeys, bills, false);
        }
        currentState = 365;
        setUpPieCharForEntry(currentMode);
        setUpBarChart(true);

    }

    private void getTheBills() {
        bills = singleton.getBillCollection().getBillList();
    }


    private void setUpPieChar(final List<Journey> journeyList, final List<Bill> billList, final boolean ifForSingleDay) {
        float emissionTotal = 0;
        PieChart chart = (PieChart) findViewById(R.id.pie_chart);
        chart.setVisibility(View.VISIBLE);
        chart.clear();
        chart.setHighlightPerTapEnabled(true);

        List<PieEntry> list = new ArrayList<>();
        if (journeyList != null && !journeyList.isEmpty()) {
            for (Journey journey : journeyList) {
                list.add(new PieEntry(journey.getCO2Emission(), "", journey));
                emissionTotal += journey.getCO2Emission();
            }
        }
        if (billList != null && !billList.isEmpty()) {
            for (Bill bill : billList) {
                if (ifForSingleDay) {
                    list.add(new PieEntry(bill.getDailyShareForCO2(), "", bill));
                    System.out.println(bill.getDailyShareForCO2());
                    emissionTotal += bill.getDailyShareForCO2();
                } else {
                    list.add(new PieEntry(bill.getCO2Emission(), "", bill));
                    emissionTotal += bill.getCO2Emission();
                }
            }
        }


        PieDataSet dataSet = new PieDataSet(list, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setHighlightEnabled(true);
        PieData data = new PieData(dataSet);


        chart.setData(data);
        chart.setHoleRadius(50f);
        Description desc = new Description();
        desc.setText("Total of C02 Emissions");
        chart.setDescription(desc);
        chart.setCenterText("Total of C02 Emissions is\n" + emissionTotal + " KG");
        chart.getLegend().setEnabled(false);
        chart.invalidate();


//        TableLayout tb = (TableLayout) findViewById(R.id.main_table);

    }

    private void setUpBarChart(boolean ifYear) {
        PieChart pieChart = (PieChart) findViewById(R.id.pie_chart);



        float electricityEmissions = 0;
        float gasEmissions = 0;
        float carsEmissions = 0;
        float skyTrainEmissions = 0;
        float busEmissions = 0;

        List<BarEntry> entries = new ArrayList<>();
        final List<String> xVals = new ArrayList<>();
        int index = 0;

        if (!ifYear) {
            electricityEmissions = singleton.getBillCollection().getCO2ElectricityForLast28Days();
            gasEmissions = singleton.getBillCollection().getCO2GasForLast28Days();
            carsEmissions = singleton.getJourneyCollection().getC02ForLast28DaysCars();
            skyTrainEmissions = singleton.getJourneyCollection().getC02ForLast28DaysSkytrains();
            busEmissions = singleton.getJourneyCollection().getC02ForLast28DaysBuses();
            xVals.add("Average");
            entries.add(new BarEntry(index++, AVERAGE_DAILY_C02_PER_CANADIAN * 28));
            xVals.add("Target");
            entries.add(new BarEntry(index++, TARGET_DAILY_C02_PER_CANADIAN * 28));
        } else {
            electricityEmissions = singleton.getBillCollection().getCO2ElectricityForLast365Days();
            gasEmissions = singleton.getBillCollection().getCO2GasForLast365Days();
            carsEmissions = singleton.getJourneyCollection().getC02ForLast365DaysCars();
            skyTrainEmissions = singleton.getJourneyCollection().getC02ForLast365DaysSkytrains();
            busEmissions = singleton.getJourneyCollection().getC02ForLast365DaysBuses();
            xVals.add("Average");
            entries.add(new BarEntry(index++, AVERAGE_DAILY_C02_PER_CANADIAN * 365));
            xVals.add("Target");
            entries.add(new BarEntry(index++, TARGET_DAILY_C02_PER_CANADIAN * 365));
        }


        // Set-up entries(Y-Vals)


        if (electricityEmissions != 0) {
            xVals.add("Elec.");
            entries.add(new BarEntry(index++, electricityEmissions));
        }
        if (gasEmissions != 0) {
            xVals.add("Gas");
            entries.add(new BarEntry(index++, gasEmissions));
        }
        if (carsEmissions != 0) {
            xVals.add("Cars");
            entries.add(new BarEntry(index++, carsEmissions));

        }
        if (skyTrainEmissions != 0) {
            xVals.add("Skytrains");
            entries.add(new BarEntry(index++, skyTrainEmissions));
        }

        if (busEmissions != 0) {
            xVals.add("Buses");
            entries.add(new BarEntry(index++, busEmissions));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Types");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);


        List<IBarDataSet> list = new ArrayList<>();
        list.add(dataSet);


        BarData data = new BarData(dataSet);
        Description description = new Description();
        description.setText("");

        BarChart barChart = (BarChart) findViewById(R.id.top_bar_chart);
        barChart.setDescription(description);
        barChart.setVisibility(View.VISIBLE);
        barChart.setData(data);


        // X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMaximum(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        //Y-axis

        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        //Legend
        Legend l = barChart.getLegend();
        l.setEnabled(false);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setWordWrapEnabled(true);

        l.setDrawInside(true);
//        l.setYOffset(20f);
        l.setXEntrySpace(5f);
//        l.setYEntrySpace(0f);
        l.setTextSize(14f);
        barChart.invalidate();


    }


    public static Intent getIntent(Context context) {
        return new Intent(context, DisplayFootPrint.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_bar, menu);
        for (int i = 0; i < 4; i++) {
            actionBarIcons.add(menu.getItem(i));
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem myItem = item;
        switch (item.getItemId()) {
            //go back to last activity
            case R.id.backButton:
                super.onBackPressed();
                finish();
                return true;
            case R.id.calendar_everything:
                currentState = 0;
                item.setIcon(R.drawable.carbon_highlighted);
                returnDefaultIconsExcept("carbon", myItem);
                ifSelectedCarbon = true;
                getTheJourneys();
                return true;
            case R.id.calendar_day:
                currentState = 1;

                item.setIcon(R.drawable.calendar_everything_highlighted);
                returnDefaultIconsExcept("day", myItem);
                ifSelected1Day = true;
                pick1Day();
                return true;
            case R.id.calendar_28:
                currentState = 28;
                item.setIcon(R.drawable.calendar_28_highlighted);
                returnDefaultIconsExcept("28days", myItem);
                ifSelected28Days = true;

                setUpBarChart(false);
                setUpPieCharForEntry(currentMode);
                return true;
            case R.id.calendar_year:

                item.setIcon(R.drawable.calendar_year_highlighted);
                returnDefaultIconsExcept("year", myItem);
                ifSelectedYear = true;

                currentState = 365;
                setUpPieCharForEntry(currentMode);
                setUpBarChart(true);
                return true;
            case R.id.mode_overflow:
                currentMode = "mode";
                singleton.setCurrentMode(currentMode);
                setUpPieCharForEntry(currentMode);
                return true;
            case R.id.route_overflow:
                currentMode = "route";
                singleton.setCurrentMode(currentMode);
                setUpPieCharForEntry(currentMode);
                return true;

            case R.id.human_related_unit:
                humanRelatedUnit = true;
                singleton.setHumanRelatedUnit(humanRelatedUnit);
                setUpPieCharForEntry(currentMode);
                setUpBarChart(currentState == 365);
                return true;
            case R.id.regular_unit:
                humanRelatedUnit = false;
                singleton.setHumanRelatedUnit(humanRelatedUnit);
                setUpPieCharForEntry(currentMode);
                setUpBarChart(currentState == 365);
                return true;



            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }

    private void returnDefaultIconsExcept(String year, final MenuItem item) {

        if (!actionBarIcons.get(0).equals(item)) {
            actionBarIcons.get(0).setIcon(R.drawable.carbon);
        }
        if (!actionBarIcons.get(1).equals(item)) {
            actionBarIcons.get(1).setIcon(R.drawable.calendar_everything);
        }
        if (!actionBarIcons.get(2).equals(item)) {
            actionBarIcons.get(2).setIcon(R.drawable.calendar_year);
            System.out.println("HERE");
        }
        if (!actionBarIcons.get(3).equals(item)) {
            actionBarIcons.get(3).setIcon(R.drawable.calendar_28);
            System.out.println("HERE");
        }

    }

    private void pick1Day() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        pickedDate.setDate(dayOfMonth);
                        pickedDate.setYear(year - 1900);
                        pickedDate.setMonth(month);
                        List<Journey> dayList = singleton.getJourneyCollection().getJourneysForADay(pickedDate);
                        List<Bill> billList = new ArrayList<>();
                        if (singleton.getBillCollection().getClosestElectricityBill(pickedDate) != null) {
                            billList.add(singleton.getBillCollection().getClosestElectricityBill(pickedDate));
                        }
                        if (singleton.getBillCollection().getClosestGasBill(pickedDate) != null) {
                            billList.add(singleton.getBillCollection().getClosestGasBill(pickedDate));
                        }

//                        setUpPieChar(dayList, billList, true);
                        setUpPieCharForEntry(currentMode);


                    }
                });
        newFragment.show(getFragmentManager(), "Date Picker");
    }

    private void setUpPieChar(final boolean ifYear) {
        float emissionTotal = 0;

        float electricityEmissions = 0;
        float gasEmissions = 0;
        float carsEmissions = 0;
        float skyTrainEmissions = 0;
        float busEmissions = 0;

        if (!ifYear) {
            electricityEmissions = singleton.getBillCollection().getCO2ElectricityForLast28Days();
            gasEmissions = singleton.getBillCollection().getCO2GasForLast28Days();
            carsEmissions = singleton.getJourneyCollection().getC02ForLast28DaysCars();
            skyTrainEmissions = singleton.getJourneyCollection().getC02ForLast28DaysSkytrains();
            busEmissions = singleton.getJourneyCollection().getC02ForLast28DaysBuses();
        } else {
            electricityEmissions = singleton.getBillCollection().getCO2ElectricityForLast365Days();
            gasEmissions = singleton.getBillCollection().getCO2GasForLast365Days();
            carsEmissions = singleton.getJourneyCollection().getC02ForLast365DaysCars();
            skyTrainEmissions = singleton.getJourneyCollection().getC02ForLast365DaysSkytrains();
            busEmissions = singleton.getJourneyCollection().getC02ForLast365DaysBuses();
        }

        PieChart chart = (PieChart) findViewById(R.id.pie_chart);
        chart.setHighlightPerTapEnabled(true);

        List<PieEntry> list = new ArrayList<>();


        list.add(new PieEntry(electricityEmissions, ""));
        list.add(new PieEntry(gasEmissions, ""));
        list.add(new PieEntry(electricityEmissions, ""));
        list.add(new PieEntry(carsEmissions, ""));
        list.add(new PieEntry(skyTrainEmissions, ""));
        list.add(new PieEntry(busEmissions, ""));

        emissionTotal += electricityEmissions + gasEmissions + carsEmissions + skyTrainEmissions + busEmissions;


        PieDataSet dataSet = new PieDataSet(list, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setHighlightEnabled(true);
        PieData data = new PieData(dataSet);


        chart.setData(data);
        chart.setHoleRadius(50f);
        Description desc = new Description();
        desc.setText("Total of C02 Emissions");
        chart.setDescription(desc);
        chart.setCenterText("Total of C02 Emissions is\n" + emissionTotal + " KG");
        chart.getLegend().setEnabled(false);
        chart.invalidate();


    }

    private void setUpPieCharForEntry(String entry) {

        float emissionTotal = 0;
        float busEmissions = 0;
        float skyTrainEmissions = 0;


        PieChart chart = (PieChart) findViewById(R.id.pie_chart);
        chart.setVisibility(View.VISIBLE);
        chart.setHighlightPerTapEnabled(true);
        List<PieEntry> list = new ArrayList<>();

        List<Journey> carJourneysToDisplay = new ArrayList<>();
        List<Journey> skyTrainJourneysToDisplay = new ArrayList<>();
        List<Journey> busJourneysToDisplay = new ArrayList<>();
        float electricity = 0;
        float gas = 0;


        switch (entry) {

            case ("mode"):
                if (currentState == 28) {
                    carJourneysToDisplay = singleton.getJourneyCollection().getJourneysForCars28Days();
                    skyTrainJourneysToDisplay = singleton.getJourneyCollection().getSkyTrainJourneysForLast28Days();
                    busJourneysToDisplay = singleton.getJourneyCollection().getBusJourneysForLast28Days();
                    gas = singleton.getBillCollection().getCO2GasForLast28Days();
                    electricity = singleton.getBillCollection().getCO2ElectricityForLast28Days();

                } else if (currentState == 365) {
                    carJourneysToDisplay = singleton.getJourneyCollection().getJourneysForCars365Days();
                    skyTrainJourneysToDisplay = singleton.getJourneyCollection().getSkyTrainJourneysForLast365Days();
                    busJourneysToDisplay = singleton.getJourneyCollection().getBusJourneysForLast365Days();
                    gas = singleton.getBillCollection().getCO2GasForLast365Days();
                    electricity = singleton.getBillCollection().getCO2ElectricityForLast365Days();
                } else if (currentState == 1){
                    carJourneysToDisplay = singleton.getJourneyCollection().getJourneysForCars1Day(pickedDate);
                    skyTrainJourneysToDisplay = singleton.getJourneyCollection().getSkyTrainJourneysFor1Day(pickedDate);
                    busJourneysToDisplay = singleton.getJourneyCollection().getBusJourneysFor1Day(pickedDate);
                    if(singleton.getBillCollection().getClosestGasBill(pickedDate) != null) {
                        gas = singleton.getBillCollection().getClosestGasBill(pickedDate).getDailyShareForCO2();
                    } else {
                        gas = 0;
                    }
                    if(singleton.getBillCollection().getClosestElectricityBill(pickedDate) != null) {
                        electricity = singleton.getBillCollection().getClosestElectricityBill(pickedDate).getDailyShareForCO2();
                    } else {
                        electricity = 0;
                    }


                }

                for (Journey journey : carJourneysToDisplay) {
                    list.add(new PieEntry(journey.getCO2Emission(), journey.getCar().getName()));
                    emissionTotal += journey.getCO2Emission();
                }

                for (Journey journey : busJourneysToDisplay) {
                    busEmissions += journey.getCO2Emission();
                    emissionTotal += journey.getCO2Emission();
                }
                if(busEmissions != 0) {
                    list.add(new PieEntry(busEmissions, "Buses"));
                }

                for (Journey journey : skyTrainJourneysToDisplay) {
                    skyTrainEmissions += journey.getCO2Emission();
                    emissionTotal += journey.getCO2Emission();
                }
                if(skyTrainEmissions != 0) {
                    list.add(new PieEntry(skyTrainEmissions, "SkyTrains"));
                }
                if(humanRelatedUnit) {
                    emissionTotal /= 5.023;
                }
                emissionTotal += electricity;
                emissionTotal += gas;
                if(gas != 0) {
                    list.add(new PieEntry(gas, "Gas"));
                }
                if(electricity != 0) {
                    list.add(new PieEntry(electricity, "Electricity"));
                }
                break;

            case ("route"):

                if (currentState == 28) {
                    carJourneysToDisplay = singleton.getJourneyCollection().getJourneysForCars28DaysRoutes();
                    skyTrainJourneysToDisplay = singleton.getJourneyCollection().getSkyTrainJourneysForLast28Days();
                    busJourneysToDisplay = singleton.getJourneyCollection().getBusJourneysForLast28Days();
                    gas = singleton.getBillCollection().getCO2GasForLast28Days();
                    electricity = singleton.getBillCollection().getCO2ElectricityForLast28Days();

                } else if (currentState == 365) {
                    carJourneysToDisplay = singleton.getJourneyCollection().getJourneysForCars365DaysRoutes();
                    skyTrainJourneysToDisplay = singleton.getJourneyCollection().getSkyTrainJourneysForLast365Days();
                    busJourneysToDisplay = singleton.getJourneyCollection().getBusJourneysForLast365Days();
                    gas = singleton.getBillCollection().getCO2GasForLast365Days();
                    electricity = singleton.getBillCollection().getCO2ElectricityForLast365Days();
                } else if (currentState == 1){
                    carJourneysToDisplay = singleton.getJourneyCollection().getJourneysForCars1DayRoutes(pickedDate);
                    skyTrainJourneysToDisplay = singleton.getJourneyCollection().getSkyTrainJourneysFor1Day(pickedDate);
                    busJourneysToDisplay = singleton.getJourneyCollection().getBusJourneysFor1Day(pickedDate);
                    if(singleton.getBillCollection().getClosestGasBill(pickedDate) != null) {
                        gas = singleton.getBillCollection().getClosestGasBill(pickedDate).getDailyShareForCO2();
                    } else {
                        gas = 0;
                    }
                    if(singleton.getBillCollection().getClosestElectricityBill(pickedDate) != null) {
                        electricity = singleton.getBillCollection().getClosestElectricityBill(pickedDate).getDailyShareForCO2();
                    } else {
                        electricity = 0;
                    }


                }

                for (Journey journey : carJourneysToDisplay) {
                    list.add(new PieEntry(journey.getCO2Emission(), journey.getRoute().getName()));
                    emissionTotal += journey.getCO2Emission();
                }

                for (Journey journey : busJourneysToDisplay) {
                    busEmissions += journey.getCO2Emission();
                    emissionTotal += journey.getCO2Emission();
                }
                if(busEmissions != 0) {
                    list.add(new PieEntry(busEmissions, "Buses"));
                }

                for (Journey journey : skyTrainJourneysToDisplay) {
                    skyTrainEmissions += journey.getCO2Emission();
                    emissionTotal += journey.getCO2Emission();
                }
                if(skyTrainEmissions != 0) {
                    list.add(new PieEntry(skyTrainEmissions, "SkyTrains"));
                }

                if(humanRelatedUnit) {
                    emissionTotal /= 5.023;
                }
                emissionTotal += electricity;
                emissionTotal += gas;
                if(gas != 0) {
                    list.add(new PieEntry(gas, "Gas"));
                }
                if(electricity != 0) {
                    list.add(new PieEntry(electricity, "Electricity"));
                }

                break;
        }


        PieDataSet dataSet = new PieDataSet(list, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setHighlightEnabled(true);
        PieData data = new PieData(dataSet);


        chart.setData(data);
        chart.setEntryLabelTextSize(10f);
        chart.setHoleRadius(50f);
        Description desc = new Description();
        desc.setText("Total of C02 Emissions");
        chart.setDescription(desc);
        chart.setCenterText("Total of C02 Emissions is\n" + emissionTotal + (humanRelatedUnit == true? " Trees" : " KGs"));
        chart.getLegend().setEnabled(false);
        chart.invalidate();


    }
}

