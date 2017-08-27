package com.vsevolod.carbontracker.Model;

/**
 * Created by vsevolod on 2017-03-21.
 */


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
        import android.os.Bundle;

        import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {


    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener onDateSetListener) {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.setOnDateSetListener(onDateSetListener);

        return pickerFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert, onDateSetListener, year, month, day);
    }

    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }


}
