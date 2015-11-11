package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.uikit.UIEdgeInsets;

/**
 * Created on 7/31/2015.
 */
public class InsetDrawableConstantState extends DrawableConstantState {

    private UIEdgeInsets insets;
    private Drawable drawable;

    public InsetDrawableConstantState(InsetDrawableConstantState state, InsetDrawable owner) {
        super();

        if(state != null) {
            Drawable copiedDrawble = state.drawable.copy();
            copiedDrawble.setDelegate(owner);
            this.drawable = copiedDrawble;

            this.insets  = state.insets ;
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public UIEdgeInsets getInsets() {
        return insets;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setInsets(UIEdgeInsets insets) {
        this.insets = insets;
    }
}
