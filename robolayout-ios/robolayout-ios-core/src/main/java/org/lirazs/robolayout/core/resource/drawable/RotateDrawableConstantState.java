package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.coregraphics.CGPoint;

/**
 * Created on 7/31/2015.
 */
public class RotateDrawableConstantState extends DrawableConstantState {

    private Drawable drawable;
    private CGPoint pivot;
    private boolean pivotXRelative;
    private boolean pivotYRelative;
    private double fromDegrees;
    private double toDegrees;
    private double currentDegrees;

    public RotateDrawableConstantState(RotateDrawableConstantState state, RotateDrawable owner) {
        super();

        if(state != null) {
            Drawable copiedDrawble = state.drawable.copy();
            copiedDrawble.setDelegate(owner);
            this.drawable = copiedDrawble;

            this.pivot = state.pivot;
            this.pivotXRelative = state.pivotXRelative;
            this.pivotYRelative = state.pivotYRelative;

            this.fromDegrees = this.currentDegrees = state.fromDegrees;
            this.toDegrees = state.toDegrees;
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public CGPoint getPivot() {
        return pivot;
    }

    public void setPivot(CGPoint pivot) {
        this.pivot = pivot;
    }

    public boolean isPivotXRelative() {
        return pivotXRelative;
    }

    public void setPivotXRelative(boolean pivotXRelative) {
        this.pivotXRelative = pivotXRelative;
    }

    public boolean isPivotYRelative() {
        return pivotYRelative;
    }

    public void setPivotYRelative(boolean pivotYRelative) {
        this.pivotYRelative = pivotYRelative;
    }

    public double getFromDegrees() {
        return fromDegrees;
    }

    public void setFromDegrees(double fromDegrees) {
        this.fromDegrees = fromDegrees;
    }

    public double getToDegrees() {
        return toDegrees;
    }

    public void setToDegrees(double toDegrees) {
        this.toDegrees = toDegrees;
    }

    public double getCurrentDegrees() {
        return currentDegrees;
    }

    public void setCurrentDegrees(double currentDegrees) {
        this.currentDegrees = currentDegrees;
    }
}
