package com.example.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ContactsManager.db";
    // User table name
    private static final String TABLE_CONTACT = "user";
    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_NUMBER = "user_number";
    private static final String COLUMN_IMAGE_URL = "image";
    // create table sql query

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_IMAGE_URL + " TEXT, " + COLUMN_USER_NUMBER + " TEXT " + ")";
    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_CONTACT;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void addUser(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, contact.getName());
        values.put(COLUMN_USER_EMAIL, contact.getEmail());
        values.put(COLUMN_USER_NUMBER, contact.getNumber());
        values.put(COLUMN_IMAGE_URL, contact.getImage());

        // Inserting Row
        db.insert(TABLE_CONTACT, null, values);
        db.close();
    }

    public List<Contact> getAllContact() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_NUMBER,
                COLUMN_IMAGE_URL
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACT,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                contact.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NUMBER)));
                contact.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                // Adding user record to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return contactList;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, contact.getName());
        values.put(COLUMN_USER_EMAIL, contact.getEmail());
        values.put(COLUMN_USER_NUMBER, contact.getNumber());
        values.put(COLUMN_IMAGE_URL, contact.getImage());

        // updating row
        return db.update(TABLE_CONTACT, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});

    }


    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_CONTACT, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public boolean checkContact(String number) {
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_NUMBER + " = ?";
        String[] selectionArgs = {number};

        Cursor cursor = db.query(TABLE_CONTACT,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

}
