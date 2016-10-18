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
 * Created by Leshik on 16.10.2016.
 */

public class MoviesListAdapter extends ArrayAdapter<MovieInfo> {
    public MoviesListAdapter(Context context, List<MovieInfo> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movielist_item, parent, false);
        }

        ImageView itemView = (ImageView) convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext())
                .load(MovieUtils.basePosterUrl
                        + "w185" // !!!
                        + item.getPosterPath()).into(itemView);
        return convertView;
    }
}
