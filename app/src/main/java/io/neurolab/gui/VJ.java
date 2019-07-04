package io.neurolab.gui;


import android.content.Context;

import java.util.LinkedList;

import io.neurolab.main.NFBServer;
import io.neurolab.main.output.audio.AudioFeedback;
import io.neurolab.main.output.visual.LongtermFFTVisualizer;
import io.neurolab.main.task.OscForwardTask;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.model.FFTPreprocessor;
import io.neurolab.model.GenericFeedbackSettings;
import io.neurolab.model.NFBProcessor;
import io.neurolab.interfaces.Task;
import io.neurolab.settings.FeedbackSettings;

public class VJ {

    private Context context;
    private NFBServer rn;
    private FeedbackSettings currentFeedbackSettings;

    public void run(Context context) {
        this.context = context;
        Config config = new Config("filename");
        try {

            rn = new NFBServer(config);

            // fft data and preprocessor task
            DefaultFFTData fftData = new DefaultFFTData(NFBServer.samplesPerSecond, NFBServer.bins, NFBServer.numChannels);
            FFTPreprocessor fftPreprocessor = new FFTPreprocessor(fftData, rn);

            // feedback settings and processor task
            FeedbackSettings currentFeedbackSettings = new GenericFeedbackSettings(fftData, NFBServer.getLock(), config);
            NFBProcessor nfbProcessor = new NFBProcessor(currentFeedbackSettings);

            OscForwardTask oscForwardTask = new OscForwardTask(rn);

            // longterm fft visualization
            LongtermFFTVisualizer longtermFFTVisualizer = new LongtermFFTVisualizer(fftData, rn);

            LinkedList<Task> tasks = rn.getTasks();
            tasks.add(fftPreprocessor);
            tasks.add(nfbProcessor);
            tasks.add(longtermFFTVisualizer);
            tasks.add(oscForwardTask);
            rn.setTasks(tasks);

            rn.setCurrentFeedbackSettings(currentFeedbackSettings);

            addFeedback();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addFeedback() {
        AudioFeedback audioFeedback = new AudioFeedback(context, rn.getCurrentFeedbackSettings());
        currentFeedbackSettings.addFeedback(audioFeedback);
    }
}
