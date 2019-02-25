package io.neurolab.settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.R;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.output.feedback.Feedback;

public class FeedbackSettings extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    protected int smpls_per_sec;
    protected int bins;
    protected int num_channels;

    public String[] binLabels;
    protected int[] binRanges;
    protected int[] binRangesAmount;

    protected double[][] rewardFFTBins = new double[bins][num_channels];
    protected double[][] lastRewardFFTBins = new double[bins][num_channels];

    protected float currentFeedback;
    protected float lastFeedback;

    protected boolean[] notBrainwaves = {false, false, false, false};
    protected boolean baseline = false;

    protected long currentTimestamp = System.currentTimeMillis();
    protected ReentrantLock lock;

    protected ArrayList<Feedback> feedbacks;
    protected DefaultFFTData fftData;
    private ArrayList<Thread> feedbackThreads;
    private Config config;

    private TextView smpls_txt_view;
    private TextView bins_txt_view;
    private TextView numChannels_txt_view;

    public FeedbackSettings() {

    }

    public FeedbackSettings(DefaultFFTData fftData, ReentrantLock lock, Config config) {
        this.config = config;
        this.currentFeedback = this.lastFeedback = 0;
        this.feedbackThreads = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.fftData = fftData;
        this.lock = lock;
    }

    public String getFeedbackSettingsName() {
        return "generic";
    }

    public void setFeedbacks(ArrayList<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public DefaultFFTData getFFTData() {
        return fftData;
    }

    private static float sensitivity = 0.5f;
    protected FrameLayout frame;

    public static float getSensitivity() {
        return sensitivity;
    }

    public int getNumChannels() {
        return num_channels;
    }

    public void setNumChannels(int numChannels) {
        this.num_channels = numChannels;
    }

    public int getBins() {
        return bins;
    }

    public void setBins(int bins) {
        this.bins = bins;
    }

    public String[] getBinLabels() {
        return binLabels;
    }

    public void setBinLabels(String[] binLabels) {
        this.binLabels = binLabels;
    }

    public int[] getBinRanges() {
        return binRanges;
    }

    public void setBinRanges(int[] binRanges) {
        this.binRanges = binRanges;
    }


    public double[][] getRewardFFTBins() {
        return fftData.rewardFFTBins;
    }

    public void setRewardFFTBins(double[][] rewardFFTBins) {
        this.rewardFFTBins = rewardFFTBins;
    }


    public float getCurrentFeedback() {
        return currentFeedback;
    }

    public void setCurrentFeedback(float currentFeedback) {
        this.currentFeedback = currentFeedback;
    }

    public void updateFeedback() {
        for (Feedback feedback : feedbacks) {
            feedback.updateCurrentFeedback(Math.sqrt(Math.sqrt(currentFeedback)) * (sensitivity * 2d));
        }
    }

    public void addFeedback(Feedback feedback) {
        if (!this.feedbacks.contains(feedback)) {
            this.feedbacks.add(feedback);
            if (!feedback.isRunning()) {
                Thread thread = new Thread(feedback);
                thread.run();
                this.feedbackThreads.add(thread);//TODO
            }

        }

    }

    public void base() {
        if ((fftData.baselineFFTValues != null) && (fftData.meanFFTBins != null)) {
            for (int b = 0; b < fftData.bins; b++)
                fftData.baselineFFTValues[b] = fftData.meanFFTBins[b].clone();
        }
    }

    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feedback_settings);
        Toolbar toolbar = findViewById(R.id.feedback_toolbar);
        FeedbackSettings activity = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setActionBar(toolbar);
        }
        activity.getActionBar().setTitle(getResources().getString(R.string.app_name));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        smpls_per_sec = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.samples_pref_key), "4"));
        bins = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.bins_pref_key), "3"));
        num_channels = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.num_channels_pref_key), "2"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        smpls_txt_view = findViewById(R.id.feedback_smpls_value);
        bins_txt_view = findViewById(R.id.feedback_bins_value);
        numChannels_txt_view = findViewById(R.id.feedback_numChannels_value);

        smpls_txt_view.setText(String.valueOf(smpls_per_sec));
        bins_txt_view.setText(String.valueOf(bins));
        numChannels_txt_view.setText(String.valueOf(num_channels));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == getResources().getString(R.string.samples_pref_key))
            smpls_per_sec = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.samples_pref_key), "4"));
        else if (key == getResources().getString(R.string.bins_pref_key))
            bins = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.bins_pref_key), "3"));
        else if (key == getResources().getString(R.string.num_channels_pref_key))
            num_channels = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.num_channels_pref_key), "2"));
    }
}
