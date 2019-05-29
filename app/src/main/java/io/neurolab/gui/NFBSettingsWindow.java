package io.neurolab.gui;

import java.util.ArrayList;

import io.neurolab.main.NFBServer;
import io.neurolab.settings.FeedbackSettings;

public class NFBSettingsWindow {
    private NFBServer nfbServer;
    private FeedbackSettings feedbackSettings;

    private String feedbackTitle;
    private String difficultyFactor;
    private String feedbackOutput;

    private ArrayList<String> listModel;

    public NFBSettingsWindow(NFBServer nfbServer) {

        this.nfbServer = nfbServer;
        this.feedbackSettings = nfbServer.getCurrentFeedbackSettings();

        feedbackTitle = " feedback type: " + feedbackSettings.getFeedbackSettingsName() + " ";
        listModel = new ArrayList();

        for (FeedbackSettings feedbackSettings : nfbServer.getAllSettings())
            listModel.add(feedbackSettings.getFeedbackSettingsName());
    }

    public void actionPerformed(int index) {
        feedbackSettings = nfbServer.getAllSettings().get(index);
        nfbServer.setCurrentFeedbackSettings(feedbackSettings);
    }

    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    public String getDifficultyFactor() {
        return difficultyFactor;
    }

    public void setDifficultyFactor(String difficultyFactor) {
        this.difficultyFactor = difficultyFactor;
    }

    public String getFeedbackOutput() {
        return feedbackOutput;
    }

    public void setFeedbackOutput(String feedbackOutput) {
        this.feedbackOutput = feedbackOutput;
    }
}
