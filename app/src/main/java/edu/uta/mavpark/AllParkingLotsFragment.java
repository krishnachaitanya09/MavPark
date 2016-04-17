package edu.uta.mavpark;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import edu.uta.mavpark.models.ParkingLotModel;


public class AllParkingLotsFragment extends Fragment {

ArrayList<ParkingLotModel> parkingLotModels;
    public AllParkingLotsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_parking_lot, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Parking lots");
        ListView listView = (ListView) rootView.findViewById(R.id.listview_parkinglots);
        parkingLotModels = new ArrayList<ParkingLotModel>();
        ParkingLotModel parkingLotModel = new ParkingLotModel();
        parkingLotModel.AvailableSpaces = 10;
        parkingLotModel.Id = "P01";
        parkingLotModels.add(parkingLotModel);
        ParkingLotModel parkingLotModel1 = new ParkingLotModel();
        parkingLotModel1.AvailableSpaces = 6;
        parkingLotModel1.Id = "P02";
        parkingLotModels.add(parkingLotModel1);
        ParkingLotModel parkingLotModel2 = new ParkingLotModel();
        parkingLotModel2.AvailableSpaces = 8;
        parkingLotModel2.Id = "P03";
        parkingLotModels.add(parkingLotModel2);
        ReserveParkingAdapter parkingLotAdapter = new ReserveParkingAdapter(getActivity(), parkingLotModels);
        listView.setAdapter(parkingLotAdapter);
        return rootView;
    }
}
