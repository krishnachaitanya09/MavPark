package edu.uta.mavpark;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.uta.mavpark.models.ParkingLotModel;
import edu.uta.mavpark.models.PermitModel;


public class ParkingLotDetailsFragment extends Fragment {

    private static final int PERMIT_LOADER = 0;
    private ArrayList<PermitModel> permits;
    private ListView mListView;
    private View mProgressView;
    private ParkingLotModel mParkingLotDetails;

    public ParkingLotDetailsFragment() {
        permits = new ArrayList<PermitModel>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mParkingLotDetails = (ParkingLotModel) bundle.getSerializable("parkingLotDetails");

        View rootView = inflater.inflate(R.layout.fragment_parking_lot_details, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        TextView parkingLotId = (TextView) rootView.findViewById(R.id.parking_lot_id);
        parkingLotId.setText(mParkingLotDetails.Id);
        TextView parkingLotStatus = (TextView) rootView.findViewById(R.id.parking_lot_status);
        parkingLotStatus.setText(mParkingLotDetails.IsAvailable ? "Open" : "Closed");
        TextView parkingLotDistance = (TextView) rootView.findViewById(R.id.parking_lot_distance);
        parkingLotDistance.setText(new DecimalFormat("0.00").format(mParkingLotDetails.Distance) + " mi");
        TextView parkingLotAvailableSpaces = (TextView) rootView.findViewById(R.id.parking_lot_available_spaces);
        parkingLotAvailableSpaces.setText(mParkingLotDetails.AvailableSpaces+" spaces");
        ((Button) rootView.findViewById(R.id.parking_lot_show_on_map)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?&daddr=" + mParkingLotDetails.Latitude + "," + mParkingLotDetails.Longitude));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
