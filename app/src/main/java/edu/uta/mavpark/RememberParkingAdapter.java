package edu.uta.mavpark;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.uta.mavpark.models.RememberParkingModel;

/**
 * Created by krish on 3/22/2016.
 */
public class RememberParkingAdapter extends ArrayAdapter<RememberParkingModel> {
    private Location currentLocation;

    public RememberParkingAdapter(Context context, ArrayList<RememberParkingModel> parkings, Location currentLocation) {
        super(context, 0, parkings);
        this.currentLocation = currentLocation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RememberParkingModel parking = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_remember_parking, parent, false);
        }
        // Lookup view for data population
        TextView dateTextView = (TextView) convertView.findViewById(R.id.list_item_date_textview);
        TextView monthTextView = (TextView) convertView.findViewById(R.id.list_item_month_textview);
        TextView address = (TextView) convertView.findViewById(R.id.list_item_address_textview);

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(parking.Latitude), Double.parseDouble(parking.Longitude), 1);
            address.setText(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView parkingLotDistanceTextView = (TextView) convertView.findViewById(R.id.list_item_parkingDistance_textView);
        DecimalFormat df = new DecimalFormat("0.00");
        parkingLotDistanceTextView.setText(df.format(getDistance(Double.parseDouble(parking.Latitude), Double.parseDouble(parking.Longitude))) + " mi");
        Calendar cal = Calendar.getInstance();
        cal.setTime(parking.ParkedDate);
        dateTextView.setText(new SimpleDateFormat("dd").format(cal.getTime()));
        monthTextView.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
        dateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        monthTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        // Return the completed view to render on screen
        return convertView;
    }

    private double getDistance(double latitude, double longitude) {
        Location loc1 = new Location("");
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(currentLocation.getLatitude());
        loc2.setLongitude(currentLocation.getLongitude());

        return loc1.distanceTo(loc2);
    }
}

