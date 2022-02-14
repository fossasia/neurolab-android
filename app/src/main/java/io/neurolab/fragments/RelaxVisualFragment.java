package io.neurolab.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import io.neurolab.R;
import io.neurolab.tools.Animations;

public class RelaxVisualFragment extends Fragment {

    public static final String RELAX_PROGRAM_FLAG = "Relax";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_relax_visual, container, false);

        Animations.rotateView(view.findViewById(R.id.yantraOneImageView), 360f, 0);
        Animations.rotateView(view.findViewById(R.id.yantraTwoImageView), 0, 360f);

        return view;
    }

}
