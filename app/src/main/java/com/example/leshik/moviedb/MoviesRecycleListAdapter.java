package com.example.leshik.moviedb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Leshik on 27.12.2016.
 */

public class MoviesRecycleListAdapter extends CursorRecyclerViewAdapter<MoviesRecycleListAdapter.ViewHolder> {
    public MoviesRecycleListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Picasso.with(getContext())
                .load(Utils.basePosterUrl
                        + "w185" // TODO: we have to think to adopt width of image, mayby
                        + cursor.getString(MoviesContract.SHORT_LIST_PROJECTION_INDEX_POSTER_PATH))
                .into(viewHolder.mPosterView);

        // set item click listener
        final Uri movieUri = MoviesContract.Movies.buildUri(cursor.getLong(MoviesContract.SHORT_LIST_PROJECTION_INDEX_MOVIE_ID));
        viewHolder.mPosterView.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call callback interface method to start detail activity with cursor data
                ((MovieListFragment.Callback) getContext()).
                        onItemSelected(movieUri);
                // mPosition = position;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPosterView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPosterView = (ImageView) itemView.findViewById(R.id.poster_image);
        }
    }
}
