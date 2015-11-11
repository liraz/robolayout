package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.uikit.UIEdgeInsets;

/**
 * Created on 7/31/2015.
 */
public class LayerDrawableItem {

    Drawable drawable;
    UIEdgeInsets insets;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public UIEdgeInsets getInsets() {
        return insets;
    }

    public void setInsets(UIEdgeInsets insets) {
        this.insets = insets;
    }
}
