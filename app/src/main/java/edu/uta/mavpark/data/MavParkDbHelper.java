package edu.uta.mavpark.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.uta.mavpark.data.UserInfoContract.UserInfoEntry;
import edu.uta.mavpark.data.RememberParkingContract.RememberParkingEntry;

/**
 * Created by krish on 3/18/2016.
 */
public class MavParkDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MavPark.db";

    public MavParkDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_USERINFO =  "CREATE TABLE " + UserInfoEntry.TABLE_NAME + " (" +
                UserInfoEntry.COLUMN_NAME_EMAIL + " TEXT PRIMARY KEY," +
                UserInfoEntry.COLUMN_NAME_NAME + " TEXT"+
        " )";

        final String SQL_CREATE_REMEMBERPARKING = "CREATE TABLE " + RememberParkingEntry.TABLE_NAME + " (" +
                RememberParkingEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                RememberParkingEntry.COLUMN_NAME_LATITUDE + " TEXT," +
                RememberParkingEntry.COLUMN_NAME_LONGITUDE + " TEXT,"+
                RememberParkingEntry.COLUMN_NAME_DATE + " TEXT"+
                " )";

        db.execSQL(SQL_CREATE_USERINFO);
        db.execSQL(SQL_CREATE_REMEMBERPARKING);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + UserInfoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RememberParkingEntry.TABLE_NAME);
        onCreate(db);
    }
}
