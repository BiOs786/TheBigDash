package bios786.counter.com.thebigdash;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BiOs on 24-08-2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    int numOfItems;
    private Context baseContext;

    ReviewAdapter(int count) {
        numOfItems = count;
    }


    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        baseContext = parent.getContext();
        int layoutIdForListItem = R.layout.review_viewholder;
        LayoutInflater inflater = LayoutInflater.from(baseContext);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numOfItems;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView contentTextView;
        View view;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.text_view_author);
            contentTextView = (TextView) itemView.findViewById(R.id.text_view_content);
            view = (View) itemView.findViewById(R.id.view);
        }

        void bind(int position) {
            try {
                JSONArray jsonArr = MovieDetailsActivity.getReviewForAMovie().getJSONArray("results");
                JSONObject obj = jsonArr.getJSONObject(position);
                String authorName = obj.getString("author");
                String contentString = obj.getString("content");
                authorTextView.setText("-" + authorName);
                contentTextView.setText(contentString);
                view.setVisibility(View.VISIBLE);
            } catch (JSONException e) {

            }
        }
    }
}
