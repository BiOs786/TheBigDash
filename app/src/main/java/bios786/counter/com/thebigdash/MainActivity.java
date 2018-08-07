package bios786.counter.com.thebigdash;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import java.util.HashMap;

import bios786.counter.com.thebigdash.data.FavoriteMovieContract;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>, FavoritesAdapter.FavoriteClickHandler {
    public static final int NUM_ITEMS = 20;
    public static final int CURSOR_LOADER = 901;
    public static final String LIST_STATE_KEY = "11";

    public static JSONObject movieDetailsJSONObject = null;

    public RecyclerView rvMovie;
    public MovieAdapter mAdapter;
    public FavoritesAdapter fAdapter;
    public GridLayoutManager gridManager;

    public ProgressBar progressBar;
    public TextView textViewForError;
    public MenuItem lastItemPressed;
    public static Cursor data;
    public String sortOn;
    public String sortBasedOn;
    public int currentVisiblePosition;


    public String projection[] = {
            FavoriteMovieContract.FavoriteMovieEntry._ID,
            FavoriteMovieContract.FavoriteMovieEntry.IMAGE_POSTER,
            FavoriteMovieContract.FavoriteMovieEntry.MOVIE_NAME,
            FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW,
            FavoriteMovieContract.FavoriteMovieEntry.RATING,
            FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE,
    };

    public static final int MOVIE_ID = 0;
    public static final int MOVIE_POSTER = 1;
    public static final int MOVIE_TITLE = 2;
    public static final int MOVIE_OVERVIEW = 3;
    public static final int MOVIE_RATING = 4;
    public static final int MOVIE_DATE = 5;

    public String selection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_Bar);
        textViewForError = (TextView) findViewById(R.id.tv_error);
        textViewForError.setVisibility(View.INVISIBLE);
        rvMovie = (RecyclerView) findViewById(R.id.rv_MoviesList);
        sortBasedOn = getResources().getString(R.string.popular_string);

        mAdapter = new MovieAdapter(NUM_ITEMS, this);
        gridManager = new GridLayoutManager(this, 2);

        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            gridManager.setSpanCount(3);
        } else {
            gridManager.setSpanCount(2);
        }

        rvMovie.setLayoutManager(gridManager);

        if (savedInstanceState != null) {
            sortBasedOn = savedInstanceState.getString("SortingOn");

            if (sortBasedOn.equals(getResources().getString(R.string.favorite_string))) {
                loadFavorites();
            }
            if (sortBasedOn.equals(getResources().getString(R.string.popular_string))) {
                makePopularSort();
            }
            if (sortBasedOn.equals(getResources().getString(R.string.top_rated_string))) {
                makeTopRatedSort();
            }

            currentVisiblePosition = savedInstanceState.getInt("Position");

        } else {
            if (isNetworkAvailable())
                makePopularSort();
            else
                loadFavorites();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int actionIDSelected = item.getItemId();

        if (actionIDSelected == R.id.action_popular) {
            makePopularSort();
        }
        if (actionIDSelected == R.id.action_top_rated) {
            makeTopRatedSort();
        }
        if (actionIDSelected == R.id.action_favorite) {
            loadFavorites();
        }

        lastItemPressed = item;
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(HashMap<String, String> temp) {
        Context context = MainActivity.this;

        Class destinationActivity = MovieDetailsActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("MovieId", temp.get("movie_id"));
        intent.putExtra("Position", temp.get("position"));
        intent.putExtra("Title", temp.get("original_title"));
        intent.putExtra("MoviePoster", temp.get("poster_path"));
        intent.putExtra("Plot", temp.get("overview"));
        intent.putExtra("Rating", temp.get("vote_average"));
        intent.putExtra("ReleaseDate", temp.get("release_date"));

        startActivity(intent);
    }

    /*
    HELPER METHODS
     */

    private void makePopularSort() {

        sortOn = getResources().getString(R.string.popular_string);

        try {
            rvMovie.getRecycledViewPool().clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        URL popularURL = NetworkUtils.buildPopularURL();
        FetchJSONObject fetch = new FetchJSONObject();

        if (isNetworkAvailable())
            fetch.execute(popularURL);
        else
            noInternet();
    }

    private void makeTopRatedSort() {

        sortOn = getResources().getString(R.string.top_rated_string);

        try {
            rvMovie.getRecycledViewPool().clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        URL topRatedURL = NetworkUtils.buildTopRatedURL();
        FetchJSONObject fetch = new FetchJSONObject();
        if (isNetworkAvailable())
            fetch.execute(topRatedURL);
        else
            noInternet();
    }

    private void loadFavorites() {

        redoInternet();

        try {
            rvMovie.getRecycledViewPool().clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        sortOn = getResources().getString(R.string.favorite_string);

        LoaderManager lManager = getLoaderManager();
        if (data == null)
            lManager.initLoader(CURSOR_LOADER, null, MainActivity.this);
        else
            lManager.restartLoader(CURSOR_LOADER, null, MainActivity.this);

        progressBar.setVisibility(View.INVISIBLE);
    }

    public static JSONObject getJSONObject() {
        return movieDetailsJSONObject;
    }


    /*
        Cursor for loading the content from the Content Provider
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FavoriteMovieContract.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.data = data;

        fAdapter = new FavoritesAdapter(this, data, this);
        rvMovie.setAdapter(fAdapter);

        gridManager.scrollToPositionWithOffset(currentVisiblePosition, 0);
        currentVisiblePosition = 0;

        rvMovie.setHasFixedSize(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.data = null;

        fAdapter.notifyDataSetChanged();

    }


    /*
        Method to fetch JSON Object from URL
     */

    public class FetchJSONObject extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            movieDetailsJSONObject = null;
            showProgressBar();
            textViewForError.setVisibility(View.INVISIBLE);
        }

        @Override
        protected JSONObject doInBackground(URL... params) {
            JSONObject object = null;
            try {
                object = NetworkUtils.getJSONFromHttpUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            movieDetailsJSONObject = jsonObject;
            mAdapter.notifyDataSetChanged();

            gridManager.scrollToPositionWithOffset(currentVisiblePosition, 0);
            currentVisiblePosition = 0;
            rvMovie.setAdapter(mAdapter);

            rvMovie.setHasFixedSize(true);
            showRecyclerView();

        }
    }

    /*
        Helper Methods for toggling Visibility
     */

    public void showProgressBar() {
        rvMovie.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void showRecyclerView() {
        rvMovie.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /*
        Internet Connection Helper Methods
     */

    public void noInternet() {
        rvMovie.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        Context context = MainActivity.this;
        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        textViewForError.setVisibility(View.VISIBLE);

    }

    public void redoInternet() {
        rvMovie.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textViewForError.setVisibility(View.INVISIBLE);
    }

    /*
    Attribution of code: For Internet Connectivity
    Source: https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     */

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /*
        On Clicking the Favorite Item this method is Triggered.
     */
    @Override
    public void onFavoriteItemClick(int position) {
        data.moveToPosition(position);

        Class destinationActivity = MovieDetailsActivity.class;
        Context context = getBaseContext();

        Intent intent = new Intent(context, destinationActivity);

        intent.putExtra("Title", data.getString(MOVIE_TITLE));
        intent.putExtra("Bitmap", data.getBlob(MOVIE_POSTER));
        intent.putExtra("Plot", data.getString(MOVIE_OVERVIEW));
        intent.putExtra("Rating", data.getString(MOVIE_RATING));
        intent.putExtra("ReleaseDate", data.getString(MOVIE_DATE));
        intent.putExtra("FavoriteActivity", "");

        startActivity(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("SortingOn", sortOn);

        int currentVisiblePosition = 0;
        currentVisiblePosition = gridManager.findFirstVisibleItemPosition();

        outState.putInt("Position", currentVisiblePosition);

    }


}
