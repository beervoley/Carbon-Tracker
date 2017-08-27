package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.Bill;
import com.vsevolod.carbontracker.Model.DatePickerFragment;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddBillActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);


    private Bill bill = new Bill();
    private static final int GET_BILL_REQUEST_CODE = 9192;
    private static final String AMOUNT_KEY = "amount";
    private static final String INVOICE_NUMBER_KEY = "invoice";
    private static final String NUMBER_OF_PEOPLE_KEY = "number of people";
    private static final String TYPE_KEY = "type";
    private static final String BILL_KEY = "bill";
    private Date startDate = new Date();
    private Date endDate = new Date();



    private List<String> typesOfBills = new ArrayList<>();

    private Spinner spinner;
    private Bill.Type inputType;
    private double amount;
    private int numberOfPeople;
    private long invoiceNumber;
    private Bill currentBill = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        updateBill(getIntent());
        spinner = (Spinner) findViewById(R.id.type_spinner);
        updateSpinner(spinner, typesOfBills);


        setUpOkButton();
        setUpCancelButton();
        setUpDateFragment();
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
        bar.setTitle(R.string.add_bill_title);
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
            //back button
            case R.id.backButton:
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateBill(Intent intent) {
        String json = intent.getStringExtra(BILL_KEY);
        if(json != null) {
            System.out.println("String is not null");
            currentBill = decodeBill(json);
            EditText etNumber = (EditText) findViewById(R.id.addBill_invoice_number);
            EditText etAmount = (EditText) findViewById(R.id.amount);
            EditText etNumberOfPeople = (EditText) findViewById(R.id.addBill_number_of_people);
            etNumber.setText(currentBill.getInvoiceNumber() + "");
            etAmount.setText(currentBill.getAmount() + "");
            etNumberOfPeople.setText(currentBill.getNumberOfPeople() + "");

        }
    }

    private void setUpCancelButton() {
        Button btn = (Button) findViewById(R.id.addBill_cancel_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void setUpDateFragment() {
        final EditText startDateEt = (EditText) findViewById(R.id.addBill_start_date);
        final EditText endDateEt = (EditText) findViewById(R.id.addBill_end_date);
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH) + 1;
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);

        if(currentBill != null) {
            System.out.println("Not null");
            Date currentStartDate = currentBill.getStartDate();
            Date currentEndDate = currentBill.getEndDate();
            startDateEt.setText(currentStartDate.getDate() + "-" + (currentStartDate.getMonth() + 1)
                    + "-" + (currentStartDate.getYear() + 1900));
            endDateEt.setText(currentEndDate.getDate() + "-" + (currentEndDate.getMonth() + 1)
                    + "-" + (currentEndDate.getYear() + 1900));
            startDate = currentStartDate;
            endDate = currentEndDate;

        } else {
            startDateEt.setText(day + "-" + month + "-" + year);
            endDateEt.setText(day + "-" + month + "-" + year);
        }

        startDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment =
                        DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDateEt.setText(dayOfMonth + "-" + (month + 1) + "-" + year);

                        startDate.setDate(dayOfMonth);
                        startDate.setYear(year - 1900);
                        startDate.setMonth(month);
                    }
                });
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });


        endDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment =
                        DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                endDateEt.setText(dayOfMonth + "-" + (month + 1) + "-" + year);

                                endDate.setDate(dayOfMonth);
                                endDate.setYear(year - 1900);
                                endDate.setMonth(month);
                            }
                        });
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });

    }

    private void setUpOkButton() {
        Button btn = (Button) findViewById(R.id.addBill_ok_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    bill.setAmount(amount);
                    bill.setType(inputType);
                    bill.setInvoiceNumber(invoiceNumber);
                    bill.setStartDate(startDate);
                    bill.setEndDate(endDate);
                    bill.setNumberOfPeople(numberOfPeople);
                    String json = encodeBill(bill);
                    Intent intent = new Intent();
                    intent.putExtra(BILL_KEY, json);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    private void updateSpinner(Spinner spinner, List<String> list) {

        list.add(getResources().getString(R.string.electricity));
        list.add(getResources().getString(R.string.natural_gus));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                list
        );
        spinner.setAdapter(adapter);

        if(currentBill != null) {
            if (currentBill.getType().equals("Electricity")) {
                spinner.setSelection(0);
            } else {
                spinner.setSelection(1);
            }
        }

        final EditText et = (EditText) findViewById(R.id.amount);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    et.setHint(R.string.electricity_hint);
                    inputType = Bill.Type.Electricity;
                } else {
                    et.setHint(R.string.natural_gas_hint);
                    inputType = Bill.Type.Gas;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateInput() {
        EditText inNumber = (EditText) findViewById(R.id.addBill_invoice_number);
        EditText peopleNumber = (EditText) findViewById(R.id.addBill_number_of_people);
        EditText newAmount = (EditText) findViewById(R.id.amount);

        if(inNumber.getText().toString().matches("")) {
            Toast.makeText(this, R.string.wrong_invoice_number, Toast.LENGTH_SHORT).show();
            return false;
        } else if(peopleNumber.getText().toString().matches("")) {
            Toast.makeText(this, R.string.wrong_number_of_people, Toast.LENGTH_SHORT).show();
            return false;
        } else if (newAmount.getText().toString().matches("")) {
            Toast.makeText(this, R.string.invalid_amount , Toast.LENGTH_SHORT).show();
            return false;
        } else {
            invoiceNumber = Long.parseLong(inNumber.getText().toString());
            amount = Double.parseDouble(newAmount.getText().toString());
            numberOfPeople = Integer.parseInt(peopleNumber.getText().toString());
            return true;
        }
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        bill = decodeBill(data.getStringExtra(BILL_KEY));
//        Intent intent = new Intent();
//        intent.putExtra(BILL_KEY, encodeBill(bill));
//        setResult(Activity.RESULT_OK, intent);
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    private String encodeBill(Bill bill) {
        Gson gson = new Gson();
        return gson.toJson(bill);
    }

    private Bill decodeBill(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, bill.getClass());

    }

    public static Intent generateIntent(Context context) {
        return new Intent(context, AddBillActivity.class);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
