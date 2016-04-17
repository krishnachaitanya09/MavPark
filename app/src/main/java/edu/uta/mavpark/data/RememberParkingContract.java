package edu.uta.mavpark.data;

import android.provider.BaseColumns;

/**
 * Created by krish on 3/18/2016.
 */
public final class RememberParkingContract {

    public RememberParkingContract() {
    }

    public static final class RememberParkingEntry implements BaseColumns {
        public static final String TABLE_NAME = "rememberParking";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
