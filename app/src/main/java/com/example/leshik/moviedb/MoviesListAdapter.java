package com.example.leshik.moviedb;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Leshik on 24.12.2016.
 */

class MoviesListAdapter extends CursorAdapter {
    MoviesListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    static class ViewHolder {
        final ImageView posterImage;
        int movie_id;

        ViewHolder(View view, int movie_id) {
            posterImage = (ImageView) view.findViewById(R.id.poster_image);
            this.movie_id = movie_id;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movielist_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, cursor.getInt(MoviesContract.SHORT_LIST_PROJECTION_INDEX_MOVIE_ID));
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.movie_id = cursor.getInt(MoviesContract.SHORT_LIST_PROJECTION_INDEX_MOVIE_ID);

        Picasso.with(context)
                .load(MovieUtils.basePosterUrl
                        + "w185" // TODO: we have to think to adopt width of image, mayby
                        + cursor.getString(MoviesContract.SHORT_LIST_PROJECTION_INDEX_POSTER_PATH))
                .into(viewHolder.posterImage);
    }
}
