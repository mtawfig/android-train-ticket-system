package org.feup.cmov.userticketapp.Controllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.R;

import java.util.Calendar;
import java.util.Date;

public class DatePickerDialog extends AppCompatDialogFragment {

    public interface DatePickerDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DatePickerDialogListener mListener;
    SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.date_picker_layout, null);

        final DialogFragment fragment = this;

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker_date_picker);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.date_picker_time_picker);
        timePicker.setIs24HourView(true);

        Date selectedDate = sharedData.getSelectedDate();
        if (selectedDate != null) {
            Calendar cal=Calendar.getInstance();
            cal.setTime(selectedDate);

            datePicker.updateDate(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));

            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }

        builder.setView(view)
                .setPositiveButton("Pick Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, datePicker.getYear());
                        cal.set(Calendar.MONTH, datePicker.getMonth());
                        cal.set(Calendar.DAY_OF_MONTH,  datePicker.getDayOfMonth());
                        cal.set(Calendar.HOUR_OF_DAY,  timePicker.getCurrentHour());
                        cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        Date dateRepresentation = cal.getTime();

                        sharedData.setSelectedDate(dateRepresentation);

                        mListener.onDialogPositiveClick(fragment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatePickerDialog.this.getDialog().cancel();
                        mListener.onDialogNegativeClick(fragment);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DatePickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}