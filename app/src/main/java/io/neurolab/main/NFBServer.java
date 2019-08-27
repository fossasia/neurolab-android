package io.neurolab.main;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.interfaces.DataReceiver;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.model.FFTPreprocessor;
import io.neurolab.model.FileOutputTask;
import io.neurolab.interfaces.Task;
import io.neurolab.settings.FeedbackSettings;
import io.neurolab.settings.NeuroSettings;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NFBServer extends NeuroSettings implements DataReceiver {

    private long currentTimestamp;
    public static int samplesPerSecond = 256;
    public static int numChannels = 2;
    public static int bins = 0;

    private List<double[]> inputData;
    protected ConcurrentLinkedDeque<double[]> currentData = new ConcurrentLinkedDeque<double[]>();
    private int numSamples;
    private static ReentrantLock lock = new ReentrantLock();
    public int minimumNewSamples = 16;
    private LinkedList<Task> tasks;
    private long lastTimestamp;
    private Config config;
    private DefaultFFTData fftData;
    private FileOutputTask fileOutputTask;
    private FeedbackSettings currentFeedbackSettings;
    private ArrayList<FeedbackSettings> feedbackSettings;

    protected int currentSamples = 0;
    protected int newSamples = 0;

    public NFBServer() {
        tasks = new LinkedList<>();
        currentFeedbackSettings = new FeedbackSettings();
        feedbackSettings = new ArrayList<>();
    }

    public NFBServer(Config config) {
        this.config = config;
    }

    public long getCurrentTimestamp() {
        currentTimestamp = System.currentTimeMillis();
        return currentTimestamp;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public List<double[]> getInputData() {
        return inputData;
    }

    public void setTasks(LinkedList<Task> tasks) {
        this.tasks = tasks;

        for (Task task : tasks) {
            if (task instanceof FFTPreprocessor)
                this.fftData = ((FFTPreprocessor) task).getFFTData();
            if (task instanceof FileOutputTask)
                this.fileOutputTask = (FileOutputTask) task;
        }
    }

    public ConcurrentLinkedDeque<double[]> getCurrentData() {
        return currentData;
    }

    public FeedbackSettings getCurrentFeedbackSettings() {
        return currentFeedbackSettings;
    }

    public void setCurrentFeedbackSettings(FeedbackSettings currentFeedbackSettings) {
        this.currentFeedbackSettings = currentFeedbackSettings;
    }

    public Config getConfig() {
        return config;
    }

    public DefaultFFTData getFftData() {
        return fftData;
    }

    @Override
    public void appendData(List<double[]> data) {
        lock.lock();
        try {
            currentTimestamp = System.nanoTime();
            inputData = data;
            numSamples = data.size();
            currentSamples += numSamples;

            for (int i = 0; i < numSamples; i++) {
                currentData.removeLast();
                currentData.addFirst(data.get(i));
            }
            newSamples += numSamples;
            if (newSamples < minimumNewSamples) {
                lock.unlock();
                return;
            } else
                newSamples = 0;

            for (Task task : tasks)
                task.run();

            lastTimestamp = currentTimestamp;
        } catch (Exception e) {
            e.printStackTrace();
            lock.unlock();
        }
        lock.unlock();
    }

    public static ReentrantLock getLock() {
        return lock;
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<FeedbackSettings> getAllSettings() {
        return feedbackSettings;
    }

    public ArrayList<FeedbackSettings> getFeedbackSettings() {
        return feedbackSettings;
    }

    public void setFeedbackSettings(ArrayList<FeedbackSettings> feedbackSettings) {
        this.feedbackSettings = feedbackSettings;
    }
}
