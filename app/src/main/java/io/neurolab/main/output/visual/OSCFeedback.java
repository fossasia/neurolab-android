package io.neurolab.main.output.visual;

import java.net.SocketException;

import io.neurolab.main.output.feedback.Feedback;
import io.neurolab.settings.FeedbackSettings;
import com.illposed.osc.OSCPortIn;

public class OSCFeedback extends Feedback {

    private static OSCPortIn receiver;
    double currentFeedback = 0;

    public OSCFeedback(FeedbackSettings feedbackSettings) {
        super(feedbackSettings);
    }

    public void setCurrentFeedback(float currentFeedback) {
        FocusOMeterGLRenderer.setCurrentFeedback(currentFeedback);
    }

    public void setOSCInput(int port) {
        try {
            receiver = new OSCPortIn(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCurrentFeedback(double currentFeedback) {
        super.updateCurrentFeedback(currentFeedback);
        setCurrentFeedback((float) currentFeedback);
    }
}