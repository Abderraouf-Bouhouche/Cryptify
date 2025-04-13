package com.example.cryptify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "namebrk.db";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PUBLICKEY = "publickey";
    private static final String COLUMN_PRIVATEKEY = "privatekey";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("CREATE TABLE " + TABLE_USERS + " (" + 
            COLUMN_USERNAME + " TEXT PRIMARY KEY, " + 
            COLUMN_PASSWORD + " TEXT, " + 
            COLUMN_PUBLICKEY + " TEXT, " + 
            COLUMN_PRIVATEKEY + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            MyDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "_new (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_PUBLICKEY + " TEXT, " +
                    COLUMN_PRIVATEKEY + " TEXT)");

            MyDB.execSQL("INSERT INTO " + TABLE_USERS + "_new SELECT * FROM " + TABLE_USERS);
            MyDB.execSQL("DROP TABLE " + TABLE_USERS);
            MyDB.execSQL("ALTER TABLE " + TABLE_USERS + "_new RENAME TO " + TABLE_USERS);
        }
    }

    public boolean insertData(String username, String password, String publickey, String privatekey) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_PUBLICKEY, publickey);
        contentValues.put(COLUMN_PRIVATEKEY, privatekey);
        long result = MyDatabase.insert(TABLE_USERS, null, contentValues);

        return result != -1;
    }


    public boolean checkUsername(String username) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            MyDatabase.close();
        }
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            MyDatabase.close();
        }
    }

    public String getPublicKeyByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String publicKey = null;

        // Define the query
        String[] columns = {"publickey"};
        String selection = "username=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,  // table name
                columns,            // columns to return
                selection,          // WHERE clause
                selectionArgs,      // WHERE arguments
                null, null, null
        );

        if (cursor.moveToFirst()) {
            publicKey = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return publicKey;
    }

    public String getPrivateKeyByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String privateKey= null;

        // Define the query
        String[] columns = {"privatekey"};
        String selection = "username=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,  // table name
                columns,            // columns to return
                selection,          // WHERE clause
                selectionArgs,      // WHERE arguments
                null, null, null
        );

        if (cursor.moveToFirst()) {
            privateKey= cursor.getString(0);
        }

        cursor.close();
        db.close();

        return privateKey;
    }
}