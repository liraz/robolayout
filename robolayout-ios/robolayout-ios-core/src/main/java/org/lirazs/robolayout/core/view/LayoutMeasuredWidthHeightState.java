package org.lirazs.robolayout.core.view;

/**
 * Created on 8/3/2015.
 */
public class LayoutMeasuredWidthHeightState {

    private LayoutMeasuredState widthState;
    private LayoutMeasuredState heightState;

    public LayoutMeasuredWidthHeightState() {
    }

    public LayoutMeasuredWidthHeightState(LayoutMeasuredState width, LayoutMeasuredState height) {
        this.widthState = width;
        this.heightState = height;
    }

    public LayoutMeasuredWidthHeightState(long widthValue, long heightValue) {
        this.widthState = LayoutMeasuredState.create(widthValue);
        this.heightState = LayoutMeasuredState.create(heightValue);
    }

    public static LayoutMeasuredWidthHeightState combineMeasuredStates(LayoutMeasuredWidthHeightState currState,
                                                                       LayoutMeasuredWidthHeightState newState) {

        long currWidthValue = currState.getWidthState().value();
        long currHeightValue = currState.getHeightState().value();

        currWidthValue |= newState.getWidthState().value();
        currHeightValue |= newState.getHeightState().value();

        return new LayoutMeasuredWidthHeightState(currWidthValue, currHeightValue);
    }

    public LayoutMeasuredState getWidthState() {
        return widthState;
    }

    public void setWidthState(LayoutMeasuredState widthState) {
        this.widthState = widthState;
    }

    public LayoutMeasuredState getHeightState() {
        return heightState;
    }

    public void setHeightState(LayoutMeasuredState heightState) {
        this.heightState = heightState;
    }
}
