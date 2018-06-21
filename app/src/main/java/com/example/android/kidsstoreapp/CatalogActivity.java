package com.example.android.kidsstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

        /**
        * Displays list of products that were entered and stored in the app.
        */

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView listView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);


        mCursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(KidsEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);

            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }



    private void insertProduct() {
        /**Create a ContentValues object where column names are the keys,
         and kids bed attributes are the values. */
        ContentValues values = new ContentValues();
        values.put(KidsEntry.COLUMN_PRODUCT_NAME, "Kids Bed");
        values.put(KidsEntry.COLUMN_PRICE, 300);
        values.put(KidsEntry.COLUMN_QUANTITY, 1);
        values.put(KidsEntry.COLUMN_SUPPLIER_NAME, "Drewex");
        values.put(KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "609848542");
        values.put(KidsEntry.COLUMN_CATEGORY, KidsEntry.CATEGORY_KIDS_ROOM);
        Uri newUri = getContentResolver().insert(KidsEntry.CONTENT_URI, values);
    }

            /**
             * Helper method to delete all products in the database.
             */
            private void deleteAllProducts() {
                int rowsDeleted = getContentResolver().delete(KidsEntry.CONTENT_URI, null, null);
                Log.v("CatalogActivity", rowsDeleted + " rows deleted from products database");
            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu options from the res/menu/menu_catalog.xml file.
                // This adds menu items to the app bar.
                getMenuInflater().inflate(R.menu.menu_catalog, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // User clicked on a menu option in the app bar overflow menu
                switch (item.getItemId()) {
                    // Respond to a click on the "Insert dummy data" menu option
                    case R.id.action_insert_dummy_data:
                        insertProduct();
                        return true;
                    // Respond to a click on the "Delete all entries" menu option
                    case R.id.action_delete_all:
                        deleteAllProducts();
                        return true;
                }
                return super.onOptionsItemSelected(item);
            }

            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle args) {
                String[] projection = {
                        KidsEntry._ID,
                        KidsEntry.COLUMN_PRODUCT_NAME,
                        KidsEntry.COLUMN_PRICE };

                return new CursorLoader(this, KidsEntry.CONTENT_URI, projection, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mCursorAdapter.swapCursor(data);

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mCursorAdapter.swapCursor(null);

            }
        }



