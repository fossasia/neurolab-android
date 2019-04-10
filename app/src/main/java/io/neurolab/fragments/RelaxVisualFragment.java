package io.neurolab.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.neurolab.R;
import io.neurolab.tools.Animations;

/**
 * A simple {@link Fragment} subclass.
 */
public class RelaxVisualFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_relax_visual, container, false);

        Animations.rotateView(mView.findViewById(R.id.yantraOneImageView), 360f, 0);
        Animations.rotateView(mView.findViewById(R.id.yantraTwoImageView), 0, 360f);

        return mView;
    }

}
