package com.smallacademy.userroles;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "product_database";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_QUANTITY = "quantity";

    // Create the database table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_BARCODE + " TEXT,"
            + COLUMN_DESC + " TEXT,"
            + COLUMN_CATEGORY + " TEXT,"
            + COLUMN_IMAGE + " TEXT,"
            + COLUMN_QUANTITY + " INTEGER)";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Recreate the table
        onCreate(db);
    }

    public long insertProduct(String name, double price, String barcode, String desc, String category, String image, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_BARCODE, barcode);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_QUANTITY, quantity);

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }
}
