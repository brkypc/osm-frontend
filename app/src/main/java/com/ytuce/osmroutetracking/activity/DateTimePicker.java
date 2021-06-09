package com.ytuce.osmroutetracking.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DateTimePicker {

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        Activity activity;

        public TimePickerFragment(MapWithListActivity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            Log.e("TImePicker", "hour = " + i + " minute = " + i1);
            ((MapWithListActivity) activity).setCalendar(i, i1);
        }
    }

    public static class DatePickerFragment  extends DialogFragment
                               implements DatePickerDialog.OnDateSetListener {

        Activity activity;

        public DatePickerFragment(MapWithListActivity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            Log.e("DatePicker", "year = " + i + " month = " + i1 + " day = " + i2);
            ((MapWithListActivity) activity).showTimePickerDialog();
            ((MapWithListActivity) activity).setCalendar(i, i1, i2);
        }
    }
}



