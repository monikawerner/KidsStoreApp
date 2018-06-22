package com.example.android.kidsstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static android.content.UriMatcher.NO_MATCH;


public class KidsProvider extends ContentProvider {
    private KidsDbHelper mDbHelper;
    /** URI matcher code for the content URI for the product table */
    private static final int KIDS = 100;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int KIDS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(KidsContract.CONTENT_AUTHORITY, KidsContract.PATH_KIDS, KIDS);
        sUriMatcher.addURI(KidsContract.CONTENT_AUTHORITY, KidsContract.PATH_KIDS + "/#", KIDS_ID);
    }


    /** Tag for the log messages */
    public static final String LOG_TAG = KidsProvider.class.getSimpleName();

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
    public Cursor query(Uri uri, String[] projection, String selection, String[]selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case KIDS:
                cursor = database.query(KidsContract.KidsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case KIDS_ID:
                selection = KidsContract.KidsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(KidsContract.KidsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor; }
    /**
     * Insert new data into the provider with the given ContentValues.
     */

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name"); }

        String supplierName = values.getAsString(KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product requires a supplier name"); }

        Integer category = values.getAsInteger(KidsContract.KidsEntry.COLUMN_CATEGORY);
        if (category == null || !KidsContract.KidsEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("Product requires valid category");}

        Integer quantity = values.getAsInteger(KidsContract.KidsEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        Integer price = values.getAsInteger(KidsContract.KidsEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(KidsContract.KidsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }
    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case KIDS_ID:
                selection = KidsContract.KidsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
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
        if (values.containsKey(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link KidsEntry#COLUMN_CATEGORY} key is present,
        // check that the category value is valid.
        if (values.containsKey(KidsContract.KidsEntry.COLUMN_CATEGORY)) {
            Integer category = values.getAsInteger(KidsContract.KidsEntry.COLUMN_CATEGORY);
            if (category == null || !KidsContract.KidsEntry.isValidCategory(category)) {
                throw new IllegalArgumentException("Product requires valid category");
            }
        }

        // If the {@link KidsEntry#COLUMN_PRICE} key is present,
        // check that the weight value is valid.
        if (values.containsKey(KidsContract.KidsEntry.COLUMN_PRICE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer price = values.getAsInteger(KidsContract.KidsEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(KidsContract.KidsEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(KidsContract.KidsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case KIDS_ID:
                // Delete a single row given by the ID in the URI
                selection = KidsContract.KidsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(KidsContract.KidsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;

    }


    /**
     * Returns the MIME type of data for the content URI.
     */

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case KIDS:
                return KidsContract.KidsEntry.CONTENT_LIST_TYPE;
            case KIDS_ID:
                return KidsContract.KidsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}