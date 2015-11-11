package org.lirazs.robolayout.core.view;

import org.robovm.apple.coregraphics.CGSize;

/**
 * Created on 8/3/2015.
 */
public class LayoutMeasuredDimension {
    private double size;
    private LayoutMeasuredState state;

    public static double resolveSize(double size, LayoutMeasureSpec measureSpec) {
        return resolveSizeAndState(size, measureSpec, LayoutMeasuredState.None).getSize();
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec.  Will take the desired size, unless a different size
     * is imposed by the constraints.  The returned value is a compound integer,
     * with the resolved size in the {@link #MEASURED_SIZE_MASK} bits and
     * optionally the bit {@link #MEASURED_STATE_TOO_SMALL} set if the resulting
     * size is smaller than the size the view wants to be.
     *
     * @param size How big the view wants to be
     * @param measureSpec Constraints imposed by the parent
     * @return Size information bit mask as defined by
     * {@link #MEASURED_SIZE_MASK} and {@link #MEASURED_STATE_TOO_SMALL}.
     */
    public static LayoutMeasuredDimension resolveSizeAndState(double size, LayoutMeasureSpec measureSpec,
                                                              LayoutMeasuredState childMeasuredState) {

        LayoutMeasuredDimension result = new LayoutMeasuredDimension(size, LayoutMeasuredState.None);

        switch (measureSpec.getMode()) {
            case Unspecified:
                result.setSize(size);
                break;
            case AtMost:
                if(measureSpec.getSize() < size) {
                    result.setSize(measureSpec.getSize());
                    result.setState(LayoutMeasuredState.TooSmall);
                } else {
                    result.setSize(size);
                }
                break;
            case Exactly:
                result.setSize(measureSpec.getSize());
                break;
        }
        long resultStateValue = result.getState().value();
        LayoutMeasuredState state = LayoutMeasuredState.create(resultStateValue | childMeasuredState.value());

        result.setState(state);
        return result;
    }

    public static LayoutMeasuredDimension getDefaultSize(double size, LayoutMeasureSpec measureSpec) {
        double result = size;

        LayoutMeasureSpecMode specMode = measureSpec.getMode();
        double specSize = measureSpec.getSize();

        switch (specMode) {
            case Unspecified:
                result = size;
                break;
            case AtMost:
            case Exactly:
                result = specSize;
                break;
        }
        return new LayoutMeasuredDimension(result, LayoutMeasuredState.None);
    }

    public LayoutMeasuredDimension() {
        this.size = 0;
        this.state = LayoutMeasuredState.None;
    }

    public LayoutMeasuredDimension(double size, LayoutMeasuredState state) {
        this.size = size;
        this.state = state;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public LayoutMeasuredState getState() {
        return state;
    }

    public void setState(LayoutMeasuredState state) {
        this.state = state;
    }
}
