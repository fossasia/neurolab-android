package io.neurolab.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.neurolab.R;
import io.neurolab.main.output.visual.SpaceAnimationVisuals;

public class FocusVisualFragment extends android.support.v4.app.Fragment {

    private int lastPos = 0;
    private int newPos = -300;
    private boolean moving = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus_visual, container, false);

        SpaceAnimationVisuals.moveRocket(view.findViewById(R.id.rocketimage), lastPos, newPos, moving);

        return view;
    }

}
