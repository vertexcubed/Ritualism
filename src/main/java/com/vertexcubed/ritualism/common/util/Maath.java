package com.vertexcubed.ritualism.common.util;

import org.joml.Vector2f;

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

}
