package io.neurolab.settings;

import android.content.SharedPreferences;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.main.output.feedback.Feedback;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;

public class FeedbackSettings {

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
        return fftData.getRewardFFTBins();
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
        if ((fftData.getBaselineFFTValues() != null) && (fftData.getMeanFFTBins() != null)) {
            for (int b = 0; b < fftData.getBins(); b++)
                fftData.getBaselineFFTValues()[b] = fftData.getMeanFFTBins()[b].clone();
        }
    }

    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }

}
