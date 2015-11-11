package org.lirazs.robolayout.core.widget.category;

import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutMeasureSpec;
import org.lirazs.robolayout.core.view.LayoutMeasureSpecMode;
import org.lirazs.robolayout.core.view.LayoutMeasuredSize;
import org.lirazs.robolayout.core.view.LayoutMeasuredState;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UINavigationBar;
import org.robovm.apple.uikit.UISwitch;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UISwitchLayoutUtil {

    public static void applyAttributes(UISwitch uiSwitch, Map<String, String> attrs, NSObject actionTarget) {
        UIControlLayoutUtil.applyAttributes(uiSwitch, attrs, actionTarget);

        UIColor tintColor = ResourceAttributesUtil.getColorValue(attrs, "tintColor");
        if(tintColor != null) {
            uiSwitch.setTintColor(tintColor);
        }
        UIColor onTintColor = ResourceAttributesUtil.getColorValue(attrs, "onTintColor");
        if(tintColor != null) {
            uiSwitch.setOnTintColor(onTintColor);
        }
        UIColor thumbTintColor = ResourceAttributesUtil.getColorValue(attrs, "thumbTintColor");
        if(tintColor != null) {
            uiSwitch.setThumbTintColor(thumbTintColor);
        }

        String isOn = attrs.get("isOn");
        if(isOn != null) {
            uiSwitch.setOn(Boolean.parseBoolean(isOn));
        }
    }

    public static void measure(UISwitch uiSwitch, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {

        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize();
        measuredSize.getWidth().setSize(uiSwitch.getFrame().getSize().getWidth());
        measuredSize.getWidth().setState(LayoutMeasuredState.None);

        measuredSize.getHeight().setSize(uiSwitch.getFrame().getSize().getHeight());
        measuredSize.getHeight().setState(LayoutMeasuredState.None);

        UIViewLayoutUtil.setMeasuredDimensionSize(uiSwitch, measuredSize);
    }
}
