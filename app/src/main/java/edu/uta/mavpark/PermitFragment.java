package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import edu.uta.mavpark.models.PermitModel;
import edu.uta.mavpark.requests.Permit;
import edu.uta.mavpark.requests.RestService;
import retrofit2.Call;
import retrofit2.Response;


public class PermitFragment extends Fragment {

    private static final int PERMIT_LOADER = 0;
    private RestService restService;
    private GetPermitsTask mGetPermitsTask = null;
    private ArrayAdapter<PermitModel> permitListAdapter;
    private ArrayList<PermitModel> permits;
    private ListView mListView;
    private View mProgressView;

    public PermitFragment() {
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


        View rootView = inflater.inflate(R.layout.fragment_permit, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_permit);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment permitDetailsFragment = new PermitDetailsFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("permitConfirmation", (PermitModel)parent.getItemAtPosition(position));
                permitDetailsFragment.setArguments(bundles);
                transaction.replace(R.id.fragmentContainer, permitDetailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        mProgressView = rootView.findViewById(R.id.permit_progress);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mGetPermitsTask != null) {
            return;
        }
        showProgress(true);
        mGetPermitsTask = new GetPermitsTask(getActivity());
        mGetPermitsTask.execute((Void) null);
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

    public class GetPermitsTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        GetPermitsTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Permit permit = restService.create(Permit.class);
            Call<ArrayList<PermitModel>> call = permit.getPermits();
            try {
                Response<ArrayList<PermitModel>> response = call.execute();
                if (response.isSuccessful()) {
                    permits = response.body();
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
            if(isAdded())
            {
                showProgress(false);
                permitListAdapter = new PermitAdapter(context, permits);
                mListView.setAdapter(permitListAdapter);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(isAdded())
            showProgress(true);
        }
    }
}
