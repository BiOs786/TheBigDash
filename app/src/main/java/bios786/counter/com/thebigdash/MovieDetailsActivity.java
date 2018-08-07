package bios786.counter.com.thebigdash;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import bios786.counter.com.thebigdash.data.FavoriteMovieContract;
import butterknife.BindView;
import butterknife.ButterKnife;

import static bios786.counter.com.thebigdash.MainActivity.LIST_STATE_KEY;
import static bios786.counter.com.thebigdash.MainActivity.NUM_ITEMS;
import static bios786.counter.com.thebigdash.NetworkUtils.BASE_URL_FOR_POSTERPATH;
import static bios786.counter.com.thebigdash.NetworkUtils.SIZE_VALUE;
import static bios786.counter.com.thebigdash.NetworkUtils.buildTrailerURL;
import static bios786.counter.com.thebigdash.NetworkUtils.movieDetailsObject;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.TrailerOnClickHandler {

    private static final int NUM_ITEMS = 3;
    private static final int LOADER_TRAILER = 1001;
    private static final int LOADER_REVIEW = 1003;
    private static final String LIST_STATE_KEY_REVIEW = "999";
    private static final String LIST_STATE_KEY_TRAILER = "95";

    public static JSONObject trailerDetailsForAMovie = null;
    public static JSONObject reviewForAMovie = null;
    private Context context;
    private MovieDetailsActivity movieActivity;
    private int positionOfThisAdapter = 0;
    private boolean isThisFavoriteActivity;

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_plot_synopsis)
    TextView mPlot;
    @BindView(R.id.tv_user_rating)
    TextView mRating;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDate;
    @BindView(R.id.iv_poster_thumbnail)
    ImageView mPosterPhoto;
    private static int movieId;

    @BindView(R.id.recycler_view_trailers)
    public RecyclerView rvTrailer;
    public TrailerAdapter trailerAdapter;
    public LinearLayoutManager layoutManager;
    public Parcelable trailerParcel;

    @BindView(R.id.recycler_view_reviews)
    public RecyclerView rvReview;
    public ReviewAdapter reviewAdapter;
    public LinearLayoutManager reviewLayoutManager;
    public Parcelable reviewParcel;

    private LoaderManager.LoaderCallbacks<JSONObject> fetchTrailerDetailsLoader = null;
    private LoaderManager.LoaderCallbacks<JSONObject> fetchReviewDetailsLoader = null;
    public Bundle dataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        context = getBaseContext();
        movieActivity = this;

        Intent intentThatStartedThis = getIntent();

        checkForExtra(intentThatStartedThis);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (!intentThatStartedThis.hasExtra("FavoriteActivity")) {
            fetchReviews();
            fetchVideoInformation();
        }
    }

    public void fetchVideoInformation() {

        fetchTrailerDetailsLoader = new LoaderManager.LoaderCallbacks<JSONObject>() {
            @Override
            public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<JSONObject>(MovieDetailsActivity.this) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (trailerDetailsForAMovie != null)
                            deliverResult(trailerDetailsForAMovie);
                        else
                            forceLoad();
                    }

                    @Override
                    public JSONObject loadInBackground() {
                        JSONObject jsonObj = null;
                        URL movieUrl = NetworkUtils.buildTrailerURL(String.valueOf(movieId));
                        try {

                            jsonObj = NetworkUtils.getJSONFromHttpUrl(movieUrl);

                        } catch (IOException e) {
                            Log.d("IOException", e.getMessage());
                        }
                        return jsonObj;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
                trailerDetailsForAMovie = data;

                int sizeOfTrailer = 0;
                try {
                    sizeOfTrailer = trailerDetailsForAMovie.getJSONArray("results").length();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {

                }

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                else
                    layoutManager = new GridLayoutManager(context, 3);

                trailerAdapter = new TrailerAdapter(sizeOfTrailer, movieActivity);

                rvTrailer.setHasFixedSize(true);
                rvTrailer.setLayoutManager(layoutManager);
                rvTrailer.setAdapter(trailerAdapter);

            }

            @Override
            public void onLoaderReset(Loader<JSONObject> loader) {
                trailerDetailsForAMovie = null;
                fetchTrailerDetailsLoader = null;
            }
        };

        LoaderManager lManager = getSupportLoaderManager();
        Bundle bundleForTrailer = null;
        if (isNetworkAvailable()) {
            if (trailerDetailsForAMovie == null)
                lManager.initLoader(LOADER_TRAILER, bundleForTrailer, fetchTrailerDetailsLoader);
            else
                lManager.restartLoader(LOADER_TRAILER, bundleForTrailer, fetchTrailerDetailsLoader);
        }

    }

    public void fetchReviews() {
        fetchReviewDetailsLoader = new LoaderManager.LoaderCallbacks<JSONObject>() {
            @Override
            public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<JSONObject>(MovieDetailsActivity.this) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (reviewForAMovie != null)
                            deliverResult(reviewForAMovie);
                        else
                            forceLoad();

                    }

                    @Override
                    public JSONObject loadInBackground() {

                        URL reviewURL = NetworkUtils.buildReviewURL(String.valueOf(movieId));
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = NetworkUtils.getJSONFromHttpUrl(reviewURL);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return jsonObj;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
                reviewForAMovie = data;

                int reviewSize = 0;

                try {
                    reviewSize = reviewForAMovie.getJSONArray("results").length();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {

                }
                if (reviewSize > 2)
                    reviewSize = 2;

                rvReview.setHasFixedSize(true);
                reviewLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                reviewAdapter = new ReviewAdapter(reviewSize);
                rvReview.setLayoutManager(reviewLayoutManager);
                rvReview.setAdapter(reviewAdapter);
            }

            @Override
            public void onLoaderReset(Loader<JSONObject> loader) {
                reviewForAMovie = null;
                fetchReviewDetailsLoader = null;
            }
        };

        LoaderManager lManager = getSupportLoaderManager();
        Bundle bundleForReview = null;

        if (isNetworkAvailable()) {
            if (reviewForAMovie == null)
                lManager.initLoader(LOADER_REVIEW, bundleForReview, fetchReviewDetailsLoader);
            else
                lManager.restartLoader(LOADER_REVIEW, bundleForReview, fetchReviewDetailsLoader);
        }
    }

    public void checkForExtra(Intent intentThatStartedThis) {
        if (intentThatStartedThis.hasExtra("Title")) {
            mTitle.setText(intentThatStartedThis.getStringExtra("Title"));
        }
        if (intentThatStartedThis.hasExtra("Plot")) {
            mPlot.setText(intentThatStartedThis.getStringExtra("Plot"));
        }
        if (intentThatStartedThis.hasExtra("Rating")) {
            mRating.setText(intentThatStartedThis.getStringExtra("Rating") + "/10");
        }
        if (intentThatStartedThis.hasExtra("ReleaseDate")) {
            mReleaseDate.setText(intentThatStartedThis.getStringExtra("ReleaseDate"));
        }
        if (intentThatStartedThis.hasExtra("MoviePoster")) {
            Context context = MovieDetailsActivity.this;
            Uri uri = Uri.parse(BASE_URL_FOR_POSTERPATH + SIZE_VALUE + "/" + intentThatStartedThis.getStringExtra("MoviePoster"));
            Picasso.with(context).load(uri).into(mPosterPhoto);
        }

        if (intentThatStartedThis.hasExtra("MovieId")) {
            movieId = Integer.parseInt(intentThatStartedThis.getStringExtra("MovieId"));
        }
        if (intentThatStartedThis.hasExtra("Position")) {
            positionOfThisAdapter = Integer.parseInt(intentThatStartedThis.getStringExtra("Position"));
        }
        if (intentThatStartedThis.hasExtra("Bitmap")) {
            byte[] b = intentThatStartedThis.getByteArrayExtra("Bitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

            mPosterPhoto.setImageBitmap(bitmap);
        }
        if (intentThatStartedThis.hasExtra("FavoriteActivity")) {
            isThisFavoriteActivity = true;

            rvReview.setVisibility(View.INVISIBLE);
            rvTrailer.setVisibility(View.INVISIBLE);

            TextView tv = (TextView) findViewById(R.id.watch_trailer_text);
            tv.setVisibility(View.INVISIBLE);
            TextView tve = (TextView) findViewById(R.id.watch_review);
            tve.setVisibility(View.INVISIBLE);

            ((ViewManager) rvTrailer.getParent()).removeView(rvTrailer);
            ((ViewManager) rvReview.getParent()).removeView(rvReview);
            ((ViewManager) tve.getParent()).removeView(tve);
            ((ViewManager) tv.getParent()).removeView(tv);

        }
    }

    @Override
    public void onClick(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String key = null;
        try {
            JSONArray jsonArr = trailerDetailsForAMovie.getJSONArray("results");
            JSONObject obj = jsonArr.getJSONObject(position);
            key = obj.getString("key");
        } catch (JSONException e) {

        }
        Uri intentUri = NetworkUtils.buildIntentForTrailer(key);
        intent.setData(intentUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public static JSONObject getReviewForAMovie() {
        return reviewForAMovie;
    }

    public static JSONObject getTrailerDetailsForAMovie() {
        return trailerDetailsForAMovie;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(MovieDetailsActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }


    public void onAddFavorite(MenuItem item) {
        String movieTitle = mTitle.getText().toString();

        ContentValues contentValueForThisMovie = new ContentValues();
        contentValueForThisMovie.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_NAME, movieTitle);
        contentValueForThisMovie.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW, mPlot.getText().toString());
        contentValueForThisMovie.put(FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE, mReleaseDate.getText().toString());
        contentValueForThisMovie.put(FavoriteMovieContract.FavoriteMovieEntry.RATING, mRating.getText().toString());

        Bitmap image = null;

        try {
            URL url = new URL(NetworkUtils.buildPosterPathURL(positionOfThisAdapter).toString());
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentValueForThisMovie.put(FavoriteMovieContract.FavoriteMovieEntry.IMAGE_POSTER, getBytesFromBitmap(image));

        getContentResolver().insert(FavoriteMovieContract.CONTENT_URI, contentValueForThisMovie);
        Toast.makeText(this, mTitle.getText().toString() + " added to favorites", Toast.LENGTH_LONG).show();
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public void restoreData(Bundle bundle) {
        if (bundle != null) {

            mTitle.setText(bundle.getString(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_NAME));
            mPlot.setText(bundle.getString(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW));
            mRating.setText(bundle.getString(FavoriteMovieContract.FavoriteMovieEntry.RATING));
            mReleaseDate.setText(bundle.getString(FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE));

            byte[] b = bundle.getByteArray(FavoriteMovieContract.FavoriteMovieEntry.IMAGE_POSTER);
            mPosterPhoto.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));

        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);


        // SAVING ALL THE DATA
        savedInstanceState.putString(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_NAME, mTitle.getText().toString());
        savedInstanceState.putString(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW, mPlot.getText().toString());
        savedInstanceState.putString(FavoriteMovieContract.FavoriteMovieEntry.RATING, mRating.getText().toString());
        savedInstanceState.putString(FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE, mReleaseDate.getText().toString());

        Bitmap image = null;
        try {
            URL url = new URL(NetworkUtils.buildPosterPathURL(positionOfThisAdapter).toString());
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        savedInstanceState.putByteArray(FavoriteMovieContract.FavoriteMovieEntry.IMAGE_POSTER, getBytesFromBitmap(image));

        if (!isThisFavoriteActivity) {
            reviewParcel = reviewLayoutManager.onSaveInstanceState();
            savedInstanceState.putParcelable(LIST_STATE_KEY_REVIEW, reviewParcel);

            trailerParcel = layoutManager.onSaveInstanceState();
            savedInstanceState.putParcelable(LIST_STATE_KEY_TRAILER, trailerParcel);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            reviewParcel = savedInstanceState.getParcelable(LIST_STATE_KEY_REVIEW);
            trailerParcel = savedInstanceState.getParcelable(LIST_STATE_KEY_TRAILER);
            restoreData(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (reviewParcel != null) {
            reviewLayoutManager.onRestoreInstanceState(reviewParcel);
        }

        if (trailerParcel != null) {
            layoutManager.onRestoreInstanceState(trailerParcel);
        }

    }

}
