package com.example.android.kidsstoreapp;


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.kidsstoreapp.data.KidsContract.KidsEntry;

public class ProductCursorAdapter extends CursorAdapter{

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
        TextView productName = (TextView) view.findViewById(R.id.name);
        TextView productPrice = (TextView) view.findViewById(R.id.price);
        //Find the columns of pet attributes
        int nameColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(KidsEntry.COLUMN_PRICE);
        // Extract properties from cursor
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current product
        productName.setText(name);
        productPrice.setText(price);
    }
}
