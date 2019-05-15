package io.neurolab.main.output.visual;

import android.content.Context;

import com.illposed.osc.OSCPortIn;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import java.net.SocketException;

import io.neurolab.main.output.feedback.Feedback;
import io.neurolab.settings.FeedbackSettings;

public class FocusOMeter extends Feedback {

    private static OSCPortIn receiver;
    private Context context;

    private GLCapabilities glcapabilities;

    public FocusOMeter(Context context) {
        this.context = context;
    }

    public void setCurrentFeedback(float currentFeedback) {
        FocusOMeterGLRenderer.setCurrentFeedback(currentFeedback);
    }

    double currentFeedback = 0;

    public void setOSCInput(int port) {
        try {
            receiver = new OSCPortIn(port);

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public FocusOMeter(FeedbackSettings feedbackSettings) {
        super(feedbackSettings);

        GLProfile glprofile = GLProfile.getDefault();
        glcapabilities = new GLCapabilities(glprofile);
    }

    public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
        FocusOMeterGLRenderer.setup(glautodrawable.getGL().getGL2(), width, height);
    }

    public void display(GLAutoDrawable glautodrawable) {
        FocusOMeterGLRenderer.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
    }

    public GLCapabilities getGlcapabilities() {
        return glcapabilities;
    }

    @Override
    public void updateCurrentFeedback(double currentFeedback) {
        super.updateCurrentFeedback(currentFeedback);
        setCurrentFeedback((float) currentFeedback);
    }
}
