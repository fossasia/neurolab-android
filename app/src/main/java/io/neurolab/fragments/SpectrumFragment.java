package io.neurolab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.neurolab.R;
import io.neurolab.program_modes.MemoryGraphParent;

public class SpectrumFragment extends Fragment {

    public static Fragment newInstance() {
        Fragment fragment = new SpectrumFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MemoryGraphParent) getActivity()).setActionBarTitle(getResources().getString(R.string.spectrum));
        View rootView = inflater.inflate(R.layout.fragment_spectrum, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
