package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private EditText mFromDateEditText;
    private EditText mToDateEditText;
    private EditText mFromTimeEditText;
    private EditText mToTimeEditText;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private Toolbar toolbar;

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
                showProgress(false);
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
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.parking_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_okay) {

                    long currentTime = Calendar.getInstance().getTime().getTime();
                    if (fromCalendar.getTime().getTime() >= currentTime && toCalendar.getTime().getTime() >= currentTime && fromCalendar.getTime().getTime() <= toCalendar.getTime().getTime()) {
                        tracker = new LocationTracker(getActivity());
                        getLocation();
                    } else {
                        Toast.makeText(getActivity(), "Please select a valid date and time", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.listview_reserveParking);
        mProgressView = rootView.findViewById(R.id.reserveParking_progress);
        mFromDateEditText = (EditText) rootView.findViewById(R.id.from_date);
        mToDateEditText = (EditText) rootView.findViewById(R.id.to_date);
        mFromTimeEditText = (EditText) rootView.findViewById(R.id.from_time);
        mToTimeEditText = (EditText) rootView.findViewById(R.id.to_time);

        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, monthOfYear);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mFromDateEditText.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(fromCalendar.getTime()));
            }

        };

        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toCalendar.set(Calendar.YEAR, year);
                toCalendar.set(Calendar.MONTH, monthOfYear);
                toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mToDateEditText.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(toCalendar.getTime()));
            }

        };

        final TimePickerDialog.OnTimeSetListener fromTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fromCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                fromCalendar.set(Calendar.MINUTE, minute);
                mFromTimeEditText.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(fromCalendar.getTime()));
            }
        };

        final TimePickerDialog.OnTimeSetListener toTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                toCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                toCalendar.set(Calendar.MINUTE, minute);
                mToTimeEditText.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(toCalendar.getTime()));
            }
        };

        mFromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), fromDate, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });


        mToDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), toDate, toCalendar
                        .get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                        toCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });


        mFromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), fromTime, fromCalendar
                        .get(Calendar.HOUR_OF_DAY), fromCalendar.get(Calendar.MINUTE),
                        false);
                timePickerDialog.show();
            }
        });

        mToTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), toTime, toCalendar
                        .get(Calendar.HOUR_OF_DAY), toCalendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                 FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                 Fragment parkingLotDetailsFragment = new ReserveParkingDetailsFragment();
                                                 Bundle bundles = new Bundle();
                                                 ParkingLotModel parkingLotModel = (ParkingLotModel) parent.getItemAtPosition(position);
                                                 bundles.putSerializable("parkingLotDetails", parkingLotModel);
                                                 bundles.putSerializable("fromCalendar", fromCalendar);
                                                 bundles.putSerializable("toCalendar", toCalendar);
                                                 parkingLotDetailsFragment.setArguments(bundles);
                                                 transaction.replace(R.id.fragmentContainer, parkingLotDetailsFragment);
                                                 transaction.addToBackStack(null);
                                                 transaction.commit();
                                             }
                                         }

        );
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /*tracker = new LocationTracker(getActivity());
        getLocation();*/
    }

    @Override
    public void onDestroy() {
        toolbar.getMenu().clear();
        super.onDestroy();
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
            Call<ArrayList<ParkingLotModel>> call = parking.getParkingLots(latitude, longitude, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(fromCalendar.getTime()), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(toCalendar.getTime()));
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
            mGetParkingLotsTask = null;
            parkingLotListAdapter = new ReserveParkingAdapter(context, parkingLots);
            mListView.setAdapter(parkingLotListAdapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showProgress(false);
            mGetParkingLotsTask = null;
        }
    }
}
