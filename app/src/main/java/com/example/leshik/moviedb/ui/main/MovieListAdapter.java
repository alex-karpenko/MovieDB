package com.example.leshik.moviedb.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.NO_ID;

/**
 * Created by alex on 3/7/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    Context context;
    private List<Movie> movieList;
    PreferenceInterface prefStorage;

    public MovieListAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
        prefStorage = PreferenceStorage.getInstance(context.getApplicationContext());
    }

    public void setMovieList(List<Movie> newList) {
        movieList = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (movieList != null) {
            Movie movie = movieList.get(position);
//            Picasso.with(context)
            Glide.with(context)
                    .load(prefStorage.getPosterSmallUri(movie.getPosterPath()))
                    .thumbnail(0.01f)
                    .crossFade()
                    .fitCenter()
                    .into(holder.mPosterView);

            // set item click listener
            final long movieId = movie.getMovieId();
            final ImageView posterView = holder.mPosterView;
            holder.mPosterView.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call callback interface method to start detail activity with cursor data
                    ((MovieListFragment.Callback) context).
                            onItemSelected(movieId, posterView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (movieList != null) return movieList.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (movieList != null) return movieList.get(position).getMovieId();
        else return NO_ID;
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
