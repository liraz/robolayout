package org.lirazs.robolayout.core.view;

import org.robovm.apple.foundation.NSObject;

/**
 * Created on 8/3/2015.
 */
public class LayoutMeasuredSize extends NSObject {

    private LayoutMeasuredDimension width;
    private LayoutMeasuredDimension height;

    public LayoutMeasuredSize() {
        this.width = new LayoutMeasuredDimension();
        this.height = new LayoutMeasuredDimension();
    }

    public LayoutMeasuredSize(LayoutMeasuredDimension width, LayoutMeasuredDimension height) {
        this.width = width;
        this.height = height;
    }

    public LayoutMeasuredDimension getWidth() {
        return width;
    }

    public void setWidth(LayoutMeasuredDimension width) {
        this.width = width;
    }

    public LayoutMeasuredDimension getHeight() {
        return height;
    }

    public void setHeight(LayoutMeasuredDimension height) {
        this.height = height;
    }
}
