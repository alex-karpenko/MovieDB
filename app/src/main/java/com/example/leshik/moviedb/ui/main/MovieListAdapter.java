package com.example.leshik.moviedb.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.MovieListViewItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.NO_ID;

/**
 * Created by alex on 3/7/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    Context context;
    private List<MovieListViewItem> movieListViewItems;
    PreferenceInterface prefStorage;

    public MovieListAdapter(Context context) {
        this.context = context;
        this.movieListViewItems = new ArrayList<>();
        prefStorage = PreferenceStorage.getInstance(context.getApplicationContext());
    }

    public void updateListItem(MovieListViewItem newItem) {
        if (movieListViewItems.size() <= newItem.listPosition) {
            movieListViewItems.add(newItem);
            notifyItemInserted(newItem.listPosition);
            return;
        }

        MovieListViewItem oldItem = movieListViewItems.get(newItem.listPosition);
        if (oldItem.listPosition == newItem.listPosition) {
            movieListViewItems.set(newItem.listPosition, newItem);
            notifyItemChanged(newItem.listPosition);
        } else {
            movieListViewItems.add(newItem.listPosition, newItem);
            notifyItemInserted(newItem.listPosition);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (movieListViewItems != null) {
            MovieListViewItem movie = movieListViewItems.get(position);
            Picasso.with(context)
                    .load(prefStorage.getPosterSmallUri(movie.posterPath))
                    .into(holder.mPosterView);

            // set item click listener
            final long movieId = movie.movieId;
            final ImageView posterView = holder.mPosterView;
            holder.mPosterView.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call callback interface method to start detail activity with cursor data
                    ((MovieListFragment.Callback) context).
                            onMovieListItemSelected(movieId, posterView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (movieListViewItems != null) return movieListViewItems.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (movieListViewItems != null) return movieListViewItems.get(position).movieId;
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
