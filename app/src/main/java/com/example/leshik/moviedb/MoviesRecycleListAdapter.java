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

import com.example.leshik.moviedb.data.MoviesContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Leshik on 27.12.2016.
 *
 * Adapter class to fill in movies lists
 */

class MoviesRecycleListAdapter extends CursorRecyclerViewAdapter<MoviesRecycleListAdapter.ViewHolder> {
    MoviesRecycleListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Picasso.with(getContext())
                .load(Utils.getPosterSmallUri(cursor.getString(MoviesContract.SHORT_LIST_PROJECTION_INDEX_POSTER_PATH)))
                .into(viewHolder.mPosterView);

        // set item click listener
        final Uri movieUri = MoviesContract.Movies.buildUri(cursor.getLong(MoviesContract.SHORT_LIST_PROJECTION_INDEX_MOVIE_ID));
        final ImageView posterView = viewHolder.mPosterView;
        viewHolder.mPosterView.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call callback interface method to start detail activity with cursor data
                ((MovieListFragment.Callback) getContext()).
                        onItemSelected(movieUri, posterView);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.poster_image)
        ImageView mPosterView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
