package io.neurolab.main.output.audio;

import android.content.Context;

import io.neurolab.main.output.feedback.Feedback;
import io.neurolab.model.Config;
import io.neurolab.settings.FeedbackSettings;
import io.neurolab.tools.ResourceManager;

import java.io.IOException;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.devices.AudioDeviceFactory;
import com.jsyn.ports.QueueDataCommand;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

public class AudioFeedback extends Feedback {

    public float masterVolume = .95f;
    double[] oldFeedbacks;
    private Context context;
    private boolean multiModelFeedback = false;
    private Synthesizer synth;
    private LineOut lineOut;
    private FloatSample[] sample;
    private VariableRateDataReader[] samplePlayer;
    private int numSamples = 5;
    private float[] volume = {.5f, .5f, .5f, .5f, .7f};
    private float[] defaultVolume = {.5f, .5f, .5f, .5f, .7f};
    private String[] soundMixSamples = {"audio/pad_.wav", "audio/pad1.wav", "audio/lownoise.wav", "audio/pad2.wav", "audio/forest.wav"};
    private double[] rates;
    private Config config;
    private double oldValue = 0d;

    public AudioFeedback(Context context, FeedbackSettings feedbackSettings) {
        this(context, feedbackSettings, null);
    }

    public AudioFeedback(Context context, FeedbackSettings feedbackSettings, Config config) {
        super(feedbackSettings);
        this.context = context;
        this.config = config;

        if (this.config != null) {
            for (int i = 0; i < numSamples; i++) {
                soundMixSamples[i] = this.config.getPref(Config.audiofeedback, String.valueOf(Config.audiofeedback_params.sample) + i);
                volume[i] = Float.valueOf(config.getPref(Config.audiofeedback, String.valueOf(Config.audiofeedback_params.volume) + i));
            }
            int x = Integer.valueOf(config.getPref(Config.audiofeedback, String.valueOf(Config.audiofeedback_params.x)));
            int y = Integer.valueOf(config.getPref(Config.audiofeedback, String.valueOf(Config.audiofeedback_params.y)));
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = masterVolume;
        samplePlayer[4].amplitude.set(masterVolume * volume[4]);
    }

    public float[] getVolume() {
        return volume;
    }

    public float[] getDefaultVolume() {
        return defaultVolume;
    }

    @Override
    public void run() {

        sample = new FloatSample[numSamples];
        synth = JSyn.createSynthesizer(AudioDeviceFactory.createAudioDeviceManager(true));
        rates = new double[numSamples];

        volume = new float[numSamples];
        samplePlayer = new VariableRateDataReader[numSamples];
        lineOut = new LineOut();
        this.synth.add(lineOut);

        int loopStartFrame = Integer.MAX_VALUE;
        int loopSize = Integer.MAX_VALUE;


        for (int i = 0; i < numSamples; i++) {

            try {

                this.sample[i] = SampleLoader.loadFloatSample(ResourceManager.getInstance().getResource(context, soundMixSamples[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (sample[i].getChannelsPerFrame() == 1) {
                synth.add(samplePlayer[i] = new VariableRateMonoReader());
                samplePlayer[i].output.connect(0, lineOut.input, 0);
            } else if (sample[i].getChannelsPerFrame() == 2) {
                synth.add(samplePlayer[i] = new VariableRateStereoReader());
                samplePlayer[i].output.connect(0, lineOut.input, 0);
                samplePlayer[i].output.connect(1, lineOut.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }

            loopStartFrame = (int) (sample[i].getNumFrames() * 0.2);
            loopSize = (int) (sample[i].getNumFrames() * 0.7);

            samplePlayer[i].rate.set(rates[i] = sample[i].getFrameRate() / 2);
            // Start at arbitrary position near beginning of sample.

            this.synth.start();

            // Start the LineOut. It will pull data from the oscillator.
            lineOut.start();

            samplePlayer[i].dataQueue.queue(sample[i], 0, loopStartFrame);

            samplePlayer[i].amplitude.set(volume[i] * masterVolume);
            volume[i] = defaultVolume[i];

            int crossFadeSize = (2000);

            // For complex queuing operations, create a command and then customize it.
            QueueDataCommand command = samplePlayer[i].dataQueue.createQueueDataCommand(sample[i], loopStartFrame, loopSize);
            command.setNumLoops(-1);
            command.setSkipIfOthers(true);
            command.setCrossFadeIn(crossFadeSize);

            System.out.println("Queue: " + loopStartFrame + ", #" + loopSize + ", X=" + crossFadeSize);
            synth.queueCommand(command);
        }

        samplePlayer[3].amplitude.set(0);
        running = true;
    }

    @Override
    public void updateCurrentFeedback(double currentFeedback) {

        super.updateCurrentFeedback(currentFeedback);

        currentFeedback = oldValue * .9d + currentFeedback * .1d;

        if (multiModelFeedback) {

            for (int b = 0; b < this.feedbackSettings.getFFTData().getBinRanges().length; b++) {
                double feedback = 0;
                for (int c = 0; c < this.feedbackSettings.getFFTData().getNumChannels(); c++) {
                    double rewardBin = this.feedbackSettings.getFFTData().getRewardFFTBins()[b][c];
                    feedback += rewardBin;
                }
                feedback /= (double) this.feedbackSettings.getFFTData().getNumChannels();
                feedback = Math.max(Math.min(.95d, feedback), 0d);
                feedback = oldFeedbacks[b] * .9d + feedback * .1d;
                if (!Double.isNaN(feedback))
                    feedback = oldFeedbacks[b] * .9d + feedback * .1d;
                samplePlayer[b].amplitude.set(Math.sqrt(feedback) * masterVolume);
                oldFeedbacks[b] = feedback;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (!Double.isNaN(currentFeedback)) {
                    samplePlayer[i].amplitude.set(Math.max(Math.sqrt(currentFeedback), .003125d) * volume[i] * masterVolume);
                } else {
                    samplePlayer[i].amplitude.set((Math.max(Math.sqrt(oldValue * .9999d), .003125d)) * volume[i] * masterVolume);
                }
            }

        }
        if (currentFeedback > .2)
            for (int i = 0; i < numSamples; i++) {
                rates[i] += 1.1;
                samplePlayer[i].rate.set(rates[i]);
            }

        if (!Double.isNaN(currentFeedback))
            oldValue = currentFeedback;
        else
            oldValue = oldValue * .995;
    }

}
