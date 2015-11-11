package org.lirazs.robolayout.core.resource.drawable;

/**
 * Created on 7/31/2015.
 */
public enum ClipDrawableOrientation {
    None,
    Horizontal,
    Vertical;

    public static ClipDrawableOrientation get(String string) {
        ClipDrawableOrientation result = Horizontal;
        if(string.equals("vertical")) {
            result = Vertical;
        }
        return result;
    }
}
