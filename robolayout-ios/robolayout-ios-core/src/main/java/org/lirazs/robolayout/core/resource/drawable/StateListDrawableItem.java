package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.uikit.UIControlState;

/**
 * Created on 7/30/2015.
 */
public class StateListDrawableItem {

    private UIControlState state;
    private Drawable drawable;

    public UIControlState getState() {
        return state;
    }

    public void setState(UIControlState state) {
        this.state = state;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
