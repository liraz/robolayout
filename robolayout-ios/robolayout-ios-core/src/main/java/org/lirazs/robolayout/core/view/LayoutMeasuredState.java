package org.lirazs.robolayout.core.view;

import org.robovm.rt.bro.Bits;

/**
 * Created on 8/2/2015.
 */
public class LayoutMeasuredState extends Bits<LayoutMeasuredState> {

    public static final LayoutMeasuredState None = new LayoutMeasuredState(0x0);
    public static final LayoutMeasuredState TooSmall = new LayoutMeasuredState(0x1);

    private static final LayoutMeasuredState[] values = (LayoutMeasuredState[])_values(LayoutMeasuredState.class);

    public static LayoutMeasuredState create(long value) {
        return new LayoutMeasuredState(value);
    }

    @Override
    protected LayoutMeasuredState wrap(long value, long mask) {
        return new LayoutMeasuredState(value, mask);
    }

    public LayoutMeasuredState(long value) {
        super(value);
    }

    private LayoutMeasuredState(long value, long mask) {
        super(value, mask);
    }

    @Override
    protected LayoutMeasuredState[] _values() {
        return values;
    }
}
