package com.example.a2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FastMartDatabase {

    private static final String DATABASE_NAME = "FastMart.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to add imageUrl column

    // Table names
    public static final String TABLE_FAVOURITES = "favourites";
    public static final String TABLE_CART = "cart";

    // Column names
    public static final String COLUMN_ID = "productId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "imageResId";
    public static final String COLUMN_IMAGE_URL = "imageUrl"; // New column for Bonus marks
    public static final String COLUMN_QUANTITY = "quantity";

    private SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public FastMartDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Favourites logic
    public long addFavourite(String id, String name, String price, int img, String url) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IMAGE, img);
        cv.put(COLUMN_IMAGE_URL, url);
        return db.insertWithOnConflict(TABLE_FAVOURITES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeFavourite(String id) {
        db.delete(TABLE_FAVOURITES, COLUMN_ID + " = ?", new String[]{id});
    }

    public Cursor getAllFavourites() {
        return db.query(TABLE_FAVOURITES, null, null, null, null, null, null);
    }

    // Cart logic
    public long addToCart(String id, String name, String price, int img, String url, int qty) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IMAGE, img);
        cv.put(COLUMN_IMAGE_URL, url);
        cv.put(COLUMN_QUANTITY, qty);
        return db.insertWithOnConflict(TABLE_CART, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void updateCartQuantity(String id, int qty) {
        String query = "UPDATE " + TABLE_CART + " SET " + COLUMN_QUANTITY + " = " + qty + " WHERE " + COLUMN_ID + " = '" + id + "'";
        db.execSQL(query);
    }

    public void removeFromCart(String id) {
        db.delete(TABLE_CART, COLUMN_ID + " = ?", new String[]{id});
    }

    public Cursor getAllCartItems() {
        return db.query(TABLE_CART, null, null, null, null, null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_FAVOURITES + " (" + 
                COLUMN_ID + " TEXT PRIMARY KEY, " + 
                COLUMN_NAME + " TEXT, " + 
                COLUMN_PRICE + " TEXT, " + 
                COLUMN_IMAGE + " INTEGER, " + 
                COLUMN_IMAGE_URL + " TEXT)");
            
            db.execSQL("CREATE TABLE " + TABLE_CART + " (" + 
                COLUMN_ID + " TEXT PRIMARY KEY, " + 
                COLUMN_NAME + " TEXT, " + 
                COLUMN_PRICE + " TEXT, " + 
                COLUMN_IMAGE + " INTEGER, " + 
                COLUMN_IMAGE_URL + " TEXT, " + 
                COLUMN_QUANTITY + " INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int nextVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            onCreate(db);
        }
    }
}
