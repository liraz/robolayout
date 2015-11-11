package org.lirazs.robolayout.core.resource.drawable;

/**
 * Created on 7/31/2015.
 */
public class GradientDrawableCornerRadius {

    public static GradientDrawableCornerRadius zero() {
        GradientDrawableCornerRadius gradientDrawableCornerRadius = new GradientDrawableCornerRadius();

        gradientDrawableCornerRadius.topLeft = 0;
        gradientDrawableCornerRadius.topRight = 0;
        gradientDrawableCornerRadius.bottomLeft = 0;
        gradientDrawableCornerRadius.bottomRight = 0;

        return gradientDrawableCornerRadius;
    }

    private double topLeft;
    private double topRight;
    private double bottomLeft;
    private double bottomRight;

    public double getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(double topLeft) {
        this.topLeft = topLeft;
    }

    public double getTopRight() {
        return topRight;
    }

    public void setTopRight(double topRight) {
        this.topRight = topRight;
    }

    public double getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(double bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public double getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(double bottomRight) {
        this.bottomRight = bottomRight;
    }

    public boolean equalTo(GradientDrawableCornerRadius obj) {
        return this.topLeft == obj.topLeft && this.topRight == obj.topRight
                && this.bottomLeft == obj.bottomLeft &&
                this.bottomRight == obj.bottomRight;
    }
}
