package org.lirazs.robolayout.core.view;

import org.robovm.rt.bro.Bits;

/**
 * Created on 8/3/2015.
 */
public final class ViewContentGravity extends Bits<ViewContentGravity> {

    public static final ViewContentGravity None = new ViewContentGravity(0x0000);
    public static final ViewContentGravity Top = new ViewContentGravity((GravityAxis.AXIS_PULL_BEFORE | GravityAxis.AXIS_SPECIFIED) << GravityAxis.AXIS_Y_SHIFT);
    public static final ViewContentGravity Bottom = new ViewContentGravity((GravityAxis.AXIS_PULL_AFTER | GravityAxis.AXIS_SPECIFIED) << GravityAxis.AXIS_Y_SHIFT);
    public static final ViewContentGravity Left = new ViewContentGravity((GravityAxis.AXIS_PULL_BEFORE | GravityAxis.AXIS_SPECIFIED) << GravityAxis.AXIS_X_SHIFT);
    public static final ViewContentGravity Right = new ViewContentGravity((GravityAxis.AXIS_PULL_AFTER | GravityAxis.AXIS_SPECIFIED) << GravityAxis.AXIS_X_SHIFT);
    public static final ViewContentGravity CenterVertical = new ViewContentGravity(GravityAxis.AXIS_SPECIFIED << GravityAxis.AXIS_Y_SHIFT);
    public static final ViewContentGravity FillVertical = new ViewContentGravity(ViewContentGravity.Top.set(ViewContentGravity.Bottom).value());
    public static final ViewContentGravity CenterHorizontal = new ViewContentGravity(GravityAxis.AXIS_SPECIFIED << GravityAxis.AXIS_X_SHIFT);
    public static final ViewContentGravity FillHorizontal = new ViewContentGravity(ViewContentGravity.Left.set(ViewContentGravity.Right).value());
    public static final ViewContentGravity Center = new ViewContentGravity(ViewContentGravity.CenterVertical.set(ViewContentGravity.CenterHorizontal).value());
    public static final ViewContentGravity Fill = new ViewContentGravity(ViewContentGravity.FillVertical.set(ViewContentGravity.FillHorizontal).value());

    private static final ViewContentGravity[] values = (ViewContentGravity[])_values(ViewContentGravity.class);

    public interface GravityAxis {
        long AXIS_SPECIFIED = 0x0001;
        long AXIS_PULL_BEFORE = 0x0002;
        long AXIS_PULL_AFTER = 0x0004;
        long AXIS_CLIP = 0x0008;
        long AXIS_X_SHIFT = 0;
        long AXIS_Y_SHIFT = 4;
    }

    public static long HORIZONTAL_GRAVITY_MASK = (GravityAxis.AXIS_SPECIFIED | GravityAxis.AXIS_PULL_BEFORE | GravityAxis.AXIS_PULL_AFTER) << GravityAxis.AXIS_X_SHIFT;
    public static long VERTICAL_GRAVITY_MASK = (GravityAxis.AXIS_SPECIFIED | GravityAxis.AXIS_PULL_BEFORE | GravityAxis.AXIS_PULL_AFTER) << GravityAxis.AXIS_Y_SHIFT;
    public static long RELATIVE_HORIZONTAL_GRAVITY_MASK = (Left.set(Right).value());

    public static ViewContentGravity create(long value) {
        return new ViewContentGravity(value);
    }

    public static ViewContentGravity get(String gravityString) {
        ViewContentGravity result = None;

        if(gravityString.equals("top")) {
            result = Top;
        } else if(gravityString.equals("bottom")) {
            result = Bottom;
        } else if(gravityString.equals("left")) {
            result = Left;
        } else if(gravityString.equals("right")) {
            result = Right;
        } else if(gravityString.equals("center_vertical")) {
            result = CenterVertical;
        } else if(gravityString.equals("fill_vertical")) {
            result = FillVertical;
        } else if(gravityString.equals("center_horizontal")) {
            result = CenterHorizontal;
        } else if(gravityString.equals("fill_horizontal")) {
            result = FillHorizontal;
        } else if(gravityString.equals("center")) {
            result = Center;
        } else if(gravityString.equals("fill")) {
            result = Fill;
        }
        return result;
    }

    public static ViewContentGravity getFromAttribute(String gravityAttribute) {
        long result = None.value();

        if(gravityAttribute != null) {
            String[] components = gravityAttribute.split("|");
            for (String component : components) {
                result |= get(component).value();
            }
        }
        return new ViewContentGravity(result);
    }

    @Override
    protected ViewContentGravity wrap(long value, long mask) {
       return new ViewContentGravity(value, mask);
    }

    public ViewContentGravity(long value) {
        super(value);
    }

    private ViewContentGravity(long value, long mask) {
        super(value, mask);
    }

    @Override
    protected ViewContentGravity[] _values() {
        return values;
    }
}
