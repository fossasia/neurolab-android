package io.neurolab.main.output.feedback;

import io.neurolab.settings.FeedbackSettings;

public abstract class Feedback implements Runnable {
    protected FeedbackSettings feedbackSettings;
    protected double[][][] currentMultivariateFeedback;
    protected double currentFeedback;
    protected boolean running;

    protected Feedback() {
    }

    public void updateCurrentMultivariateFeedback(double[][][] currentMultivariateFeedback) {
        this.currentMultivariateFeedback = currentMultivariateFeedback;
    }

    public void updateCurrentFeedback(double currentFeedback) {
        this.currentFeedback = currentFeedback;
    }

    public double getCurrentFeedback() {
        return currentFeedback;
    }

    public Feedback(FeedbackSettings feedbackSettings) {
        this.feedbackSettings = feedbackSettings;
    }

    public void init() {
    }

    @Override
    public void run() {
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

}