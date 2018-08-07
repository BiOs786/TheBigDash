package bios786.counter.com.thebigdash.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by BiOs on 24-08-2017.
 */

public class FavoriteContentProvider extends ContentProvider {

    private static final int MOVIE_ID = 101;
    private static final int MOVIE_TABLE = 100;

    FavoriteMovieDbHelper movieDbHelper;
    public static UriMatcher sUriMatcher;

    @Override
    public boolean onCreate() {

        movieDbHelper = new FavoriteMovieDbHelper(getContext());
        sUriMatcher = buildUriMatcher();
        return false;
    }

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = "bios786.counter.com.thebigdash";

        uriMatcher.addURI(authority, "movie", MOVIE_TABLE);
        uriMatcher.addURI(authority, "movie/#", MOVIE_ID);
        return uriMatcher;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase sqlDb = movieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursorToReturn;

        switch (match) {
            case MOVIE_TABLE:

                cursorToReturn = sqlDb.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        sortOrder);

                break;

            default:
                throw new IllegalArgumentException("Not valid Uri:" + uri.toString());
        }
        cursorToReturn.setNotificationUri(getContext().getContentResolver(), uri);
        return cursorToReturn;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase sqlDb = movieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_TABLE:

                long id = sqlDb.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    Uri newUri = ContentUris.withAppendedId(uri, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return newUri;
                }
                break;

            default:
                throw new IllegalArgumentException("Not valid Uri:" + uri.toString());
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
