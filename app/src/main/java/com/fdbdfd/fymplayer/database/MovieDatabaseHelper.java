package com.fdbdfd.fymplayer.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_MOVIE = "create table movie ("
            + "id integer primary key autoincrement, "
            + "number integer, "
            + "path text, "
            + "time real)";

    private Context mContext;

    public MovieDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
