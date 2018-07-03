package com.example.android.kidsstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

/**
 * Database helper for KidsStore app. Manages database creation and version management.
 */
public class KidsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "kids.db";

    private static final String SQL_DELETE_PRODUCTS_ENTRIES =
            "DROP TABLE IF EXISTS " + KidsEntry.TABLE_NAME;

    public KidsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_KIDS_PRODUCTS_TABLE = "CREATE TABLE " + KidsEntry.TABLE_NAME + " ("
                + KidsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KidsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + KidsEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + KidsEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + KidsEntry.COLUMN_CATEGORY + " INTEGER NOT NULL DEFAULT 0, "
                + KidsEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

        db.execSQL(SQL_CREATE_KIDS_PRODUCTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PRODUCTS_ENTRIES);
        onCreate(db);
    }
}

