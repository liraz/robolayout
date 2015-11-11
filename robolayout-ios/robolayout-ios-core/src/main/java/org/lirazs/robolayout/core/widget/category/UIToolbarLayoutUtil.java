package org.lirazs.robolayout.core.widget.category;

import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutMeasureSpec;
import org.lirazs.robolayout.core.view.LayoutMeasureSpecMode;
import org.lirazs.robolayout.core.view.LayoutMeasuredSize;
import org.lirazs.robolayout.core.view.LayoutMeasuredState;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UINavigationBar;
import org.robovm.apple.uikit.UIToolbar;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIToolbarLayoutUtil {

    public static UIBarStyle getBarStyleByAttribute(String barStyle) {
        UIBarStyle result = UIBarStyle.Default;

        if(barStyle.equals("black")) {
            result = UIBarStyle.Black;
        } else if(barStyle.equals("default")) {
            result = UIBarStyle.Default;
        }
        return result;
    }

    public static void applyAttributes(UIToolbar toolbar, Map<String, String> attrs, NSObject actionTarget) {
        UIViewLayoutUtil.applyAttributes(toolbar, attrs);

        UIColor tintColor = ResourceAttributesUtil.getColorValue(attrs, "tintColor");
        if(tintColor != null) {
            toolbar.setTintColor(tintColor);
        }

        String barStyle = attrs.get("barStyle");
        if(barStyle != null) {
            toolbar.setBarStyle(UIToolbarLayoutUtil.getBarStyleByAttribute(barStyle));
        }

        String translucent = attrs.get("translucent");
        if(translucent != null) {
            toolbar.setTranslucent(Boolean.parseBoolean(translucent));
        }
    }

    public static void measure(UINavigationBar navigationBar, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();

        double widthSize = widthMeasureSpec.getSize();
        double heightSize = heightMeasureSpec.getSize();

        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize();
        measuredSize.getWidth().setState(LayoutMeasuredState.None);
        measuredSize.getHeight().setState(LayoutMeasuredState.None);

        switch (widthMode) {
            case AtMost:
            case Exactly:
                measuredSize.getWidth().setSize(widthSize);
                break;

            default:
                measuredSize.getWidth().setSize(320);
                break;
        }

        switch (heightMode) {
            case Exactly:
                measuredSize.getHeight().setSize(heightSize);
                break;
            default:
                measuredSize.getHeight().setSize(44);
                break;
        }

        CGSize minSize = UIViewLayoutUtil.getMinSize(navigationBar);
        measuredSize.getWidth().setSize(Math.max(measuredSize.getWidth().getSize(), minSize.getHeight()));

        UIViewLayoutUtil.setMeasuredDimensionSize(navigationBar, measuredSize);
    }
}
