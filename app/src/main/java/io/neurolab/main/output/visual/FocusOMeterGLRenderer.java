package io.neurolab.main.output.visual;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;


public class FocusOMeterGLRenderer {

    private static float currentFeedback = 0;

    public static void setCurrentFeedback(float currentFeedback) {
        FocusOMeterGLRenderer.currentFeedback = currentFeedback;
    }

    protected static void setup(GL2 gl2, int width, int height) {
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();

        GLU glu = new GLU();
        glu.gluOrtho2D(0.0f, width, 0.0f, height);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();

        gl2.glViewport(0, 0, width, height);
    }

    protected static void render(GL2 gl2, int width, int height) {
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl2.glLoadIdentity();
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glColor3f(1, 0, 0);
        gl2.glVertex2f(0, 0);
        gl2.glColor3f(1, 0, 0);
        gl2.glVertex2f(width, 0);
        gl2.glColor3f(1, 1, 0);
        gl2.glVertex2f(width, height / 2);
        gl2.glColor3f(1, 1, 0);
        gl2.glVertex2f(0, height / 2);
        gl2.glEnd();

        gl2.glBegin(GL2.GL_QUADS);
        gl2.glColor3f(1, 1, 0);
        gl2.glVertex2f(0, height / 2);
        gl2.glColor3f(1, 1, 0);
        gl2.glVertex2f(width, height / 2);
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex2f(width, height);
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex2f(0, height);
        gl2.glEnd();

        gl2.glBegin(GL2.GL_QUADS);
        gl2.glColor3f(0, 0, 0);
        gl2.glVertex2f(0, height);
        gl2.glColor3f(0, 0, 0);
        gl2.glVertex2f(width, height);
        gl2.glColor3f(0, 0, 0);
        gl2.glVertex2f(width, currentFeedback * height / 2 + height / 2);
        gl2.glColor3f(0, 0, 0);
        gl2.glVertex2f(0, currentFeedback * height / 2 + height / 2);
        gl2.glEnd();
    }

}