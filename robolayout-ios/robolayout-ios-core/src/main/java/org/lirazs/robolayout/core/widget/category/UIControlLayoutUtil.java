package org.lirazs.robolayout.core.widget.category;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.ViewContentGravity;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIControlLayoutUtil {

    public static void applyAttributes(UIControl control, Map<String, String> attrs, NSObject actionTarget) {
        UIViewLayoutUtil.applyAttributes(control, attrs);

        if(actionTarget != null) {
            String onClickKeyPath = attrs.get("onClickKeyPath");
            String onClickSelector = attrs.get("onClick");

            Selector selector = null;
            if(onClickSelector != null && (selector = Selector.register(onClickSelector)) != null) {
                if(!onClickKeyPath.isEmpty()) {
                    control.addTarget(actionTarget.getKeyValueCoder().getValue(onClickKeyPath), selector, UIControlEvents.TouchUpInside);
                } else {
                    control.addTarget(actionTarget, selector, UIControlEvents.TouchUpInside);
                }
            }
        }
    }

    public static ViewContentGravity getGravity(UIControl control) {
        long result = ViewContentGravity.None.value();

        switch (control.getContentVerticalAlignment()) {
            case Top:
                result |= ViewContentGravity.Top.value();
                break;
            case Bottom:
                result |= ViewContentGravity.Bottom.value();
                break;
            case Center:
                result |= ViewContentGravity.CenterVertical.value();
                break;
            case Fill:
                result |= ViewContentGravity.FillVertical.value();
                break;
        }

        switch (control.getContentHorizontalAlignment()) {
            case Left:
                result |= ViewContentGravity.Left.value();
                break;
            case Right:
                result |= ViewContentGravity.Right.value();
                break;
            case Center:
                result |= ViewContentGravity.CenterHorizontal.value();
                break;
            case Fill:
                result |= ViewContentGravity.FillHorizontal.value();
                break;
        }
        return new ViewContentGravity(result);
    }

    public static void setGravity(UIControl control, ViewContentGravity gravity) {
        if((gravity.value() & ViewContentGravity.Top.value()) == ViewContentGravity.Top.value()) {
            control.setContentVerticalAlignment(UIControlContentVerticalAlignment.Top);
        } else if((gravity.value() & ViewContentGravity.Bottom.value()) == ViewContentGravity.Bottom.value()) {
            control.setContentVerticalAlignment(UIControlContentVerticalAlignment.Bottom);
        } else if((gravity.value() & ViewContentGravity.FillVertical.value()) == ViewContentGravity.FillVertical.value()) {
            control.setContentVerticalAlignment(UIControlContentVerticalAlignment.Fill);
        } else if((gravity.value() & ViewContentGravity.CenterVertical.value()) == ViewContentGravity.CenterVertical.value()) {
            control.setContentVerticalAlignment(UIControlContentVerticalAlignment.Center);
        }

        if((gravity.value() & ViewContentGravity.Left.value()) == ViewContentGravity.Left.value()) {
            control.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Left);
        } else if((gravity.value() & ViewContentGravity.Right.value()) == ViewContentGravity.Right.value()) {
            control.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Right);
        } else if((gravity.value() & ViewContentGravity.FillHorizontal.value()) == ViewContentGravity.FillHorizontal.value()) {
            control.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Fill);
        } else if((gravity.value() & ViewContentGravity.CenterHorizontal.value()) == ViewContentGravity.CenterHorizontal.value()) {
            control.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Center);
        }
    }
}
