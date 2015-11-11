package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.coregraphics.*;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEdgeInsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/31/2015.
 */
public class GradientDrawableConstantState extends DrawableConstantState {

    private List<UIColor> colors;
    private List<CGColor> cgColors;
    private double[] colorPositions;
    private UIEdgeInsets padding;
    private boolean hasPadding;
    private double strokeWidth;
    private UIColor strokeColor;
    private double dashWidth;
    private double dashGap;

    private double innerRadius;
    private double innerRadiusRatio;
    private double thickness;
    private double thicknessRatio;

    private GradientDrawableGradientType gradientType;
    private CGPoint relativeGradientCenter;
    private double gradientRadius;
    private boolean gradientRadiusIsRelative;
    private GradientDrawableShape shape;
    private GradientDrawableCornerRadius corners;
    private CGSize size;
    private double gradientAngle;

    private CGColorSpace colorSpace;
    private CGGradient gradient;

    public GradientDrawableConstantState() {
        this(null);
    }

    public GradientDrawableConstantState(GradientDrawableConstantState state) {
        super();

        if(state != null) {
            List<UIColor> colors = new ArrayList<>(state.colors);
            this.colors = colors;

            if(state.colorPositions != null) {
                this.colorPositions = new double[this.colors.size()];
                System.arraycopy(state.colorPositions, 0, this.colorPositions, 0, this.colors.size());
            }

            List<CGColor> cgColors = new ArrayList<>(state.cgColors);
            this.cgColors = cgColors;
            this.padding = state.padding;
            this.hasPadding = state.hasPadding;
            this.strokeWidth = state.strokeWidth;
            this.strokeColor = state.strokeColor;
            this.dashWidth = state.dashWidth;
            this.dashGap = state.dashGap;

            this.innerRadius = state.innerRadius;
            this.innerRadiusRatio = state.innerRadiusRatio;
            this.thickness = state.thickness;
            this.thicknessRatio = state.thicknessRatio;

            this.shape = state.shape;
            this.corners = state.corners;
            this.size = state.size;
            this.gradientAngle = state.gradientAngle;
            this.colorSpace = state.colorSpace;
            this.gradient = state.gradient;
            this.relativeGradientCenter = state.relativeGradientCenter;
            this.gradientRadius = state.gradientRadius;
            this.gradientRadiusIsRelative = state.gradientRadiusIsRelative;
            this.gradientType = state.gradientType;
        } else {
            this.colorSpace = CGColorSpace.createDeviceRGB();
            this.innerRadius = -1;
            this.thickness = -1;
        }
    }

    public List<CGColor> getCgColors() {
        if(this.cgColors == null) {
            List<CGColor> cgColors = new ArrayList<>(this.colors.size());
            for (UIColor color : colors) {
                CGColor cgColor = color.getCGColor();
                if(cgColor != null) {
                    cgColors.add(cgColor);
                }
            }
            this.cgColors = cgColors;
        }
        return this.cgColors;
    }

    public CGGradient getCurrentGradient() {
        if(this.gradient == null) {
            this.gradient = CGGradient.create(this.colorSpace, (CGColor[]) this.cgColors.toArray(), this.colorPositions);
        }
        return this.gradient;
    }

    public GradientDrawableCornerRadius getCorners() {
        return corners;
    }

    public GradientDrawableShape getShape() {
        return shape;
    }

    public void setShape(GradientDrawableShape shape) {
        this.shape = shape;
    }

    public double getThickness() {
        return thickness;
    }

    public double getThicknessRatio() {
        return thicknessRatio;
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public List<UIColor> getColors() {
        return colors;
    }

    public GradientDrawableGradientType getGradientType() {
        return gradientType;
    }

    public double getGradientAngle() {
        return gradientAngle;
    }

    public CGPoint getRelativeGradientCenter() {
        return relativeGradientCenter;
    }

    public double getGradientRadius() {
        return gradientRadius;
    }

    public boolean isGradientRadiusIsRelative() {
        return gradientRadiusIsRelative;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public UIColor getStrokeColor() {
        return strokeColor;
    }

    public double getDashWidth() {
        return dashWidth;
    }

    public double getDashGap() {
        return dashGap;
    }

    public void setInnerRadius(double innerRadius) {
        this.innerRadius = innerRadius;
    }

    public void setInnerRadiusRatio(double innerRadiusRatio) {
        this.innerRadiusRatio = innerRadiusRatio;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public void setThicknessRatio(double thicknessRatio) {
        this.thicknessRatio = thicknessRatio;
    }

    public void setGradientType(GradientDrawableGradientType gradientType) {
        this.gradientType = gradientType;
    }

    public void setGradientRadius(double gradientRadius) {
        this.gradientRadius = gradientRadius;
    }

    public void setGradientRadiusIsRelative(boolean gradientRadiusIsRelative) {
        this.gradientRadiusIsRelative = gradientRadiusIsRelative;
    }

    public void setGradientAngle(double gradientAngle) {
        this.gradientAngle = gradientAngle;
    }

    public void setColors(List<UIColor> colors) {
        this.colors = colors;
    }

    public void setColorPositions(double[] colorPositions) {
        this.colorPositions = colorPositions;
    }

    public void setPadding(UIEdgeInsets padding) {
        this.padding = padding;
    }

    public void setHasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
    }

    public void setCorners(GradientDrawableCornerRadius corners) {
        this.corners = corners;
    }

    public void setSize(CGSize size) {
        this.size = size;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStrokeColor(UIColor strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setDashWidth(double dashWidth) {
        this.dashWidth = dashWidth;
    }

    public void setDashGap(double dashGap) {
        this.dashGap = dashGap;
    }

    public UIEdgeInsets getPadding() {
        return padding;
    }

    public boolean hasPadding() {
        return hasPadding;
    }

    public CGSize getSize() {
        return size;
    }
}
