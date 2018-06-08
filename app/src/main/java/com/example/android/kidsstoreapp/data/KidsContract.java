package com.example.android.kidsstoreapp.data;


import android.provider.BaseColumns;

/**
 * API Contract for the KidsStore app.
 */

public final class KidsContract {
    private KidsContract() {
    }

    /**
     * Inner class that defines constant values for the kids products database table.
     * Each entry in the table represents a single kids product.
     */
    public static final class KidsEntry implements BaseColumns {
        public final static String TABLE_NAME = "kids_products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
        public final static String COLUMN_CATEGORY = "category";

        /**
         * Possible values for the category of the kids product.
         */
        public static final int CATEGORY_OTHER = 0;
        public static final int CATEGORY_CLOTHES = 1;
        public static final int CATEGORY_TOYS = 2;
        public static final int CATEGORY_KIDS_ROOM = 3;
        public static final int CATEGORY_HEALTH_AND_HYGIENE = 4;

    }
}