package bios786.counter.com.thebigdash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.id;
import static android.R.attr.version;
import static android.webkit.WebSettings.PluginState.ON;

/**
 * Created by BiOs on 24-08-2017.
 */

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "favorite_movie.db";

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(SQLiteDatabase database) {
        FavoriteMovieDbHelper.database = database;
    }

    private static SQLiteDatabase database;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATABASE = "Create TABLE " +
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_NAME + " TEXT NOT NULL UNIQUE, " +
                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMovieContract.FavoriteMovieEntry.RATING + " INTEGER NOT NULL, " +
                FavoriteMovieContract.FavoriteMovieEntry.IMAGE_POSTER + " BLOB, " +
                FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE + " TEXT NOT NULL," +
                " UNIQUE (" + FavoriteMovieContract.FavoriteMovieEntry.MOVIE_NAME + ") ON CONFLICT REPLACE);";

        db.execSQL(CREATE_DATABASE);

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
