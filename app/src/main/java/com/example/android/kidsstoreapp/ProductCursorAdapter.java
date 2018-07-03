package com.example.android.kidsstoreapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

public class ProductCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new ProductCursorAdapter.
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
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
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
    public void bindView(final View view, final Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String name = cursor.getString(cursor.getColumnIndex(KidsEntry.COLUMN_PRODUCT_NAME));
        double priceDouble = cursor.getDouble(cursor.getColumnIndex(KidsEntry.COLUMN_PRICE));
        String price = String.valueOf(priceDouble);
        final int quantityInt = cursor.getInt(cursor.getColumnIndex(KidsEntry.COLUMN_QUANTITY));
        String quantity = String.valueOf(quantityInt);
        String supplier = cursor.getString(cursor.getColumnIndex(KidsEntry.COLUMN_SUPPLIER_NAME));
        String phone = cursor.getString(cursor.getColumnIndex(KidsEntry.COLUMN_SUPPLIER_PHONE_NUMBER));

        holder.nameView.setText(name);
        holder.priceView.setText(price);
        holder.quantityView.setText(quantity);
        holder.supplierNameView.setText(supplier);
        holder.supplierPhoneView.setText(phone);


        final String id = String.valueOf(cursor.getInt(cursor.getColumnIndex(KidsEntry._ID)));

        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityInt >= 1) {
                    Uri currentProductUri = Uri.withAppendedPath(KidsEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(KidsEntry.COLUMN_QUANTITY, quantityInt - 1);
                    if (quantityInt == 3) {
                        Toast.makeText(view.getContext(), "Only two left", Toast.LENGTH_SHORT).show();
                    }
                    int rowsAffected = view.getContext().getContentResolver().update(currentProductUri, values, null, null);
                    if (rowsAffected == 0) {
                        Toast.makeText(view.getContext(), "Error with updating the book", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(view.getContext(), "Cannot sale", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class ViewHolder {
        private TextView nameView;
        private TextView priceView;
        private TextView quantityView;
        private TextView supplierNameView;
        private TextView supplierPhoneView;
        private Button saleButton;

        private ViewHolder(View view) {
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            quantityView = view.findViewById(R.id.quantity);
            supplierNameView = view.findViewById(R.id.supplier);
            supplierPhoneView = view.findViewById(R.id.supplier_phone);
            saleButton = view.findViewById(R.id.sale_button);

        }
    }

}
