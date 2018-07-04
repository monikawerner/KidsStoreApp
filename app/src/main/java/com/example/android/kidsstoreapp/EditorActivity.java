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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    int quantity;
    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;
    private Spinner mCategorySpinner;
    private int mCategory = KidsEntry.CATEGORY_OTHER;
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
                    Toast.makeText(getApplicationContext(), getText(R.string.empty_inventory), Toast.LENGTH_SHORT).show();
                } else {
                    quantity = quantity - 1;
                    mProductHasChanged = true;
                    mQuantityEditText.setText(Integer.toString(quantity));
                    if (quantity <= 3) {
                        Toast.makeText(getApplicationContext(), getText(R.string.quantity_few), Toast.LENGTH_SHORT).show();
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
            }
        });

        /** Set up FAB  to save the product */
        FloatingActionButton saveProduct = findViewById(R.id.save);
        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

        /** Set up FAB  to delete the product */
        FloatingActionButton deleteProduct = findViewById(R.id.delete);
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
            Toast.makeText(this, getText(R.string.name_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (quantityString.equals("")) {
            Toast.makeText(this, getText(R.string.quantity_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        String priceString = mPriceEditText.getText().toString().trim();
        if (priceString.equals("")) {
            Toast.makeText(this, getText(R.string.price_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        if (supplierNameString.equals("")) {
            Toast.makeText(this, getText(R.string.supplier_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();
        if (supplierPhoneNumberString.equals("")) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString) && mCategory == KidsContract.KidsEntry.CATEGORY_OTHER) {
            return;
        }


        /** Create a ContentValues object where column names are the keys,
         * and product attributes from the editor are the values. */
        ContentValues values = new ContentValues();
        values.put(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(KidsContract.KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);
        values.put(KidsContract.KidsEntry.COLUMN_CATEGORY, mCategory);
        values.put(KidsContract.KidsEntry.COLUMN_QUANTITY, quantityString);
        values.put(KidsContract.KidsEntry.COLUMN_PRICE, priceString);


        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(KidsContract.KidsEntry.CONTENT_URI, values);

            if (newUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierNameString)
                    && TextUtils.isEmpty(supplierPhoneNumberString) && TextUtils.isEmpty(quantityString)
                    && TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /** Respond to a click on the "Save" menu option */
            case R.id.action_save:
                saveProduct();
                return true;
            /** Respond to a click on the "Delete" menu option */
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            /** Respond to a click on the "Up" arrow button in the app bar */
            case R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                KidsContract.KidsEntry._ID,
                KidsContract.KidsEntry.COLUMN_PRODUCT_NAME,
                KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME,
                KidsContract.KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                KidsContract.KidsEntry.COLUMN_CATEGORY,
                KidsContract.KidsEntry.COLUMN_QUANTITY,
                KidsContract.KidsEntry.COLUMN_PRICE};

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_PRODUCT_NAME);
            int supplierNameColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int categoryColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_CATEGORY);
            int quantityColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(KidsContract.KidsEntry.COLUMN_PRICE);

            String name = cursor.getString(nameColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);

            mNameEditText.setText(name);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Double.toString(price));

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
        mNameEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
        mCategorySpinner.setSelection(0);
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

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

    /**
     * Perform the deletion of the product in the database.
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
            }
        }
        finish();
    }
}
