package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uta.mavpark.models.ErrorModel;
import edu.uta.mavpark.models.ParkingLotModel;
import edu.uta.mavpark.models.PermitModel;
import edu.uta.mavpark.requests.Permit;
import edu.uta.mavpark.requests.RestService;
import retrofit2.Call;
import retrofit2.Response;


public class ReserveParkingDetailsFragment extends Fragment {

    private static final int PERMIT_LOADER = 0;
    private PermitModel permitModel;
    private ListView mListView;
    private View mProgressView;
    private TextView mLicensePlatView;
    private ParkingLotModel mParkingLotDetails;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private RestService restService;
    private BookPermitTask mBookPermitTask = null;


    public ReserveParkingDetailsFragment() {
        permitModel = new PermitModel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restService = new RestService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mParkingLotDetails = (ParkingLotModel) bundle.getSerializable("parkingLotDetails");
        fromCalendar = (Calendar) bundle.getSerializable("fromCalendar");
        toCalendar = (Calendar) bundle.getSerializable("toCalendar");

        View rootView = inflater.inflate(R.layout.fragment_reserve_parking_details, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        TextView parkingLotId = (TextView) rootView.findViewById(R.id.parking_lot_id);
        parkingLotId.setText(mParkingLotDetails.Id);
        TextView parkingLotStatus = (TextView) rootView.findViewById(R.id.parking_lot_status);
        parkingLotStatus.setText(mParkingLotDetails.IsAvailable ? "Open" : "Closed");
        TextView parkingLotDistance = (TextView) rootView.findViewById(R.id.parking_lot_distance);
        parkingLotDistance.setText(new DecimalFormat("0.00").format(mParkingLotDetails.Distance) + " mi");
        TextView parkingLotAvailableSpaces = (TextView) rootView.findViewById(R.id.parking_lot_available_spaces);
        parkingLotAvailableSpaces.setText(mParkingLotDetails.AvailableSpaces + " spaces");
        ((TextView) rootView.findViewById(R.id.parking_lot_from)).setText(new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.US).format(fromCalendar.getTime()));
        ((TextView) rootView.findViewById(R.id.parking_lot_to)).setText(new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.US).format(toCalendar.getTime()));

        ((Button) rootView.findViewById(R.id.parking_lot_book_permit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookPermitTask != null) {
                    return;
                }
                showProgress(true);
                mBookPermitTask = new BookPermitTask(getActivity());
                mBookPermitTask.execute((Void) null);
            }
        });

        ((Button) rootView.findViewById(R.id.parking_lot_show_on_map)).setOnClickListener(new View.OnClickListener() {
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

        mProgressView = rootView.findViewById(R.id.book_permit_progress);
        mLicensePlatView = (TextView) rootView.findViewById(R.id.license_plate_id);
        mLicensePlatView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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

    public class BookPermitTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        BookPermitTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Permit permit = restService.create(Permit.class);
            Call<PermitModel> call = permit.bookPermit(mParkingLotDetails.Id, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(fromCalendar.getTime()), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(toCalendar.getTime()), mLicensePlatView.getText().toString());
            try {
                Response<PermitModel> response = call.execute();
                if (response.isSuccessful()) {
                    permitModel = response.body();
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
            mBookPermitTask = null;

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment PermitConfirmationFragment = new PermitConfirmationFragment();
            Bundle bundles = new Bundle();
            bundles.putSerializable("permitConfirmation", permitModel);
            PermitConfirmationFragment.setArguments(bundles);
            transaction.replace(R.id.fragmentContainer, PermitConfirmationFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showProgress(false);
            mBookPermitTask = null;
        }
    }
}
