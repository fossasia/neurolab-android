package io.neurolab.main;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.model.Task;
import io.neurolab.settings.NeuroSettings;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NFBServer extends NeuroSettings implements DataReceiver {

    private long currentTimestamp;
    public static int samplesPerSecond = 256;
    public static int numChannels = 2;

    private List<double[]> inputData;
    protected ConcurrentLinkedDeque<double[]> currentData = new ConcurrentLinkedDeque<double[]>();
    private int numSamples;
    private static ReentrantLock lock = new ReentrantLock();
    public int minimumNewSamples = 16;
    private LinkedList<Task> tasks;
    private long lastTimestamp;

    protected int currentSamples = 0;
    protected int newSamples = 0;

    public NFBServer() {
        tasks = new LinkedList<>();
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
}
