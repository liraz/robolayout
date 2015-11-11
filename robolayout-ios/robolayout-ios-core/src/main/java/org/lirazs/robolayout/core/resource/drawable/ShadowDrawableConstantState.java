package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIColor;

/**
 * Created on 7/31/2015.
 */
public class ShadowDrawableConstantState extends DrawableConstantState {

    private Drawable drawable;
    private double alpha;
    private CGSize offset;
    private double blur;
    private UIColor shadowColor;

    public ShadowDrawableConstantState(ShadowDrawableConstantState state, ShadowDrawable owner) {
        super();

        if(state != null) {
            Drawable copiedDrawble = state.drawable.copy();
            copiedDrawble.setDelegate(owner);
            this.drawable = copiedDrawble;

            this.alpha = state.alpha;
            this.blur = state.blur;
            this.offset = state.offset;
            this.shadowColor = state.shadowColor;
        } else {
            this.alpha = 1.d;
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public CGSize getOffset() {
        return offset;
    }

    public void setOffset(CGSize offset) {
        this.offset = offset;
    }

    public double getBlur() {
        return blur;
    }

    public void setBlur(double blur) {
        this.blur = blur;
    }

    public UIColor getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(UIColor shadowColor) {
        this.shadowColor = shadowColor;
    }
}
