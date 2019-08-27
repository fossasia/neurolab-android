package io.neurolab.main;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.neurolab.interfaces.DataReceiver;
import io.neurolab.interfaces.InputInterface;
import io.neurolab.tools.ResourceManager;

public class PlaybackStream implements InputInterface {

    static DataReceiver receiver;
    static int currentIndex = 0;
    private int numberOfChannels;
    private boolean loopPlayback = false;
    private Timer timer;
    static Random random = new Random();
    static ArrayList<ArrayList<double[]>> data;

    private Context context;

    public PlaybackStream(Context context, DataReceiver receiver, int numberOfChannels, String playbackFile, boolean loop) {
        this.context = context;
        PlaybackStream.receiver = receiver;
        this.numberOfChannels = numberOfChannels;
        this.setPlaybackFile(playbackFile, loop);

    }

    @Override
    public boolean sendCommand(String string) {
        return true;
    }

    public void setPlaybackFile(String file, boolean loop) {
        System.out.println("file:" + file);
        File playbackFile = ResourceManager.getInstance().getResource(context, file);

        this.loopPlayback = loop;
        data = new ArrayList<>();

        if (!playbackFile.exists()) {
            for (int i = 0; i < 256; i++) {
                System.out.println("i:" + i);
                ArrayList<double[]> currentSamples = new ArrayList<>();
                double[] currentSample = new double[4];
                for (int c = 0; c < this.numberOfChannels; c++) {
                    currentSample[c] = 12.5d * Math.sin(((double) (i + c * 16) / 128d) * Math.PI * 6d);
                    currentSample[c] += 12.5d * Math.sin(((double) (i + c * 16) / 128d) * Math.PI * 25d);
                }
                currentSamples.add(currentSample.clone());
                data.add(currentSamples);
            }
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(playbackFile.toString()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    ArrayList<double[]> currentSamples = new ArrayList<double[]>();
                    double[] currentSample = new double[4];
                    String[] values = line.split(",");
                    if (values.length < 3)
                        continue;

                    for (int c = 0; c < this.numberOfChannels; c++)
                        currentSample[c] = Double.valueOf(values[c + 1]);

                    currentSamples.add(currentSample.clone());
                    data.add(currentSamples);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        timer = new Timer();
        timer.schedule(new AppendDataTask(this), 500, 1);
    }

    @Override
    public int shutDown() {
        return 0;
    }

    class AppendDataTask extends TimerTask {

        private PlaybackStream playbackStream;

        AppendDataTask(PlaybackStream playbackStream) {
            this.playbackStream = playbackStream;

        }

        public void run() {
            receiver.appendData(data.get(currentIndex));
            currentIndex++;
            if (currentIndex > data.size() - 1)
                currentIndex = 0;
        }
    }

    @Override
    public boolean isConnectionSuccessful() {
        return true;
    }

    @Override
    public boolean record(String filename) {
        return false;
    }

    @Override
    public void stopRecording() {
    }

    @Override
    public boolean isRecording() {
        return false;
    }

}