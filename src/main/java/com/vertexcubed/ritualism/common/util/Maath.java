package com.vertexcubed.ritualism.common.util;

import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static net.minecraft.util.Mth.*;

/**
 * Like {@link net.minecraft.util.Mth} but worse!
 */
public class Maath {


    public static Vector2f rotate(Vector2f vector, float theta) {
        return new Vector2f(vector.x * cos(theta) - vector.y * sin(theta), vector.x * sin(theta) + vector.y * cos(theta));
    }

    public static Vector2f rotateDegrees(Vector2f vector, float theta) {
        return rotate(vector, theta * PI / 180.0f);
    }

    public static int argbToInt(int a, int r, int g, int b) {
        a = Mth.clamp(a, 0, 255);
        r = Mth.clamp(r, 0, 255);
        g = Mth.clamp(g, 0, 255);
        b = Mth.clamp(b, 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int rgbToInt(int r, int g, int b) {
        r = Mth.clamp(r, 0, 255);
        g = Mth.clamp(g, 0, 255);
        b = Mth.clamp(b, 0, 255);
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static int argbFloatToInt(float a, float r, float g, float b) {
        return argbToInt((int) (a * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public static float[] intToArgb(int color) {
        float alpha = (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        return new float[] {alpha, red, green, blue};
    }

}
