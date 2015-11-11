package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.view.ViewContentGravity;

/**
 * Created on 7/31/2015.
 */
public class ClipDrawableConstantState extends DrawableConstantState {

    private Drawable drawable;
    private ClipDrawableOrientation orientation;
    private ViewContentGravity gravity;

    public ClipDrawableConstantState(ClipDrawableConstantState state, ClipDrawable owner) {
        super();

        if(state != null) {
            Drawable copiedDrawble = state.drawable.copy();
            copiedDrawble.setDelegate(owner);
            this.drawable = copiedDrawble;

            this.orientation = state.orientation;
            this.gravity = state.gravity;
        } else {
            this.gravity = ViewContentGravity.Left;
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public ClipDrawableOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(ClipDrawableOrientation orientation) {
        this.orientation = orientation;
    }

    public ViewContentGravity getGravity() {
        return gravity;
    }

    public void setGravity(ViewContentGravity gravity) {
        this.gravity = gravity;
    }
}
