package io.neurolab.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import io.neurolab.R;
import io.neurolab.activities.MemoryGraphParent;

public class SpectrumFragment extends Fragment {

    private LineChart mChart;
    private AlertDialog infoDialog;

    public static Fragment newInstance() {
        Fragment fragment = new SpectrumFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MemoryGraphParent) getActivity()).setActionBarTitle(getResources().getString(R.string.spectrum));
        View rootView = inflater.inflate(R.layout.fragment_spectrum, container, false);
        LayoutInflater layoutInflater = getLayoutInflater();
        View spectrumInfoView = layoutInflater.inflate(R.layout.spectrum_info_layout, null);
        AlertDialog.Builder palette = new AlertDialog.Builder(getContext());
        palette.setCancelable(true);
        palette.setTitle(R.string.program_info_label);
        palette.setView(spectrumInfoView);
        infoDialog = palette.create();

        mChart = rootView.findViewById(R.id.frequency_spectrum_chart);
        mChart.setGridBackgroundColor(Color.GREEN);
        mChart.setDrawGridBackground(true);

        mChart.setDrawBorders(true);
        mChart.setPinchZoom(false);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText(getResources().getString(R.string.eeg_data_label));
        mChart.getDescription().setTextColor(Color.WHITE);
        mChart.getDescription().setTextSize(10);

        Legend l = mChart.getLegend();
        l.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(32, true);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(256);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawGridLines(false);

        XAxis xl = mChart.getXAxis();
        xl.setDrawGridLines(false);
        xl.setTextColor(Color.WHITE);
        xl.setEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        setData(50);
        return rootView;
    }

    private double[] convertToDouble(String[] parsedData) {
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

    private void setData(float range) {
        ArrayList<Entry> yVals = new ArrayList<>();
        float effectiveDistance = 50;

        if (StatisticsFragment.parsedData != null) {
            double[] dataset = convertToDouble(StatisticsFragment.parsedData);
            int i = 0;
            for (; i < dataset.length / 2; i++) {
                float val = (float) (Math.abs(dataset[i] - dataset[i + 1]) * range) + effectiveDistance;
                yVals.add(new Entry(i, val));
            }
        }

        LineDataSet set1;

        set1 = new LineDataSet(yVals, "Data Set 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.GREEN);
        set1.setDrawCircles(false);
        set1.setLineWidth(3f);
        set1.setFillAlpha(255);
        set1.setDrawFilled(true);
        set1.setFillColor(Color.RED);

        LineData data = new LineData(set1);
        data.setDrawValues(true);

        mChart.setData(data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.spectrum_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.info_spectrum:
                infoDialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
