package org.lirazs.robolayout.core.resource.drawable;

/**
 * Created on 7/31/2015.
 */
public enum GradientDrawableGradientType {
    None,
    Linear,
    Radial,
    Sweep;

    public static GradientDrawableGradientType get(String string) {
        GradientDrawableGradientType result = None;
        if(string.isEmpty() || string.equals("linear")) {
            result = Linear;
        } else if(string.equals("radial")) {
            result = Radial;
        } else if(string.equals("sweep")) {
            result = Sweep;
        }
        return result;
    }
}
