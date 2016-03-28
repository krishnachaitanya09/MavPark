package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import edu.uta.mavpark.models.ErrorModel;
import edu.uta.mavpark.models.ParkingLotModel;
import edu.uta.mavpark.requests.Parking;
import edu.uta.mavpark.requests.RestService;
import retrofit2.Call;
import retrofit2.Response;


public class ReserveParkingFragment extends Fragment {

    private LocationTracker tracker;
    double longitude, latitude;
    private RestService restService;
    private GetParkingLotsTask mGetParkingLotsTask = null;
    private ArrayList<ParkingLotModel> parkingLots;
    private ArrayAdapter<ParkingLotModel> parkingLotListAdapter;
    private ListView mListView;
    private View mProgressView;

    public ReserveParkingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restService = new RestService(getActivity());
    }

    private void getLocation() {
        showProgress(true);
        if (tracker.isLocationEnabled) {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            if (mGetParkingLotsTask != null) {
                return;
            }
            mGetParkingLotsTask = new GetParkingLotsTask(getActivity());
            mGetParkingLotsTask.execute((Void) null);

        } else {
            // show dialog box to user to enable location
            tracker.askToOnLocation();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reserve_parking, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_reserveParking);
        mProgressView = rootView.findViewById(R.id.reserveParking_progress);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment parkingLotDetailsFragment = new ParkingLotDetailsFragment();
                Bundle bundles = new Bundle();
                ParkingLotModel parkingLotModel = (ParkingLotModel) parent.getItemAtPosition(position);
                bundles.putSerializable("parkingLotDetails", parkingLotModel);
                parkingLotDetailsFragment.setArguments(bundles);
                transaction.replace(R.id.fragmentContainer, parkingLotDetailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tracker = new LocationTracker(getActivity());
        getLocation();
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

    public class GetParkingLotsTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        GetParkingLotsTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Parking parking = restService.create(Parking.class);
            Call<ArrayList<ParkingLotModel>> call = parking.getParkingLots(latitude, longitude);
            try {
                Response<ArrayList<ParkingLotModel>> response = call.execute();
                if (response.isSuccessful()) {
                    parkingLots = response.body();
                } else {
                    Gson gson = new GsonBuilder().create();
                    final ErrorModel errorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, errorModel.Message.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showProgress(false);
            parkingLotListAdapter = new ReserveParkingAdapter(context, parkingLots);
            mListView.setAdapter(parkingLotListAdapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showProgress(true);
        }
    }
}
