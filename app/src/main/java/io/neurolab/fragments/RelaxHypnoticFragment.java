package io.neurolab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.neurolab.R;
import io.neurolab.tools.Animations;

public class RelaxHypnoticFragment extends Fragment {
    public static final String RELAX_PROGRAM_HYPNOTIC_FLAG = "Hypnotic";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_relax_hypnotic, container, false);
        Animations.rotateView(view.findViewById(R.id.hypno_image_view2), 360f, 0);
        return view;
    }
}
