package io.neurolab.model;

import io.neurolab.interfaces.Task;
import io.neurolab.settings.FeedbackSettings;

public class NFBProcessor implements Task {

    private FeedbackSettings feedbackSettings = null;

    public NFBProcessor(FeedbackSettings feedbackSettings) {
        setFeedbackSettings(feedbackSettings);
    }

    public void setFeedbackSettings(FeedbackSettings feedbackSettings) {
        this.feedbackSettings = feedbackSettings;
    }

    @Override
    public void run() {
        if (feedbackSettings!=null)
            feedbackSettings.updateFeedback();
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

}
