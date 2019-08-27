package io.neurolab.tools;

/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2002-2004 Sun Microsystems, Inc.
 * Portions Copyright 2002-2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "README" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

import java.util.Arrays;

import android.annotation.TargetApi;
import android.graphics.Color;

public class ColorMap {
    public static int size;
    public static byte[] r;
    public static byte[] g;
    public static byte[] b;
    public static Color[] table;

    /**
     * Create a color map witn n entries that looks like Matlab's jet color map
     */
    @TargetApi(26)
    public static void getJet(int n) {
        r = new byte[n];
        g = new byte[n];
        b = new byte[n];

        int maxval = 255;
        Arrays.fill(g, 0, n / 8, (byte) 0);
        for (int x = 0; x < n / 4; x++)
            g[x + n / 8] = (byte) (maxval * x * 4 / n);
        Arrays.fill(g, n * 3 / 8, n * 5 / 8, (byte) maxval);
        for (int x = 0; x < n / 4; x++)
            g[x + n * 5 / 8] = (byte) (maxval - (maxval * x * 4 / n));
        Arrays.fill(g, n * 7 / 8, n, (byte) 0);

        for (int x = 0; x < g.length; x++)
            b[x] = g[(x + n / 4) % g.length];
        Arrays.fill(b, n * 7 / 8, n, (byte) 0);
        Arrays.fill(g, 0, n / 8, (byte) 0);
        for (int x = n / 8; x < g.length; x++)
            r[x] = g[(x + n * 6 / 8) % g.length];

        size = n;
        table = new Color[n];
        for (int x = 0; x < n; x++){
            table[x] = Color.valueOf(Color.rgb(r[x], g[x], b[x]));
        }
    }

    /**
     * Get the RGB value associated with an entry in this ColorMap
     */
    public static int getColor(int idx) {
        int pixel = ((r[idx] << 16) & 0xff0000) | ((g[idx] << 8) & 0xff00) | (b[idx] & 0xff);
        return pixel;
    }

    public static float[] getRGB(float h, float s, float b) {
        float[] hsv = {h, s, b};
        int rgb = Color.HSVToColor(255, hsv);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new float[] { (float) red / 255f, (float) green / 255f, (float) blue / 255f, 1f };
    }

    public String toString() {
        StringBuffer s = new StringBuffer(500);
        for (int x = 0; x < size; x++) {
            s.append(x + ": {" + r[x] + ",\t" + g[x] + ",\t" + b[x] + "}\t");
            if (x % 3 == 2)
                s.append("\n");
        }

        return s.toString();
    }

}