package com.cocorporation.rainbow.utils;

import android.graphics.Color;

/**
 * Created by Corentin on 7/9/2016.
 */
public class Util {
    public static Point extendSegmentToDistance(float x1, float y1, float x2, float y2, float radius) {
        Double numerator = (double)(radius * Math.abs(x1 - x2));
        Double denominator = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        float x3, y3;
        if (x2 < x1) {
            x3 = (float) (x1 - numerator / denominator);
        } else {
            x3 = (float) (x1 + numerator / denominator);
        }

        float a = (y2 - y1) / (x2 - x1);
        float b = y1 - a * x1;

        y3 = a * x3 + b;

        // Should never happen if your view is positioned at the bottom
        if (y3 > y1) {
            y3 = y1;
            if (x3 < x1) {
                x3 = x1 - radius;
            } else {
                x3 = x1 + radius;
            }
        }

        return new Point(x3, y3);
    }

    public static float angleBetweenSegments(float x1, float y1, float x2, float y2) {
        float hypotenus = (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        float adj = (Math.abs(x2 - x1));

        float angle = (float) Math.acos(adj / hypotenus);

        if (x2 > x1) {
            angle = (float) (Math.PI - angle);
        }

        return angle;
    }

    public static int colorFromAngle(float angle, int[] colors) {
        int numberOfColors = colors.length;
        float piPart = (float) Math.PI / (numberOfColors - 1);

        int indexOfFirstAngle = (int) (angle / piPart);
        if (indexOfFirstAngle == numberOfColors - 1) {
            return colors[numberOfColors - 1];
        }

        int firstColor = colors[indexOfFirstAngle];
        int secondColor = colors[indexOfFirstAngle + 1];
        float firstAngle = indexOfFirstAngle * piPart;
        float secondAngle = (indexOfFirstAngle + 1) * piPart;

        float percentageOfSecondColor = percentageOfArc(firstAngle, secondAngle, angle);
        float percentageOfFirstColor = 1 - percentageOfSecondColor;

        int red = (int) (Color.red(firstColor) * percentageOfFirstColor + Color.red(secondColor) * percentageOfSecondColor);
        int green = (int) (Color.green(firstColor) * percentageOfFirstColor + Color.green(secondColor) * percentageOfSecondColor);
        int blue = (int) (Color.blue(firstColor) * percentageOfFirstColor + Color.blue(secondColor) * percentageOfSecondColor);

        return Color.rgb(red, green, blue);
    }

    private static float percentageOfArc(float orig, float dest, float angle) {
        return (angle - orig) / (dest - orig);
    }
}
