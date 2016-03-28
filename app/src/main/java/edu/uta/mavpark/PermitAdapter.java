package edu.uta.mavpark;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.uta.mavpark.models.PermitModel;

/**
 * Created by krish on 3/22/2016.
 */
public class PermitAdapter extends ArrayAdapter<PermitModel> {
    public PermitAdapter(Context context, ArrayList<PermitModel> permits) {
        super(context, 0, permits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PermitModel permit = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_permit, parent, false);
        }
        // Lookup view for data population
        TextView dateTextView = (TextView) convertView.findViewById(R.id.list_item_date_textview);
        TextView monthTextView = (TextView) convertView.findViewById(R.id.list_item_month_textview);
        TextView licensePlateIdTextView = (TextView) convertView.findViewById(R.id.list_item_licensePlateId_textview);
        TextView parkingLotTextView = (TextView)convertView.findViewById(R.id.list_item_parkingLot_textView);
        Calendar cal = Calendar.getInstance();
        cal.setTime(permit.ToDateTime);
        dateTextView.setText(new SimpleDateFormat("dd").format(cal.getTime()));
        monthTextView.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
        if(permit.ToDateTime.getTime() >= new Date().getTime())
        {
            dateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            monthTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
       else {
            dateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            monthTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }

        licensePlateIdTextView.setText(permit.LicensePlateId);
        parkingLotTextView.setText(permit.ParkingLotId);
        // Return the completed view to render on screen
        return convertView;
    }
}

