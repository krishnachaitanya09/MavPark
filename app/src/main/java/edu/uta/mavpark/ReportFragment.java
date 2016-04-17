package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.uta.mavpark.models.ErrorModel;
import edu.uta.mavpark.models.ReportModel;
import edu.uta.mavpark.requests.Report;
import edu.uta.mavpark.requests.RestService;
import retrofit2.Call;
import retrofit2.Response;


public class ReportFragment extends Fragment {

    private RestService restService;
    private SubmitReportTask mSubmitReportTask = null;
    private EditText mReportType;
    private EditText mReportComments;
    private Spinner mParkingLotId;
    private EditText mLicensePlateId;
    private ProgressBar mProgressView;

    public ReportFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restService = new RestService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Report");
        mReportType = (EditText) rootView.findViewById(R.id.report_type);
        mReportComments = (EditText) rootView.findViewById(R.id.report_comments);
        mParkingLotId = (Spinner) rootView.findViewById(R.id.report_parkinglot_id);
        mLicensePlateId = (EditText) rootView.findViewById(R.id.report_license_plate_id);
        mProgressView =(ProgressBar) rootView.findViewById(R.id.report_progress);
        Button submitButton = (Button) rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubmitReportTask != null) {
                    return;
                }
                showProgress(true);
                mSubmitReportTask = new SubmitReportTask(getActivity());
                mSubmitReportTask.execute();
            }
        });
        return rootView;
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


    public class SubmitReportTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        SubmitReportTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Report report = restService.create(Report.class);
            Call<ReportModel> call = report.submitReport(mReportType.getText().toString(), mReportComments.getText().toString(),
                    mParkingLotId.getSelectedItem().toString(), mLicensePlateId.getText().toString(), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Calendar.getInstance().getTime()));
            try {
                Response<ReportModel> response = call.execute();
                if (response.isSuccessful()) {
                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Report has been submitted.", Toast.LENGTH_LONG).show();
                        }
                    });
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
            if (isAdded()) {
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (isAdded())
                showProgress(true);
        }
    }
}
