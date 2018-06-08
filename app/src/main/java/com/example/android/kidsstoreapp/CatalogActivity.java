package com.example.android.kidsstoreapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.kidsstoreapp.data.KidsDbHelper;
import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private KidsDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /** Access the database */
        mDbHelper = new KidsDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the kids products database.
     */
    private void displayDatabaseInfo() {

        KidsDbHelper mDbHelper = new KidsDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {KidsEntry._ID,
                KidsEntry.COLUMN_PRODUCT_NAME,
                KidsEntry.COLUMN_PRICE,
                KidsEntry.COLUMN_QUANTITY,
                KidsEntry.COLUMN_SUPPLIER_NAME,
                KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                KidsEntry.COLUMN_CATEGORY};


        Cursor cursor = db.query(KidsEntry.TABLE_NAME, projection, null, null, null, null, null);
        TextView displayView = (TextView) findViewById(R.id.text_view_kids_product);

        try {
            /** Create a header in the Text View */
            displayView.setText("The products table contains  " + cursor.getCount() + " kids products.\n\n");
            displayView.append(KidsEntry._ID + " - " + KidsEntry.COLUMN_PRODUCT_NAME + " - "
                    + KidsEntry.COLUMN_PRICE + " - " + KidsEntry.COLUMN_QUANTITY + " - "
                    + KidsEntry.COLUMN_SUPPLIER_NAME + " - " + KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " - "
                    + KidsEntry.COLUMN_CATEGORY + "\n");

            /** Figure out the index of each column */
            int idColumnIndex = cursor.getColumnIndex(KidsEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int categoryColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_CATEGORY);

            /**  Iterate through all the returned rows in the cursor*/

            while (cursor.moveToNext()) {
                /**  Use that index to extract the String or Int value of the word
                 // at the current row the cursor is on */
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
                int currentCategory = cursor.getInt(categoryColumnIndex);

                /** Display the values from each column of the current row in the cursor in the TextView */
                displayView.append(("\n" + currentID + " - " +
                        currentProductName + " - " + currentPrice + " - " + currentQuantity + " - "
                        + currentSupplierName + " - " + currentSupplierPhoneNumber + " - " + currentCategory));
            }
        } finally {
            cursor.close();
        }
    }

}

