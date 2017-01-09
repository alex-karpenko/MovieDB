package com.example.leshik.moviedb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullPosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullPosterFragment extends Fragment {
    private static final String ARG_POSTER_NAME = "POSTER_NAME";

    private String mPosterName;
    private ImageView mPosterImage;

    boolean isImageFitToScreen;

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
        mPosterImage = (ImageView) rootView.findViewById(R.id.full_poster_image);
        if (mPosterName != null) {
            Picasso.with(getActivity())
                    .load(Utils.basePosterSecureUrl
                            + "original" // TODO: we have to think to adopt width on image
                            + mPosterName)
                    .into(mPosterImage);
        }

        // Listener for change image size anf fit
        // Source: http://stackoverflow.com/questions/24463691/how-to-show-imageview-full-screen-on-imageview-click
        mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageFitToScreen) {
                    isImageFitToScreen = false;
                    mPosterImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    mPosterImage.setAdjustViewBounds(true);
                } else {
                    isImageFitToScreen = true;
                    mPosterImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    mPosterImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });

        return rootView;
    }
}
