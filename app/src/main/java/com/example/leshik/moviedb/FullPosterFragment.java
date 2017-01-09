package com.example.leshik.moviedb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullPosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullPosterFragment extends Fragment {
    private static final String ARG_POSTER_NAME = "POSTER_NAME";

    private String mPosterName;
    private TouchImageView mPosterImage;

    public FullPosterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param posterName - file name of poster image.
     * @return A new instance of fragment FullPosterFragment.
     */
    public static FullPosterFragment newInstance(String posterName) {
        FullPosterFragment fragment = new FullPosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POSTER_NAME, posterName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosterName = getArguments().getString(ARG_POSTER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_full_poster, container, false);
        // Inflate the layout for this fragment
        mPosterImage = (TouchImageView) rootView.findViewById(R.id.full_poster_image);
        if (mPosterName != null) {
            Picasso.with(getActivity())
                    .load(Utils.basePosterSecureUrl
                            + "original"
                            + mPosterName)
                    .into(mPosterImage);
        }

        return rootView;
    }
}
