package bios786.counter.com.thebigdash;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import static android.R.attr.key;
import static bios786.counter.com.thebigdash.MainActivity.movieDetailsJSONObject;
import static bios786.counter.com.thebigdash.MovieDetailsActivity.trailerDetailsForAMovie;

/**
 * Created by BiOs on 27-07-2017.
 */

public final class NetworkUtils {

    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";


    public static final String BASE_URL_FOR_POSTERPATH = "http://image.tmdb.org/t/p/";
    public static final String SIZE_PARAM = "size";
    public static final String SIZE_VALUE = "w185";


    public static JSONObject movieDetailsObject;


    public static JSONObject getJSONFromHttpUrl(URL url) throws IOException {
        JSONObject object = null;

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            object = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return object;
    }

    public static URL buildPopularURL() {
        String popularString = BASE_URL + "popular?";

        Uri builderUri = Uri.parse(popularString).buildUpon().appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN).build();
        URL url = null;
        try {
            url = new URL(builderUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTopRatedURL() {
        String popularString = BASE_URL + "top_rated?";

        Uri builderUri = Uri.parse(popularString).buildUpon().appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN).build();
        URL url = null;
        try {
            url = new URL(builderUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildPosterPathURL(int position) {
        StringBuffer posterPathString = new StringBuffer(BASE_URL_FOR_POSTERPATH + SIZE_VALUE + "/");
        URL posterURL = null;
        try {

            /*
            Retrieving the JSONObject of the particular movie then extracting the poster path.
             */
            JSONArray jsonArray = movieDetailsJSONObject.getJSONArray("results");
            JSONObject movieDetails = jsonArray.getJSONObject(position);
            String posterPath = movieDetails.getString("poster_path");
            posterPathString.append(posterPath);
            posterURL = new URL(posterPathString.toString());
            Log.i("Poster Url", posterURL.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return posterURL;
    }

    public static URL buildTrailerURL(String movieId) {
        final String BASE_URL_TRAILER = BASE_URL + movieId + "/videos?";

        Uri uriBuilder = Uri.parse(BASE_URL_TRAILER).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .appendQueryParameter("language", "en-US")
                .build();

        URL returnURL = null;

        try {
            returnURL = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return returnURL;
    }

    public static Uri buildIntentForTrailer(String key) {
        Uri returnUri = null;
        String baseUriString = "https://www.youtube.com/watch?";

        returnUri = Uri.parse(baseUriString).buildUpon()
                .appendQueryParameter("v", key).build();

        return returnUri;
    }

    public static URL buildThumbnailURL(int position) {
        String movieKey = null;
        URL returnURL = null;

        try {

            JSONArray jsonArr = trailerDetailsForAMovie.getJSONArray("results");
            JSONObject obj = jsonArr.getJSONObject(position);
            movieKey = obj.getString("key");
            String baseUrl = "http://img.youtube.com/vi/" + movieKey + "/0.jpg";

            returnURL = new URL(baseUrl);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return returnURL;
    }

    public static URL buildReviewURL(String movieId) {
        final String BASE_URL_REVIEW = BASE_URL + movieId + "/reviews?";

        Uri uriBuilder = Uri.parse(BASE_URL_REVIEW).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .appendQueryParameter("language", "en-US")
                .build();

        URL returnURL = null;

        try {
            returnURL = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return returnURL;
    }

}
