package io.neurolab.gui;

import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.model.FFTPreprocessor;
import io.neurolab.settings.NeuroSettings;
import io.neurolab.settings.RelaxFeedbackSettings;
import io.neurolab.tools.ColorMap;
import io.neurolab.tools.Utils;
import io.neurolab.utilities.MathBasics;
import io.neurolab.utilities.NeuroUtils;

public class LongtermAnalyzer {

    private double[][] powerZScore;
    private double powerZMin = Double.MIN_VALUE;
    private double powerZMax = Double.MAX_VALUE;

    private int minimumNewSamples = 8;
    private boolean selection = false;

    private DecimalFormat df = new DecimalFormat("#.###");

    // default analysis settings
    private int size = 500;
    private int maxDisplayFreq = 60;
    private int stepX = 64;
    private double stepY = 4d;
    private double p2pLimit = 5d;
    private double rewardMax = Double.MIN_VALUE;
    private double rewardMin = Double.MAX_VALUE;
    private int numChannels = 2;
    private int bins = 4;
    private int selectionIn = -1;
    private int selectionOut = -1;
    private double zoomLevel = 1d;
    private double[][][] freqs;
    private int[][] penalties;
    private long[] timestamps;
    private int rewardCount = 0;
    private int maxPenalty = Integer.MIN_VALUE;

    private FFTPreprocessor fftPreprocessor;
    private ReentrantLock lock;
    private DefaultFFTData fftData;
    private NeuroSettings neuroSettings;

    private int numberOfLines = 0;
    private double maxFFT = 250;
    private double minFFT = 0;
    private double range = Math.max(minFFT, maxFFT) - Math.min(minFFT, maxFFT);

    private int trackWidth;
    private int trackHeight;
    private int binsPerPixel;
    private int heightPerChannel;
    private int sp;

    private double totalPowerMax = Double.MIN_VALUE;
    private double totalPowerMin = Double.MAX_VALUE;
    private double[][] totalPower;
    private double totalPowerDiff = 0d;
    private double[][] fftValues;
    private double[] fftValuesMax;
    private double[] reward;
    private double rewardRange;

    private int displaySampleOffset = 0;
    private double sampleTimePerSecond = 33;
    private int barStep = 100;
    private int numBars = 14;
    private RelaxFeedbackSettings rewardSettings;
    private int currentCounter = 0;
    private boolean fileOpened = false;
    private long lastSecondTimestamp = 0;
    private int minSamplesPerSecond = Integer.MAX_VALUE;
    private int maxSamplesPerSecond = Integer.MIN_VALUE;

    private PrintWriter out;
    private File file;
    private Config config;
    private DefaultCategoryDataset dataset;

    public LongtermAnalyzer(Config config) {
        this.config = config;
    }

    private boolean openFile(File file) {
        resetWindow();
        if (file.exists()) {
            this.file = file;
            try {
                numberOfLines = Utils.countLines(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double[][] samples;
            if (numberOfLines > 0) {
                samples = new double[numberOfLines][numChannels];
                timestamps = new long[numberOfLines];
                freqs = new double[numberOfLines][numChannels][size / 2];
                totalPower = new double[numberOfLines][numChannels];
                fftValues = new double[numberOfLines][numChannels];
                penalties = new int[numberOfLines][numChannels];

                powerZScore = new double[numberOfLines][numChannels];

                fftValuesMax = new double[numChannels];
                reward = new double[numberOfLines];
                rewardRange = 0;
                rewardCount = 0;
                sampleTimePerSecond = 1000d / (double) size;

                neuroSettings = new NeuroSettings(size, numChannels, bins);
                fftData = new DefaultFFTData();
                fftPreprocessor = new FFTPreprocessor(fftData, neuroSettings);
                binsPerPixel = (int) Math.ceil((double) maxDisplayFreq * numChannels / (double) trackHeight);
                // heightPerChannel = trackHeight / numChannels;
                heightPerChannel = maxDisplayFreq * (int) (stepY);
                sp = size;
                ColorMap.getJet(256);
                fftPreprocessor.getFFTData().setPeakToPeakLimit(this.p2pLimit);
                fftPreprocessor.enableFIRFilter(false);

                rewardSettings = new RelaxFeedbackSettings(fftData, lock, config);
                fileOpened = true;
            } else {
                fileOpened = false;
                return false;
            }

            try {
                Scanner scanner = new Scanner(file);

                double currentTime = 0l;
                double startTime = 0;
                ConcurrentLinkedDeque<double[]> currentData = neuroSettings.getCurrentData();

                int s = 0;
                while (scanner.hasNextLine()) {
                    String currentLine = scanner.nextLine();
                    currentCounter++;
                    if ((s > 1) && ((timestamps[s - 1] - lastSecondTimestamp) / 1000000 >= 1000)) {
                        if (currentCounter > 100) {
                            minSamplesPerSecond = Math.min(minSamplesPerSecond, currentCounter);
                            maxSamplesPerSecond = Math.max(maxSamplesPerSecond, currentCounter);
                        }
                        currentCounter = 0;
                        lastSecondTimestamp = timestamps[s - 1];
                    }

                    if (currentLine.contains("i") || currentLine.contains("s")) {
                        currentData.addFirst(new double[]{0, 0});
                        currentData.removeLast();
                        timestamps[s] = timestamps[s - 1] + 1;
                        s++;
                        continue;
                    }

                    // split by old delimiter "comma"
                    String[] l = currentLine.split(",");
                    if (l.length < 3) { // check if delimiter is "tabulator"
                        l = currentLine.split(" |\t");
                        if (l.length == 5)
                            currentData.addFirst(new double[]{-5000d + 10000d *
                                    NeuroUtils.parseUnsignedHex(l[1] + l[2]) / 16777216d, -5000d + 10000d *
                                    NeuroUtils.parseUnsignedHex(l[3] + l[4]) / 16777216d});
                        else // something else, or broken -> skip
                            continue;
                    } else
                        currentData.addFirst(new double[]{Double.valueOf(l[1]), Double.valueOf(l[2])});

                    if (s == 0)
                        startTime = nanoToSeconds(l[0]);
                    timestamps[s] = Long.valueOf(l[0]);

                    samples[s] = currentData.getFirst().clone();

                    if (s > size - 2) {
                        if (s % minimumNewSamples == 0)
                            fftPreprocessor.run();

                        double a;
                        if ((s > numberOfLines / 8) && (fftPreprocessor.getFFTData().getTrainingFactor() < 0.45d))
                            fftPreprocessor.getFFTData().setTrainingFactor(.5d);

                        for (int c = 0; c < numChannels; c++) {
                            penalties[s][c] = new Integer(fftPreprocessor.getFFTData().getPackagePenalty()[c]);
                            maxPenalty = Math.max(penalties[s][c], maxPenalty);

                            for (int f = 1; f < maxDisplayFreq; f++) {
                                freqs[s][c][f] = fftPreprocessor.getFFTData().getCurrentFFTs()[c][f];
                                totalPower[s][c] += fftPreprocessor.getFFTData().getCurrentFFTs()[c][f];

                                powerZScore[s][c] = MathBasics.getZScore(fftPreprocessor.getFFTData().getCurrentFFTValue()[c],
                                        fftPreprocessor.getFFTData().meanFFTValue[c], Math.sqrt(fftPreprocessor.getFFTData().varFFTValue[c]));

                                if ((s > 3000) && (s < numberOfLines - 3000) && (!Double.isNaN(powerZScore[s][c]))) {
                                    powerZMin = Math.min(powerZScore[s][c], powerZMin);
                                    powerZMax = Math.max(powerZScore[s][c], powerZMax);
                                }

                                totalPowerMin = Math.min(totalPowerMin, totalPower[s][c]);
                                totalPowerMax = Math.min(totalPowerMax, totalPower[s][c]);
                            }
                            fftValues[s][c] = fftPreprocessor.getFFTData().getCurrentFFTValue()[c];
                        }
                        if (s % minimumNewSamples == 0) {
                            rewardSettings.updateFeedback();
                            reward[s] = rewardSettings.getCurrentFeedback();
                            if ((s > 3000) && (s < numberOfLines - 3000) && (!Double.isNaN(reward[s]))) {
                                rewardMax = Math.max(rewardMax, reward[s]);
                                rewardMin = Math.min(rewardMin, reward[s]);
                            }
                        } else
                            reward[s] = reward[s - 1];
                        if (reward[s] > 0d)
                            rewardCount++;
                        currentData.removeLast();
                    }
                    s++;
                }
                rewardRange = rewardMax - rewardMin;

                for (int c = 0; c < numChannels; c++)
                    fftValuesMax[c] = fftPreprocessor.getFFTData().meanFFTValue[c];

                totalPowerDiff = totalPowerMax - totalPowerMin;
                updateCurrentTrack();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private double nanoToSeconds(Object nanoSeconds) {
        if (nanoSeconds instanceof String)
            return Double.valueOf((String) nanoSeconds) / 1000000000.0d;
        return 0d;
    }

    private void resetWindow() {
        selectionIn = -1;
        selectionOut = -1;
        zoomLevel = 1d;
    }

    private DefaultCategoryDataset updateDataset() {
        if (dataset == null)
            dataset = new DefaultCategoryDataset();
        else
            dataset.clear();
        if ((selection) && (selectionIn > 0) && (Math.abs(selectionIn - selectionOut) > 0)) {
            double[][] meanFreqs = new double[numChannels][maxDisplayFreq];
            int i = Math.max(0, (Math.min(selectionIn, selectionOut) + displaySampleOffset) * stepX);
            int o = Math.min((Math.max(selectionIn, selectionOut) + displaySampleOffset) * stepX, numberOfLines);
            int n = o - i;
            int p = 0;

            for (int f = 4; f < 40; f++) {
                for (int c = 0; c < numChannels; c++) {
                    for (int s = i; s < o; s++) {
                        meanFreqs[c][f] += freqs[s][c][f];
                    }
                    meanFreqs[c][f] /= (double) Math.max(1, (n - p));
                    dataset.addValue(meanFreqs[c][f], "channel " + (c + 1), f + "");
                }
            }
        }
        return dataset;
    }

    private void saveCSVFile(File file) {
        try {
            out = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            Scanner scanner = new Scanner(this.file);
            String outputLine = "";

            int s = 0;

            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();

                if (currentLine.contains("i") || currentLine.contains("s")) {
                    s++;
                    continue;
                }

                // split by old delimiter "comma"
                String[] l = currentLine.split(",");
                if (l.length < 3) { // check if delimiter is "tabulator"
                    l = currentLine.split(" |\t");
                    if (l.length == 5)
                        outputLine = (-5000d +
                                10000d * NeuroUtils.parseUnsignedHex(l[1] + l[2]) / 16777216d) +
                                "," + (-5000d + 10000d * NeuroUtils.parseUnsignedHex(l[3] + l[4]) / 16777216d);
                    else // something else, or broken -> skip
                        continue;
                } else
                    outputLine = Double.valueOf(l[1]) + "," + Double.valueOf(l[2]);

                s++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFFTMinMax(double min, double max) {
        minFFT = min / 1000d;
        maxFFT = max;
        range = Math.max(minFFT, maxFFT) - Math.min(minFFT, maxFFT);
    }

    public void updateCurrentTrack() {
        // TODO: Update the current track and trigger the event for doing the corresponding changes in the User Interface in a seperate Activity.
    }

}