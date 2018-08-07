package bios786.counter.com.thebigdash;

import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by BiOs on 23-08-2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private TrailerOnClickHandler trailerClickHandler;
    private int numOfItems = 0;


    TrailerAdapter(int count, TrailerOnClickHandler handler) {
        numOfItems = count;
        trailerClickHandler = handler;
    }

    public interface TrailerOnClickHandler {
        void onClick(int position);
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_viewholder_layout;
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numOfItems;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        ImageView imageView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_view_trailer);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            String trailerKey = null;
            try {
                JSONArray jsonArr = MovieDetailsActivity.trailerDetailsForAMovie.getJSONArray("results");
                JSONObject obj = jsonArr.getJSONObject(position);
                trailerKey = obj.getString("key");

                Context context = view.getContext();
                URL url = NetworkUtils.buildThumbnailURL(position);

                Uri uri = Uri.parse(url.toString());


                Picasso.with(context).load(uri)
                        .into(imageView);

            } catch (JSONException e) {
                Log.i(TrailerAdapter.class.getSimpleName(), "JSON Error");
            }

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            trailerClickHandler.onClick(adapterPosition);
        }
    }
}
