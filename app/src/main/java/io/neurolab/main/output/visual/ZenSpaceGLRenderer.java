package io.neurolab.main.output.visual;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import io.neurolab.tools.ResourceManager;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class ZenSpaceGLRenderer {

    private Context context;

    private float currentFeedback = 0;
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

    private static Random random = new Random();
    public GLProfile glprofile;

    private String[] themes = {"nforest", "universe"};
    int currentTheme = 1;

    private float oldFeedback = 0f;

    public ZenSpaceGLRenderer(Context context, GLProfile glprofile2) {
        this.context = context;
        this.glprofile = glprofile2;
    }

    public void nextTheme() {
        currentTheme++;
        if (currentTheme+1>themes.length)
            currentTheme = 0;
    }

    public void setCurrentFeedback(float currentFeedback) {
        this.currentFeedback = currentFeedback;
    }

    public void setup(GL2 gl2, int width, int height) {
        angle = 0f;
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();

        GLU glu = new GLU();
        glu.gluOrtho2D(0.0f, width, 0.0f, height);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();

        gl2.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;

        if (texYantra == null) {
            try {
                File resourceFile = ResourceManager.getInstance().getResource(context, "yantra_white.png");
                Bitmap bitmap = BitmapFactory.decodeFile(resourceFile.getAbsolutePath());
                texYantra = AWTTextureIO.newTexture(resourceFile, false);

                textures = new Texture[3];
                for (int i = 0; i < textures.length; i++) {
                    textures[i] = AWTTextureIO.newTexture(ResourceManager.getInstance().getResource(context, themes[currentTheme]+(i+1)+".png"), false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (width<height) {
            shortSide = width;
            offsetX = 0;
            offsetY = height / 2;
        }
        else {
            shortSide = height;
            offsetX = width / 2;
            offsetY = 0;
        }

        shortSideHalf = shortSide/2;
    }

    public void render(GL2 gl2, int width, int height) {
        if ((currentFeedback<0) || Float.isNaN(currentFeedback))
            currentFeedback = 0;
        else if (currentFeedback>1)
            currentFeedback = 1;

        angle +=  .25f - currentFeedback/4f;

        if (angle > 720f) {
            currentForestOut=currentForestIn;
            currentForestIn++;

            if (currentForestIn > textures.length-1)
                currentForestIn = 0;

            angle = 0f;
        }

        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl2.glLoadIdentity();
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glColor4f(1, 1, 1, 1);

        int i = currentForestIn;

        float scaleIn = angle/720f;
        float scaleInSquared = (float)Math.sqrt(scaleIn);
        textures[i].bind(gl2);
        textures[i].enable(gl2);
        gl2.glPushMatrix();
        gl2.glTranslatef((float)(width/2), (float)(height/2), 0);
        gl2.glRotatef(180, 0, 0, -1);
        gl2.glScalef(1f+scaleIn/2f, 1f+scaleIn/2f, 1);
        gl2.glColor4f(.5f, .2f, 1, scaleInSquared);
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glTexCoord3f(0, 0, 0);
        gl2.glVertex3f(-width/2f, -height/2f, 0);
        gl2.glTexCoord3f(1, 0, 0);
        gl2.glVertex3f(width/2f, -height/2f, 0);
        gl2.glTexCoord3f(1, 1, 0);
        gl2.glVertex3f(width/2f, height/2f, 0);
        gl2.glTexCoord3f(0, 1, 0);
        gl2.glVertex3f(-width/2f, height/2f, 0);
        gl2.glEnd();
        gl2.glPopMatrix();
        textures[i].disable(gl2);

        i = currentForestOut;
        textures[i].bind(gl2);
        textures[i].enable(gl2);
        gl2.glPushMatrix();
        gl2.glTranslatef((float)(width/2), (float)(height/2), 0);
        gl2.glRotatef(180, 0, 0, -1);
        gl2.glScalef(1.5f + scaleIn, 1.5f + scaleIn, 1);
        gl2.glColor4f(.5f, .2f, 1, 1f-scaleIn);
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glTexCoord3f(0, 0, 0);
        gl2.glVertex3f(-width/2f, -height/2f, 0);
        gl2.glTexCoord3f(1, 0, 0);
        gl2.glVertex3f(width/2f, -height/2f, 0);
        gl2.glTexCoord3f(1, 1, 0);
        gl2.glVertex3f(width/2f, height/2f, 0);
        gl2.glTexCoord3f(0, 1, 0);
        gl2.glVertex3f(-width/2f, height/2f, 0);
        gl2.glEnd();
        gl2.glPopMatrix();
        textures[i].disable(gl2);


        texYantra.bind(gl2);
        texYantra.enable(gl2);
        gl2.glPushMatrix();
        gl2.glTranslatef((float)(width/2), (float)(height/2), 0);
        gl2.glRotatef(angle/2f, 0, 0, 1);
        gl2.glColor4f(1, 1, 0, 1f);
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glTexCoord3f(0, 0, 0);
        gl2.glVertex3f(-shortSideHalf, -shortSideHalf, 0);
        gl2.glTexCoord3f(1, 0, 0);
        gl2.glVertex3f(shortSideHalf, -shortSideHalf, 0);
        gl2.glTexCoord3f(1, 1, 0);
        gl2.glVertex3f(shortSideHalf, shortSideHalf, 0);
        gl2.glTexCoord3f(0, 1, 0);
        gl2.glVertex3f(-shortSideHalf, shortSideHalf, 0);
        gl2.glEnd();
        gl2.glPopMatrix();

        texYantra.bind(gl2);
        texYantra.enable(gl2);
        gl2.glPushMatrix();
        gl2.glTranslatef((float)(width/2), (float)(height/2), 0);
        gl2.glRotatef(180, 0, -1, 0);
        gl2.glRotatef(angle/2f, 0, 0, 1);
        gl2.glColor4f(0, 1, 1, 1f);
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glTexCoord3f(0, 0, 0);
        gl2.glVertex3f(-shortSideHalf, -shortSideHalf, 0);
        gl2.glTexCoord3f(1, 0, 0);
        gl2.glVertex3f(shortSideHalf, -shortSideHalf, 0);
        gl2.glTexCoord3f(1, 1, 0);
        gl2.glVertex3f(shortSideHalf, shortSideHalf, 0);
        gl2.glTexCoord3f(0, 1, 0);
        gl2.glVertex3f(-shortSideHalf, shortSideHalf, 0);
        gl2.glEnd();
        gl2.glPopMatrix();
        texYantra.disable(gl2);
    }

    public Bitmap generateWhiteNoise(GL2 gl2, int width, int height) {
        float[] noise = new float[width * height];

        Bitmap bitmap = Bitmap.createBitmap(width / 2, height / 2, Bitmap.Config.ARGB_4444);

        for (int i = 0; i < width / 2; i++)
            for (int j = 0; j < height / 2; j++) {
                int a = (int) (255 * (.5f - currentFeedback / 2f));
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int color = (a << 24) | (0 << 16) | (g << 8) | b;
                bitmap.setPixel(i, j, color);
            }
        return bitmap;
    }

}