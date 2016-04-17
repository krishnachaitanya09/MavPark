package edu.uta.mavpark;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.uta.mavpark.data.MavParkDbHelper;
import edu.uta.mavpark.data.UserInfoContract;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        View header = navigationView.getHeaderView(0);
        initNavigationDrawerProfile(header);
    }

    private void initNavigationDrawerProfile(View header) {
        TextView nameTextView = (TextView) header.findViewById(R.id.nameTextView);
        TextView emailTextView = (TextView) header.findViewById(R.id.emailTextView);

        MavParkDbHelper dBHelper = new MavParkDbHelper(this);
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        String[] projection = {
                UserInfoContract.UserInfoEntry.COLUMN_NAME_NAME,
                UserInfoContract.UserInfoEntry.COLUMN_NAME_EMAIL
        };

        Cursor cursor = db.query(
                UserInfoContract.UserInfoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                "1"
        );
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndexOrThrow(UserInfoContract.UserInfoEntry.COLUMN_NAME_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(UserInfoContract.UserInfoEntry.COLUMN_NAME_EMAIL));
        nameTextView.setText(name);
        emailTextView.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else*/
        if (id == R.id.nav_permit) {
            toolbar.setTitle("Permits");
            Fragment permitFragment = new PermitFragment();
            transaction.replace(R.id.fragmentContainer, permitFragment);
        } else if (id == R.id.nav_reserveParking) {
            toolbar.setTitle("Reserve Parking");
            Fragment reserveParkingFragmentFragment = new ReserveParkingFragment();
            transaction.replace(R.id.fragmentContainer, reserveParkingFragmentFragment);
        } else if (id == R.id.nav_remember) {
            toolbar.setTitle("Remember Parking Space");
            Fragment rememberParkingFragment = new RememberParkingFragment();
            transaction.replace(R.id.fragmentContainer, rememberParkingFragment);
        }else if(id == R.id.nav_report)
        {
            toolbar.setTitle("Report");
            Fragment reportFragment = new ReportFragment();
            transaction.replace(R.id.fragmentContainer, reportFragment);
        }
        else if(id==R.id.nav_logout){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
