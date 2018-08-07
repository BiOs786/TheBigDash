package bios786.counter.com.thebigdash.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by BiOs on 24-08-2017.
 */

public class FavoriteMovieContract {

    final static String URI_SCHEME = "content";

    final static String BASE_URI = "content://bios786.counter.com.thebigdash";

    final static String PATH = "movie";
    public static Uri CONTENT_URI = Uri.parse(BASE_URI).buildUpon().appendPath(PATH).build();

    public class FavoriteMovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movietable";
        public static final String MOVIE_NAME = "moviename";
        public static final String MOVIE_OVERVIEW = "movieoverview";
        public static final String RATING = "ratings";
        public static final String RELEASE_DATE = "releasedate";
        public static final String IMAGE_POSTER = "imageposter";
    }
}
