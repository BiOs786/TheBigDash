package bios786.counter.com.thebigdash;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by BiOs on 24-08-2017.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    int numOfItems = 0;
    Context context = null;
    Cursor dataCursor;

    public FavoriteClickHandler mClickhandler;

    public interface FavoriteClickHandler {
        void onFavoriteItemClick(int position);
    }

    FavoritesAdapter(Context context, Cursor cursor, FavoriteClickHandler handler) {
        this.context = context;
        numOfItems = cursor.getCount();
        dataCursor = cursor;
        mClickhandler = handler;
    }

    @Override
    public FavoritesAdapter.FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int resourceId = R.layout.viewholder_layout;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(resourceId, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoritesAdapter.FavoriteViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numOfItems;
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        View view;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_poster_photo);
            view = itemView;
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            try {
                Context context = view.getContext();
                imageView.setMinimumHeight(view.getMeasuredHeight());
                dataCursor.moveToPosition(position);
                byte[] b = dataCursor.getBlob(MainActivity.MOVIE_POSTER);
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickhandler.onFavoriteItemClick(position);
        }
    }
}
