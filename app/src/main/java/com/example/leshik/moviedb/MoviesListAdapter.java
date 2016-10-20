package com.example.leshik.moviedb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter class for handling list of moviews (posters) on main screen
 */

public class MoviesListAdapter extends ArrayAdapter<MovieInfo> {
    public MoviesListAdapter(Context context, List<MovieInfo> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item object
        MovieInfo item = getItem(position);

        // if it is a new view object, create it
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movielist_item, parent, false);
        }

        // get poster view and put poster image ito it using Picasso call
        ImageView itemView = (ImageView) convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext())
                .load(MovieUtils.basePosterUrl
                        + "w185" // TODO: we have to think to adopt width of image, mayby
                        + item.getPosterPath()).into(itemView);
        return convertView;
    }
}
