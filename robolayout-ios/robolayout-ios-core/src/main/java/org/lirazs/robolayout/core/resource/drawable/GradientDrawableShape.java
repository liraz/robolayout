package org.lirazs.robolayout.core.resource.drawable;

/**
 * Created on 7/31/2015.
 */
public enum GradientDrawableShape {
    Rectangle,
    Oval,
    Line,
    Ring;

    public static GradientDrawableShape get(String string) {
        GradientDrawableShape result = Rectangle;
        if(string.equals("rectangle")) {
            result = Rectangle;
        } else if(string.equals("oval")) {
            result = Oval;
        } else if(string.equals("line")) {
            result = Line;
        } else if(string.equals("ring")) {
            result = Ring;
        }
        return result;
    }
}
