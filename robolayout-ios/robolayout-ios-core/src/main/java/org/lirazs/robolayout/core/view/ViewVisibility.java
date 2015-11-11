package org.lirazs.robolayout.core.view;

/**
 * Created on 8/3/2015.
 */
public enum ViewVisibility {

    Visible(0x00000000),
    Invisible(0x00000004),
    Gone(0x00000008);

    public static ViewVisibility valueOf(int value) {
        for (ViewVisibility visibility : ViewVisibility.values()) {
            if(visibility.getValue() == value)
                return visibility;
        }
        return null;
    }

    private int value;

    ViewVisibility(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ViewVisibility get(String visibilityString) {
        ViewVisibility visibility = ViewVisibility.Visible;

        if(visibilityString.equals("visible")) {
            visibility = ViewVisibility.Visible;
        } else if(visibilityString.equals("invisible")) {
            visibility = ViewVisibility.Invisible;
        } else if(visibilityString.equals("gone")) {
            visibility = ViewVisibility.Gone;
        }
        return visibility;
    }
}
