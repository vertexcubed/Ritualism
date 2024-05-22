package com.vertexcubed.ritualism.common.util;

import org.checkerframework.checker.units.qual.C;

/**
 * Immutable color object for use in minecraft. May add conversions to and from hsv and other color spaces too
 */
public class Color {

    private final int a;
    private final int r;
    private final int g;
    private final int b;

    private Color(int a, int r, int g, int b) {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Color rgbInt(int r, int g, int b) {
        return argbInt(255, r, g, b);
    }

    public static Color argbInt(int a, int r, int g, int b) {
        return new Color(a, r, g, b);
    }

    public static Color argbInt(int[] color) {
        return argbInt(color[0], color[1], color[2], color[3]);
    }

    public static Color rgbFloat(float r, float g, float b) {
        return argbFloat(1f, r, g, b);
    }

    public static Color argbFloat(float[] color) {
        return argbFloat(color[0], color[1], color[2], color[3]);
    }

    public static Color argbFloat(float a, float r, float g, float b) {
        return argbInt((int) (a * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public int a() {
        return a;
    }
    public int r() {
        return r;
    }
    public int g() {
        return g;
    }
    public int b() {
        return b;
    }

    public float af() {
        return a / 255f;
    }
    public float rf() {
        return r / 255f;
    }
    public float gf() {
        return g / 255f;
    }
    public float bf() {
        return b / 255f;
    }

    public Color setA(int a) {
        return new Color(a, this.r, this.g, this.b);
    }
    public Color setAf(float a) {
        return new Color((int) (a * 255), this.r, this.g, this.b);
    }

    public static Color packedInt(int color) {
        int alpha = (color >> 24 & 255);
        int red = (color >> 16 & 255);
        int green = (color >> 8 & 255);
        int blue = (color & 255);
        return new Color(alpha, red, green, blue);
    }

    public int toPackedInt() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public int[] toIntArray() {
        return new int[] {a, r, g, b};
    }
    public float[] toFloatArray() {
        return new float[] {a / 255f, r / 255f, g / 255f, b / 255f};
    }
}
