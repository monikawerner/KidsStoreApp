package com.example.android.kidsstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;




import static android.content.UriMatcher.NO_MATCH;


public class KidsProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = KidsProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the product table
     */
    private static final int KIDS = 100;
    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int KIDS_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(NO_MATCH);

    static {

        /** This URI is used to provide access to MULTIPLE rows of the table. */
        sUriMatcher.addURI(KidsContract.CONTENT_AUTHORITY, KidsContract.PATH_KIDS, KIDS);
        /** This URI is used to provide access to ONE single row of the table. */
        sUriMatcher.addURI(KidsContract.CONTENT_AUTHORITY, KidsContract.PATH_KIDS + "/#", KIDS_ID);
    }

    /**
     * Database helper object
     */
    private KidsDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new KidsDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                cursor = database.query(KidsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case KIDS_ID:
                selection = KidsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(KidsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                return KidsEntry.CONTENT_LIST_TYPE;
            case KIDS_ID:
                return KidsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        /** Prevent adding invalid book's data to database */
        validateName(values);
        validateSupplier(values);
        validatePrice(values);
        validateQuantity(values);
        validateCategory(values);
        validatePhone(values);

        /** Get writable database */
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /** Insert the new product with the given values */
        long id = database.insert(KidsEntry.TABLE_NAME, null, values);
        /** If the ID is -1, then the insertion failed. Log an error and return null. */
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        /** Return the new URI with the ID (of the newly inserted row) appended at the end */
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        /** Get writable database */
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        /** Track the number of rows that were deleted */
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                /** Delete all rows that match the selection and selection args */
                rowsDeleted = database.delete(KidsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case KIDS_ID:
                /** Delete a single row given by the ID in the URI */
                selection = KidsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(KidsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        /** If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed */
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                return updateProduct(uri, values, selection, selectionArgs);
            case KIDS_ID:
                selection = KidsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /** If there are no values to update, then don't try to update the database */
        if (values.size() == 0) {
            return 0;
        }
        if (values.containsKey(KidsEntry.COLUMN_PRODUCT_NAME)) {
            validateName(values);
        }

        if (values.containsKey(KidsEntry.COLUMN_SUPPLIER_NAME)) {
            validateSupplier(values);
        }

        if (values.containsKey(KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            validatePhone(values);
        }

        if (values.containsKey(KidsEntry.COLUMN_CATEGORY)) {
            validateCategory(values);
        }

        if (values.containsKey(KidsEntry.COLUMN_QUANTITY)) {
            validateQuantity(values);
        }

        if (values.containsKey(KidsEntry.COLUMN_PRICE)) {
            validatePrice(values);
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /** Perform the update on the database and get the number of rows affected */
        int rowsUpdated = database.update(KidsEntry.TABLE_NAME, values, selection, selectionArgs);
        /**  If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed */
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    /**
     * helper methods to validate data put by a user in editor - used when insert and update
     **/

    private void validateName(ContentValues values) {
        String name = values.getAsString(KidsEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Product requires a name.");
        }
    }

    private void validateSupplier(ContentValues values) {
        String supplier = values.getAsString(KidsEntry.COLUMN_SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplier)) {
            throw new IllegalArgumentException("Product requires a supplier.");
        }
    }

    private void validateCategory(ContentValues values) {
        Integer category = values.getAsInteger(KidsEntry.COLUMN_CATEGORY);
        if (category != null && !KidsEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("Product requires valid category");
        }
    }

    private void validatePrice(ContentValues values) {
        Double price = values.getAsDouble(KidsEntry.COLUMN_PRICE);
        if (price != null && price < 0.00) {
            throw new IllegalArgumentException("Price can't be less than zero.");
        }
    }

    private void validateQuantity(ContentValues values) {
        Integer quantity = values.getAsInteger(KidsEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity can't be less than zero.");
        }
    }

    private void validatePhone(ContentValues values) {
        String phone = values.getAsString(KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (TextUtils.isEmpty(phone)) {
            throw new IllegalArgumentException("Product requires a supplier phone number.");
        }
    }
}