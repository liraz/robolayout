package org.lirazs.robolayout.core.view;

/**
 * Created on 8/2/2015.
 */
public class LayoutMeasureSpec {

    private double size;
    private LayoutMeasureSpecMode mode;

    public LayoutMeasureSpec() {
    }

    public LayoutMeasureSpec(double size, LayoutMeasureSpecMode mode) {
        this.size = size;
        this.mode = mode;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public LayoutMeasureSpecMode getMode() {
        return mode;
    }

    public void setMode(LayoutMeasureSpecMode mode) {
        this.mode = mode;
    }
}
