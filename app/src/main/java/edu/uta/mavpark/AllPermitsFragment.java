package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.uta.mavpark.models.PermitModel;
import edu.uta.mavpark.requests.RestService;


public class AllPermitsFragment extends Fragment {

    private static final int PERMIT_LOADER = 0;
    private RestService restService;
    private ArrayAdapter<PermitModel> permitListAdapter;
    private ArrayList<PermitModel> permits;
    private ListView mListView;
    private View mProgressView;
    private Toolbar toolbar;

    public AllPermitsFragment() {
        permits = new ArrayList<PermitModel>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restService = new RestService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_all_permits, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_all_permit);
        mProgressView = rootView.findViewById(R.id.permit_progress);
/*        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.permit_menu);
        toolbar.setTitle("Permits");*/
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PermitModel permit1 = new PermitModel();
        permit1.UserId = "1001234556";
        permit1.FromDateTime = new Date(2016, 4, 4, 12, 00, 00);
        permit1.ToDateTime = new Date(2016, 4, 30, 12, 00, 00);
        permit1.ParkingLotId = "L01";
        permits.add(permit1);

        PermitModel permit2 = new PermitModel();
        permit2.UserId = "1001478965";
        permit2.FromDateTime = new Date(2016, 5, 5, 12, 00, 00);
        permit2.ToDateTime = new Date(2016, 5, 30, 12, 00, 00);
        permit2.ParkingLotId = "L02";
        permits.add(permit2);

        PermitModel permit3 = new PermitModel();
        permit3.UserId = "1001236985";
        permit3.FromDateTime = new Date(2016, 5, 8, 12, 00, 00);
        permit3.ToDateTime = new Date(2016, 5, 20, 12, 00, 00);
        permit3.ParkingLotId = "L03";
        permits.add(permit3);
        AllPermitsAdapter adapter = new AllPermitsAdapter(getActivity(), permits);
        mListView.setAdapter(adapter);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public class AllPermitsAdapter extends ArrayAdapter<PermitModel> {
        public AllPermitsAdapter(Context context, ArrayList<PermitModel> permits) {
            super(context, 0, permits);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            PermitModel permit = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_all_permit, parent, false);
            }
            // Lookup view for data population
            TextView dateTextView = (TextView) convertView.findViewById(R.id.list_item__all_date_textview);
            TextView monthTextView = (TextView) convertView.findViewById(R.id.list_item_all_month_textview);
            TextView licensePlateIdTextView = (TextView) convertView.findViewById(R.id.list_item_utaid_textview);
            TextView parkingLotTextView = (TextView) convertView.findViewById(R.id.list_item_all_parkingLot_textView);
            Calendar cal = Calendar.getInstance();
            cal.setTime(permit.ToDateTime);
            dateTextView.setText(new SimpleDateFormat("dd").format(cal.getTime()));
            monthTextView.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
            if (permit.ToDateTime.getTime() >= new Date().getTime()) {
                dateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                monthTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            } else {
                dateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                monthTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }

            licensePlateIdTextView.setText(permit.UserId);
            parkingLotTextView.setText(permit.ParkingLotId);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
