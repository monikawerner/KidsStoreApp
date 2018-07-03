package com.example.android.kidsstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.kidsstoreapp.data.KidsContract;
import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

/**
    * Allows user to create a new product or edit an existing one.
    */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    int quantity;


    private static final int EXISTING_PRODUCT_LOADER = 0;

       private Uri mCurrentProductUri;


       private EditText mNameEditText;

       private EditText mSupplierNameEditText;

       private EditText mSupplierPhoneNumberEditText;

        private Spinner mCategorySpinner;

        private int mCategory = KidsContract.KidsEntry.CATEGORY_OTHER;

        private EditText mQuantityEditText;

         private EditText mPriceEditText;

         private boolean mProductHasChanged = false;


        private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }


        /** Find all relevant views that we will need to read user input from */
        mNameEditText = findViewById(R.id.edit_product_name);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);
        mCategorySpinner = findViewById(R.id.spinner_category);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mPriceEditText = findViewById(R.id.edit_price);
        mQuantityEditText.setText("0");
        mPriceEditText.setText("0");

        mNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

        setupSpinner();

        ImageView minusButton = findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                if (quantity <= 0) {
                    Toast.makeText(getApplicationContext(), "quantity 0", Toast.LENGTH_SHORT).show();
                } else {
                    quantity = quantity - 1;
                    mProductHasChanged = true;
                    mQuantityEditText.setText(Integer.toString(quantity));
                    if (quantity <=3) {
                        Toast.makeText(getApplicationContext(), "quantity few", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ImageView plusButton = findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                quantity = quantity + 1;
                mProductHasChanged = true;
                mQuantityEditText.setText(Integer.toString(quantity));
                if (quantity <=3) {
                    Toast.makeText(getApplicationContext(), "quantity few", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** Set up FAB  to save the product */
        FloatingActionButton saveProduct = (FloatingActionButton) findViewById(R.id.save);
        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

        /** Set up FAB  to delete the product */
        FloatingActionButton deleteProduct = (FloatingActionButton) findViewById(R.id.delete);
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        Button orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mSupplierPhoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the category of the product.
     */
    private void setupSpinner() {
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_clothes))) {
                        mCategory = KidsEntry.CATEGORY_CLOTHES;
                    } else if (selection.equals(getString(R.string.category_toys))) {
                        mCategory = KidsEntry.CATEGORY_TOYS;
                    } else if (selection.equals(getString(R.string.category_kids_room))) {
                        mCategory = KidsEntry.CATEGORY_KIDS_ROOM;
                    } else if (selection.equals(getString(R.string.category_health_and_hygiene))) {
                        mCategory = KidsEntry.CATEGORY_HEALTH_AND_HYGIENE;
                    } else {
                        mCategory = KidsEntry.CATEGORY_OTHER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = KidsEntry.CATEGORY_OTHER;
            }
        });
    }

        /**
         * Get user input from editor and save product into database.
         */
    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();

        if (nameString.equals("")) {
            Toast.makeText(this,"Product requires a name.", Toast.LENGTH_LONG).show();
            return;
        }
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        if (nameString.equals("")) {
            Toast.makeText(this,"Please enter the supplier name.", Toast.LENGTH_LONG).show();
            return;
        }
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();
        if (supplierPhoneNumberString.equals("")) {
            Toast.makeText(this,"Please enter the supplier phone.", Toast.LENGTH_LONG).show();
            return;
        }
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (quantityString.equals("")) {
            Toast.makeText(this,"Please enter the valid quantity.", Toast.LENGTH_LONG).show();
            return;
        }
        String priceString = mPriceEditText.getText().toString().trim();
        if (nameString.equals("")) {
            Toast.makeText(this,"Please enter the valid price.", Toast.LENGTH_LONG).show();
            return;
        }

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString)&& mCategory == KidsContract.KidsEntry.CATEGORY_OTHER) {return;}


        /** Create a ContentValues object where column names are the keys,
         * and product attributes from the editor are the values. */
        ContentValues values = new ContentValues();
        values.put(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(KidsContract.KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);
        values.put(KidsContract.KidsEntry.COLUMN_CATEGORY, mCategory);
        values.put(KidsContract.KidsEntry.COLUMN_QUANTITY, quantityString);
        values.put(KidsContract.KidsEntry.COLUMN_PRICE, priceString);


        // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            // This is a new product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(KidsContract.KidsEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null&& TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierNameString)
                    && TextUtils.isEmpty(supplierPhoneNumberString) && TextUtils.isEmpty(quantityString)
                    && TextUtils.isEmpty(priceString))  {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                KidsContract.KidsEntry._ID,
                KidsContract.KidsEntry.COLUMN_PRODUCT_NAME,
                KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME,
                KidsContract.KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                KidsContract.KidsEntry.COLUMN_CATEGORY,
                KidsContract.KidsEntry.COLUMN_QUANTITY,
                KidsContract.KidsEntry.COLUMN_PRICE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME);
            int supplierNameColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int categoryColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_CATEGORY);
            int quantityColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));

            // Category is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (category) {
                case KidsContract.KidsEntry.CATEGORY_CLOTHES:
                    mCategorySpinner.setSelection(1);
                    break;
                case KidsContract.KidsEntry.CATEGORY_TOYS:
                    mCategorySpinner.setSelection(2);
                    break;
                case KidsContract.KidsEntry.CATEGORY_KIDS_ROOM:
                    mCategorySpinner.setSelection(3);
                    break;
                case KidsContract.KidsEntry.CATEGORY_HEALTH_AND_HYGIENE:
                    mCategorySpinner.setSelection(4);
                    break;
                default:
                    mCategorySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
        mCategorySpinner.setSelection(0);
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        /** Create and show the AlertDialog */
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            } }
        finish();
    } }
