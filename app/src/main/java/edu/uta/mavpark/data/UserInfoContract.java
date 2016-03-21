package edu.uta.mavpark.data;

import android.provider.BaseColumns;

/**
 * Created by krish on 3/18/2016.
 */
public final class UserInfoContract {

    public UserInfoContract() {
    }

    public static final class UserInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "userInfo";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
    }
}
