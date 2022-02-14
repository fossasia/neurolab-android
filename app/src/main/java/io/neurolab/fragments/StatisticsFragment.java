package io.neurolab.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.neurolab.R;
import io.neurolab.activities.MemoryGraphParent;
import io.neurolab.model.DefaultFFTData;

public class StatisticsFragment extends Fragment {

    private TextView deltaValView;
    private TextView thetaValView;
    private TextView lowAlphaValView;
    private TextView highAlphaValView;
    private ImageView concEmojiView;
    private ImageView sleepEmojiView;
    private ImageView calmEmojiView;
    private ImageView angerEmojiView;

    public static String[] parsedData;

    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
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
        ((MemoryGraphParent) getActivity()).setActionBarTitle(getResources().getString(R.string.statistics));
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        deltaValView = rootView.findViewById(R.id.delta_value);
        thetaValView = rootView.findViewById(R.id.theta_value);
        lowAlphaValView = rootView.findViewById(R.id.low_alpha_value);
        highAlphaValView = rootView.findViewById(R.id.high_alpha_value);

        concEmojiView = rootView.findViewById(R.id.concentrate_emoji);
        sleepEmojiView = rootView.findViewById(R.id.sleep_emoji);
        calmEmojiView = rootView.findViewById(R.id.calm_emoji);
        angerEmojiView = rootView.findViewById(R.id.anger_emoji);

        if (StatisticsFragment.parsedData == null)
            Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
        else
            initStatistics();
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initStatistics() {
        DefaultFFTData defaultFFTData = new DefaultFFTData(3, 4, 2);
        double[] data = convertToDouble(StatisticsFragment.parsedData);
        double[][] dfftData = dataForCalculations(data);
        defaultFFTData.updateFFTData(dfftData);
        int[] calculatedValues = defaultFFTData.getBinRangeValues();

        deltaValView.setText(String.valueOf(calculatedValues[0]));
        thetaValView.setText(String.valueOf(calculatedValues[1]));
        lowAlphaValView.setText(String.valueOf(calculatedValues[2]));
        highAlphaValView.setText(String.valueOf(calculatedValues[3]));

        concEmojiView.setVisibility(View.VISIBLE);
        sleepEmojiView.setVisibility(View.VISIBLE);
        calmEmojiView.setVisibility(View.VISIBLE);
        angerEmojiView.setVisibility(View.VISIBLE);

        if (calculatedValues[0] < 40000)
            concEmojiView.setImageResource(R.drawable.ic_sad);
        if (calculatedValues[1] < 40000)
            sleepEmojiView.setImageResource(R.drawable.ic_sad);
        if (calculatedValues[2] < 40000)
            calmEmojiView.setImageResource(R.drawable.ic_sad);
        if (calculatedValues[3] > 150000)
            angerEmojiView.setImageResource(R.drawable.ic_sad);

    }

    private double[][] dataForCalculations(double[] data) {
        int rows = 2000;
        int columns = 4;
        double[][] dfftData = new double[rows][columns];
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < columns; i++) {
                int pos = j * 4 + i;
                if (pos < data.length)
                    dfftData[j][i] = data[pos];
                else
                    return dfftData;
            }
        }
        return dfftData;
    }

    public static double[] convertToDouble(String[] parsedData) {
        double[] parsedDoubleData = new double[parsedData.length];
        for (int i = 0; i < parsedData.length; i++) {
            if (parsedData[i].length() > 0) {
                parsedDoubleData[i] = Double.parseDouble(parsedData[i]);
                if (parsedDoubleData[i] > 5060) {
                    parsedDoubleData[i] = 5060;
                }
            }
        }
        return parsedDoubleData;
    }
}
