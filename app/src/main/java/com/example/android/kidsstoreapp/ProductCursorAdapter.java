package com.example.android.kidsstoreapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.kidsstoreapp.data.KidsContract;
import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

public class ProductCursorAdapter extends CursorAdapter{

    TextView quantityTextView;
    int productQuantity;

    /**
     * Constructs a new ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name of the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView productName = view.findViewById(R.id.name);
        TextView productPrice = view.findViewById(R.id.price);
        quantityTextView = view.findViewById(R.id.quantity);
        TextView supplierName = view.findViewById(R.id.supplier);
        TextView supplierPhone = view.findViewById(R.id.supplier_phone);
        Button saleButton = view.findViewById(R.id.sale_button);

        //Find the columns of products attributes
        int nameColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        // Extract properties from cursor
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        productQuantity = cursor.getInt(quantityColumnIndex);
        String supplier = cursor.getString(supplierNameColumnIndex);
        String phone = cursor.getString(supplierPhoneColumnIndex);

        // Update the TextViews with the attributes for the current product
        productName.setText(name);
        productPrice.setText(price);
        quantityTextView.setText(Integer.toString(productQuantity));
        supplierName.setText(supplier);
        supplierPhone.setText(phone);
        saleButton.setOnClickListener(saleButtonClickListener);
    }
    private View.OnClickListener saleButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parentRow = (View) view.getParent();
            View parentParentRow = (View) parentRow.getParent();
            ListView listView = (ListView) parentParentRow.getParent();
            TextView mQuantity = parentRow.findViewById(R.id.quantity);
            final int position = listView.getPositionForView(parentParentRow);
            long id = getItemId(position);
            String quantity = mQuantity.getText().toString().trim();
            int updatedQuantity = Integer.parseInt(quantity) - 1;
            Uri currentProductUri = ContentUris.withAppendedId(KidsEntry.CONTENT_URI, id);
            if (updatedQuantity <= 0) {
                Toast.makeText(view.getContext(), "quantity 0", Toast.LENGTH_LONG).show();
            } else {
                quantityTextView.setText(Integer.toString(updatedQuantity));
                if (updatedQuantity <= 3) {
                    Toast.makeText(view.getContext(), "quantity few", Toast.LENGTH_LONG).show();
                }
                ContentValues values = new ContentValues();
                values.put(KidsEntry.COLUMN_QUANTITY, updatedQuantity);
                int rowsUpdated = view.getContext().getContentResolver().
                        update(KidsEntry.CONTENT_URI, values, "_ID = ?", new String[]{String.valueOf(id)});
            }
            }
    };

}
