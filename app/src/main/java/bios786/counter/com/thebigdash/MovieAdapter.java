package bios786.counter.com.thebigdash;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by BiOs on 27-07-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    int mNumberOfItems = 0;

    private final MovieAdapterClickHandler mClickHandler;

    public interface MovieAdapterClickHandler {
        void onClick(HashMap<String, String> temp);
    }

    public MovieAdapter(int numberOfItems, MovieAdapterClickHandler handler) {
        mNumberOfItems = numberOfItems;
        mClickHandler = handler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.viewholder_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberOfItems;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewForPoster;
        View view;

        public MovieViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageViewForPoster = (ImageView) itemView.findViewById(R.id.iv_poster_photo);
            itemView.setOnClickListener(this);
        }


        public void bind(int position) {
            try {
                Context context = view.getContext();
                URL url = NetworkUtils.buildPosterPathURL(position);
                Log.i(MovieAdapter.class.getSimpleName(), url.toString());
                Uri uri = Uri.parse(url.toString());

                imageViewForPoster.setMinimumHeight(view.getMeasuredHeight());

                Picasso.with(context).load(uri)
                        .into(imageViewForPoster);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(MovieAdapter.getDataFromJson(adapterPosition));
        }

    }

    private static HashMap<String, String> getDataFromJson(int position) {
        JSONObject jsonObj = MainActivity.getJSONObject();
        HashMap<String, String> data = new HashMap<>();
        try {
            JSONArray jsonArr = jsonObj.getJSONArray("results");
            JSONObject dataOfMovie = jsonArr.getJSONObject(position);
            data.put("position", String.valueOf(position));
            data.put("movie_id", dataOfMovie.getString("id"));
            data.put("original_title", dataOfMovie.getString("original_title"));
            data.put("poster_path", dataOfMovie.getString("poster_path"));
            data.put("overview", dataOfMovie.getString("overview"));
            data.put("vote_average", dataOfMovie.getString("vote_average"));
            data.put("release_date", dataOfMovie.getString("release_date"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

}
