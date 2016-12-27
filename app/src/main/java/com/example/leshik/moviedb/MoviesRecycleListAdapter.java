package com.example.leshik.moviedb;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movielist_item, parent, false);
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
