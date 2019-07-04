package io.neurolab.main.output.visual;

import android.graphics.Bitmap;

import java.util.Random;

import io.neurolab.tools.ColorMap;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

public class NeuroGameRenderer {

    public static Random rnd = new Random();
    public static int numHorizontalQuads = 30;
    public static int numDepthQuads = 50;
    public static float[][] landscape;
    public static int pointer = 0;
    private static float[] fogcol = {0, .5f, .5f, 1};
    private static float correctedFeedback = 0f;
    private static float slowFeedback = 0f;
    private static Random random = new Random(); // Seed to 0 for testing
    public GLProfile glprofile;
    int currentTheme = 0;
    private int defaultTexture = 0;
    private float currentFeedback = 0;
    private GLUT glut;
    private int width = 2;
    private int height = 2;
    private Texture texYantra;
    private int shortSide;
    private int shortSideHalf;
    private int offsetX;
    private int offsetY;
    private Texture[] textures;
    private int currentForestIn = 2;
    private int currentForestOut = 1;
    private Texture noiseTexture = null;
    private float angle = 0f;
    private float slowAngle = 0f;
    private String[] themes = {"nforest", "universe"};
    private float oldFeedback = 0f;

    public NeuroGameRenderer(GLProfile glprofile2) {
        this.glprofile = glprofile2;
    }

    public void nextTheme() {
        currentTheme++;
        if (currentTheme + 1 > themes.length)
            currentTheme = 0;
    }

    public void setCurrentFeedback(float currentFeedback) {
        this.currentFeedback = currentFeedback;
    }

    public void setup(GL2 gl2, int width, int height) {
        if (landscape == null) {
            landscape = new float[numDepthQuads][numHorizontalQuads];
            for (int i = 0; i < numDepthQuads; i++) {
                for (int j = 0; j < numHorizontalQuads; j++) {
                    landscape[i][j] = rnd.nextFloat();
                }
            }
        }

        angle = 0f;
        glut = new GLUT();
        GLU glu = new GLU();

        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl2.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl2.glMatrixMode(GL2.GL_PROJECTION); // choose projection matrix
        gl2.glLoadIdentity(); // reset projection matrix
        glu.gluPerspective(75.0, aspect, 1, 1400.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity(); // reset

        this.width = width;
        this.height = height;

        if (texYantra == null) {
            texYantra = generateWhiteNoise(gl2, 800, 800);
        }

        if (width < height) {
            shortSide = width;
            offsetX = 0;
            offsetY = height / 2;
        } else {
            shortSide = height;
            offsetX = width / 2;
            offsetY = 0;
        }

        shortSideHalf = shortSide / 2;
    }

    public void render(GL2 gl2, int width, int height) {
        if ((currentFeedback < 0) || Float.isNaN(currentFeedback))
            currentFeedback = 0;
        else if (currentFeedback > 1)
            currentFeedback = 1;

        correctedFeedback = correctedFeedback * .99f + currentFeedback * .01f;
        slowFeedback = slowFeedback * .9f + correctedFeedback * .1f;

        angle += 10.25f + correctedFeedback * 150f + slowFeedback * 40f;

        if (angle >= 360f) {
            angle = 0f;
            pointer--;
            if (pointer <= 0)
                pointer = numDepthQuads;
        }

        fogcol = ColorMap.getRGB(slowFeedback * 360f, .35f + slowFeedback / 4f, .8f);

        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl2.glLoadIdentity();
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl2.glClearColor(fogcol[0], fogcol[1], fogcol[2], fogcol[3]);

        float w5 = width / 5f;
        float h5 = height / 5f;
        float hw = width / 2f;
        float hh = height / 2f;
        float quadWidth = 2f * width / numHorizontalQuads;

        float pos = (0.001f + angle / 360f);

        gl2.glPushMatrix();
        gl2.glRotatef(180f, 0, 0, -1);
        gl2.glTranslatef((-width / 1f), (-height / 2f), -650f);
        gl2.glRotatef(55f - correctedFeedback * 10f, 1, 0, 0);
        gl2.glTranslatef(0, (0.001f + angle / 360f) * quadWidth, 0);
        gl2.glTranslatef(0.0f, 0.0f, -260.0f * correctedFeedback); // translate into the screen.

        float FOG_DISTANCE = 800;
        fogcol = new float[]{.5f + slowFeedback / 2f, .5f + slowFeedback / 2f, .5f + slowFeedback / 2f, 1};

        gl2.glEnable(GL2.GL_FOG);
        gl2.glFogi(GL2.GL_FOG_MODE, GL.GL_LINEAR);
        gl2.glFogf(GL2.GL_FOG_DENSITY, 0f);
        gl2.glFogfv(GL2.GL_FOG_COLOR, fogcol, 0);
        gl2.glFogf(GL2.GL_FOG_START, FOG_DISTANCE - 300);
        gl2.glFogf(GL2.GL_FOG_END, FOG_DISTANCE);
        gl2.glHint(GL2.GL_FOG_HINT, GL.GL_FASTEST);

        texYantra.bind(gl2);
        texYantra.enable(gl2);
        float lastVal = 0f;

        for (int i = 0; i < numDepthQuads; i++) {
            float ia = i + (0.001f + angle / 360f);
            int pc = (i + pointer) % numDepthQuads;
            int pn = (i + pointer + 1) % numDepthQuads;
            float z = ia / (float) numDepthQuads;
            float zn = (ia + 1) / (float) numDepthQuads;
            float sc = width - (z * hw) * 2;
            float sn = width - (zn * hw) * 2;

            lastVal = rnd.nextFloat() * 30f;
            gl2.glBegin(GL2.GL_QUAD_STRIP); // draw using triangles
            for (int j = 0; j < numHorizontalQuads; j++) {

                float zOffset = -.5f + (float) j / (float) numHorizontalQuads;
                zOffset = zOffset * zOffset;
                float[] rgb = ColorMap.getRGB(angle, .75f + slowFeedback / 4f, .8f);
                float colorc = Math.max(landscape[pc][j], 0f);
                float colorn = Math.max(landscape[pn][j], 0f);

                gl2.glColor4f(1 - colorc, colorc, 1 - colorc, 1);
                gl2.glTexCoord3f((float) j / (float) numHorizontalQuads, 0, 0);
                gl2.glVertex3f(quadWidth * (float) j, -500f + quadWidth * (float) i, 520.0f * zOffset + -60f * landscape[pc][j]);
                gl2.glColor4f(1 - colorn, colorn, 1 - colorn, 1);
                gl2.glTexCoord3f((float) j / (float) numHorizontalQuads, 1, 0);
                gl2.glVertex3f(quadWidth * (float) j, -500f + quadWidth * (float) (i + 1), 520.0f * zOffset + -60f * landscape[pn][j]);
            }
            gl2.glEnd();
        }

        texYantra.disable(gl2);

        gl2.glPopMatrix();

        gl2.glPushMatrix();
        gl2.glColor4f(.5f, .5f, .5f, 1);
        gl2.glTranslatef(0f, -150 + 800 * (0.01f + slowFeedback) / 2f, -300f - 200f * (0.01f + slowFeedback) / 2f);
        glut.glutSolidCylinder(15f, 60f, 12, 12);
        gl2.glTranslatef(0f, 0, -30f);
        gl2.glRotatef(180f, 0, -1, 0);
        glut.glutSolidCone(15f, 120f, 12, 12);
        gl2.glRotatef(-180f, 0, -1, 0);
        gl2.glTranslatef(0f, 0, 30f);
        gl2.glPushMatrix();
        gl2.glRotatef(90f, 0, -1, 0);
        gl2.glTranslatef(0, 0, 5);
        glut.glutSolidCone(15f, 50f, 12, 12);
        gl2.glPopMatrix();
        gl2.glPushMatrix();
        gl2.glRotatef(90f, 0, 1, 0);
        gl2.glTranslatef(0, 0, 5);
        glut.glutSolidCone(15f, 50f, 12, 12);
        gl2.glPopMatrix();
        gl2.glColor4f(1.4f + (0.01f + slowFeedback) / 2f, 0.7f, .0f, (0.001f + angle / 360f));
        gl2.glTranslatef(0f, 0, 60f);
        glut.glutSolidCone(10f, 30f, 12, 12);

        gl2.glPopMatrix();

        gl2.glBegin(GL2.GL_QUADS); // draw using triangles
        gl2.glColor4f(0, 0, 0, 0);
        gl2.glVertex3f(-hw, -hh, -350f);
        gl2.glVertex3f(hw, -hh, -350f);

        gl2.glColor4f(1f, 1f, .4f, (0.01f + slowFeedback) / 2f);
        gl2.glVertex3f(hw, hh, -350f);
        gl2.glVertex3f(-hw, hh, -350f);
        gl2.glEnd();
    }

    public Texture generateWhiteNoise(GL2 gl2, int width, int height) {
        float[] noise = new float[width * height];

        Bitmap bitmap = Bitmap.createBitmap(width / 2, height / 2, Bitmap.Config.ARGB_4444);
        for (int i = 0; i < width / 2; i++)
            for (int j = 0; j < height / 2; j++) {
                int a = (int) (255 * (.5f - currentFeedback / 2f));
                // int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int color = (a << 24) | (0 << 16) | (g << 8) | b;
                bitmap.setPixel(i, j, color);
            }
        Texture texture = convertBitmapToTexture(bitmap);
        return texture;
    }

    public Texture convertBitmapToTexture(Bitmap bitmap) {
        // TODO
        return new Texture(defaultTexture);
    }

}