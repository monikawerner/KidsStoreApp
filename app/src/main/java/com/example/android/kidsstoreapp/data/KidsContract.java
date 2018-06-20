package com.example.android.kidsstoreapp.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the KidsStore app.
 */

public final class KidsContract {
    private KidsContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.kids";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_KIDS = "kids";

    /**
     * Inner class that defines constant values for the kids products database table.
     * Each entry in the table represents a single kids product.
     */
    public static final class KidsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_KIDS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_KIDS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_KIDS;

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

        /**
         * Returns whether or not the given category is other, clothes, toys, kids_room, health_and_hygiene
         */
        public static boolean isValidCategory(int category) {
            if (category == CATEGORY_OTHER || category == CATEGORY_CLOTHES || category == CATEGORY_TOYS
                    || category == CATEGORY_KIDS_ROOM || category == CATEGORY_HEALTH_AND_HYGIENE
                    ) {
                return true;
            }
            return false;
        }

    }
}