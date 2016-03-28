package edu.uta.mavpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.uta.mavpark.models.ParkingLotModel;

/**
 * Created by krish on 3/22/2016.
 */
public class ReserveParkingAdapter extends ArrayAdapter<ParkingLotModel> {
    public ReserveParkingAdapter(Context context, ArrayList<ParkingLotModel> parkingLots) {
        super(context, 0, parkingLots);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParkingLotModel parkingLot = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_parking, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        TextView parkingLotIdTextView = (TextView) convertView.findViewById(R.id.list_item_parkingLotId_textView);
        TextView parkingLotDistanceTextView = (TextView) convertView.findViewById(R.id.list_item_parkingLotDistance_textView);
        parkingLotIdTextView.setText("Parking lot " + parkingLot.Id);
        DecimalFormat df = new DecimalFormat("0.00");
        parkingLotDistanceTextView.setText(df.format(parkingLot.Distance) + " mi");
        TextDrawable.IBuilder mDrawableBuilder = TextDrawable.builder().round();
        TextDrawable drawable = mDrawableBuilder
                .build(String.valueOf(parkingLot.AvailableSpaces), ColorGenerator.MATERIAL.getColor(parkingLot.AvailableSpaces));
        imageView.setImageDrawable(drawable);
        // Return the completed view to render on screen
        return convertView;
    }
}

