package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.uta.mavpark.data.MavParkDbHelper;
import edu.uta.mavpark.data.RememberParkingContract;
import edu.uta.mavpark.models.RememberParkingModel;


public class RememberParkingFragment extends Fragment {

    private LocationTracker tracker;
    private ArrayAdapter<RememberParkingModel> parkingListAdapter;
    private ArrayList<RememberParkingModel> parkings;
    private ListView mListView;
    private View mProgressView;
    private Toolbar toolbar;

    public RememberParkingFragment() {
        parkings = new ArrayList<RememberParkingModel>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_remember_parking, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_remember_parking);
        mProgressView = rootView.findViewById(R.id.remember_parking_progress);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.remember_parking_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_drop_pin) {

                    showProgress(true);
                    tracker = new LocationTracker(getActivity());
                    if (tracker.isLocationEnabled) {
                        RememberParkingModel rememberParkingModel = new RememberParkingModel();
                        rememberParkingModel.Latitude = String.valueOf(tracker.getLatitude());
                        rememberParkingModel.Longitude = String.valueOf(tracker.getLongitude());
                        rememberParkingModel.ParkedDate = Calendar.getInstance().getTime();
                        MavParkDbHelper dBHelper = new MavParkDbHelper(getActivity());
                        SQLiteDatabase db = dBHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_LATITUDE, rememberParkingModel.Latitude);
                        values.put(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_LONGITUDE, rememberParkingModel.Longitude);
                        values.put(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rememberParkingModel.ParkedDate));

                        long newRowId = db.insert(
                                RememberParkingContract.RememberParkingEntry.TABLE_NAME,
                                null,
                                values);

                        parkings.add(0, rememberParkingModel);
                        Location location = new Location("");
                        location.setLatitude(Double.parseDouble(rememberParkingModel.Latitude));
                        location.setLongitude(Double.parseDouble(rememberParkingModel.Longitude));
                        parkingListAdapter = new RememberParkingAdapter(getActivity(), parkings, location);
                        mListView.setAdapter(parkingListAdapter);
                    } else {
                        // show dialog box to user to enable location
                        tracker.askToOnLocation();
                    }
                    showProgress(false);
                }
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                 RememberParkingModel parkingModel = (RememberParkingModel) parent.getItemAtPosition(position);
                                                 Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                         Uri.parse("http://maps.google.com/maps?&daddr=" + parkingModel.Latitude + "," + parkingModel.Longitude));
                                                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                 intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                 intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                 startActivity(intent);
                                             }
                                         }

        );

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RememberParkingModel parkingModel =(RememberParkingModel) parent.getItemAtPosition(position);
                MavParkDbHelper dBHelper = new MavParkDbHelper(getActivity());
                SQLiteDatabase db = dBHelper.getWritableDatabase();
                int i = db.delete(RememberParkingContract.RememberParkingEntry.TABLE_NAME,
                        RememberParkingContract.RememberParkingEntry.COLUMN_NAME_ID + "=" + parkingModel.Id, null);
                parkings.remove(position);
                parkingListAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MavParkDbHelper dBHelper = new MavParkDbHelper(getActivity());
        SQLiteDatabase db = dBHelper.getReadableDatabase();

        Cursor cursor = db.query(
                RememberParkingContract.RememberParkingEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "1"
        );
        while (cursor.moveToNext()) {
            RememberParkingModel parkingModel = new RememberParkingModel();
            parkingModel.Id = cursor.getInt(cursor.getColumnIndexOrThrow(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_ID));
            parkingModel.Latitude = cursor.getString(cursor.getColumnIndexOrThrow(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_LATITUDE));
            parkingModel.Longitude = cursor.getString(cursor.getColumnIndexOrThrow(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_LONGITUDE));
            try {
                parkingModel.ParkedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(cursor.getString(cursor.getColumnIndexOrThrow(RememberParkingContract.RememberParkingEntry.COLUMN_NAME_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            parkings.add(parkingModel);
        }
        tracker = new LocationTracker(getActivity());
        Location location = new Location("");
        if (tracker.isLocationEnabled) {
            location.setLatitude(tracker.getLatitude());
            location.setLongitude(tracker.getLongitude());
        }

        parkingListAdapter = new RememberParkingAdapter(getActivity(), parkings, location);
        mListView.setAdapter(parkingListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
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
}
